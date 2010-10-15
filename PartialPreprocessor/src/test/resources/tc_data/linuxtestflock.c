#ifdef TEST
test
#else
nottest
#ifndef __BLA
notbla
#else

#ifdef REDIRECT
outputA
#else
outputB
#endif

#endif

#endif