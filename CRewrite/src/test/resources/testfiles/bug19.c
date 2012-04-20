int bar() {return 1;}

int hello() {
// copied from coreutils/ls.pi:80305 - 80320
	if (
#if definedEx(CONFIG_FEATURE_CLEAN_UP)
	1
#endif
#if !definedEx(CONFIG_FEATURE_CLEAN_UP)
	0
#endif
	)

#if !definedEx(CONFIG_FEATURE_LS_RECURSIVE)
	((void)0)
#endif
#if definedEx(CONFIG_FEATURE_LS_RECURSIVE)
	bar()
#endif
	;

	return 1;
}