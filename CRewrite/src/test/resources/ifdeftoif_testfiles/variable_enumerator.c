enum {
	LOGMODE_NONE = 0,
	LOGMODE_STDIO = (1 << 0),
	LOGMODE_SYSLOG = (1 << 1) * 
#if definedEx(CONFIG_FEATURE_SYSLOG)
1
#endif
#if !definedEx(CONFIG_FEATURE_SYSLOG)
0
#endif
,
	LOGMODE_BOTH = LOGMODE_SYSLOG + LOGMODE_STDIO,
};

void main() {}