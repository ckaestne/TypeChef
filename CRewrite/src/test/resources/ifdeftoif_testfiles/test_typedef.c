#if (definedEx(CONFIG_FEATURE_FIND_CONTEXT) || definedEx(CONFIG_SELINUX))
typedef unsigned int access_vector_t;
#endif

#if (definedEx(CONFIG_FEATURE_FIND_CONTEXT) || definedEx(CONFIG_SELINUX))
extern access_vector_t foo();
#endif

unsigned int foo() {
	return 1;
}

void main() {
}