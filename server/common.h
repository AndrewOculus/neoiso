/* 
 * Clean Engine
 * 
 *
 * Autor: Enchanted Hunter
 */

#ifndef COMMON
#define COMMON

typedef enum read_errors_s{
    R_OK,               /* reading is right */
    R_FILE_NAME_NULL,   /* wrong filename */
    R_DATA_PTR_NULL,    /* wrong data pointer */
    R_SIZE_PTR_NULL,    /* wrong size pointer */
    R_FD_NULL,          /* wrong file descriptor pointer */
    R_FSEEK_EOF_ERR,    /* seek end of file error */
    R_SIZE_ERR,         /* wrong file size */
    R_FSEEK_SET_ERR,    /* seek begin of file error */
    R_MALLOC_ERR,       /* malloc error */
    R_READ_ERR,         /* read error */
} read_errors_t;

typedef enum write_errors_s{
    W_OK,               /* writing is right */
    W_FILE_NAME_NULL,   /* wrong filename */
    W_DATA_PTR_NULL,    /* wrong data pointer */
    W_SIZE_ERR,         /* wrong size */
    W_FD_NULL,          /* wrong filre */
    W_WRITE_ERR,        /* write error */
} write_errors_t;

/*** 
*   This function allocates memory (file size + 1), if the return flag is 0
*   Use free to free allocated memory
*   This return read_errors_t code errors  
****/
int ReadFile( const char *filename, long *size, void **data );

/***
*   This function write size bytes to file *filename*
*   This return write_errors_t code errors  
****/
int WriteFile( const char *filename, void *data, long size );

#endif
