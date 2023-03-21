#include "map.h"


float reverseFloat( const float inFloat )
{
   float retVal;
   char *floatToConvert = ( char* ) & inFloat;
   char *returnFloat = ( char* ) & retVal;
   // swap the bytes into a temporary buffer
   returnFloat[0] = floatToConvert[3];
   returnFloat[1] = floatToConvert[2];
   returnFloat[2] = floatToConvert[1];
   returnFloat[3] = floatToConvert[0];

   return retVal;
}

int width;
int height;

int *errMap;
int **ptrMap;
int **ptrObjs;

void loadMap( int ***ptrMapH, int ***ptrObjsH, int **errMapH, int* w, int* h ){

    long len;
    void *file;
    int r = ReadFile("../android/assets/map/compressed", &len, &file);

    width   = BYTESWAP32( *((int *)file + 0) );
    height  = BYTESWAP32( *((int *)file + 1) );

    printf(" rc = %d len = %ld width = %d height = %d \n", r, len, width, height);

    int *fragmentsMap = (int *)malloc( width * height * sizeof(int) );

    errMap = (int *)malloc( GRID_TILES_WIDTH * GRID_TILES_HEIGHT * sizeof(int) );
    int *tmpMap = (int *)malloc( width * height * sizeof(int) );
    ptrMap = (int **)malloc( GRIDS_PER_FILE * GRIDS_PER_FILE * sizeof(int*) );
    int *fileMap = (int *)(file) + 2;


    int index = 0;
    for (int j = 0; j < height ; j++) {
        for (int i = 0; i < width ; i++) {
            tmpMap[ j * width + i ] = ( *(fileMap + index));

            index ++;
        }
    }


    for (int y = 0; y < GRIDS_PER_FILE; y++) {
        for (int x = 0; x < GRIDS_PER_FILE; x++) {

            int shift = y * GRIDS_PER_FILE + x;
            int *fragmentMap = fragmentsMap + shift * GRID_TILES_WIDTH * GRID_TILES_HEIGHT;

            for (int i = 0; i < GRID_TILES_WIDTH; i++) {
                for (int j = 0; j < GRID_TILES_HEIGHT; j++) {
                    int xxx = GRID_TILES_WIDTH - i - 1 + x * GRID_TILES_WIDTH;
                    int yyy = GRID_TILES_HEIGHT - j - 1 + y * GRID_TILES_HEIGHT;
                    int mapTile = tmpMap[ yyy * width + xxx ];
                    fragmentMap[ j * GRID_TILES_WIDTH + i ] = mapTile;
                }
            }
        }
    }

    int x = 0;
    int y = 0;

    for (int y = 0; y < GRIDS_PER_FILE; y++) {
        for (int x = 0; x < GRIDS_PER_FILE; x++) {
            int shift = y * GRIDS_PER_FILE + x;
            ptrMap[ shift ] = fragmentsMap + shift * GRID_TILES_WIDTH * GRID_TILES_HEIGHT;
        }
    }

    //error map
    for (int i = 0; i < GRID_TILES_WIDTH; i++) {
        for (int j = 0; j < GRID_TILES_HEIGHT; j++) {
            errMap[ j * GRID_TILES_WIDTH + i ] = 0x0;
        }
    }

    free( file );

    //game objects

    r = ReadFile("../android/assets/map/objs", &len, &file);
    int count = BYTESWAP32( *((int *)file + 0) );

    printf( "count: %d\n", count);
    int *objects = (int *)file + 1;
    ptrObjs = (int **)malloc( GRIDS_PER_FILE * GRIDS_PER_FILE * sizeof(int*) );

    for (int y = 0; y < GRIDS_PER_FILE; y++) {
        for (int x = 0; x < GRIDS_PER_FILE; x++) {
            int shift = y * GRIDS_PER_FILE + x;
            ptrObjs[ shift ] = malloc( 512 * sizeof(int) );
            *( ptrObjs[ shift ]) = 0;
        }
    }


    for ( int i = 0 ; i < count*3 ; i+=3){

        int type = BYTESWAP32( *(objects + i + 0));
        float xxx = reverseFloat( *( (float*)(objects + i + 1)));
        float yyy = reverseFloat( *( (float*)(objects + i + 2)));

        int xxxx = (int)(xxx / (GRID_TILES_WIDTH * TILE_HEIGHT));
        int yyyy = (int)(yyy / (GRID_TILES_HEIGHT * TILE_HEIGHT));

        int shift = yyyy * GRIDS_PER_FILE + xxxx;
        int nextPos = *( ptrObjs[ shift ]);
        
        if( yyyy == 0 && xxxx == 10)
        printf("nextPos %d shift %d  pos %d %d %f %f\n", nextPos, shift, xxxx, yyyy, xxx, yyy);

        *(ptrObjs[ shift ] + nextPos * 3 + 0 + 1) = *(objects + i + 0);
        *(ptrObjs[ shift ] + nextPos * 3 + 1 + 1) = *(objects + i + 1);
        *(ptrObjs[ shift ] + nextPos * 3 + 2 + 1) = *(objects + i + 2);

        // *(ptrObjs[ shift ]) = BYTESWAP32(*(ptrObjs[ shift ]));
        *(ptrObjs[ shift ]) += 1;
        // *(ptrObjs[ shift ]) = BYTESWAP32(*(ptrObjs[ shift ]));

    }

    free( file );
    *w = width;
    *h = height;

    *ptrMapH = ptrMap;
    *ptrObjsH = ptrObjs;
    *errMap = errMap;

}
