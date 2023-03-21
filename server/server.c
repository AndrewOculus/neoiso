
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <memory.h>
#include <string.h>
#include <errno.h>
#include <pthread.h>

#include "server.h"
#include "vector.h"
#include "common.h"
#include "player.h"
#include "map.h"

#define PORT 27019 

int *errMap;
int **ptrMap;
int **ptrObjs;

vector_t *players;

int width;
int height;

pthread_mutex_t mutex;

struct sockaddr_in addr;

int sendall(int s, char *buf, int len, int flags)
{
    int total = 0;
    int n;

    while(total < len)
    {
        n = send(s, buf+total, len-total, flags);
        if(n == -1) { break; }
        total += n;
    }

    return (n==-1 ? -1 : total);
}

int setPosition( server_thread_t *threadArgs, int32_t *data ){

    float x = reverseFloat( *((float *)data + 1) );
    float y = reverseFloat( *((float *)data + 2) );

    // printf("x:%f y:%f \n", x, y);
    pthread_mutex_lock(threadArgs->mutex);


    for(int i = 0; i < players->length ; i++){
        player_t *pl;
        getVector( players, i, (void**)&pl );

        if(pl->id == threadArgs->threadId ){
            pl->positionX = x;
            pl->positionY = y;
            break;
        }
    }

    int32_t size = (int32_t)players->length;

    int rcs = sendall( threadArgs->socketDescriptor, &size, 4 , 0 );

    if( rcs <= 0 ){
        pthread_mutex_unlock(threadArgs->mutex);
        return rcs;
    }

    float output[size*3];

    if(size == 0){
        goto unlock_mutex;
    }

    for( int i = 0 ; i < size ; i ++){

        player_t *pl;
        getVector( players, i, (void**)&pl );

        if(pl->id == threadArgs->threadId ){
            output[i*3 + 0] = 0.0f;
        }else{
            output[i*3 + 0] = pl->id;
        }

        output[i*3 + 1] = pl->positionX;
        output[i*3 + 2] = pl->positionY;
    }

    rcs = sendall( threadArgs->socketDescriptor, &output, 4 * 3 * size , 0 );

    if( rcs <= 0 ){
        pthread_mutex_unlock(threadArgs->mutex);
        return rcs;
    }

unlock_mutex:
    pthread_mutex_unlock(threadArgs->mutex);

    return 1;
}

int giveMap( server_thread_t *threadArgs, int32_t *data ){
    int x = BYTESWAP32(data[1]);
    int y = BYTESWAP32(data[2]);

    // printf("from: %d %d\n", x, y);

    if( x >= 0 && x < GRIDS_PER_FILE && y >= 0 && y < GRIDS_PER_FILE ){

        int numLocation = y * GRIDS_PER_FILE + x;
        int numTile = 0;

        int* response = (*(ptrMap + numLocation ) + numTile );

        int rcs = sendall( threadArgs->socketDescriptor, response, GRID_TILES_WIDTH * GRID_TILES_HEIGHT * 4 , 0 );

        if( rcs <= 0 ){
            return rcs;
        }

        response = (*(ptrObjs + numLocation ) + numTile );
        int *objectsDataPtr = response;

        // printf("have objects: %d \n", *objectsDataPtr);

        rcs = sendall( threadArgs->socketDescriptor, objectsDataPtr, 512 * 4 , 0 );

        if( rcs <= 0 ){
            return rcs;
        }

    }else{

        int* response = errMap;
        int rcs = sendall( threadArgs->socketDescriptor, response, GRID_TILES_WIDTH * GRID_TILES_HEIGHT * 4 , 0 );

        if( rcs <= 0 ){
            return rcs;
        }

        response = (*(ptrObjs + 0 ) + 0 );
        int *objectsDataPtr = response;

        rcs = sendall( threadArgs->socketDescriptor, objectsDataPtr, 512 * 4 , 0 );

        if( rcs <= 0 ){
            return rcs;
        }
    }
    return 1;
}

void *severRoutine( void *args ){

    for( int i = 0; i < players->length ;i ++){
        printf("%s ", ((player_t*)players->data)[i].name);
    }
    puts("");

    server_thread_t *threadArgs = (server_thread_t*)args;
    
    printf ("threadId: %d, socketDescriptor: %d \n", threadArgs->threadId, threadArgs->socketDescriptor);

    for( ;; ){

        int32_t data[20];
        int rc = recv( threadArgs->socketDescriptor, data, 20*4, 0 );
        
        if( rc <= 0 )
        {
            goto sock_close;
        }

        int32_t code = BYTESWAP32(data[0]);

        int ret = -1;

        switch( code ){
            case 0:
                ret = giveMap( threadArgs, data );
                break;
            case 1:
                ret = setPosition( threadArgs, data );
                break;
        }

        if( ret<= 0 )
            goto sock_close;

        // usleep( 10000 );
    }


sock_close:
    close ( threadArgs->socketDescriptor );

    printf ("connection close threadId: %d, socketDescriptor: %d \n", threadArgs->threadId, threadArgs->socketDescriptor);

    pthread_mutex_lock(threadArgs->mutex);

    for(int i = 0; i < players->length ; i++){
        player_t *pl;
        getVector( players, i, (void**)&pl );

        if(pl->id == threadArgs->threadId ){
            removeVector( &players, i );
            break;
        }
    }

    for( int i = 0; i < players->length ;i ++){
        printf("%s ", ((player_t*)players->data)[i].name);
    }
    puts("");
    pthread_mutex_unlock(threadArgs->mutex);

    free ( args );

    return 0;
}

int main(int argc, char **argv)
{

    pthread_mutex_init(&mutex, NULL);

    initVector( &players, sizeof(player_t) );

    loadMap( &ptrMap, &ptrObjs, &errMap, &width, &height );

    printf("%d %d \n", width, height);

    /*создаём сокет*/
    int s = socket(AF_INET, SOCK_STREAM, 0);
    if(s < 0)
    {
        perror("Error calling socket");
        return 0;
    }

    int true = 1;
    setsockopt( s, SOL_SOCKET, SO_REUSEADDR, &true, sizeof(int));

    /*определяем прослушиваемый порт и адрес*/
    addr.sin_family = AF_INET;
    addr.sin_port = htons(PORT);
    addr.sin_addr.s_addr = htonl(INADDR_ANY);
    if( bind(s, (struct sockaddr *)&addr, sizeof(addr)) < 0 )
    {
        perror("Error calling bind");
        return 0;
    }

    /*помечаем сокет, как пассивный - он будет слушать порт*/
    if( listen(s, 5) )
    {
        perror("Error calling listen");
        return 0;
    }

    int threadId = 1;
    /*начинаем слушать, для соединения создаём другой сокет, в котором можем общаться.*/
    for ( ;; ){
        int s1 = accept(s, NULL, NULL);
        pthread_t tid;
        server_thread_t *threadArgs = (server_thread_t*)malloc(sizeof(server_thread_t));
        
        threadArgs->threadId = threadId;
        threadArgs->socketDescriptor = s1;
        threadArgs->mutex = &mutex;

        player_t player;
        player.id = threadId;

        sprintf( player.name, "player_%d", threadId );

        pthread_mutex_lock(threadArgs->mutex);
        addVector( &players, (void*)&player );
        pthread_mutex_unlock(threadArgs->mutex);

        threadId++;
        
        pthread_create( &tid, NULL, severRoutine, threadArgs );
        pthread_detach( tid );
    }
    
    return 0;
}