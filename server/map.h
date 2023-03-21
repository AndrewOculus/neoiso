#ifndef MAP_H
#define MAP_H

#define GRID_TILES_WIDTH 16
#define GRID_TILES_HEIGHT 16

#define TILE_WIDTH 64
#define TILE_HEIGHT 32

#define GRIDS_PER_FILE 256

#define BYTESWAP16(n) (((n&0xFF00)>>8)|((n&0x00FF)<<8))
#define BYTESWAP32(n) ((BYTESWAP16((n&0xFFFF0000)>>16))|((BYTESWAP16(n&0x0000FFFF))<<16))
#define BYTESWAP64(n) ((BYTESWAP32((n&0xFFFFFFFF00000000)>>32))|((BYTESWAP32(n&0x00000000FFFFFFFF))<<32))

float reverseFloat( const float inFloat );
void loadMap( int ***ptrMapH, int ***ptrObjsH, int **errMapH, int* w, int* h );

#endif