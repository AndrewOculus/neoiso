#include "server.h"
#include "net.h"

void* netRoutine( void *args ){

    server_state_t state = *(server_state_t*)args;

    pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);

    serverHandler();

    do{
        serverLoop();
    }
    while( state.isWorkersRunning );

    free( args );
    printf("Net Routine stopped \n");

    return NULL;
}

void* clientRoutine( void *args ){

    size_t threadNumber;
    client_thread_args_t *threadArgs = (client_thread_args_t*) args;
    threadNumber = threadArgs->threadNumber;

    server_state_t *state = threadArgs->state;

    pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);

    int i;
    int count;
    int player;
    void *playerPtr;

    do{
        SEM_WAIT( &state->clientsSemaphore[threadNumber] );

        if( !state->isWorkersRunning ){
            break;
        }

        count = state->players->length / state->clientsThreadsActive;

        player_state_t *playerState;

        for( i = 0; i < count ; i++ ){
            player = i * state->clientsThreadsActive + threadNumber;
            getVector( state->players, player, &playerPtr );
            playerState = (player_state_t*)playerPtr;

        }

        SEM_POST( &state->routineSemaphore[threadNumber] );

    }
    while( state->isWorkersRunning );

    free( args );
    printf("Client Routine %lu stopped \n", threadNumber);

    return NULL;
}

void updateThreadsState( server_state_t *state, long delta ){

}

void* workRoutine( void *args ){

    server_state_t state = *(server_state_t*)args;

    pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);

    int i;
    long dt = 0;
    double start, finish;

    do{
        updateThreadsState( &state, dt );

        CURRENT_TIME_SEC( &start );

        for( i = 0 ; i < state.clientsThreadsActive ; i++ ){
            SEM_POST( &state.clientsSemaphore[i] );
        }

        for( i = 0 ; i < state.clientsThreadsActive ; i++ ){
            SEM_WAIT( &state.routineSemaphore[i] );
        }

        CURRENT_TIME_SEC( &finish );

        // printf("wait loop seconds %f \n", finish - start );

        SLEEPMS( 10 );
    }
    while( state.isServerRunning );

    printf("Work Routine stopped \n");

    return NULL;
}