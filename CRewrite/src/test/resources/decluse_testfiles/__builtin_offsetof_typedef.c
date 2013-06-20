typedef unsigned int __socklen_t;
typedef __socklen_t socklen_t;
struct sockaddr
  {
    char sa_data[14];		/* Address data.  */
  };
  struct sockaddr_in
  {
    char sa_data[14];		/* Address data.  */
  };
  struct sockaddr_in6
  {
    char sa_data[14];		/* Address data.  */
  };

typedef struct len_and_sockaddr {
	socklen_t len;
	union {
		struct sockaddr sa;
		struct sockaddr_in sin;
#if definedEx(CONFIG_FEATURE_IPV6)
		struct sockaddr_in6 sin6;
#endif
	} u;
} len_and_sockaddr;
enum {
	LSA_LEN_SIZE = __builtin_offsetof (len_and_sockaddr, u),
	LSA_SIZEOF_SA = sizeof(
		union {
			struct sockaddr sa;
			struct sockaddr_in sin;
#if definedEx(CONFIG_FEATURE_IPV6)
			struct sockaddr_in6 sin6;
#endif
		}
	)
};
void main() {}