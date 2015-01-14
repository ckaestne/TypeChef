#ifdef CONF_FOO
#define CONF_FOO _CONF_FOO
#endif

#ifdef CONF_FOO
#define FOO CONF_FOO
#else
#define FOO BLA
#endif

FOO