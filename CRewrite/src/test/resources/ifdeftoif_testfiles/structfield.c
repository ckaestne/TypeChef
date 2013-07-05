typedef unsigned int __uid_t;
typedef __uid_t uid_t;

extern int sprintf(char *__restrict __s, const char *__restrict __format, ...) __attribute__((__nothrow__));

typedef struct file_header_t {
  char *name;
  char *link_target;
  
  #if definedEx(CONFIG_FEATURE_TAR_UNAME_GNAME)
  char *tar__uname;
  #endif
  
  
  #if definedEx(CONFIG_FEATURE_TAR_UNAME_GNAME)
  char *tar__gname;
  #endif
  uid_t uid;
} file_header_t;

void main(const file_header_t *file_header) {
  #if definedEx(CONFIG_FEATURE_TAR_UNAME_GNAME)
  char *user;
  #endif
  #if definedEx(CONFIG_FEATURE_TAR_UNAME_GNAME)
  (user = file_header->tar__uname);
  #endif
  #if definedEx(CONFIG_FEATURE_TAR_UNAME_GNAME)
  char uid[((sizeof(int ) * 3) + 2)];
  #endif
  
  #if definedEx(CONFIG_FEATURE_TAR_UNAME_GNAME)
  if ((user == ((void *) 0))) {
    sprintf(uid , "%u" , ((unsigned ) file_header->uid));
    (user = uid);
  }  
  #endif
}