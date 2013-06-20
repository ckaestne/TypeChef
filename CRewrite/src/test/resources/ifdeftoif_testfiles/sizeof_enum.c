#if definedEx(CONFIG_FEATURE_IPV6)
int inttt;
#endif
struct ifdef_options {
  int config_feature_ipv6;
} options;

struct test {
  int config_feature_ipv6;
};

struct test s;
enum {
  LSA_SIZEOF_SA = sizeof(union {
  int integer;
  char *string;
  float real;
  #if definedEx(CONFIG_FEATURE_IPV6)
  void *pointer;
  #endif
  }),
  rawr
} day;


void main() {}