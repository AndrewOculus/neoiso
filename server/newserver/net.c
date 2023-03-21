#include "net.h"

int sockfd; 
#define MAXPACKET   500 
#define PORT        27019 

char buffer[MAXPACKET]; 
struct sockaddr_in servaddr, cliaddr; 

void serverHandler(){

        
    if ( (sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ) { 
        perror("socket creation failed"); 
        exit(-1); 
    } 
        
    memset(&servaddr, 0, sizeof(servaddr)); 
    memset(&cliaddr, 0, sizeof(cliaddr)); 
        
    // Filling server information 
    servaddr.sin_family         = AF_INET; // IPv4 
    servaddr.sin_addr.s_addr    = INADDR_ANY; 
    servaddr.sin_port           = htons(PORT); 
        
    // Bind the socket with the server address 
    if ( bind(sockfd, (const struct sockaddr *)&servaddr,  
            sizeof(servaddr)) < 0 ) 
    { 
        perror("bind failed"); 
        exit(-1); 
    } 
}

void serverLoop(){

    int len, n; 
    len = sizeof(cliaddr);
    char *hello = "Hello from server"; 
    char *hello1 = "Hello from server1"; 
    char *hello2 = "Hello from server2"; 

    n = recvfrom(sockfd, (char *)buffer, MAXPACKET,  
                MSG_WAITALL, ( struct sockaddr *) &cliaddr, 
                &len); 
    buffer[n] = '\0'; 
    printf("Client : %s\n", buffer); 
    sendto(sockfd, (const char *)hello, strlen(hello),  
        0, (const struct sockaddr *) &cliaddr, 
        len); 

    sendto(sockfd, (const char *)hello1, strlen(hello1),  
        0, (const struct sockaddr *) &cliaddr, 
        len); 

    sendto(sockfd, (const char *)hello2, strlen(hello2),  
        0, (const struct sockaddr *) &cliaddr, 
        len); 
    printf("Hello message sent.\n");  
}