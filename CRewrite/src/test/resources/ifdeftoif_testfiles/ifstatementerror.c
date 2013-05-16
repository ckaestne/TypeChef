#if definedEx(CONFIG_LFS)
typedef unsigned long long uoff_t;
#endif


#if !definedEx(CONFIG_LFS)
typedef unsigned long uoff_t;
#endif

typedef long int __off_t;
typedef __off_t off_t;


extern int strcmp(const char *__s1, const char *__s2) __attribute__((__nothrow__)) __attribute__((__pure__)) __attribute__((__nonnull__ (1, 2)));

typedef struct file_header_t {
  char *name;
  char *link_target;
  
  #if definedEx(CONFIG_FEATURE_TAR_UNAME_GNAME)
  char *tar__uname;
  #endif
  
  
  #if definedEx(CONFIG_FEATURE_TAR_UNAME_GNAME)
  char *tar__gname;
  #endif
} file_header_t;

typedef struct archive_handle_t {
  off_t offset;
  file_header_t *file_header;
  #if (definedEx(CONFIG_RPM2CPIO) || definedEx(CONFIG_RPM) || definedEx(CONFIG_CPIO))
  uoff_t cpio__blocks;
  #endif
} archive_handle_t ;

void main(archive_handle_t *archive_handle) {
  file_header_t *file_header =  archive_handle->file_header;
  if ((strcmp(file_header->name , "TRAILER!!!") == 0)) {
    (archive_handle->cpio__blocks = (((uoff_t ) (archive_handle->offset + 511)) >> 9));
    goto create_hardlinks;
  }
  create_hardlinks:	
}