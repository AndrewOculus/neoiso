#ifndef PLAYER_H
#define PLAYER_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>


typedef struct player_s {

    uint64_t id;
    char name[16];
    float positionX;
    float positionY;
    int32_t segmentX;
    int32_t segmentY;
    
} player_t;



#endif