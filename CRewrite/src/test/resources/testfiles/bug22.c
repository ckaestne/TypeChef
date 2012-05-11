void foo() {

    (typed->gid = read_num(ar.formatted.gid , 10));
#if definedEx(CONFIG_FEATURE_AR_LONG_FILENAMES)
    if ((ar.formatted.name[0] == '/')) {
        unsigned long_offset;
        if ((long_offset >= ar_long_name_size)) {
            bb_error_msg_and_die("can't resolve long filename");
        }
        (typed->name = xstrdup((ar_long_names + long_offset)));
    }
    else {
        (typed->name = xstrndup(ar.formatted.name , 16));
    }
#endif
#if !definedEx(CONFIG_FEATURE_AR_LONG_FILENAMES)
    {
        (typed->name = xstrndup(ar.formatted.name , 16));
    }
#endif
    (typed->name[strcspn(typed->name , " /")] = '0');
    return 0;
}
