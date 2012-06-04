#if definedEx(CONFIG_FEATURE_AR_CREATE)
  static int write_ar_archive(void)
{
    a;
    /* optional, since we exit right after we return */
    if (
#if definedEx(A)
1
#else
0
#endif
) {
        b;
    }

    return 0;
}
#endif /* FEATURE_AR_CREATE */