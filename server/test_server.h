#ifndef TEST_SERVER
#define TEST_SERVER

#include <stdio.h>
#include <pthread.h>
#include <unistd.h>
#include <signal.h>

#include "vector.h"

#ifdef __APPLE__
#include <dispatch/dispatch.h>

typedef dispatch_semaphore_t SEM_T;

void SEM_INIT( SEM_T *sem, int PERM, int VAL ){
    SEM_T S = dispatch_semaphore_create( VAL );
    *sem = S;
}
#define SEM_WAIT( S ) dispatch_semaphore_wait( *S, DISPATCH_TIME_FOREVER )
#define SEM_POST( S ) dispatch_semaphore_signal( *S )
#define SEM_DESTROY( S ) dispatch_release( *S )

#else
#include <semaphore.h>

typedef sem_t SEM_T;

#define SEM_INIT( S, PERM, VAL ) sem_init( S, PERM, VAL )
#define SEM_WAIT( S ) sem_wait( S )
#define SEM_POST( S ) sem_post( S )
#define SEM_DESTROY( S ) sem_destroy( S )

#endif

#define SLEEPMS( MS ) usleep ( MS##000 )

typedef struct player_state_s{
    int32_t x;
    int32_t y;
    float relX;
    float relY;
} player_state_t;

typedef struct server_state_s{
    vector_t *players;
    size_t clientsThreadsCount;
    pthread_t clientsThreads[4];
    SEM_T clientsSemaphore[4];
    SEM_T routineSemaphore[4];
} server_state_t;

typedef struct client_thread_args_s{
    size_t threadNumber;
    server_state_t *state;
} client_thread_args_t;

#endif
