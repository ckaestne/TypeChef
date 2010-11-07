#ifdef G_OS_WIN32
#define g_get_user_name g_get_user_name_utf8
#endif

char* g_get_user_name(void);
