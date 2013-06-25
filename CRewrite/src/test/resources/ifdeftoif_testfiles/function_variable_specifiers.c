#if definedEx(CONFIG_UNCOMPRESS)
static 
#if (definedEx(CONFIG_DESKTOP) && definedEx(CONFIG_UNCOMPRESS))
long
#endif
 
#if (definedEx(CONFIG_DESKTOP) && definedEx(CONFIG_UNCOMPRESS))
long
#endif
 int test()  {
  return 0;
}
#endif
typedef long int __time_t;
typedef __time_t time_t;
int bbunpack(char **argv, 
#if definedEx(CONFIG_DESKTOP)
long
#endif
 typedef struct unpack_info_t {
  time_t mtime;
} unpack_info_t;
#if definedEx(CONFIG_DESKTOP)
long
#endif
 int test);
 
#if definedEx(CONFIG_UNCOMPRESS)
int uncompress_main(int argc, char **argv)  {
  return bbunpack(argv , test , make_new_name_generic , "Z");
}
#endif

void main() {
}