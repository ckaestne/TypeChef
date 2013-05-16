typedef struct len_and_sockaddr {
  int j;
  union  {
    struct sockaddr  sa;
    struct sockaddr_in  sin;
    
    #if definedEx(CONFIG_FEATURE_IPV6)
    struct sockaddr_in6  sin6;
    #endif
    
  } u;
} len_and_sockaddr;
enum  {
  LSA_LEN_SIZE = __builtin_offsetof(len_and_sockaddr , u),
  LSA_SIZEOF_SA = sizeof(union  {
    #if definedEx(CONFIG_FEATURE_IPV6)
    struct sockaddr_in6  sin6;
    #endif
  } )
} ;

void main() {}