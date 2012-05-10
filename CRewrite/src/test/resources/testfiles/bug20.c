#if definedEx(CONFIG_FEATURE_AR_CREATE)
  static int write_ar_archive(void)
{
    struct stat st;
    archive_handle_t *out_handle;

    xfstat(handle->src_fd, &st, handle->ar__name);

    /* if archive exists, create a new handle for output.
     * we create it in place of the old one.
     */
    if (st.st_size != 0) {
        out_handle = init_handle();
        xunlink(handle->ar__name);
        out_handle->src_fd = xopen(handle->ar__name, 01 | 0100 | 01000);
        out_handle->accept = handle->accept;
    } else {
        out_handle = handle;
    }

    handle->ar__out = out_handle;

    xwrite(out_handle->src_fd, "!<arch>" "\n", 7 + 1);
    out_handle->offset += 7 + 1;

    /* skip to the end of the archive if we have to append stuff */
    if (st.st_size != 0) {
        handle->filter = filter_replaceable;
        handle->action_data = copy_data;
        unpack_ar_archive(handle);
    }
        while (write_ar_header(out_handle) == 0)
        continue;

    /* optional, since we exit right after we return */
    if (
#if definedEx(CONFIG_FEATURE_CLEAN_UP)
1
#else
0
#endif
) {
        close(handle->src_fd);
        if (out_handle->src_fd != handle->src_fd)
            close(out_handle->src_fd);
    }

    return 0;
}
#endif /* FEATURE_AR_CREATE */