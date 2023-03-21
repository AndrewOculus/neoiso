#include "test_server.h"


server_state_t state;
pthread_t tid;

void updateThreadsState( server_state_t *state );
void sigintHandler( int signum );
void* clientRoutine( void *args );
void* workRoutine( void *args );

int main(void){

    signal( SIGINT, sigintHandler );

    initVector( &state.players, sizeof(player_state_t) );
    state.clientsThreadsCount = 4;    

    int i;
    for( i = 0 ; i < state.clientsThreadsCount ; i++ ){
        SEM_INIT( &state.clientsSemaphore[i], 0, 0 );
        SEM_INIT( &state.routineSemaphore[i], 0, 0 );

        client_thread_args_t *threadArgs = (client_thread_args_t*)malloc(sizeof(client_thread_args_t));
        threadArgs->threadNumber = i;
        threadArgs->state = &state;
        
        pthread_create( &state.clientsThreads[i], NULL, clientRoutine, threadArgs );
        pthread_detach( state.clientsThreads[i] );
    }

    for( int i = 0 ; i < 400 ; i ++ ){
        player_state_t playerState;
        playerState.x = i;
        playerState.y = i;
        playerState.relX = (float)i;
        playerState.relY = (float)i;

        addVector( &state.players, (void*)&playerState ); 
    }

    pthread_create( &tid, NULL, (void*)workRoutine, NULL );
    pthread_detach( tid );

    while(1){
        // console
        // printf("MIN Thread wait\n");

        SLEEPMS( 5 );
    }

    return 0;
}

void* clientRoutine( void *args ){

    size_t threadNumber;
    client_thread_args_t *threadArgs = (client_thread_args_t*) args;
    threadNumber = threadArgs->threadNumber;

    server_state_t *state = threadArgs->state;

    {
        int res;
        res = pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);
    }

    int i;
    int count;
    int player;
    void *playerPtr;

    while(1){

        SEM_WAIT( &state->clientsSemaphore[threadNumber] );

        count = state->players->length / state->clientsThreadsCount;

        for( i = 0; i < count ; i++ ){
            player = i * count + threadNumber;
            getVector( state->players, player, &playerPtr );
            player_state_t *playerState = (player_state_t*)playerPtr;
            SLEEPMS( 1 );
        }

        printf("Thread num %lu finish!\n", threadNumber);

        SEM_POST( &state->routineSemaphore[threadNumber] );
    }
}

void updateThreadsState( server_state_t *state ){

    // if( state->players->length <= 10 ){
    //     state->clientsThreadsCount = 1;
    // }

    // if( state->players->length > 10 && state->players->length < 20 ){
    //     state->clientsThreadsCount = 2;
    // }

    // if( state->players->length >= 20 ){
    //     state->clientsThreadsCount = 4;
    // }

    // create or cancel threads
}

void* workRoutine( void *args ){

    {
        int res;
        res = pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);
    }

    while(1){
        // unlock semathore
        // wait until threads make loop
        for( int i = 0 ; i < state.clientsThreadsCount ; i++ ){
            SEM_POST( &state.clientsSemaphore[i] );
        }

        printf("Thread wait\n");

        for( int i = 0 ; i < state.clientsThreadsCount ; i++ ){
            SEM_WAIT( &state.routineSemaphore[i] );
        }

        updateThreadsState( &state );
        SLEEPMS( 1000 );
    }
}

void sigintHandler( int signum ){

    puts("");

    int i;
    int rc;
    for( i = 0 ; i < state.clientsThreadsCount ; i++ ){
        rc = pthread_cancel( state.clientsThreads[i] );
        if(rc == 0)
            printf("Thread %d stop\n", i);
    }

    rc = pthread_cancel( tid );
    if(rc == 0)
        printf("Thread stop\n");

    SLEEPMS( 1000 );

    for( i = 0 ; i < state.clientsThreadsCount ; i++ ){
        SEM_POST( &state.clientsSemaphore[i] );
        SEM_POST( &state.routineSemaphore[i] );

        SEM_DESTROY( &state.clientsSemaphore[i] );
        SEM_DESTROY( &state.routineSemaphore[i] );
    }

    freeVector( &state.players );

    printf("Inside handler function\n");
    exit(0);
}