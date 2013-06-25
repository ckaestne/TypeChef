typedef struct archive_handle_t {
  int src_fd;
   
  #if definedEx(CONFIG_FEATURE_AR_CREATE)
  struct archive_handle_t  *ar__out;
  #endif
  
} archive_handle_t;
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
#if definedEx(CONFIG_FEATURE_AR_CREATE)
static void copy_data(archive_handle_t *handle)  {
  archive_handle_t *out_handle =  handle->ar__out;
  struct file_header_t  *fh =  handle->file_header;
  (out_handle->file_header = fh);
}
#endif

void main() {}