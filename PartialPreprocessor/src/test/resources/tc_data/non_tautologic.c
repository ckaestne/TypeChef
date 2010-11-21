#ifdef FOO
#ifdef G_OS_WIN32
#define XX1 YY1
#define XX2 YY2
#define XX3 YY3
#define XX4 YY4
#endif

XX1
#endif


#ifndef G_OS_WIN32
XX2
#endif

#if defined(G_OS_WIN32) && defined(FOO)
XX3
#endif

#ifdef FOO
#ifdef G_OS_WIN32
#ifdef BAR
XX4
#endif
#endif
#endif