#ifdef FOO
#define DEBUG(n, format, arg...) \
    printk("%d" format, n, ## arg)
#else
#define DEBUG(n, arg...) bar(arg)
#endif

DEBUG(0, "foo");
DEBUG(1, "foo", arg);
