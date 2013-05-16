#if definedEx(CONFIG_LFS)
typedef unsigned long long uoff_t;
#endif


#if !definedEx(CONFIG_LFS)
typedef unsigned long uoff_t;
#endif

typedef struct archive_handle_t {
  int src_fd;
  #if (definedEx(CONFIG_CPIO) || definedEx(CONFIG_RPM2CPIO) || definedEx(CONFIG_RPM))
  uoff_t cpio__blocks;
  #endif
} archive_handle_t;

void main() {}