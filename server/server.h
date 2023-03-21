#ifndef SERVER_H
#define SERVER_H

/* определяет типы данных */
#include <sys/types.h>
/* "Главный" по сокетам */
#include <sys/socket.h>
/* sockaddr_in struct, sin_family, sin_port, in_addr_t, in_port_t, ...*/
#include <netinet/in.h>


typedef char byte;

typedef struct server_thread_s {

    int threadId;
    int socketDescriptor;
    int index;
    pthread_mutex_t *mutex;

} server_thread_t;

#endif