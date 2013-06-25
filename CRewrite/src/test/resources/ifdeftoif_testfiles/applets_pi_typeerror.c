#if (definedEx(CONFIG_FEATURE_FIND_CONTEXT) || definedEx(CONFIG_SELINUX))
typedef char *security_context_t;
#endif

#if (definedEx(CONFIG_FEATURE_FIND_CONTEXT) || definedEx(CONFIG_SELINUX))
union selinux_callback {
	security_context_t *ctx;
} ;
#endif
void main() {}