#if (definedEx(CONFIG_FEATURE_FIND_CONTEXT) || definedEx(CONFIG_SELINUX))
typedef struct  {
  int i;
} context_s_t;

typedef context_s_t *context_t;
#endif

void foo() {
}