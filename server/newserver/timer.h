#ifndef TIMER
#define TIMER

#ifdef __APPLE__

#include <mach/clock.h>
#include <mach/mach.h>

#define CURRENT_TIME_SEC( E ){                                          \
    clock_serv_t cclock;                                                \
    mach_timespec_t mts;                                                \
    host_get_clock_service(mach_host_self(), CALENDAR_CLOCK, &cclock);  \
    clock_get_time(cclock, &mts);                                       \
    mach_port_deallocate(mach_task_self(), cclock);                     \
    *E = mts.tv_sec;                                                    \
    *E += mts.tv_nsec / 1000000000.0;                                   \
}

#else

#define POSIX_C_SOURCE 199309L

typedef struct timespec TIME_T;

#define CURRENT_TIME_SEC( E ){          \
    TIME_T T;                           \
    clock_gettime(CLOCK_MONOTONIC, T);  \
    *E = T.tv_sec;                      \
    *E += T.tv_nsec / 1000000000.0;     \
}

#endif

#endif