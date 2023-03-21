#ifndef SERVER
#define SERVER

#include <stdio.h>
#include <pthread.h>
#include <unistd.h>
#include <signal.h>
#include <time.h>

#include "vector.h"
#include "sem.h"
#include "timer.h"
#include "net.h"

#define SLEEPMS( MS ) usleep ( MS##000 )

typedef size_t  boolean;
#define true    1
#define false   0

typedef struct player_state_s{
    int32_t         x;
    int32_t         y;
    float           relX;
    float           relY;
} player_state_t;

typedef struct server_state_s{
    vector_t        *players;
    pthread_t       *clientsThreads;
    SEM_T           *clientsSemaphore;
    SEM_T           *routineSemaphore;
    size_t          clientsThreadsCount;
    size_t          clientsThreadsActive;
    boolean         isWorkersRunning;
    boolean         isServerRunning;
} server_state_t;

typedef struct client_thread_args_s{
    size_t          threadNumber;
    server_state_t  *state;
} client_thread_args_t;

void    updateThreadsState  ( server_state_t *state, long delta );
void*   clientRoutine       ( void *args );
void*   workRoutine         ( void *args );
void*   netRoutine          ( void *args );

#endif