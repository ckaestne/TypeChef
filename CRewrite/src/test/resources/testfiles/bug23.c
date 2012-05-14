void foo() {
    if ((archive_handle->filter(archive_handle) == 0)) {
        archive_handle->action_header(typed);
#if (definedEx(CONFIG_DPKG) || definedEx(CONFIG_DPKG_DEB))
        if (archive_handle->dpkg__sub_archive) {
            while ((archive_handle->dpkg__action_data_subarchive(archive_handle->dpkg__sub_archive) == 0)) continue;
        }
        else archive_handle->action_data(archive_handle);
#endif
#if (!definedEx(CONFIG_DPKG) && !definedEx(CONFIG_DPKG_DEB))
        archive_handle->action_data(archive_handle);
#endif
    }
    else {
        data_skip(archive_handle);
    }
    (archive_handle->offset += typed->size);
    lseek(archive_handle->src_fd , archive_handle->offset , 0);
    return 0;
}
