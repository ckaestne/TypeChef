#if defined(CONFIG_H) && defined(CONFIG_A)
int x;
#elif (!defined(CONFIG_H)) && defined(CONFIG_A)
int y;
#endif
