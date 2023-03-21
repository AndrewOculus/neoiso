#ifndef VECTOR_H
#define VECTOR_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define SAFE_VECTOR
#define MIN_CAPACITY 16

typedef struct vector_s{
    size_t capacity;
    size_t length;
    size_t size;
    void *data;
} vector_t;

typedef enum vector_error_s{
    V_OK,               /* operation is right */
    V_NULL_VECTOR_PTR,  /* wrong vector argument */
    V_NULL_DATA_PTR,    /* wrong data pointer */
    V_NULL_ITEM_PTR,    /* wrong item pointer */
    V_WRONG_CAPACITY,   /* wrong capacity value */
    V_WRONG_SIZE,       /* wrong size argument */
    V_WRONG_INDEX,      /* wrong index argument */
    V_EMPTY             /* vector zero length */
} vector_error_t;

vector_error_t initVector   ( vector_t **vector, size_t size );
vector_error_t addVector    ( vector_t **vector, const void *item );
vector_error_t getVector    ( vector_t  *vector, size_t index, void **data );
vector_error_t removeVector ( vector_t **vector, size_t index );
vector_error_t freeVector   ( vector_t **vector );
vector_error_t printVector  ( vector_t  *vector );

#endif