void foo() {
    if (a) {
        func01();
#if (definedEx(CONFIG_DPKG) || definedEx(CONFIG_DPKG_DEB))
        if (b) {
            while (c) continue;
        }
        else func02();
#endif
#if (!definedEx(CONFIG_DPKG) && !definedEx(CONFIG_DPKG_DEB))
        func03();
#endif
    }
    else {
        func04();
    }
    int e;
}
