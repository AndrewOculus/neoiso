/* 
 * Clean Engine
 * 
 *
 * Autor: Enchanted Hunter
 */

#include <stdio.h>
#include <stdlib.h>

#include "common.h"

int ReadFile(const char *filename, long *size, void **data){

    if(filename == NULL)
        return R_FILE_NAME_NULL;

    if(data == NULL)
        return R_DATA_PTR_NULL;

    if(size == NULL)
        return R_SIZE_PTR_NULL;

    FILE *fd;
    int rs;

    fd = fopen(filename, "rb");
    if( fd == NULL )
        return R_FD_NULL;

    rs = fseek(fd, 0L, SEEK_END);

    if(rs != 0){
        fclose(fd);
        return R_FSEEK_EOF_ERR;
    }
    
    *size = ftell(fd);

    if(*size <= 0){
        fclose(fd);
        return R_SIZE_ERR;
    }

    rs = fseek(fd, 0L, SEEK_SET);

    if(rs != 0){
        fclose(fd);
        return R_FSEEK_SET_ERR;
    }

    *data = malloc(*size + 1);

    if(*data == NULL){
        fclose(fd);
        return R_MALLOC_ERR;
    }

    rs = fread(*data, sizeof(char), *size, fd);

    if(rs <= 0){
        free(*data);
        *data = NULL;

        fclose(fd);
        return R_READ_ERR;
    }

    fclose(fd);

    *(((char*)*data) + *size) = '\0';

    return R_OK;
}

int WriteFile(const char *filename, void *data, long size){

    if(filename == NULL)
        return W_FILE_NAME_NULL;

    if(data == NULL)
        return W_DATA_PTR_NULL;

    if(size <= 0)
        return W_SIZE_ERR;

    FILE *fd;
    
    fd = fopen(filename, "w");

    if( fd == NULL )
        return W_FD_NULL;

    int rs;

    rs = fwrite (data , sizeof(char), size, fd);
    
    if(rs != size){
        fclose(fd);
        return W_WRITE_ERR;
    }

    fclose(fd);
    return W_OK;
}