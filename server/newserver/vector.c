#include "vector.h" 

vector_error_t addVector( vector_t **vector, const void *item ){

#ifdef SAFE_VECTOR
    if( *vector == NULL )
        return V_NULL_VECTOR_PTR;

    if( (*vector)->data == NULL )
        return V_NULL_DATA_PTR;

    if( (*vector)->length > (*vector)->capacity )
        return V_WRONG_CAPACITY;

    if( item == NULL )
        return V_NULL_ITEM_PTR;
#endif

    if( (*vector)->length + 1 > (*vector)->capacity ){
        size_t newCapacity = (size_t)( (*vector)->capacity * 1.5 );
        size_t newVectorLen = (size_t)( newCapacity * (*vector)->size + sizeof(vector_t) );
        void *newVec = realloc( *vector, newVectorLen );
        *vector = newVec;
        (*vector)->capacity = newCapacity;
        (*vector)->data = (*vector) + 1;
    }

    memcpy( (void*)((*vector) + 1) + (*vector)->size * (*vector)->length, item, (*vector)->size );
    (*vector)->length += 1;

    return V_OK;
}

vector_error_t initVector( vector_t **vector, size_t size ){

#ifdef SAFE_VECTOR

    if( vector == NULL )
        return V_NULL_VECTOR_PTR;

    if( size == 0)
        return V_WRONG_SIZE;

#endif

    size_t capacity = MIN_CAPACITY;

    *vector = (vector_t *)malloc(sizeof(vector_t) + capacity * size);
    (*vector)->size = size;
    (*vector)->length = 0;
    (*vector)->capacity = capacity;
    (*vector)->data = (*vector) + 1;

    return V_OK;
}

vector_error_t getVector( vector_t *vector, size_t index, void **data ){

#ifdef SAFE_VECTOR

    if( vector == NULL )
        return V_NULL_VECTOR_PTR;

    if( vector->length < index )
        return V_WRONG_INDEX;

    if( vector->data == NULL )
        return V_NULL_DATA_PTR;

    if( vector->length > vector->capacity )
        return V_WRONG_CAPACITY;

#endif

    *data = vector->data + index * vector->size;

    return V_OK;
}

vector_error_t removeVector( vector_t **vector, size_t index ){

#ifdef SAFE_VECTOR

    if( (*vector) == NULL )
        return V_NULL_VECTOR_PTR;

    if( (*vector)->length < index )
        return V_WRONG_INDEX;

    if( (*vector)->data == NULL )
        return V_NULL_DATA_PTR;

    if( (*vector)->length > (*vector)->capacity )
        return V_WRONG_CAPACITY;

#endif

    if( (*vector)->length == 0 )
        return V_EMPTY;

    memmove( (*vector)->data + index * (*vector)->size, (*vector)->data + (index + 1) * (*vector)->size, ((*vector)->length - index) * (*vector)->size );

    (*vector)->length -= 1;

    if( (*vector)->length < (*vector)->capacity * 0.5 && (*vector)->capacity != MIN_CAPACITY ){

        size_t newCapacity = (size_t)( (*vector)->capacity * 0.5 );
        newCapacity = newCapacity < MIN_CAPACITY ? MIN_CAPACITY : newCapacity;
        size_t newVectorLen = (size_t)( newCapacity * (*vector)->size + sizeof(vector_t) );
        void *newVec = realloc( *vector, newVectorLen );
        *vector = newVec;
        (*vector)->capacity = newCapacity;
        (*vector)->data = (*vector) + 1;

    }

    return V_OK;
}

vector_error_t freeVector( vector_t **vector ){

    if( vector == NULL )
        return V_NULL_VECTOR_PTR;

    if( *vector == NULL )
        return V_NULL_VECTOR_PTR;

    free( *vector );
    *vector = NULL;

    return V_OK;
}

vector_error_t printVector  ( vector_t  *vector ){

#ifdef SAFE_VECTOR

    if( vector == NULL )
        return V_NULL_VECTOR_PTR;
        
#endif

    printf( "Length: %lu, Capacity: %lu, Size: %lu, DataPtrISNull: %d \n", vector->length, vector->capacity, vector->size, vector->data == NULL );

    return V_OK;
}