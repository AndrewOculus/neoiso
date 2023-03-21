#include "server.h"


server_state_t state;
pthread_t workThreadId;
pthread_t netThreadId;

void sigintHandler( int signum );
void fillPlayers();

int main(void){

    signal( SIGINT, sigintHandler );
    initVector( &state.players, sizeof(player_state_t) );
    state.isServerRunning = true;
    state.isWorkersRunning = true;
    state.clientsThreadsCount = 8;
    state.clientsThreadsActive = 3;    

    state.clientsSemaphore  = (SEM_T*) malloc ( sizeof(SEM_T) * state.clientsThreadsCount );
    state.routineSemaphore  = (SEM_T*) malloc ( sizeof(SEM_T) * state.clientsThreadsCount );
    state.clientsThreads    = (pthread_t*) malloc ( sizeof(pthread_t) * state.clientsThreadsCount );

    fillPlayers();

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

    pthread_create( &workThreadId, NULL, (void*)workRoutine, &state );
    pthread_detach( workThreadId );

    pthread_create( &netThreadId, NULL, (void*)netRoutine, &state );
    pthread_detach( netThreadId );

    while(1){
        SLEEPMS( 200 );
    }

    return 0;
}

void fillPlayers(){
    for( int i = 0 ; i < 500 ; i ++ ){
        player_state_t playerState;
        playerState.x = i;
        playerState.y = i;
        playerState.relX = (float)i;
        playerState.relY = (float)i;

        addVector( &state.players, (void*)&playerState ); 
    }
}

void sigintHandler( int signum ){

    int i;
    int rc;

    puts("");

    state.isWorkersRunning = false;

    SLEEPMS( 1000 );

    for( i = 0 ; i < state.clientsThreadsCount ; i++ ){
        SEM_POST( &state.clientsSemaphore[i] );
    }

    state.isServerRunning = false;

    for( i = 0 ; i < state.clientsThreadsCount ; i++ ){
        SEM_POST( &state.routineSemaphore[i] );
    }

    SLEEPMS( 1000 );

    // for( i = 0 ; i < state.clientsThreadsCount ; i++ ){
    //     rc = pthread_cancel( state.clientsThreads[i] );
    //     if(rc == 0)
    //         printf("Thread %d stop\n", i);
    // }

    rc = pthread_cancel( workThreadId );
    if(rc == 0)
        printf("Thread stop\n");

    rc = pthread_cancel( netThreadId );
    if(rc == 0)
        printf("Thread stop\n");

    SLEEPMS( 1500 );

    for( i = 0 ; i < state.clientsThreadsCount ; i++ ){
        SEM_POST( &state.clientsSemaphore[i] );
        SEM_POST( &state.routineSemaphore[i] );
        SEM_DESTROY( &state.clientsSemaphore[i] );
        SEM_DESTROY( &state.routineSemaphore[i] );
    }

    freeVector( &state.players );

    free( state.routineSemaphore );
    free( state.clientsSemaphore );
    free( state.clientsThreads   );

    printf("Inside handler function\n");
    exit(0);
}