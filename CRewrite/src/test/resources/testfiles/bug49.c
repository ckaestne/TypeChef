int ls_main() {

	if (b) {
		c;
	} else {
		d;
		if (e) {
			if (
            #if definedEx(CONFIG_FEATURE_CLEAN_UP)
            1
            #endif
            #if !definedEx(CONFIG_FEATURE_CLEAN_UP)
            0
            #endif
            )
				i;
		}
	}
	if (
    #if definedEx(CONFIG_FEATURE_CLEAN_UP)
    j
    #endif
    #if !definedEx(CONFIG_FEATURE_CLEAN_UP)
    k
    #endif
    )
        #if !definedEx(CONFIG_FEATURE_LS_RECURSIVE)
        l
        #endif
        #if definedEx(CONFIG_FEATURE_LS_RECURSIVE)
        m
        #endif
        ;
	n;
}
