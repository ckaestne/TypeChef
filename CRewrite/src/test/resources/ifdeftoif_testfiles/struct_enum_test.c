#if definedEx(CONFIG_FEATURE_IPV6)
enum  {
  ACTION_RECURSE = (1 << 0)
} ;
#endif

#if definedEx(CONFIG_FEATURE_IPV6)
enum  _ARR {
  ACTION_RECURSE = (1 << 0)
} ;
#endif

#if (!definedEx(CONFIG_USE_BB_SHADOW) && definedEx(CONFIG_FEATURE_SHADOWPASSWDS))
struct spwd {
  char *sp_namp;
  char *sp_pwdp;
  long int sp_lstchg;
  long int sp_min;
  long int sp_max;
  long int sp_warn;
  long int sp_inact;
  long int sp_expire;
  unsigned long int sp_flag;
} ;
#endif

void main() {}