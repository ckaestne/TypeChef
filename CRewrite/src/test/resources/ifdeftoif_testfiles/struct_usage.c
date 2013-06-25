struct passwd {
  char *pw_name;
  char *pw_passwd;
  char *pw_gecos;
  char *pw_dir;
  char *pw_shell;
} ;

#if definedEx(CONFIG_USE_BB_PWD_GRP)
extern struct passwd  *bb_internal_getpwnam(const char *__name);
#endif

void main() {}