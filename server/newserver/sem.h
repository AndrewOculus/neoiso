#ifndef SEM
#define SEM

#ifdef __APPLE__
#include <dispatch/dispatch.h>

typedef dispatch_semaphore_t SEM_T;

#define SEM_INIT( sem, PERM, VAL ){                 \
    SEM_T S = dispatch_semaphore_create( VAL );     \
    *sem = S;                                       \
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

#endif