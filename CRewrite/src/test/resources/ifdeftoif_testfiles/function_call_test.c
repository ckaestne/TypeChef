extern int getopt32(char **argv, const char *applet_opts, ...);
#if definedEx(CONFIG_UNLZMA)
void foo() {
  #if (definedEx(CONFIG_LZMA) && definedEx(CONFIG_UNLZMA))
  int opts =  getopt32(argv , "cfvdt");
  #endif
  #if (!definedEx(CONFIG_LZMA) || !definedEx(CONFIG_UNLZMA))
  getopt32(argv , "cfvdt");
  #endif
}
#endif

void main() {}