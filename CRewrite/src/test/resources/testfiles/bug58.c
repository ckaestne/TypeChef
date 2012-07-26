int foo() {
    a;

#if definedEx(B)
    b;
    llist_add_to(&control_tar_llist, (char*)"control.tar.gz");
#endif
#if definedEx(C)
    llist_add_to(&ar_archive->accept, (char*)"data.tar.bz2");
    llist_add_to(&control_tar_llist, (char*)"control.tar.bz2");
#endif
#if definedEx(D)
    llist_add_to(&ar_archive->accept, (char*)"data.tar.lzma");
    llist_add_to(&control_tar_llist, (char*)"control.tar.lzma");
#endif
    e;
}