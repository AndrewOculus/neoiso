#include <stdio.h>

#include "vector.h"

typedef struct test_s{
    size_t varA;
    size_t varB;
    size_t varC;
} test_t;

int main(void){

    vector_t *vector;
    initVector( &vector, sizeof(test_t) );

    for( int i = 0 ; i < 20 ; i++){
        test_t item;
        item.varA = i;
        item.varB = i;
        item.varC = i*2;
        addVector( &vector, (void**)&item );
    }

    test_t *item;

    printf( "%zu %zu |", vector->length, vector->capacity );
    for( int ii = 0; ii < vector->length ; ii ++){

        getVector( vector, ii, (void**)&item);
        printf( "%lu %lu %lu ", item->varA, item->varB, item->varC );
        
    }
    puts("");

    for( int ii = 0; ii < 12 ; ii ++){
        removeVector( &vector, 1 );
    }
    
    printf( "%zu %zu |", vector->length, vector->capacity );

    for( int ii = 0; ii < vector->length ; ii ++){

        getVector( vector, ii, (void**)&item );
        printf( "%lu %lu %lu ", item->varA, item->varB, item->varC );

    }
    puts("");

    printVector( vector );

    freeVector( &vector );

    return 0;
}