typedef unsigned int __uid_t;
struct passwd
{
  char *pw_name;		/* Username.  */
  char *pw_passwd;		/* Password.  */
  __uid_t pw_uid;		/* User ID.  */
  char *pw_gecos;		/* Real name.  */
  char *pw_dir;			/* Home directory.  */
  char *pw_shell;		/* Shell program.  */
};
extern struct passwd *bb_internal_getpwnam(const char *__name);
extern struct passwd *getpwnam (const char *__name);
extern __uid_t getuid (void) __attribute__ ((__nothrow__));
static char *rpm_getstr(int tag, int itemindex);

static void test(int fileref) {
	struct passwd *pw = 
#if definedEx(CONFIG_USE_BB_PWD_GRP)
bb_internal_getpwnam
#endif
#if !definedEx(CONFIG_USE_BB_PWD_GRP)
getpwnam
#endif
(rpm_getstr(1039, fileref));
	int uid = pw ? pw->pw_uid : getuid();
}

void main() {}