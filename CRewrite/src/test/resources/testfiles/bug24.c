#if definedEx(CONFIG_UNLZMA)
int unlzma_main(void)
{

#if definedEx(CONFIG_LZMA)
int opts =
#endif
 getopt32(y);

#if definedEx(CONFIG_LZMA)
    /* lzma without -d or -t? */
    if (x)
        bb_show_usage();
#endif

}
#endif
