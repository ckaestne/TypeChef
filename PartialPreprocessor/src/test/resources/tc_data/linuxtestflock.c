#ifdef TEST
#undef __USE_FILE_OFFSET64 
test
#else
#undef __USE_FILE_OFFSET64 
undef



#ifndef __USE_FILE_OFFSET64
notbla
#else

#ifdef REDIRECT
outputA
#else
outputB
#endif

#endif


#endif
