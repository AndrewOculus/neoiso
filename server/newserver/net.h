#ifndef NET
#define NET

#include <stdio.h>
#include <string.h>
#include <sys/types.h> 
#include <sys/socket.h> 
#include <netinet/in.h> 


void serverHandler();
void serverLoop();

#endif