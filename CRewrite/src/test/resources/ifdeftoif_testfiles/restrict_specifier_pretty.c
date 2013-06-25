typedef long int __time_t;
typedef long int __suseconds_t;
struct timezone {
  int tz_minuteswest;
  int tz_dsttime;
} ;
typedef struct timezone  *__restrict __timezone_ptr_t;
struct timeval {
  __time_t tv_sec;
  __suseconds_t tv_usec;
} ;
extern int gettimeofday(struct timeval  *__restrict__tv, __timezone_ptr_t __tz) __attribute__((__nothrow__)) __attribute__((__nonnull__ (1)));
 main()  {
  
}