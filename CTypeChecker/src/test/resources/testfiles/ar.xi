






















































typedef signed char int8_t;
typedef short int int16_t;
typedef int int32_t;

typedef long int int64_t;





typedef unsigned char uint8_t;
typedef unsigned short int uint16_t;

typedef unsigned int uint32_t;



typedef unsigned long int uint64_t;






typedef signed char int_least8_t;
typedef short int int_least16_t;
typedef int int_least32_t;

typedef long int int_least64_t;




typedef unsigned char uint_least8_t;
typedef unsigned short int uint_least16_t;
typedef unsigned int uint_least32_t;

typedef unsigned long int uint_least64_t;






typedef signed char int_fast8_t;

typedef long int int_fast16_t;
typedef long int int_fast32_t;
typedef long int int_fast64_t;






typedef unsigned char uint_fast8_t;

typedef unsigned long int uint_fast16_t;
typedef unsigned long int uint_fast32_t;
typedef unsigned long int uint_fast64_t;

typedef long int intptr_t;


typedef unsigned long int uintptr_t;

typedef long int intmax_t;
typedef unsigned long int uintmax_t;

typedef int bb__aliased_int __attribute__((__may_alias__));
typedef uint16_t bb__aliased_uint16_t __attribute__((__may_alias__));
typedef uint32_t bb__aliased_uint32_t __attribute__((__may_alias__));






typedef signed char smallint;
typedef unsigned char smalluint;













typedef unsigned char __u_char;
typedef unsigned short int __u_short;
typedef unsigned int __u_int;
typedef unsigned long int __u_long;


typedef signed char __int8_t;
typedef unsigned char __uint8_t;
typedef signed short int __int16_t;
typedef unsigned short int __uint16_t;
typedef signed int __int32_t;
typedef unsigned int __uint32_t;

typedef signed long int __int64_t;
typedef unsigned long int __uint64_t;





typedef long int __quad_t;
typedef unsigned long int __u_quad_t;




typedef unsigned long int __dev_t;
typedef unsigned int __uid_t;
typedef unsigned int __gid_t;
typedef unsigned long int __ino_t;
typedef unsigned long int __ino64_t;
typedef unsigned int __mode_t;
typedef unsigned long int __nlink_t;
typedef long int __off_t;
typedef long int __off64_t;
typedef int __pid_t;
typedef struct { int __val[2]; } __fsid_t;
typedef long int __clock_t;
typedef unsigned long int __rlim_t;
typedef unsigned long int __rlim64_t;
typedef unsigned int __id_t;
typedef long int __time_t;
typedef unsigned int __useconds_t;
typedef long int __suseconds_t;

typedef int __daddr_t;
typedef long int __swblk_t;
typedef int __key_t;


typedef int __clockid_t;


typedef void * __timer_t;


typedef long int __blksize_t;




typedef long int __blkcnt_t;
typedef long int __blkcnt64_t;


typedef unsigned long int __fsblkcnt_t;
typedef unsigned long int __fsblkcnt64_t;


typedef unsigned long int __fsfilcnt_t;
typedef unsigned long int __fsfilcnt64_t;

typedef long int __ssize_t;



typedef __off64_t __loff_t;
typedef __quad_t *__qaddr_t;
typedef char *__caddr_t;


typedef long int __intptr_t;


typedef unsigned int __socklen_t;







enum
{
  _ISupper = ((0) < 8 ? ((1 << (0)) << 8) : ((1 << (0)) >> 8)),
  _ISlower = ((1) < 8 ? ((1 << (1)) << 8) : ((1 << (1)) >> 8)),
  _ISalpha = ((2) < 8 ? ((1 << (2)) << 8) : ((1 << (2)) >> 8)),
  _ISdigit = ((3) < 8 ? ((1 << (3)) << 8) : ((1 << (3)) >> 8)),
  _ISxdigit = ((4) < 8 ? ((1 << (4)) << 8) : ((1 << (4)) >> 8)),
  _ISspace = ((5) < 8 ? ((1 << (5)) << 8) : ((1 << (5)) >> 8)),
  _ISprint = ((6) < 8 ? ((1 << (6)) << 8) : ((1 << (6)) >> 8)),
  _ISgraph = ((7) < 8 ? ((1 << (7)) << 8) : ((1 << (7)) >> 8)),
  _ISblank = ((8) < 8 ? ((1 << (8)) << 8) : ((1 << (8)) >> 8)),
  _IScntrl = ((9) < 8 ? ((1 << (9)) << 8) : ((1 << (9)) >> 8)),
  _ISpunct = ((10) < 8 ? ((1 << (10)) << 8) : ((1 << (10)) >> 8)),
  _ISalnum = ((11) < 8 ? ((1 << (11)) << 8) : ((1 << (11)) >> 8))
};

extern const unsigned short int **__ctype_b_loc (void)
     __attribute__ ((__nothrow__)) __attribute__ ((const));
extern const __int32_t **__ctype_tolower_loc (void)
     __attribute__ ((__nothrow__)) __attribute__ ((const));
extern const __int32_t **__ctype_toupper_loc (void)
     __attribute__ ((__nothrow__)) __attribute__ ((const));

extern int isalnum (int) __attribute__ ((__nothrow__));
extern int isalpha (int) __attribute__ ((__nothrow__));
extern int iscntrl (int) __attribute__ ((__nothrow__));
extern int isdigit (int) __attribute__ ((__nothrow__));
extern int islower (int) __attribute__ ((__nothrow__));
extern int isgraph (int) __attribute__ ((__nothrow__));
extern int isprint (int) __attribute__ ((__nothrow__));
extern int ispunct (int) __attribute__ ((__nothrow__));
extern int isspace (int) __attribute__ ((__nothrow__));
extern int isupper (int) __attribute__ ((__nothrow__));
extern int isxdigit (int) __attribute__ ((__nothrow__));



extern int tolower (int __c) __attribute__ ((__nothrow__));


extern int toupper (int __c) __attribute__ ((__nothrow__));

extern int isblank (int) __attribute__ ((__nothrow__));

extern int isascii (int __c) __attribute__ ((__nothrow__));



extern int toascii (int __c) __attribute__ ((__nothrow__));



extern int _toupper (int) __attribute__ ((__nothrow__));
extern int _tolower (int) __attribute__ ((__nothrow__));


typedef struct __locale_struct
{

  struct __locale_data *__locales[13];


  const unsigned short int *__ctype_b;
  const int *__ctype_tolower;
  const int *__ctype_toupper;


  const char *__names[13];
} *__locale_t;


typedef __locale_t locale_t;


extern int isalnum_l (int, __locale_t) __attribute__ ((__nothrow__));
extern int isalpha_l (int, __locale_t) __attribute__ ((__nothrow__));
extern int iscntrl_l (int, __locale_t) __attribute__ ((__nothrow__));
extern int isdigit_l (int, __locale_t) __attribute__ ((__nothrow__));
extern int islower_l (int, __locale_t) __attribute__ ((__nothrow__));
extern int isgraph_l (int, __locale_t) __attribute__ ((__nothrow__));
extern int isprint_l (int, __locale_t) __attribute__ ((__nothrow__));
extern int ispunct_l (int, __locale_t) __attribute__ ((__nothrow__));
extern int isspace_l (int, __locale_t) __attribute__ ((__nothrow__));
extern int isupper_l (int, __locale_t) __attribute__ ((__nothrow__));
extern int isxdigit_l (int, __locale_t) __attribute__ ((__nothrow__));

extern int isblank_l (int, __locale_t) __attribute__ ((__nothrow__));



extern int __tolower_l (int __c, __locale_t __l) __attribute__ ((__nothrow__));
extern int tolower_l (int __c, __locale_t __l) __attribute__ ((__nothrow__));


extern int __toupper_l (int __c, __locale_t __l) __attribute__ ((__nothrow__));
extern int toupper_l (int __c, __locale_t __l) __attribute__ ((__nothrow__));








struct dirent
  {

    __ino_t d_ino;
    __off_t d_off;



    unsigned short int d_reclen;
    unsigned char d_type;
    char d_name[256];
  };


enum
  {
    DT_UNKNOWN = 0,

    DT_FIFO = 1,

    DT_CHR = 2,

    DT_DIR = 4,

    DT_BLK = 6,

    DT_REG = 8,

    DT_LNK = 10,

    DT_SOCK = 12,

    DT_WHT = 14

  };







typedef struct __dirstream DIR;






extern DIR *opendir (const char *__name) __attribute__ ((__nonnull__ (1)));






extern DIR *fdopendir (int __fd);






extern int closedir (DIR *__dirp) __attribute__ ((__nonnull__ (1)));

extern struct dirent *readdir (DIR *__dirp) __attribute__ ((__nonnull__ (1)));

extern int readdir_r (DIR *__restrict __dirp,
        struct dirent *__restrict __entry,
        struct dirent **__restrict __result)
     __attribute__ ((__nonnull__ (1, 2, 3)));

extern void rewinddir (DIR *__dirp) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));




extern void seekdir (DIR *__dirp, long int __pos) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));


extern long int telldir (DIR *__dirp) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));



extern int dirfd (DIR *__dirp) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));




typedef long unsigned int size_t;







extern int scandir (const char *__restrict __dir,
      struct dirent ***__restrict __namelist,
      int (*__selector) (const struct dirent *),
      int (*__cmp) (const struct dirent **,
      const struct dirent **))
     __attribute__ ((__nonnull__ (1, 2)));

extern int alphasort (const struct dirent **__e1,
        const struct dirent **__e2)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));

extern __ssize_t getdirentries (int __fd, char *__restrict __buf,
    size_t __nbytes,
    __off_t *__restrict __basep)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2, 4)));














extern int *__errno_location (void) __attribute__ ((__nothrow__)) __attribute__ ((__const__));














typedef __u_char u_char;
typedef __u_short u_short;
typedef __u_int u_int;
typedef __u_long u_long;
typedef __quad_t quad_t;
typedef __u_quad_t u_quad_t;
typedef __fsid_t fsid_t;



typedef __loff_t loff_t;



typedef __ino_t ino_t;

typedef __dev_t dev_t;



typedef __gid_t gid_t;



typedef __mode_t mode_t;



typedef __nlink_t nlink_t;



typedef __uid_t uid_t;




typedef __off_t off_t;

typedef __pid_t pid_t;



typedef __id_t id_t;



typedef __ssize_t ssize_t;




typedef __daddr_t daddr_t;
typedef __caddr_t caddr_t;




typedef __key_t key_t;






typedef __clock_t clock_t;





typedef __time_t time_t;




typedef __clockid_t clockid_t;




typedef __timer_t timer_t;






typedef unsigned long int ulong;
typedef unsigned short int ushort;
typedef unsigned int uint;

typedef unsigned int u_int8_t __attribute__ ((__mode__ (__QI__)));
typedef unsigned int u_int16_t __attribute__ ((__mode__ (__HI__)));
typedef unsigned int u_int32_t __attribute__ ((__mode__ (__SI__)));
typedef unsigned int u_int64_t __attribute__ ((__mode__ (__DI__)));

typedef int register_t __attribute__ ((__mode__ (__word__)));













typedef int __sig_atomic_t;




typedef struct
  {
    unsigned long int __val[(1024 / (8 * sizeof (unsigned long int)))];
  } __sigset_t;




typedef __sigset_t sigset_t;






struct timespec
  {
    __time_t tv_sec;
    long int tv_nsec;
  };







struct timeval
  {
    __time_t tv_sec;
    __suseconds_t tv_usec;
  };



typedef __suseconds_t suseconds_t;



typedef long int __fd_mask;

typedef struct
  {





     __fd_mask __fds_bits[1024 / (8 * (int) sizeof (__fd_mask))];


  } fd_set;






typedef __fd_mask fd_mask;

extern int select (int __nfds, fd_set *__restrict __readfds,
     fd_set *__restrict __writefds,
     fd_set *__restrict __exceptfds,
     struct timeval *__restrict __timeout);

extern int pselect (int __nfds, fd_set *__restrict __readfds,
      fd_set *__restrict __writefds,
      fd_set *__restrict __exceptfds,
      const struct timespec *__restrict __timeout,
      const __sigset_t *__restrict __sigmask);









__extension__
extern unsigned int gnu_dev_major (unsigned long long int __dev)
     __attribute__ ((__nothrow__));
__extension__
extern unsigned int gnu_dev_minor (unsigned long long int __dev)
     __attribute__ ((__nothrow__));
__extension__
extern unsigned long long int gnu_dev_makedev (unsigned int __major,
            unsigned int __minor)
     __attribute__ ((__nothrow__));



typedef __blksize_t blksize_t;





typedef __blkcnt_t blkcnt_t;



typedef __fsblkcnt_t fsblkcnt_t;



typedef __fsfilcnt_t fsfilcnt_t;




typedef unsigned long int pthread_t;


typedef union
{
  char __size[56];
  long int __align;
} pthread_attr_t;



typedef struct __pthread_internal_list
{
  struct __pthread_internal_list *__prev;
  struct __pthread_internal_list *__next;
} __pthread_list_t;







typedef union
{
  struct __pthread_mutex_s
  {
    int __lock;
    unsigned int __count;
    int __owner;

    unsigned int __nusers;



    int __kind;

    int __spins;
    __pthread_list_t __list;

  } __data;
  char __size[40];
  long int __align;
} pthread_mutex_t;

typedef union
{
  char __size[4];
  int __align;
} pthread_mutexattr_t;




typedef union
{
  struct
  {
    int __lock;
    unsigned int __futex;
    __extension__ unsigned long long int __total_seq;
    __extension__ unsigned long long int __wakeup_seq;
    __extension__ unsigned long long int __woken_seq;
    void *__mutex;
    unsigned int __nwaiters;
    unsigned int __broadcast_seq;
  } __data;
  char __size[48];
  __extension__ long long int __align;
} pthread_cond_t;

typedef union
{
  char __size[4];
  int __align;
} pthread_condattr_t;



typedef unsigned int pthread_key_t;



typedef int pthread_once_t;





typedef union
{

  struct
  {
    int __lock;
    unsigned int __nr_readers;
    unsigned int __readers_wakeup;
    unsigned int __writer_wakeup;
    unsigned int __nr_readers_queued;
    unsigned int __nr_writers_queued;
    int __writer;
    int __shared;
    unsigned long int __pad1;
    unsigned long int __pad2;


    unsigned int __flags;
  } __data;

  char __size[56];
  long int __align;
} pthread_rwlock_t;

typedef union
{
  char __size[8];
  long int __align;
} pthread_rwlockattr_t;



typedef volatile int pthread_spinlock_t;




typedef union
{
  char __size[32];
  long int __align;
} pthread_barrier_t;

typedef union
{
  char __size[4];
  int __align;
} pthread_barrierattr_t;





struct flock
  {
    short int l_type;
    short int l_whence;

    __off_t l_start;
    __off_t l_len;



    __pid_t l_pid;
  };







struct stat
  {
    __dev_t st_dev;




    __ino_t st_ino;





     __nlink_t st_nlink;
    __mode_t st_mode;

    __uid_t st_uid;
    __gid_t st_gid;

    int __pad0;

    __dev_t st_rdev;




    __off_t st_size;


    __blksize_t st_blksize;

    __blkcnt_t st_blocks;

    struct timespec st_atim;
    struct timespec st_mtim;
    struct timespec st_ctim;

    long int __unused[3];






  };


extern int fcntl (int __fd, int __cmd, ...);

extern int open (const char *__file, int __oflag, ...) __attribute__ ((__nonnull__ (1)));

extern int openat (int __fd, const char *__file, int __oflag, ...)
     __attribute__ ((__nonnull__ (2)));

extern int creat (const char *__file, __mode_t __mode) __attribute__ ((__nonnull__ (1)));

extern int lockf (int __fd, int __cmd, __off_t __len);

extern int posix_fadvise (int __fd, __off_t __offset, __off_t __len,
     int __advise) __attribute__ ((__nothrow__));

extern int posix_fallocate (int __fd, __off_t __offset, __off_t __len);











 typedef int __gwchar_t;

typedef struct
  {
    long int quot;
    long int rem;
  } imaxdiv_t;

extern intmax_t imaxabs (intmax_t __n) __attribute__ ((__nothrow__)) __attribute__ ((__const__));


extern imaxdiv_t imaxdiv (intmax_t __numer, intmax_t __denom)
      __attribute__ ((__nothrow__)) __attribute__ ((__const__));


extern intmax_t strtoimax (const char *__restrict __nptr,
      char **__restrict __endptr, int __base) __attribute__ ((__nothrow__));


extern uintmax_t strtoumax (const char *__restrict __nptr,
       char ** __restrict __endptr, int __base) __attribute__ ((__nothrow__));


extern intmax_t wcstoimax (const __gwchar_t *__restrict __nptr,
      __gwchar_t **__restrict __endptr, int __base)
     __attribute__ ((__nothrow__));


extern uintmax_t wcstoumax (const __gwchar_t *__restrict __nptr,
       __gwchar_t ** __restrict __endptr, int __base)
     __attribute__ ((__nothrow__));





















struct iovec
  {
    void *iov_base;
    size_t iov_len;
  };


extern ssize_t readv (int __fd, const struct iovec *__iovec, int __count)
  ;

extern ssize_t writev (int __fd, const struct iovec *__iovec, int __count)
  ;

extern ssize_t preadv (int __fd, const struct iovec *__iovec, int __count,
         __off_t __offset) ;

extern ssize_t pwritev (int __fd, const struct iovec *__iovec, int __count,
   __off_t __offset) ;











typedef __socklen_t socklen_t;



enum __socket_type
{
  SOCK_STREAM = 1,


  SOCK_DGRAM = 2,


  SOCK_RAW = 3,

  SOCK_RDM = 4,

  SOCK_SEQPACKET = 5,


  SOCK_DCCP = 6,

  SOCK_PACKET = 10,







  SOCK_CLOEXEC = 02000000,


  SOCK_NONBLOCK = 04000


};


typedef unsigned short int sa_family_t;



struct sockaddr
  {
    sa_family_t sa_family;
    char sa_data[14];
  };

struct sockaddr_storage
  {
    sa_family_t ss_family;
    unsigned long int __ss_align;
    char __ss_padding[(128 - (2 * sizeof (unsigned long int)))];
  };



enum
  {
    MSG_OOB = 0x01,

    MSG_PEEK = 0x02,

    MSG_DONTROUTE = 0x04,






    MSG_CTRUNC = 0x08,

    MSG_PROXY = 0x10,

    MSG_TRUNC = 0x20,

    MSG_DONTWAIT = 0x40,

    MSG_EOR = 0x80,

    MSG_WAITALL = 0x100,

    MSG_FIN = 0x200,

    MSG_SYN = 0x400,

    MSG_CONFIRM = 0x800,

    MSG_RST = 0x1000,

    MSG_ERRQUEUE = 0x2000,

    MSG_NOSIGNAL = 0x4000,

    MSG_MORE = 0x8000,

    MSG_WAITFORONE = 0x10000,


    MSG_CMSG_CLOEXEC = 0x40000000



  };




struct msghdr
  {
    void *msg_name;
    socklen_t msg_namelen;

    struct iovec *msg_iov;
    size_t msg_iovlen;

    void *msg_control;
    size_t msg_controllen;




    int msg_flags;
  };

struct cmsghdr
  {
    size_t cmsg_len;




    int cmsg_level;
    int cmsg_type;

    __extension__ unsigned char __cmsg_data [];

  };

extern struct cmsghdr *__cmsg_nxthdr (struct msghdr *__mhdr,
          struct cmsghdr *__cmsg) __attribute__ ((__nothrow__));

enum
  {
    SCM_RIGHTS = 0x01





  };









struct linger
  {
    int l_onoff;
    int l_linger;
  };

extern int recvmmsg (int __fd, struct mmsghdr *__vmessages,
       unsigned int __vlen, int __flags,
       const struct timespec *__tmo);





struct osockaddr
  {
    unsigned short int sa_family;
    unsigned char sa_data[14];
  };



enum
{
  SHUT_RD = 0,

  SHUT_WR,

  SHUT_RDWR

};

extern int socket (int __domain, int __type, int __protocol) __attribute__ ((__nothrow__));





extern int socketpair (int __domain, int __type, int __protocol,
         int __fds[2]) __attribute__ ((__nothrow__));


extern int bind (int __fd, const struct sockaddr * __addr, socklen_t __len)
     __attribute__ ((__nothrow__));


extern int getsockname (int __fd, struct sockaddr *__restrict __addr,
   socklen_t *__restrict __len) __attribute__ ((__nothrow__));

extern int connect (int __fd, const struct sockaddr * __addr, socklen_t __len);



extern int getpeername (int __fd, struct sockaddr *__restrict __addr,
   socklen_t *__restrict __len) __attribute__ ((__nothrow__));






extern ssize_t send (int __fd, const void *__buf, size_t __n, int __flags);






extern ssize_t recv (int __fd, void *__buf, size_t __n, int __flags);






extern ssize_t sendto (int __fd, const void *__buf, size_t __n,
         int __flags, const struct sockaddr * __addr,
         socklen_t __addr_len);

extern ssize_t recvfrom (int __fd, void *__restrict __buf, size_t __n,
    int __flags, struct sockaddr *__restrict __addr,
    socklen_t *__restrict __addr_len);







extern ssize_t sendmsg (int __fd, const struct msghdr *__message,
   int __flags);






extern ssize_t recvmsg (int __fd, struct msghdr *__message, int __flags);





extern int getsockopt (int __fd, int __level, int __optname,
         void *__restrict __optval,
         socklen_t *__restrict __optlen) __attribute__ ((__nothrow__));




extern int setsockopt (int __fd, int __level, int __optname,
         const void *__optval, socklen_t __optlen) __attribute__ ((__nothrow__));





extern int listen (int __fd, int __n) __attribute__ ((__nothrow__));

extern int accept (int __fd, struct sockaddr *__restrict __addr,
     socklen_t *__restrict __addr_len);

extern int shutdown (int __fd, int __how) __attribute__ ((__nothrow__));




extern int sockatmark (int __fd) __attribute__ ((__nothrow__));





extern int isfdtype (int __fd, int __fdtype) __attribute__ ((__nothrow__));








enum
  {
    IPPROTO_IP = 0,

    IPPROTO_HOPOPTS = 0,

    IPPROTO_ICMP = 1,

    IPPROTO_IGMP = 2,

    IPPROTO_IPIP = 4,

    IPPROTO_TCP = 6,

    IPPROTO_EGP = 8,

    IPPROTO_PUP = 12,

    IPPROTO_UDP = 17,

    IPPROTO_IDP = 22,

    IPPROTO_TP = 29,

    IPPROTO_DCCP = 33,

    IPPROTO_IPV6 = 41,

    IPPROTO_ROUTING = 43,

    IPPROTO_FRAGMENT = 44,

    IPPROTO_RSVP = 46,

    IPPROTO_GRE = 47,

    IPPROTO_ESP = 50,

    IPPROTO_AH = 51,

    IPPROTO_ICMPV6 = 58,

    IPPROTO_NONE = 59,

    IPPROTO_DSTOPTS = 60,

    IPPROTO_MTP = 92,

    IPPROTO_ENCAP = 98,

    IPPROTO_PIM = 103,

    IPPROTO_COMP = 108,

    IPPROTO_SCTP = 132,

    IPPROTO_UDPLITE = 136,

    IPPROTO_RAW = 255,

    IPPROTO_MAX
  };



typedef uint16_t in_port_t;


enum
  {
    IPPORT_ECHO = 7,
    IPPORT_DISCARD = 9,
    IPPORT_SYSTAT = 11,
    IPPORT_DAYTIME = 13,
    IPPORT_NETSTAT = 15,
    IPPORT_FTP = 21,
    IPPORT_TELNET = 23,
    IPPORT_SMTP = 25,
    IPPORT_TIMESERVER = 37,
    IPPORT_NAMESERVER = 42,
    IPPORT_WHOIS = 43,
    IPPORT_MTP = 57,

    IPPORT_TFTP = 69,
    IPPORT_RJE = 77,
    IPPORT_FINGER = 79,
    IPPORT_TTYLINK = 87,
    IPPORT_SUPDUP = 95,


    IPPORT_EXECSERVER = 512,
    IPPORT_LOGINSERVER = 513,
    IPPORT_CMDSERVER = 514,
    IPPORT_EFSSERVER = 520,


    IPPORT_BIFFUDP = 512,
    IPPORT_WHOSERVER = 513,
    IPPORT_ROUTESERVER = 520,


    IPPORT_RESERVED = 1024,


    IPPORT_USERRESERVED = 5000
  };



typedef uint32_t in_addr_t;
struct in_addr
  {
    in_addr_t s_addr;
  };

struct in6_addr
  {
    union
      {
 uint8_t __u6_addr8[16];

 uint16_t __u6_addr16[8];
 uint32_t __u6_addr32[4];

      } __in6_u;





  };

extern const struct in6_addr in6addr_any;
extern const struct in6_addr in6addr_loopback;

struct sockaddr_in
  {
    sa_family_t sin_family;
    in_port_t sin_port;
    struct in_addr sin_addr;


    unsigned char sin_zero[sizeof (struct sockaddr) -
      (sizeof (unsigned short int)) -
      sizeof (in_port_t) -
      sizeof (struct in_addr)];
  };


struct sockaddr_in6
  {
    sa_family_t sin6_family;
    in_port_t sin6_port;
    uint32_t sin6_flowinfo;
    struct in6_addr sin6_addr;
    uint32_t sin6_scope_id;
  };




struct ip_mreq
  {

    struct in_addr imr_multiaddr;


    struct in_addr imr_interface;
  };

struct ip_mreq_source
  {

    struct in_addr imr_multiaddr;


    struct in_addr imr_interface;


    struct in_addr imr_sourceaddr;
  };


struct ipv6_mreq
  {

    struct in6_addr ipv6mr_multiaddr;


    unsigned int ipv6mr_interface;
  };




struct group_req
  {

    uint32_t gr_interface;


    struct sockaddr_storage gr_group;
  };

struct group_source_req
  {

    uint32_t gsr_interface;


    struct sockaddr_storage gsr_group;


    struct sockaddr_storage gsr_source;
  };



struct ip_msfilter
  {

    struct in_addr imsf_multiaddr;


    struct in_addr imsf_interface;


    uint32_t imsf_fmode;


    uint32_t imsf_numsrc;

    struct in_addr imsf_slist[1];
  };





struct group_filter
  {

    uint32_t gf_interface;


    struct sockaddr_storage gf_group;


    uint32_t gf_fmode;


    uint32_t gf_numsrc;

    struct sockaddr_storage gf_slist[1];
};


struct ip_opts
  {
    struct in_addr ip_dst;
    char ip_opts[40];
  };


struct ip_mreqn
  {
    struct in_addr imr_multiaddr;
    struct in_addr imr_address;
    int imr_ifindex;
  };


struct in_pktinfo
  {
    int ipi_ifindex;
    struct in_addr ipi_spec_dst;
    struct in_addr ipi_addr;
  };


extern uint32_t ntohl (uint32_t __netlong) __attribute__ ((__nothrow__)) __attribute__ ((__const__));
extern uint16_t ntohs (uint16_t __netshort)
     __attribute__ ((__nothrow__)) __attribute__ ((__const__));
extern uint32_t htonl (uint32_t __hostlong)
     __attribute__ ((__nothrow__)) __attribute__ ((__const__));
extern uint16_t htons (uint16_t __hostshort)
     __attribute__ ((__nothrow__)) __attribute__ ((__const__));





extern int bindresvport (int __sockfd, struct sockaddr_in *__sock_in) __attribute__ ((__nothrow__));


extern int bindresvport6 (int __sockfd, struct sockaddr_in6 *__sock_in)
     __attribute__ ((__nothrow__));











struct rpcent
{
  char *r_name;
  char **r_aliases;
  int r_number;
};

extern void setrpcent (int __stayopen) __attribute__ ((__nothrow__));
extern void endrpcent (void) __attribute__ ((__nothrow__));
extern struct rpcent *getrpcbyname (const char *__name) __attribute__ ((__nothrow__));
extern struct rpcent *getrpcbynumber (int __number) __attribute__ ((__nothrow__));
extern struct rpcent *getrpcent (void) __attribute__ ((__nothrow__));


extern int getrpcbyname_r (const char *__name, struct rpcent *__result_buf,
      char *__buffer, size_t __buflen,
      struct rpcent **__result) __attribute__ ((__nothrow__));

extern int getrpcbynumber_r (int __number, struct rpcent *__result_buf,
        char *__buffer, size_t __buflen,
        struct rpcent **__result) __attribute__ ((__nothrow__));

extern int getrpcent_r (struct rpcent *__result_buf, char *__buffer,
   size_t __buflen, struct rpcent **__result) __attribute__ ((__nothrow__));



struct netent
{
  char *n_name;
  char **n_aliases;
  int n_addrtype;
  uint32_t n_net;
};


extern int *__h_errno_location (void) __attribute__ ((__nothrow__)) __attribute__ ((__const__));

extern void herror (const char *__str) __attribute__ ((__nothrow__));


extern const char *hstrerror (int __err_num) __attribute__ ((__nothrow__));


struct hostent
{
  char *h_name;
  char **h_aliases;
  int h_addrtype;
  int h_length;
  char **h_addr_list;



};






extern void sethostent (int __stay_open);





extern void endhostent (void);






extern struct hostent *gethostent (void);






extern struct hostent *gethostbyaddr (const void *__addr, __socklen_t __len,
          int __type);





extern struct hostent *gethostbyname (const char *__name);

extern struct hostent *gethostbyname2 (const char *__name, int __af);

extern int gethostent_r (struct hostent *__restrict __result_buf,
    char *__restrict __buf, size_t __buflen,
    struct hostent **__restrict __result,
    int *__restrict __h_errnop);

extern int gethostbyaddr_r (const void *__restrict __addr, __socklen_t __len,
       int __type,
       struct hostent *__restrict __result_buf,
       char *__restrict __buf, size_t __buflen,
       struct hostent **__restrict __result,
       int *__restrict __h_errnop);

extern int gethostbyname_r (const char *__restrict __name,
       struct hostent *__restrict __result_buf,
       char *__restrict __buf, size_t __buflen,
       struct hostent **__restrict __result,
       int *__restrict __h_errnop);

extern int gethostbyname2_r (const char *__restrict __name, int __af,
        struct hostent *__restrict __result_buf,
        char *__restrict __buf, size_t __buflen,
        struct hostent **__restrict __result,
        int *__restrict __h_errnop);






extern void setnetent (int __stay_open);





extern void endnetent (void);






extern struct netent *getnetent (void);






extern struct netent *getnetbyaddr (uint32_t __net, int __type);





extern struct netent *getnetbyname (const char *__name);

extern int getnetent_r (struct netent *__restrict __result_buf,
   char *__restrict __buf, size_t __buflen,
   struct netent **__restrict __result,
   int *__restrict __h_errnop);

extern int getnetbyaddr_r (uint32_t __net, int __type,
      struct netent *__restrict __result_buf,
      char *__restrict __buf, size_t __buflen,
      struct netent **__restrict __result,
      int *__restrict __h_errnop);

extern int getnetbyname_r (const char *__restrict __name,
      struct netent *__restrict __result_buf,
      char *__restrict __buf, size_t __buflen,
      struct netent **__restrict __result,
      int *__restrict __h_errnop);


struct servent
{
  char *s_name;
  char **s_aliases;
  int s_port;
  char *s_proto;
};






extern void setservent (int __stay_open);





extern void endservent (void);






extern struct servent *getservent (void);






extern struct servent *getservbyname (const char *__name,
          const char *__proto);






extern struct servent *getservbyport (int __port, const char *__proto);

extern int getservent_r (struct servent *__restrict __result_buf,
    char *__restrict __buf, size_t __buflen,
    struct servent **__restrict __result);

extern int getservbyname_r (const char *__restrict __name,
       const char *__restrict __proto,
       struct servent *__restrict __result_buf,
       char *__restrict __buf, size_t __buflen,
       struct servent **__restrict __result);

extern int getservbyport_r (int __port, const char *__restrict __proto,
       struct servent *__restrict __result_buf,
       char *__restrict __buf, size_t __buflen,
       struct servent **__restrict __result);


struct protoent
{
  char *p_name;
  char **p_aliases;
  int p_proto;
};






extern void setprotoent (int __stay_open);





extern void endprotoent (void);






extern struct protoent *getprotoent (void);





extern struct protoent *getprotobyname (const char *__name);





extern struct protoent *getprotobynumber (int __proto);

extern int getprotoent_r (struct protoent *__restrict __result_buf,
     char *__restrict __buf, size_t __buflen,
     struct protoent **__restrict __result);

extern int getprotobyname_r (const char *__restrict __name,
        struct protoent *__restrict __result_buf,
        char *__restrict __buf, size_t __buflen,
        struct protoent **__restrict __result);

extern int getprotobynumber_r (int __proto,
          struct protoent *__restrict __result_buf,
          char *__restrict __buf, size_t __buflen,
          struct protoent **__restrict __result);

extern int setnetgrent (const char *__netgroup);







extern void endnetgrent (void);

extern int getnetgrent (char **__restrict __hostp,
   char **__restrict __userp,
   char **__restrict __domainp);

extern int innetgr (const char *__netgroup, const char *__host,
      const char *__user, const char *__domain);







extern int getnetgrent_r (char **__restrict __hostp,
     char **__restrict __userp,
     char **__restrict __domainp,
     char *__restrict __buffer, size_t __buflen);

extern int rcmd (char **__restrict __ahost, unsigned short int __rport,
   const char *__restrict __locuser,
   const char *__restrict __remuser,
   const char *__restrict __cmd, int *__restrict __fd2p);

extern int rcmd_af (char **__restrict __ahost, unsigned short int __rport,
      const char *__restrict __locuser,
      const char *__restrict __remuser,
      const char *__restrict __cmd, int *__restrict __fd2p,
      sa_family_t __af);

extern int rexec (char **__restrict __ahost, int __rport,
    const char *__restrict __name,
    const char *__restrict __pass,
    const char *__restrict __cmd, int *__restrict __fd2p);

extern int rexec_af (char **__restrict __ahost, int __rport,
       const char *__restrict __name,
       const char *__restrict __pass,
       const char *__restrict __cmd, int *__restrict __fd2p,
       sa_family_t __af);

extern int ruserok (const char *__rhost, int __suser,
      const char *__remuser, const char *__locuser);

extern int ruserok_af (const char *__rhost, int __suser,
         const char *__remuser, const char *__locuser,
         sa_family_t __af);

extern int iruserok (uint32_t __raddr, int __suser,
       const char *__remuser, const char *__locuser);

extern int iruserok_af (const void *__raddr, int __suser,
   const char *__remuser, const char *__locuser,
   sa_family_t __af);

extern int rresvport (int *__alport);

extern int rresvport_af (int *__alport, sa_family_t __af);




struct addrinfo
{
  int ai_flags;
  int ai_family;
  int ai_socktype;
  int ai_protocol;
  socklen_t ai_addrlen;
  struct sockaddr *ai_addr;
  char *ai_canonname;
  struct addrinfo *ai_next;
};

extern int getaddrinfo (const char *__restrict __name,
   const char *__restrict __service,
   const struct addrinfo *__restrict __req,
   struct addrinfo **__restrict __pai);


extern void freeaddrinfo (struct addrinfo *__ai) __attribute__ ((__nothrow__));


extern const char *gai_strerror (int __ecode) __attribute__ ((__nothrow__));





extern int getnameinfo (const struct sockaddr *__restrict __sa,
   socklen_t __salen, char *__restrict __host,
   socklen_t __hostlen, char *__restrict __serv,
   socklen_t __servlen, unsigned int __flags);










typedef long int __jmp_buf[8];






struct __jmp_buf_tag
  {




    __jmp_buf __jmpbuf;
    int __mask_was_saved;
    __sigset_t __saved_mask;
  };




typedef struct __jmp_buf_tag jmp_buf[1];



extern int setjmp (jmp_buf __env) __attribute__ ((__nothrow__));






extern int __sigsetjmp (struct __jmp_buf_tag __env[1], int __savemask) __attribute__ ((__nothrow__));




extern int _setjmp (struct __jmp_buf_tag __env[1]) __attribute__ ((__nothrow__));

extern void longjmp (struct __jmp_buf_tag __env[1], int __val)
     __attribute__ ((__nothrow__)) __attribute__ ((__noreturn__));







extern void _longjmp (struct __jmp_buf_tag __env[1], int __val)
     __attribute__ ((__nothrow__)) __attribute__ ((__noreturn__));





typedef struct __jmp_buf_tag sigjmp_buf[1];

extern void siglongjmp (sigjmp_buf __env, int __val)
     __attribute__ ((__nothrow__)) __attribute__ ((__noreturn__));






extern int __sigismember (const __sigset_t *, int);
extern int __sigaddset (__sigset_t *, int);
extern int __sigdelset (__sigset_t *, int);








typedef __sig_atomic_t sig_atomic_t;














typedef union sigval
  {
    int sival_int;
    void *sival_ptr;
  } sigval_t;

typedef struct siginfo
  {
    int si_signo;
    int si_errno;

    int si_code;

    union
      {
 int _pad[((128 / sizeof (int)) - 4)];


 struct
   {
     __pid_t si_pid;
     __uid_t si_uid;
   } _kill;


 struct
   {
     int si_tid;
     int si_overrun;
     sigval_t si_sigval;
   } _timer;


 struct
   {
     __pid_t si_pid;
     __uid_t si_uid;
     sigval_t si_sigval;
   } _rt;


 struct
   {
     __pid_t si_pid;
     __uid_t si_uid;
     int si_status;
     __clock_t si_utime;
     __clock_t si_stime;
   } _sigchld;


 struct
   {
     void *si_addr;
   } _sigfault;


 struct
   {
     long int si_band;
     int si_fd;
   } _sigpoll;
      } _sifields;
  } siginfo_t;

enum
{
  SI_ASYNCNL = -60,

  SI_TKILL = -6,

  SI_SIGIO,

  SI_ASYNCIO,

  SI_MESGQ,

  SI_TIMER,

  SI_QUEUE,

  SI_USER,

  SI_KERNEL = 0x80

};



enum
{
  ILL_ILLOPC = 1,

  ILL_ILLOPN,

  ILL_ILLADR,

  ILL_ILLTRP,

  ILL_PRVOPC,

  ILL_PRVREG,

  ILL_COPROC,

  ILL_BADSTK

};


enum
{
  FPE_INTDIV = 1,

  FPE_INTOVF,

  FPE_FLTDIV,

  FPE_FLTOVF,

  FPE_FLTUND,

  FPE_FLTRES,

  FPE_FLTINV,

  FPE_FLTSUB

};


enum
{
  SEGV_MAPERR = 1,

  SEGV_ACCERR

};


enum
{
  BUS_ADRALN = 1,

  BUS_ADRERR,

  BUS_OBJERR

};


enum
{
  TRAP_BRKPT = 1,

  TRAP_TRACE

};


enum
{
  CLD_EXITED = 1,

  CLD_KILLED,

  CLD_DUMPED,

  CLD_TRAPPED,

  CLD_STOPPED,

  CLD_CONTINUED

};


enum
{
  POLL_IN = 1,

  POLL_OUT,

  POLL_MSG,

  POLL_ERR,

  POLL_PRI,

  POLL_HUP

};

typedef struct sigevent
  {
    sigval_t sigev_value;
    int sigev_signo;
    int sigev_notify;

    union
      {
 int _pad[((64 / sizeof (int)) - 4)];



 __pid_t _tid;

 struct
   {
     void (*_function) (sigval_t);
     void *_attribute;
   } _sigev_thread;
      } _sigev_un;
  } sigevent_t;






enum
{
  SIGEV_SIGNAL = 0,

  SIGEV_NONE,

  SIGEV_THREAD,


  SIGEV_THREAD_ID = 4

};



typedef void (*__sighandler_t) (int);




extern __sighandler_t __sysv_signal (int __sig, __sighandler_t __handler)
     __attribute__ ((__nothrow__));

extern __sighandler_t signal (int __sig, __sighandler_t __handler)
     __attribute__ ((__nothrow__));

extern int kill (__pid_t __pid, int __sig) __attribute__ ((__nothrow__));





extern int killpg (__pid_t __pgrp, int __sig) __attribute__ ((__nothrow__));



extern int raise (int __sig) __attribute__ ((__nothrow__));




extern __sighandler_t ssignal (int __sig, __sighandler_t __handler)
     __attribute__ ((__nothrow__));
extern int gsignal (int __sig) __attribute__ ((__nothrow__));



extern void psignal (int __sig, const char *__s);



extern void psiginfo (const siginfo_t *__pinfo, const char *__s);

extern int __sigpause (int __sig_or_mask, int __is_sig);

extern int sigblock (int __mask) __attribute__ ((__nothrow__)) __attribute__ ((__deprecated__));


extern int sigsetmask (int __mask) __attribute__ ((__nothrow__)) __attribute__ ((__deprecated__));


extern int siggetmask (void) __attribute__ ((__nothrow__)) __attribute__ ((__deprecated__));

typedef __sighandler_t sig_t;



extern int sigemptyset (sigset_t *__set) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));


extern int sigfillset (sigset_t *__set) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));


extern int sigaddset (sigset_t *__set, int __signo) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));


extern int sigdelset (sigset_t *__set, int __signo) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));


extern int sigismember (const sigset_t *__set, int __signo)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));


struct sigaction
  {


    union
      {

 __sighandler_t sa_handler;

 void (*sa_sigaction) (int, siginfo_t *, void *);
      }
    __sigaction_handler;





    __sigset_t sa_mask;


    int sa_flags;


    void (*sa_restorer) (void);
  };



extern int sigprocmask (int __how, const sigset_t *__restrict __set,
   sigset_t *__restrict __oset) __attribute__ ((__nothrow__));






extern int sigsuspend (const sigset_t *__set) __attribute__ ((__nonnull__ (1)));


extern int sigaction (int __sig, const struct sigaction *__restrict __act,
        struct sigaction *__restrict __oact) __attribute__ ((__nothrow__));


extern int sigpending (sigset_t *__set) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));






extern int sigwait (const sigset_t *__restrict __set, int *__restrict __sig)
     __attribute__ ((__nonnull__ (1, 2)));






extern int sigwaitinfo (const sigset_t *__restrict __set,
   siginfo_t *__restrict __info) __attribute__ ((__nonnull__ (1)));






extern int sigtimedwait (const sigset_t *__restrict __set,
    siginfo_t *__restrict __info,
    const struct timespec *__restrict __timeout)
     __attribute__ ((__nonnull__ (1)));



extern int sigqueue (__pid_t __pid, int __sig, const union sigval __val)
     __attribute__ ((__nothrow__));





extern const char *const _sys_siglist[65];
extern const char *const sys_siglist[65];


struct sigvec
  {
    __sighandler_t sv_handler;
    int sv_mask;

    int sv_flags;

  };

extern int sigvec (int __sig, const struct sigvec *__vec,
     struct sigvec *__ovec) __attribute__ ((__nothrow__));




struct _fpreg
{
  unsigned short significand[4];
  unsigned short exponent;
};

struct _fpxreg
{
  unsigned short significand[4];
  unsigned short exponent;
  unsigned short padding[3];
};

struct _xmmreg
{
  __uint32_t element[4];
};

 struct _fpstate
{

  __uint16_t cwd;
  __uint16_t swd;
  __uint16_t ftw;
  __uint16_t fop;
  __uint64_t rip;
  __uint64_t rdp;
  __uint32_t mxcsr;
  __uint32_t mxcr_mask;
  struct _fpxreg _st[8];
  struct _xmmreg _xmm[16];
  __uint32_t padding[24];
};

struct sigcontext
{
  unsigned long r8;
  unsigned long r9;
  unsigned long r10;
  unsigned long r11;
  unsigned long r12;
  unsigned long r13;
  unsigned long r14;
  unsigned long r15;
  unsigned long rdi;
  unsigned long rsi;
  unsigned long rbp;
  unsigned long rbx;
  unsigned long rdx;
  unsigned long rax;
  unsigned long rcx;
  unsigned long rsp;
  unsigned long rip;
  unsigned long eflags;
  unsigned short cs;
  unsigned short gs;
  unsigned short fs;
  unsigned short __pad0;
  unsigned long err;
  unsigned long trapno;
  unsigned long oldmask;
  unsigned long cr2;
  struct _fpstate * fpstate;
  unsigned long __reserved1 [8];
};



extern int sigreturn (struct sigcontext *__scp) __attribute__ ((__nothrow__));






extern int siginterrupt (int __sig, int __interrupt) __attribute__ ((__nothrow__));


struct sigstack
  {
    void *ss_sp;
    int ss_onstack;
  };



enum
{
  SS_ONSTACK = 1,

  SS_DISABLE

};

typedef struct sigaltstack
  {
    void *ss_sp;
    int ss_flags;
    size_t ss_size;
  } stack_t;













typedef long int greg_t;





typedef greg_t gregset_t[23];

struct _libc_fpxreg
{
  unsigned short int significand[4];
  unsigned short int exponent;
  unsigned short int padding[3];
};

struct _libc_xmmreg
{
  __uint32_t element[4];
};

struct _libc_fpstate
{

  __uint16_t cwd;
  __uint16_t swd;
  __uint16_t ftw;
  __uint16_t fop;
  __uint64_t rip;
  __uint64_t rdp;
  __uint32_t mxcsr;
  __uint32_t mxcr_mask;
  struct _libc_fpxreg _st[8];
  struct _libc_xmmreg _xmm[16];
  __uint32_t padding[24];
};


typedef struct _libc_fpstate *fpregset_t;


typedef struct
  {
    gregset_t gregs;

    fpregset_t fpregs;
    unsigned long __reserved1 [8];
} mcontext_t;


typedef struct ucontext
  {
    unsigned long int uc_flags;
    struct ucontext *uc_link;
    stack_t uc_stack;
    mcontext_t uc_mcontext;
    __sigset_t uc_sigmask;
    struct _libc_fpstate __fpregs_mem;
  } ucontext_t;





extern int sigstack (struct sigstack *__ss, struct sigstack *__oss)
     __attribute__ ((__nothrow__)) __attribute__ ((__deprecated__));



extern int sigaltstack (const struct sigaltstack *__restrict __ss,
   struct sigaltstack *__restrict __oss) __attribute__ ((__nothrow__));




extern int pthread_sigmask (int __how,
       const __sigset_t *__restrict __newmask,
       __sigset_t *__restrict __oldmask)__attribute__ ((__nothrow__));


extern int pthread_kill (pthread_t __threadid, int __signo) __attribute__ ((__nothrow__));






extern int __libc_current_sigrtmin (void) __attribute__ ((__nothrow__));

extern int __libc_current_sigrtmax (void) __attribute__ ((__nothrow__));















struct _IO_FILE;



typedef struct _IO_FILE FILE;

typedef struct _IO_FILE __FILE;








typedef struct
{
  int __count;
  union
  {

    unsigned int __wch;


    char __wchb[4];
  } __value;
} __mbstate_t;


typedef struct
{
  __off_t __pos;
  __mbstate_t __state;
} _G_fpos_t;
typedef struct
{
  __off64_t __pos;
  __mbstate_t __state;
} _G_fpos64_t;

typedef int _G_int16_t __attribute__ ((__mode__ (__HI__)));
typedef int _G_int32_t __attribute__ ((__mode__ (__SI__)));
typedef unsigned int _G_uint16_t __attribute__ ((__mode__ (__HI__)));
typedef unsigned int _G_uint32_t __attribute__ ((__mode__ (__SI__)));



typedef __builtin_va_list __gnuc_va_list;


struct _IO_jump_t; struct _IO_FILE;







 typedef void _IO_lock_t;



struct _IO_marker {
  struct _IO_marker *_next;
  struct _IO_FILE *_sbuf;



  int _pos;

};


enum __codecvt_result
{
  __codecvt_ok,
  __codecvt_partial,
  __codecvt_error,
  __codecvt_noconv
};

struct _IO_FILE {
  int _flags;




  char* _IO_read_ptr;
  char* _IO_read_end;
  char* _IO_read_base;
  char* _IO_write_base;
  char* _IO_write_ptr;
  char* _IO_write_end;
  char* _IO_buf_base;
  char* _IO_buf_end;

  char *_IO_save_base;
  char *_IO_backup_base;
  char *_IO_save_end;

  struct _IO_marker *_markers;

  struct _IO_FILE *_chain;

  int _fileno;


   int _flags2;

  __off_t _old_offset;



  unsigned short _cur_column;
  signed char _vtable_offset;
  char _shortbuf[1];



  _IO_lock_t *_lock;

  __off64_t _offset;







   void *__pad1;
  void *__pad2;
  void *__pad3;
  void *__pad4;
  size_t __pad5;

  int _mode;

  char _unused2[15 * sizeof (int) - 4 * sizeof (void *) - sizeof (size_t)];

};


typedef struct _IO_FILE _IO_FILE;

struct _IO_FILE_plus;

extern struct _IO_FILE_plus _IO_2_1_stdin_;
extern struct _IO_FILE_plus _IO_2_1_stdout_;
extern struct _IO_FILE_plus _IO_2_1_stderr_;

typedef __ssize_t __io_read_fn (void *__cookie, char *__buf, size_t __nbytes);







typedef __ssize_t __io_write_fn (void *__cookie, const char *__buf,
     size_t __n);







typedef int __io_seek_fn (void *__cookie, __off64_t *__pos, int __w);


typedef int __io_close_fn (void *__cookie);

extern int __underflow (_IO_FILE *);
extern int __uflow (_IO_FILE *);
extern int __overflow (_IO_FILE *, int);

extern int _IO_getc (_IO_FILE *__fp);
extern int _IO_putc (int __c, _IO_FILE *__fp);
extern int _IO_feof (_IO_FILE *__fp) __attribute__ ((__nothrow__));
extern int _IO_ferror (_IO_FILE *__fp) __attribute__ ((__nothrow__));

extern int _IO_peekc_locked (_IO_FILE *__fp);





extern void _IO_flockfile (_IO_FILE *) __attribute__ ((__nothrow__));
extern void _IO_funlockfile (_IO_FILE *) __attribute__ ((__nothrow__));
extern int _IO_ftrylockfile (_IO_FILE *) __attribute__ ((__nothrow__));

extern int _IO_vfscanf (_IO_FILE * __restrict, const char * __restrict,
   __gnuc_va_list, int *__restrict);
extern int _IO_vfprintf (_IO_FILE *__restrict, const char *__restrict,
    __gnuc_va_list);
extern __ssize_t _IO_padn (_IO_FILE *, int, __ssize_t);
extern size_t _IO_sgetn (_IO_FILE *, void *, size_t);

extern __off64_t _IO_seekoff (_IO_FILE *, __off64_t, int, int);
extern __off64_t _IO_seekpos (_IO_FILE *, __off64_t, int);

extern void _IO_free_backup_area (_IO_FILE *) __attribute__ ((__nothrow__));





typedef __gnuc_va_list va_list;

typedef _G_fpos_t fpos_t;





extern struct _IO_FILE *stdin;
extern struct _IO_FILE *stdout;
extern struct _IO_FILE *stderr;

extern int remove (const char *__filename) __attribute__ ((__nothrow__));

extern int rename (const char *__old, const char *__new) __attribute__ ((__nothrow__));




extern int renameat (int __oldfd, const char *__old, int __newfd,
       const char *__new) __attribute__ ((__nothrow__));







extern FILE *tmpfile (void) ;

extern char *tmpnam (char *__s) __attribute__ ((__nothrow__)) ;





extern char *tmpnam_r (char *__s) __attribute__ ((__nothrow__)) ;

extern char *tempnam (const char *__dir, const char *__pfx)
     __attribute__ ((__nothrow__)) __attribute__ ((__malloc__)) ;






extern int fclose (FILE *__stream);




extern int fflush (FILE *__stream);

extern int fflush_unlocked (FILE *__stream);

extern FILE *fopen (const char *__restrict __filename,
      const char *__restrict __modes) ;




extern FILE *freopen (const char *__restrict __filename,
        const char *__restrict __modes,
        FILE *__restrict __stream) ;

extern FILE *fdopen (int __fd, const char *__modes) __attribute__ ((__nothrow__)) ;

extern FILE *fmemopen (void *__s, size_t __len, const char *__modes)
  __attribute__ ((__nothrow__)) ;




extern FILE *open_memstream (char **__bufloc, size_t *__sizeloc) __attribute__ ((__nothrow__)) ;




extern void setbuf (FILE *__restrict __stream, char *__restrict __buf) __attribute__ ((__nothrow__));



extern int setvbuf (FILE *__restrict __stream, char *__restrict __buf,
      int __modes, size_t __n) __attribute__ ((__nothrow__));





extern void setbuffer (FILE *__restrict __stream, char *__restrict __buf,
         size_t __size) __attribute__ ((__nothrow__));


extern void setlinebuf (FILE *__stream) __attribute__ ((__nothrow__));






extern int fprintf (FILE *__restrict __stream,
      const char *__restrict __format, ...);




extern int printf (const char *__restrict __format, ...);

extern int sprintf (char *__restrict __s,
      const char *__restrict __format, ...) __attribute__ ((__nothrow__));





extern int vfprintf (FILE *__restrict __s, const char *__restrict __format,
       __gnuc_va_list __arg);




extern int vprintf (const char *__restrict __format, __gnuc_va_list __arg);

extern int vsprintf (char *__restrict __s, const char *__restrict __format,
       __gnuc_va_list __arg) __attribute__ ((__nothrow__));





extern int snprintf (char *__restrict __s, size_t __maxlen,
       const char *__restrict __format, ...)
     __attribute__ ((__nothrow__)) __attribute__ ((__format__ (__printf__, 3, 4)));

extern int vsnprintf (char *__restrict __s, size_t __maxlen,
        const char *__restrict __format, __gnuc_va_list __arg)
     __attribute__ ((__nothrow__)) __attribute__ ((__format__ (__printf__, 3, 0)));

extern int vdprintf (int __fd, const char *__restrict __fmt,
       __gnuc_va_list __arg)
     __attribute__ ((__format__ (__printf__, 2, 0)));
extern int dprintf (int __fd, const char *__restrict __fmt, ...)
     __attribute__ ((__format__ (__printf__, 2, 3)));






extern int fscanf (FILE *__restrict __stream,
     const char *__restrict __format, ...) ;




extern int scanf (const char *__restrict __format, ...) ;

extern int sscanf (const char *__restrict __s,
     const char *__restrict __format, ...) __attribute__ ((__nothrow__));






extern int fscanf (FILE *__restrict __stream,
 const char *__restrict __format, ...) __asm__ ("" "__isoc99_fscanf") ;
extern int scanf (const char *__restrict __format, ...) __asm__ ("" "__isoc99_scanf") ;
extern int sscanf (const char *__restrict __s,
 const char *__restrict __format, ...) __asm__ ("" "__isoc99_sscanf") __attribute__ ((__nothrow__));

extern int vfscanf (FILE *__restrict __s, const char *__restrict __format,
      __gnuc_va_list __arg)
     __attribute__ ((__format__ (__scanf__, 2, 0))) ;





extern int vscanf (const char *__restrict __format, __gnuc_va_list __arg)
     __attribute__ ((__format__ (__scanf__, 1, 0))) ;


extern int vsscanf (const char *__restrict __s,
      const char *__restrict __format, __gnuc_va_list __arg)
     __attribute__ ((__nothrow__)) __attribute__ ((__format__ (__scanf__, 2, 0)));






extern int vfscanf
(FILE *__restrict __s,
 const char *__restrict __format, __gnuc_va_list __arg) __asm__ ("" "__isoc99_vfscanf")
     __attribute__ ((__format__ (__scanf__, 2, 0))) ;
extern int vscanf (const char *__restrict __format,
 __gnuc_va_list __arg) __asm__ ("" "__isoc99_vscanf")
     __attribute__ ((__format__ (__scanf__, 1, 0))) ;
extern int vsscanf
(const char *__restrict __s,
 const char *__restrict __format,
 __gnuc_va_list __arg) __asm__ ("" "__isoc99_vsscanf") __attribute__ ((__nothrow__))
     __attribute__ ((__format__ (__scanf__, 2, 0)));

extern int fgetc (FILE *__stream);
extern int getc (FILE *__stream);





extern int getchar (void);

extern int getc_unlocked (FILE *__stream);
extern int getchar_unlocked (void);

extern int fgetc_unlocked (FILE *__stream);

extern int fputc (int __c, FILE *__stream);
extern int putc (int __c, FILE *__stream);





extern int putchar (int __c);

extern int fputc_unlocked (int __c, FILE *__stream);






extern int putc_unlocked (int __c, FILE *__stream);
extern int putchar_unlocked (int __c);



extern int getw (FILE *__stream);


extern int putw (int __w, FILE *__stream);






extern char *fgets (char *__restrict __s, int __n, FILE *__restrict __stream)
     ;






extern char *gets (char *__s) ;

extern __ssize_t __getdelim (char **__restrict __lineptr,
          size_t *__restrict __n, int __delimiter,
          FILE *__restrict __stream) ;
extern __ssize_t getdelim (char **__restrict __lineptr,
        size_t *__restrict __n, int __delimiter,
        FILE *__restrict __stream) ;







extern __ssize_t getline (char **__restrict __lineptr,
       size_t *__restrict __n,
       FILE *__restrict __stream) ;






extern int fputs (const char *__restrict __s, FILE *__restrict __stream);





extern int puts (const char *__s);






extern int ungetc (int __c, FILE *__stream);






extern size_t fread (void *__restrict __ptr, size_t __size,
       size_t __n, FILE *__restrict __stream) ;




extern size_t fwrite (const void *__restrict __ptr, size_t __size,
        size_t __n, FILE *__restrict __s) ;

extern size_t fread_unlocked (void *__restrict __ptr, size_t __size,
         size_t __n, FILE *__restrict __stream) ;
extern size_t fwrite_unlocked (const void *__restrict __ptr, size_t __size,
          size_t __n, FILE *__restrict __stream) ;






extern int fseek (FILE *__stream, long int __off, int __whence);




extern long int ftell (FILE *__stream) ;




extern void rewind (FILE *__stream);

extern int fseeko (FILE *__stream, __off_t __off, int __whence);




extern __off_t ftello (FILE *__stream) ;

extern int fgetpos (FILE *__restrict __stream, fpos_t *__restrict __pos);




extern int fsetpos (FILE *__stream, const fpos_t *__pos);

extern void clearerr (FILE *__stream) __attribute__ ((__nothrow__));

extern int feof (FILE *__stream) __attribute__ ((__nothrow__)) ;

extern int ferror (FILE *__stream) __attribute__ ((__nothrow__)) ;




extern void clearerr_unlocked (FILE *__stream) __attribute__ ((__nothrow__));
extern int feof_unlocked (FILE *__stream) __attribute__ ((__nothrow__)) ;
extern int ferror_unlocked (FILE *__stream) __attribute__ ((__nothrow__)) ;






extern void perror (const char *__s);


extern int sys_nerr;
extern const char *const sys_errlist[];





extern int fileno (FILE *__stream) __attribute__ ((__nothrow__)) ;



extern int fileno_unlocked (FILE *__stream) __attribute__ ((__nothrow__)) ;






extern FILE *popen (const char *__command, const char *__modes) ;





extern int pclose (FILE *__stream);



extern char *ctermid (char *__s) __attribute__ ((__nothrow__));

extern void flockfile (FILE *__stream) __attribute__ ((__nothrow__));



extern int ftrylockfile (FILE *__stream) __attribute__ ((__nothrow__)) ;


extern void funlockfile (FILE *__stream) __attribute__ ((__nothrow__));






typedef int wchar_t;







union wait
  {
    int w_status;
    struct
      {

 unsigned int __w_termsig:7;
 unsigned int __w_coredump:1;
 unsigned int __w_retcode:8;
 unsigned int:16;







      } __wait_terminated;
    struct
      {

 unsigned int __w_stopval:8;
 unsigned int __w_stopsig:8;
 unsigned int:16;






      } __wait_stopped;
  };


typedef union
  {
    union wait *__uptr;
    int *__iptr;
  } __WAIT_STATUS __attribute__ ((__transparent_union__));

typedef struct
  {
    int quot;
    int rem;
  } div_t;



typedef struct
  {
    long int quot;
    long int rem;
  } ldiv_t;







__extension__ typedef struct
  {
    long long int quot;
    long long int rem;
  } lldiv_t;

extern size_t __ctype_get_mb_cur_max (void) __attribute__ ((__nothrow__)) ;




extern double atof (const char *__nptr)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1))) ;

extern int atoi (const char *__nptr)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1))) ;

extern long int atol (const char *__nptr)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1))) ;





__extension__ extern long long int atoll (const char *__nptr)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1))) ;




extern double strtod (const char *__restrict __nptr,
        char **__restrict __endptr)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;





extern float strtof (const char *__restrict __nptr,
       char **__restrict __endptr) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;

extern long double strtold (const char *__restrict __nptr,
       char **__restrict __endptr)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;




extern long int strtol (const char *__restrict __nptr,
   char **__restrict __endptr, int __base)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;

extern unsigned long int strtoul (const char *__restrict __nptr,
      char **__restrict __endptr, int __base)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;




__extension__
extern long long int strtoq (const char *__restrict __nptr,
        char **__restrict __endptr, int __base)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;

__extension__
extern unsigned long long int strtouq (const char *__restrict __nptr,
           char **__restrict __endptr, int __base)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;




__extension__
extern long long int strtoll (const char *__restrict __nptr,
         char **__restrict __endptr, int __base)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;

__extension__
extern unsigned long long int strtoull (const char *__restrict __nptr,
     char **__restrict __endptr, int __base)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;

extern char *l64a (long int __n) __attribute__ ((__nothrow__)) ;


extern long int a64l (const char *__s)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1))) ;








extern long int random (void) __attribute__ ((__nothrow__));


extern void srandom (unsigned int __seed) __attribute__ ((__nothrow__));





extern char *initstate (unsigned int __seed, char *__statebuf,
   size_t __statelen) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2)));



extern char *setstate (char *__statebuf) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));







struct random_data
  {
    int32_t *fptr;
    int32_t *rptr;
    int32_t *state;
    int rand_type;
    int rand_deg;
    int rand_sep;
    int32_t *end_ptr;
  };

extern int random_r (struct random_data *__restrict __buf,
       int32_t *__restrict __result) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));

extern int srandom_r (unsigned int __seed, struct random_data *__buf)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2)));

extern int initstate_r (unsigned int __seed, char *__restrict __statebuf,
   size_t __statelen,
   struct random_data *__restrict __buf)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2, 4)));

extern int setstate_r (char *__restrict __statebuf,
         struct random_data *__restrict __buf)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));




extern int rand (void) __attribute__ ((__nothrow__));

extern void srand (unsigned int __seed) __attribute__ ((__nothrow__));




extern int rand_r (unsigned int *__seed) __attribute__ ((__nothrow__));





extern double drand48 (void) __attribute__ ((__nothrow__));
extern double erand48 (unsigned short int __xsubi[3]) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));


extern long int lrand48 (void) __attribute__ ((__nothrow__));
extern long int nrand48 (unsigned short int __xsubi[3])
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));


extern long int mrand48 (void) __attribute__ ((__nothrow__));
extern long int jrand48 (unsigned short int __xsubi[3])
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));


extern void srand48 (long int __seedval) __attribute__ ((__nothrow__));
extern unsigned short int *seed48 (unsigned short int __seed16v[3])
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));
extern void lcong48 (unsigned short int __param[7]) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));





struct drand48_data
  {
    unsigned short int __x[3];
    unsigned short int __old_x[3];
    unsigned short int __c;
    unsigned short int __init;
    unsigned long long int __a;
  };


extern int drand48_r (struct drand48_data *__restrict __buffer,
        double *__restrict __result) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));
extern int erand48_r (unsigned short int __xsubi[3],
        struct drand48_data *__restrict __buffer,
        double *__restrict __result) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));


extern int lrand48_r (struct drand48_data *__restrict __buffer,
        long int *__restrict __result)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));
extern int nrand48_r (unsigned short int __xsubi[3],
        struct drand48_data *__restrict __buffer,
        long int *__restrict __result)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));


extern int mrand48_r (struct drand48_data *__restrict __buffer,
        long int *__restrict __result)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));
extern int jrand48_r (unsigned short int __xsubi[3],
        struct drand48_data *__restrict __buffer,
        long int *__restrict __result)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));


extern int srand48_r (long int __seedval, struct drand48_data *__buffer)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2)));

extern int seed48_r (unsigned short int __seed16v[3],
       struct drand48_data *__buffer) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));

extern int lcong48_r (unsigned short int __param[7],
        struct drand48_data *__buffer)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));







extern void *malloc (size_t __size) __attribute__ ((__nothrow__)) __attribute__ ((__malloc__)) ;

extern void *calloc (size_t __nmemb, size_t __size)
     __attribute__ ((__nothrow__)) __attribute__ ((__malloc__)) ;

extern void *realloc (void *__ptr, size_t __size)
     __attribute__ ((__nothrow__)) __attribute__ ((__warn_unused_result__));

extern void free (void *__ptr) __attribute__ ((__nothrow__));




extern void cfree (void *__ptr) __attribute__ ((__nothrow__));












extern void *alloca (size_t __size) __attribute__ ((__nothrow__));




extern void *valloc (size_t __size) __attribute__ ((__nothrow__)) __attribute__ ((__malloc__)) ;



extern int posix_memalign (void **__memptr, size_t __alignment, size_t __size)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;



extern void abort (void) __attribute__ ((__nothrow__)) __attribute__ ((__noreturn__));



extern int atexit (void (*__func) (void)) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));

extern int on_exit (void (*__func) (int __status, void *__arg), void *__arg)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));





extern void exit (int __status) __attribute__ ((__nothrow__)) __attribute__ ((__noreturn__));

extern void _Exit (int __status) __attribute__ ((__nothrow__)) __attribute__ ((__noreturn__));




extern char *getenv (const char *__name) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;




extern char *__secure_getenv (const char *__name)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;





extern int putenv (char *__string) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));




extern int setenv (const char *__name, const char *__value, int __replace)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2)));


extern int unsetenv (const char *__name) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));





extern int clearenv (void) __attribute__ ((__nothrow__));






extern char *mktemp (char *__template) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;

extern int mkstemp (char *__template) __attribute__ ((__nonnull__ (1))) ;

extern int mkstemps (char *__template, int __suffixlen) __attribute__ ((__nonnull__ (1))) ;

extern char *mkdtemp (char *__template) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;

extern int system (const char *__command) ;

extern char *realpath (const char *__restrict __name,
         char *__restrict __resolved) __attribute__ ((__nothrow__)) ;




typedef int (*__compar_fn_t) (const void *, const void *);

extern void *bsearch (const void *__key, const void *__base,
        size_t __nmemb, size_t __size, __compar_fn_t __compar)
     __attribute__ ((__nonnull__ (1, 2, 5))) ;



extern void qsort (void *__base, size_t __nmemb, size_t __size,
     __compar_fn_t __compar) __attribute__ ((__nonnull__ (1, 4)));






extern int abs (int __x) __attribute__ ((__nothrow__)) __attribute__ ((__const__)) ;
extern long int labs (long int __x) __attribute__ ((__nothrow__)) __attribute__ ((__const__)) ;



__extension__ extern long long int llabs (long long int __x)
     __attribute__ ((__nothrow__)) __attribute__ ((__const__)) ;





extern div_t div (int __numer, int __denom)
     __attribute__ ((__nothrow__)) __attribute__ ((__const__)) ;
extern ldiv_t ldiv (long int __numer, long int __denom)
     __attribute__ ((__nothrow__)) __attribute__ ((__const__)) ;




__extension__ extern lldiv_t lldiv (long long int __numer,
        long long int __denom)
     __attribute__ ((__nothrow__)) __attribute__ ((__const__)) ;

extern char *ecvt (double __value, int __ndigit, int *__restrict __decpt,
     int *__restrict __sign) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (3, 4))) ;




extern char *fcvt (double __value, int __ndigit, int *__restrict __decpt,
     int *__restrict __sign) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (3, 4))) ;




extern char *gcvt (double __value, int __ndigit, char *__buf)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (3))) ;




extern char *qecvt (long double __value, int __ndigit,
      int *__restrict __decpt, int *__restrict __sign)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (3, 4))) ;
extern char *qfcvt (long double __value, int __ndigit,
      int *__restrict __decpt, int *__restrict __sign)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (3, 4))) ;
extern char *qgcvt (long double __value, int __ndigit, char *__buf)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (3))) ;




extern int ecvt_r (double __value, int __ndigit, int *__restrict __decpt,
     int *__restrict __sign, char *__restrict __buf,
     size_t __len) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (3, 4, 5)));
extern int fcvt_r (double __value, int __ndigit, int *__restrict __decpt,
     int *__restrict __sign, char *__restrict __buf,
     size_t __len) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (3, 4, 5)));

extern int qecvt_r (long double __value, int __ndigit,
      int *__restrict __decpt, int *__restrict __sign,
      char *__restrict __buf, size_t __len)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (3, 4, 5)));
extern int qfcvt_r (long double __value, int __ndigit,
      int *__restrict __decpt, int *__restrict __sign,
      char *__restrict __buf, size_t __len)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (3, 4, 5)));





extern int mblen (const char *__s, size_t __n) __attribute__ ((__nothrow__)) ;


extern int mbtowc (wchar_t *__restrict __pwc,
     const char *__restrict __s, size_t __n) __attribute__ ((__nothrow__)) ;


extern int wctomb (char *__s, wchar_t __wchar) __attribute__ ((__nothrow__)) ;



extern size_t mbstowcs (wchar_t *__restrict __pwcs,
   const char *__restrict __s, size_t __n) __attribute__ ((__nothrow__));

extern size_t wcstombs (char *__restrict __s,
   const wchar_t *__restrict __pwcs, size_t __n)
     __attribute__ ((__nothrow__));

extern int rpmatch (const char *__response) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;

extern int getsubopt (char **__restrict __optionp,
        char *const *__restrict __tokens,
        char **__restrict __valuep)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2, 3))) ;

extern int getloadavg (double __loadavg[], int __nelem)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));





typedef long int ptrdiff_t;













extern void *memcpy (void *__restrict __dest,
       const void *__restrict __src, size_t __n)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));


extern void *memmove (void *__dest, const void *__src, size_t __n)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));






extern void *memccpy (void *__restrict __dest, const void *__restrict __src,
        int __c, size_t __n)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));



extern void *memset (void *__s, int __c, size_t __n) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));


extern int memcmp (const void *__s1, const void *__s2, size_t __n)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));

 extern void *memchr (const void *__s, int __c, size_t __n)
      __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1)));

extern char *strcpy (char *__restrict __dest, const char *__restrict __src)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));

extern char *strncpy (char *__restrict __dest,
        const char *__restrict __src, size_t __n)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));


extern char *strcat (char *__restrict __dest, const char *__restrict __src)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));

extern char *strncat (char *__restrict __dest, const char *__restrict __src,
        size_t __n) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));


extern int strcmp (const char *__s1, const char *__s2)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));

extern int strncmp (const char *__s1, const char *__s2, size_t __n)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));


extern int strcoll (const char *__s1, const char *__s2)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));

extern size_t strxfrm (char *__restrict __dest,
         const char *__restrict __src, size_t __n)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2)));




extern int strcoll_l (const char *__s1, const char *__s2, __locale_t __l)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2, 3)));

extern size_t strxfrm_l (char *__dest, const char *__src, size_t __n,
    __locale_t __l) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2, 4)));



extern char *strdup (const char *__s)
     __attribute__ ((__nothrow__)) __attribute__ ((__malloc__)) __attribute__ ((__nonnull__ (1)));





extern char *strndup (const char *__string, size_t __n)
     __attribute__ ((__nothrow__)) __attribute__ ((__malloc__)) __attribute__ ((__nonnull__ (1)));

 extern char *strchr (const char *__s, int __c)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1)));

 extern char *strrchr (const char *__s, int __c)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1)));

extern size_t strcspn (const char *__s, const char *__reject)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));


extern size_t strspn (const char *__s, const char *__accept)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));

 extern char *strpbrk (const char *__s, const char *__accept)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));

 extern char *strstr (const char *__haystack, const char *__needle)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));


extern char *strtok (char *__restrict __s, const char *__restrict __delim)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2)));




extern char *__strtok_r (char *__restrict __s,
    const char *__restrict __delim,
    char **__restrict __save_ptr)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2, 3)));

extern char *strtok_r (char *__restrict __s, const char *__restrict __delim,
         char **__restrict __save_ptr)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2, 3)));

extern size_t strlen (const char *__s)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1)));





extern size_t strnlen (const char *__string, size_t __maxlen)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1)));



extern char *strerror (int __errnum) __attribute__ ((__nothrow__));

extern int strerror_r
(int __errnum, char *__buf, size_t __buflen) __asm__ ("" "__xpg_strerror_r") __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2)));

extern char *strerror_l (int __errnum, __locale_t __l) __attribute__ ((__nothrow__));



extern void __bzero (void *__s, size_t __n) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));



extern void bcopy (const void *__src, void *__dest, size_t __n)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));


extern void bzero (void *__s, size_t __n) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));


extern int bcmp (const void *__s1, const void *__s2, size_t __n)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));

 extern char *index (const char *__s, int __c)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1)));

 extern char *rindex (const char *__s, int __c)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1)));



extern int ffs (int __i) __attribute__ ((__nothrow__)) __attribute__ ((__const__));

extern int strcasecmp (const char *__s1, const char *__s2)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));


extern int strncasecmp (const char *__s1, const char *__s2, size_t __n)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));

extern char *strsep (char **__restrict __stringp,
       const char *__restrict __delim)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));



extern char *strsignal (int __sig) __attribute__ ((__nothrow__));


extern char *__stpcpy (char *__restrict __dest, const char *__restrict __src)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));
extern char *stpcpy (char *__restrict __dest, const char *__restrict __src)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));



extern char *__stpncpy (char *__restrict __dest,
   const char *__restrict __src, size_t __n)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));
extern char *stpncpy (char *__restrict __dest,
        const char *__restrict __src, size_t __n)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));







typedef unsigned long int nfds_t;


struct pollfd
  {
    int fd;
    short int events;
    short int revents;
  };

extern int poll (struct pollfd *__fds, nfds_t __nfds, int __timeout);























struct winsize
  {
    unsigned short int ws_row;
    unsigned short int ws_col;
    unsigned short int ws_xpixel;
    unsigned short int ws_ypixel;
  };


struct termio
  {
    unsigned short int c_iflag;
    unsigned short int c_oflag;
    unsigned short int c_cflag;
    unsigned short int c_lflag;
    unsigned char c_line;
    unsigned char c_cc[8];
};







extern int ioctl (int __fd, unsigned long int __request, ...) __attribute__ ((__nothrow__));











extern void *mmap (void *__addr, size_t __len, int __prot,
     int __flags, int __fd, __off_t __offset) __attribute__ ((__nothrow__));

extern int munmap (void *__addr, size_t __len) __attribute__ ((__nothrow__));




extern int mprotect (void *__addr, size_t __len, int __prot) __attribute__ ((__nothrow__));







extern int msync (void *__addr, size_t __len, int __flags);




extern int madvise (void *__addr, size_t __len, int __advice) __attribute__ ((__nothrow__));



extern int posix_madvise (void *__addr, size_t __len, int __advice) __attribute__ ((__nothrow__));



extern int mlock (const void *__addr, size_t __len) __attribute__ ((__nothrow__));


extern int munlock (const void *__addr, size_t __len) __attribute__ ((__nothrow__));




extern int mlockall (int __flags) __attribute__ ((__nothrow__));



extern int munlockall (void) __attribute__ ((__nothrow__));







extern int mincore (void *__start, size_t __len, unsigned char *__vec)
     __attribute__ ((__nothrow__));

extern int shm_open (const char *__name, int __oflag, mode_t __mode);


extern int shm_unlink (const char *__name);













extern int stat (const char *__restrict __file,
   struct stat *__restrict __buf) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));



extern int fstat (int __fd, struct stat *__buf) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2)));

extern int fstatat (int __fd, const char *__restrict __file,
      struct stat *__restrict __buf, int __flag)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2, 3)));

extern int lstat (const char *__restrict __file,
    struct stat *__restrict __buf) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));

extern int chmod (const char *__file, __mode_t __mode)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));





extern int lchmod (const char *__file, __mode_t __mode)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));



extern int fchmod (int __fd, __mode_t __mode) __attribute__ ((__nothrow__));




extern int fchmodat (int __fd, const char *__file, __mode_t __mode,
       int __flag)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2))) ;



extern __mode_t umask (__mode_t __mask) __attribute__ ((__nothrow__));







extern int mkdir (const char *__path, __mode_t __mode)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));





extern int mkdirat (int __fd, const char *__path, __mode_t __mode)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2)));





extern int mknod (const char *__path, __mode_t __mode, __dev_t __dev)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));





extern int mknodat (int __fd, const char *__path, __mode_t __mode,
      __dev_t __dev) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2)));



extern int mkfifo (const char *__path, __mode_t __mode)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));





extern int mkfifoat (int __fd, const char *__path, __mode_t __mode)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2)));





extern int utimensat (int __fd, const char *__path,
        const struct timespec __times[2],
        int __flags)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2)));



extern int futimens (int __fd, const struct timespec __times[2]) __attribute__ ((__nothrow__));

extern int __fxstat (int __ver, int __fildes, struct stat *__stat_buf)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (3)));
extern int __xstat (int __ver, const char *__filename,
      struct stat *__stat_buf) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2, 3)));
extern int __lxstat (int __ver, const char *__filename,
       struct stat *__stat_buf) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2, 3)));
extern int __fxstatat (int __ver, int __fildes, const char *__filename,
         struct stat *__stat_buf, int __flag)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (3, 4)));

extern int __xmknod (int __ver, const char *__path, __mode_t __mode,
       __dev_t *__dev) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2, 4)));

extern int __xmknodat (int __ver, int __fd, const char *__path,
         __mode_t __mode, __dev_t *__dev)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (3, 5)));













struct timezone
  {
    int tz_minuteswest;
    int tz_dsttime;
  };

typedef struct timezone *__restrict __timezone_ptr_t;







extern int gettimeofday (struct timeval *__restrict __tv,
    __timezone_ptr_t __tz) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));




extern int settimeofday (const struct timeval *__tv,
    const struct timezone *__tz)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));





extern int adjtime (const struct timeval *__delta,
      struct timeval *__olddelta) __attribute__ ((__nothrow__));


enum __itimer_which
  {

    ITIMER_REAL = 0,


    ITIMER_VIRTUAL = 1,



    ITIMER_PROF = 2

  };



struct itimerval
  {

    struct timeval it_interval;

    struct timeval it_value;
  };





 typedef int __itimer_which_t;



extern int getitimer (__itimer_which_t __which,
        struct itimerval *__value) __attribute__ ((__nothrow__));




extern int setitimer (__itimer_which_t __which,
        const struct itimerval *__restrict __new,
        struct itimerval *__restrict __old) __attribute__ ((__nothrow__));




extern int utimes (const char *__file, const struct timeval __tvp[2])
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));



extern int lutimes (const char *__file, const struct timeval __tvp[2])
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));


extern int futimes (int __fd, const struct timeval __tvp[2]) __attribute__ ((__nothrow__));





















enum __rlimit_resource
{

  RLIMIT_CPU = 0,



  RLIMIT_FSIZE = 1,



  RLIMIT_DATA = 2,



  RLIMIT_STACK = 3,



  RLIMIT_CORE = 4,






  __RLIMIT_RSS = 5,



  RLIMIT_NOFILE = 7,
  __RLIMIT_OFILE = RLIMIT_NOFILE,




  RLIMIT_AS = 9,



  __RLIMIT_NPROC = 6,



  __RLIMIT_MEMLOCK = 8,



  __RLIMIT_LOCKS = 10,



  __RLIMIT_SIGPENDING = 11,



  __RLIMIT_MSGQUEUE = 12,





  __RLIMIT_NICE = 13,




  __RLIMIT_RTPRIO = 14,


  __RLIMIT_NLIMITS = 15,
  __RLIM_NLIMITS = __RLIMIT_NLIMITS


};

typedef __rlim_t rlim_t;





struct rlimit
  {

    rlim_t rlim_cur;

    rlim_t rlim_max;
  };

enum __rusage_who
{

  RUSAGE_SELF = 0,



  RUSAGE_CHILDREN = -1

};




struct rusage
  {

    struct timeval ru_utime;

    struct timeval ru_stime;

    long int ru_maxrss;


    long int ru_ixrss;

    long int ru_idrss;

    long int ru_isrss;


    long int ru_minflt;

    long int ru_majflt;

    long int ru_nswap;


    long int ru_inblock;

    long int ru_oublock;

    long int ru_msgsnd;

    long int ru_msgrcv;

    long int ru_nsignals;



    long int ru_nvcsw;


    long int ru_nivcsw;
  };







enum __priority_which
{
  PRIO_PROCESS = 0,

  PRIO_PGRP = 1,

  PRIO_USER = 2

};


 typedef int __rlimit_resource_t;
typedef int __rusage_who_t;
typedef int __priority_which_t;




extern int getrlimit (__rlimit_resource_t __resource,
        struct rlimit *__rlimits) __attribute__ ((__nothrow__));

extern int setrlimit (__rlimit_resource_t __resource,
        const struct rlimit *__rlimits) __attribute__ ((__nothrow__));

extern int getrusage (__rusage_who_t __who, struct rusage *__usage) __attribute__ ((__nothrow__));





extern int getpriority (__priority_which_t __which, id_t __who) __attribute__ ((__nothrow__));



extern int setpriority (__priority_which_t __which, id_t __who, int __prio)
     __attribute__ ((__nothrow__));


typedef enum
{
  P_ALL,
  P_PID,
  P_PGID
} idtype_t;






extern __pid_t wait (__WAIT_STATUS __stat_loc);

extern __pid_t waitpid (__pid_t __pid, int *__stat_loc, int __options);





extern int waitid (idtype_t __idtype, __id_t __id, siginfo_t *__infop,
     int __options);




struct rusage;






extern __pid_t wait3 (__WAIT_STATUS __stat_loc, int __options,
        struct rusage * __usage) __attribute__ ((__nothrow__));



extern __pid_t wait4 (__pid_t __pid, __WAIT_STATUS __stat_loc, int __options,
        struct rusage *__usage) __attribute__ ((__nothrow__));








typedef unsigned char cc_t;
typedef unsigned int speed_t;
typedef unsigned int tcflag_t;


struct termios
  {
    tcflag_t c_iflag;
    tcflag_t c_oflag;
    tcflag_t c_cflag;
    tcflag_t c_lflag;
    cc_t c_line;
    cc_t c_cc[32];
    speed_t c_ispeed;
    speed_t c_ospeed;


  };








extern speed_t cfgetospeed (const struct termios *__termios_p) __attribute__ ((__nothrow__));


extern speed_t cfgetispeed (const struct termios *__termios_p) __attribute__ ((__nothrow__));


extern int cfsetospeed (struct termios *__termios_p, speed_t __speed) __attribute__ ((__nothrow__));


extern int cfsetispeed (struct termios *__termios_p, speed_t __speed) __attribute__ ((__nothrow__));



extern int cfsetspeed (struct termios *__termios_p, speed_t __speed) __attribute__ ((__nothrow__));


extern int tcgetattr (int __fd, struct termios *__termios_p) __attribute__ ((__nothrow__));



extern int tcsetattr (int __fd, int __optional_actions,
        const struct termios *__termios_p) __attribute__ ((__nothrow__));




extern void cfmakeraw (struct termios *__termios_p) __attribute__ ((__nothrow__));


extern int tcsendbreak (int __fd, int __duration) __attribute__ ((__nothrow__));





extern int tcdrain (int __fd);



extern int tcflush (int __fd, int __queue_selector) __attribute__ ((__nothrow__));



extern int tcflow (int __fd, int __action) __attribute__ ((__nothrow__));











struct tm
{
  int tm_sec;
  int tm_min;
  int tm_hour;
  int tm_mday;
  int tm_mon;
  int tm_year;
  int tm_wday;
  int tm_yday;
  int tm_isdst;


  long int tm_gmtoff;
  const char *tm_zone;



};






struct itimerspec
  {
    struct timespec it_interval;
    struct timespec it_value;
  };


struct sigevent;

extern clock_t clock (void) __attribute__ ((__nothrow__));


extern time_t time (time_t *__timer) __attribute__ ((__nothrow__));


extern double difftime (time_t __time1, time_t __time0)
     __attribute__ ((__nothrow__)) __attribute__ ((__const__));


extern time_t mktime (struct tm *__tp) __attribute__ ((__nothrow__));





extern size_t strftime (char *__restrict __s, size_t __maxsize,
   const char *__restrict __format,
   const struct tm *__restrict __tp) __attribute__ ((__nothrow__));



extern size_t strftime_l (char *__restrict __s, size_t __maxsize,
     const char *__restrict __format,
     const struct tm *__restrict __tp,
     __locale_t __loc) __attribute__ ((__nothrow__));

extern struct tm *gmtime (const time_t *__timer) __attribute__ ((__nothrow__));



extern struct tm *localtime (const time_t *__timer) __attribute__ ((__nothrow__));





extern struct tm *gmtime_r (const time_t *__restrict __timer,
       struct tm *__restrict __tp) __attribute__ ((__nothrow__));



extern struct tm *localtime_r (const time_t *__restrict __timer,
          struct tm *__restrict __tp) __attribute__ ((__nothrow__));




extern char *asctime (const struct tm *__tp) __attribute__ ((__nothrow__));


extern char *ctime (const time_t *__timer) __attribute__ ((__nothrow__));







extern char *asctime_r (const struct tm *__restrict __tp,
   char *__restrict __buf) __attribute__ ((__nothrow__));


extern char *ctime_r (const time_t *__restrict __timer,
        char *__restrict __buf) __attribute__ ((__nothrow__));


extern char *__tzname[2];
extern int __daylight;
extern long int __timezone;




extern char *tzname[2];



extern void tzset (void) __attribute__ ((__nothrow__));


extern int daylight;
extern long int timezone;




extern int stime (const time_t *__when) __attribute__ ((__nothrow__));

extern time_t timegm (struct tm *__tp) __attribute__ ((__nothrow__));


extern time_t timelocal (struct tm *__tp) __attribute__ ((__nothrow__));


extern int dysize (int __year) __attribute__ ((__nothrow__)) __attribute__ ((__const__));






extern int nanosleep (const struct timespec *__requested_time,
        struct timespec *__remaining);



extern int clock_getres (clockid_t __clock_id, struct timespec *__res) __attribute__ ((__nothrow__));


extern int clock_gettime (clockid_t __clock_id, struct timespec *__tp) __attribute__ ((__nothrow__));


extern int clock_settime (clockid_t __clock_id, const struct timespec *__tp)
     __attribute__ ((__nothrow__));






extern int clock_nanosleep (clockid_t __clock_id, int __flags,
       const struct timespec *__req,
       struct timespec *__rem);


extern int clock_getcpuclockid (pid_t __pid, clockid_t *__clock_id) __attribute__ ((__nothrow__));


extern int timer_create (clockid_t __clock_id,
    struct sigevent *__restrict __evp,
    timer_t *__restrict __timerid) __attribute__ ((__nothrow__));


extern int timer_delete (timer_t __timerid) __attribute__ ((__nothrow__));


extern int timer_settime (timer_t __timerid, int __flags,
     const struct itimerspec *__restrict __value,
     struct itimerspec *__restrict __ovalue) __attribute__ ((__nothrow__));


extern int timer_gettime (timer_t __timerid, struct itimerspec *__value)
     __attribute__ ((__nothrow__));


extern int timer_getoverrun (timer_t __timerid) __attribute__ ((__nothrow__));















typedef __useconds_t useconds_t;

extern int access (const char *__name, int __type) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));

extern int faccessat (int __fd, const char *__file, int __type, int __flag)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2))) ;

extern __off_t lseek (int __fd, __off_t __offset, int __whence) __attribute__ ((__nothrow__));

extern int close (int __fd);






extern ssize_t read (int __fd, void *__buf, size_t __nbytes) ;





extern ssize_t write (int __fd, const void *__buf, size_t __n) ;

extern ssize_t pread (int __fd, void *__buf, size_t __nbytes,
        __off_t __offset) ;






extern ssize_t pwrite (int __fd, const void *__buf, size_t __n,
         __off_t __offset) ;

extern int pipe (int __pipedes[2]) __attribute__ ((__nothrow__)) ;

extern unsigned int alarm (unsigned int __seconds) __attribute__ ((__nothrow__));

extern unsigned int sleep (unsigned int __seconds);






extern __useconds_t ualarm (__useconds_t __value, __useconds_t __interval)
     __attribute__ ((__nothrow__));






extern int usleep (__useconds_t __useconds);






extern int pause (void);



extern int chown (const char *__file, __uid_t __owner, __gid_t __group)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;



extern int fchown (int __fd, __uid_t __owner, __gid_t __group) __attribute__ ((__nothrow__)) ;




extern int lchown (const char *__file, __uid_t __owner, __gid_t __group)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;





extern int fchownat (int __fd, const char *__file, __uid_t __owner,
       __gid_t __group, int __flag)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2))) ;


extern int chdir (const char *__path) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;



extern int fchdir (int __fd) __attribute__ ((__nothrow__)) ;

extern char *getcwd (char *__buf, size_t __size) __attribute__ ((__nothrow__)) ;

extern char *getwd (char *__buf)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) __attribute__ ((__deprecated__)) ;


extern int dup (int __fd) __attribute__ ((__nothrow__)) ;


extern int dup2 (int __fd, int __fd2) __attribute__ ((__nothrow__));







extern char **__environ;





extern int execve (const char *__path, char *const __argv[],
     char *const __envp[]) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));




extern int fexecve (int __fd, char *const __argv[], char *const __envp[])
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2)));


extern int execv (const char *__path, char *const __argv[])
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));



extern int execle (const char *__path, const char *__arg, ...)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));



extern int execl (const char *__path, const char *__arg, ...)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));



extern int execvp (const char *__file, char *const __argv[])
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));




extern int execlp (const char *__file, const char *__arg, ...)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));

extern int nice (int __inc) __attribute__ ((__nothrow__)) ;


extern void _exit (int __status) __attribute__ ((__noreturn__));


enum
  {
    _PC_LINK_MAX,

    _PC_MAX_CANON,

    _PC_MAX_INPUT,

    _PC_NAME_MAX,

    _PC_PATH_MAX,

    _PC_PIPE_BUF,

    _PC_CHOWN_RESTRICTED,

    _PC_NO_TRUNC,

    _PC_VDISABLE,

    _PC_SYNC_IO,

    _PC_ASYNC_IO,

    _PC_PRIO_IO,

    _PC_SOCK_MAXBUF,

    _PC_FILESIZEBITS,

    _PC_REC_INCR_XFER_SIZE,

    _PC_REC_MAX_XFER_SIZE,

    _PC_REC_MIN_XFER_SIZE,

    _PC_REC_XFER_ALIGN,

    _PC_ALLOC_SIZE_MIN,

    _PC_SYMLINK_MAX,

    _PC_2_SYMLINKS

  };


enum
  {
    _SC_ARG_MAX,

    _SC_CHILD_MAX,

    _SC_CLK_TCK,

    _SC_NGROUPS_MAX,

    _SC_OPEN_MAX,

    _SC_STREAM_MAX,

    _SC_TZNAME_MAX,

    _SC_JOB_CONTROL,

    _SC_SAVED_IDS,

    _SC_REALTIME_SIGNALS,

    _SC_PRIORITY_SCHEDULING,

    _SC_TIMERS,

    _SC_ASYNCHRONOUS_IO,

    _SC_PRIORITIZED_IO,

    _SC_SYNCHRONIZED_IO,

    _SC_FSYNC,

    _SC_MAPPED_FILES,

    _SC_MEMLOCK,

    _SC_MEMLOCK_RANGE,

    _SC_MEMORY_PROTECTION,

    _SC_MESSAGE_PASSING,

    _SC_SEMAPHORES,

    _SC_SHARED_MEMORY_OBJECTS,

    _SC_AIO_LISTIO_MAX,

    _SC_AIO_MAX,

    _SC_AIO_PRIO_DELTA_MAX,

    _SC_DELAYTIMER_MAX,

    _SC_MQ_OPEN_MAX,

    _SC_MQ_PRIO_MAX,

    _SC_VERSION,

    _SC_PAGESIZE,


    _SC_RTSIG_MAX,

    _SC_SEM_NSEMS_MAX,

    _SC_SEM_VALUE_MAX,

    _SC_SIGQUEUE_MAX,

    _SC_TIMER_MAX,




    _SC_BC_BASE_MAX,

    _SC_BC_DIM_MAX,

    _SC_BC_SCALE_MAX,

    _SC_BC_STRING_MAX,

    _SC_COLL_WEIGHTS_MAX,

    _SC_EQUIV_CLASS_MAX,

    _SC_EXPR_NEST_MAX,

    _SC_LINE_MAX,

    _SC_RE_DUP_MAX,

    _SC_CHARCLASS_NAME_MAX,


    _SC_2_VERSION,

    _SC_2_C_BIND,

    _SC_2_C_DEV,

    _SC_2_FORT_DEV,

    _SC_2_FORT_RUN,

    _SC_2_SW_DEV,

    _SC_2_LOCALEDEF,


    _SC_PII,

    _SC_PII_XTI,

    _SC_PII_SOCKET,

    _SC_PII_INTERNET,

    _SC_PII_OSI,

    _SC_POLL,

    _SC_SELECT,

    _SC_UIO_MAXIOV,

    _SC_IOV_MAX = _SC_UIO_MAXIOV,

    _SC_PII_INTERNET_STREAM,

    _SC_PII_INTERNET_DGRAM,

    _SC_PII_OSI_COTS,

    _SC_PII_OSI_CLTS,

    _SC_PII_OSI_M,

    _SC_T_IOV_MAX,



    _SC_THREADS,

    _SC_THREAD_SAFE_FUNCTIONS,

    _SC_GETGR_R_SIZE_MAX,

    _SC_GETPW_R_SIZE_MAX,

    _SC_LOGIN_NAME_MAX,

    _SC_TTY_NAME_MAX,

    _SC_THREAD_DESTRUCTOR_ITERATIONS,

    _SC_THREAD_KEYS_MAX,

    _SC_THREAD_STACK_MIN,

    _SC_THREAD_THREADS_MAX,

    _SC_THREAD_ATTR_STACKADDR,

    _SC_THREAD_ATTR_STACKSIZE,

    _SC_THREAD_PRIORITY_SCHEDULING,

    _SC_THREAD_PRIO_INHERIT,

    _SC_THREAD_PRIO_PROTECT,

    _SC_THREAD_PROCESS_SHARED,


    _SC_NPROCESSORS_CONF,

    _SC_NPROCESSORS_ONLN,

    _SC_PHYS_PAGES,

    _SC_AVPHYS_PAGES,

    _SC_ATEXIT_MAX,

    _SC_PASS_MAX,


    _SC_XOPEN_VERSION,

    _SC_XOPEN_XCU_VERSION,

    _SC_XOPEN_UNIX,

    _SC_XOPEN_CRYPT,

    _SC_XOPEN_ENH_I18N,

    _SC_XOPEN_SHM,


    _SC_2_CHAR_TERM,

    _SC_2_C_VERSION,

    _SC_2_UPE,


    _SC_XOPEN_XPG2,

    _SC_XOPEN_XPG3,

    _SC_XOPEN_XPG4,


    _SC_CHAR_BIT,

    _SC_CHAR_MAX,

    _SC_CHAR_MIN,

    _SC_INT_MAX,

    _SC_INT_MIN,

    _SC_LONG_BIT,

    _SC_WORD_BIT,

    _SC_MB_LEN_MAX,

    _SC_NZERO,

    _SC_SSIZE_MAX,

    _SC_SCHAR_MAX,

    _SC_SCHAR_MIN,

    _SC_SHRT_MAX,

    _SC_SHRT_MIN,

    _SC_UCHAR_MAX,

    _SC_UINT_MAX,

    _SC_ULONG_MAX,

    _SC_USHRT_MAX,


    _SC_NL_ARGMAX,

    _SC_NL_LANGMAX,

    _SC_NL_MSGMAX,

    _SC_NL_NMAX,

    _SC_NL_SETMAX,

    _SC_NL_TEXTMAX,


    _SC_XBS5_ILP32_OFF32,

    _SC_XBS5_ILP32_OFFBIG,

    _SC_XBS5_LP64_OFF64,

    _SC_XBS5_LPBIG_OFFBIG,


    _SC_XOPEN_LEGACY,

    _SC_XOPEN_REALTIME,

    _SC_XOPEN_REALTIME_THREADS,


    _SC_ADVISORY_INFO,

    _SC_BARRIERS,

    _SC_BASE,

    _SC_C_LANG_SUPPORT,

    _SC_C_LANG_SUPPORT_R,

    _SC_CLOCK_SELECTION,

    _SC_CPUTIME,

    _SC_THREAD_CPUTIME,

    _SC_DEVICE_IO,

    _SC_DEVICE_SPECIFIC,

    _SC_DEVICE_SPECIFIC_R,

    _SC_FD_MGMT,

    _SC_FIFO,

    _SC_PIPE,

    _SC_FILE_ATTRIBUTES,

    _SC_FILE_LOCKING,

    _SC_FILE_SYSTEM,

    _SC_MONOTONIC_CLOCK,

    _SC_MULTI_PROCESS,

    _SC_SINGLE_PROCESS,

    _SC_NETWORKING,

    _SC_READER_WRITER_LOCKS,

    _SC_SPIN_LOCKS,

    _SC_REGEXP,

    _SC_REGEX_VERSION,

    _SC_SHELL,

    _SC_SIGNALS,

    _SC_SPAWN,

    _SC_SPORADIC_SERVER,

    _SC_THREAD_SPORADIC_SERVER,

    _SC_SYSTEM_DATABASE,

    _SC_SYSTEM_DATABASE_R,

    _SC_TIMEOUTS,

    _SC_TYPED_MEMORY_OBJECTS,

    _SC_USER_GROUPS,

    _SC_USER_GROUPS_R,

    _SC_2_PBS,

    _SC_2_PBS_ACCOUNTING,

    _SC_2_PBS_LOCATE,

    _SC_2_PBS_MESSAGE,

    _SC_2_PBS_TRACK,

    _SC_SYMLOOP_MAX,

    _SC_STREAMS,

    _SC_2_PBS_CHECKPOINT,


    _SC_V6_ILP32_OFF32,

    _SC_V6_ILP32_OFFBIG,

    _SC_V6_LP64_OFF64,

    _SC_V6_LPBIG_OFFBIG,


    _SC_HOST_NAME_MAX,

    _SC_TRACE,

    _SC_TRACE_EVENT_FILTER,

    _SC_TRACE_INHERIT,

    _SC_TRACE_LOG,


    _SC_LEVEL1_ICACHE_SIZE,

    _SC_LEVEL1_ICACHE_ASSOC,

    _SC_LEVEL1_ICACHE_LINESIZE,

    _SC_LEVEL1_DCACHE_SIZE,

    _SC_LEVEL1_DCACHE_ASSOC,

    _SC_LEVEL1_DCACHE_LINESIZE,

    _SC_LEVEL2_CACHE_SIZE,

    _SC_LEVEL2_CACHE_ASSOC,

    _SC_LEVEL2_CACHE_LINESIZE,

    _SC_LEVEL3_CACHE_SIZE,

    _SC_LEVEL3_CACHE_ASSOC,

    _SC_LEVEL3_CACHE_LINESIZE,

    _SC_LEVEL4_CACHE_SIZE,

    _SC_LEVEL4_CACHE_ASSOC,

    _SC_LEVEL4_CACHE_LINESIZE,



    _SC_IPV6 = _SC_LEVEL1_ICACHE_SIZE + 50,

    _SC_RAW_SOCKETS,


    _SC_V7_ILP32_OFF32,

    _SC_V7_ILP32_OFFBIG,

    _SC_V7_LP64_OFF64,

    _SC_V7_LPBIG_OFFBIG,


    _SC_SS_REPL_MAX,


    _SC_TRACE_EVENT_NAME_MAX,

    _SC_TRACE_NAME_MAX,

    _SC_TRACE_SYS_MAX,

    _SC_TRACE_USER_EVENT_MAX,


    _SC_XOPEN_STREAMS,


    _SC_THREAD_ROBUST_PRIO_INHERIT,

    _SC_THREAD_ROBUST_PRIO_PROTECT

  };


enum
  {
    _CS_PATH,


    _CS_V6_WIDTH_RESTRICTED_ENVS,



    _CS_GNU_LIBC_VERSION,

    _CS_GNU_LIBPTHREAD_VERSION,


    _CS_V5_WIDTH_RESTRICTED_ENVS,



    _CS_V7_WIDTH_RESTRICTED_ENVS,



    _CS_LFS_CFLAGS = 1000,

    _CS_LFS_LDFLAGS,

    _CS_LFS_LIBS,

    _CS_LFS_LINTFLAGS,

    _CS_LFS64_CFLAGS,

    _CS_LFS64_LDFLAGS,

    _CS_LFS64_LIBS,

    _CS_LFS64_LINTFLAGS,


    _CS_XBS5_ILP32_OFF32_CFLAGS = 1100,

    _CS_XBS5_ILP32_OFF32_LDFLAGS,

    _CS_XBS5_ILP32_OFF32_LIBS,

    _CS_XBS5_ILP32_OFF32_LINTFLAGS,

    _CS_XBS5_ILP32_OFFBIG_CFLAGS,

    _CS_XBS5_ILP32_OFFBIG_LDFLAGS,

    _CS_XBS5_ILP32_OFFBIG_LIBS,

    _CS_XBS5_ILP32_OFFBIG_LINTFLAGS,

    _CS_XBS5_LP64_OFF64_CFLAGS,

    _CS_XBS5_LP64_OFF64_LDFLAGS,

    _CS_XBS5_LP64_OFF64_LIBS,

    _CS_XBS5_LP64_OFF64_LINTFLAGS,

    _CS_XBS5_LPBIG_OFFBIG_CFLAGS,

    _CS_XBS5_LPBIG_OFFBIG_LDFLAGS,

    _CS_XBS5_LPBIG_OFFBIG_LIBS,

    _CS_XBS5_LPBIG_OFFBIG_LINTFLAGS,


    _CS_POSIX_V6_ILP32_OFF32_CFLAGS,

    _CS_POSIX_V6_ILP32_OFF32_LDFLAGS,

    _CS_POSIX_V6_ILP32_OFF32_LIBS,

    _CS_POSIX_V6_ILP32_OFF32_LINTFLAGS,

    _CS_POSIX_V6_ILP32_OFFBIG_CFLAGS,

    _CS_POSIX_V6_ILP32_OFFBIG_LDFLAGS,

    _CS_POSIX_V6_ILP32_OFFBIG_LIBS,

    _CS_POSIX_V6_ILP32_OFFBIG_LINTFLAGS,

    _CS_POSIX_V6_LP64_OFF64_CFLAGS,

    _CS_POSIX_V6_LP64_OFF64_LDFLAGS,

    _CS_POSIX_V6_LP64_OFF64_LIBS,

    _CS_POSIX_V6_LP64_OFF64_LINTFLAGS,

    _CS_POSIX_V6_LPBIG_OFFBIG_CFLAGS,

    _CS_POSIX_V6_LPBIG_OFFBIG_LDFLAGS,

    _CS_POSIX_V6_LPBIG_OFFBIG_LIBS,

    _CS_POSIX_V6_LPBIG_OFFBIG_LINTFLAGS,


    _CS_POSIX_V7_ILP32_OFF32_CFLAGS,

    _CS_POSIX_V7_ILP32_OFF32_LDFLAGS,

    _CS_POSIX_V7_ILP32_OFF32_LIBS,

    _CS_POSIX_V7_ILP32_OFF32_LINTFLAGS,

    _CS_POSIX_V7_ILP32_OFFBIG_CFLAGS,

    _CS_POSIX_V7_ILP32_OFFBIG_LDFLAGS,

    _CS_POSIX_V7_ILP32_OFFBIG_LIBS,

    _CS_POSIX_V7_ILP32_OFFBIG_LINTFLAGS,

    _CS_POSIX_V7_LP64_OFF64_CFLAGS,

    _CS_POSIX_V7_LP64_OFF64_LDFLAGS,

    _CS_POSIX_V7_LP64_OFF64_LIBS,

    _CS_POSIX_V7_LP64_OFF64_LINTFLAGS,

    _CS_POSIX_V7_LPBIG_OFFBIG_CFLAGS,

    _CS_POSIX_V7_LPBIG_OFFBIG_LDFLAGS,

    _CS_POSIX_V7_LPBIG_OFFBIG_LIBS,

    _CS_POSIX_V7_LPBIG_OFFBIG_LINTFLAGS,


    _CS_V6_ENV,

    _CS_V7_ENV

  };



extern long int pathconf (const char *__path, int __name)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));


extern long int fpathconf (int __fd, int __name) __attribute__ ((__nothrow__));


extern long int sysconf (int __name) __attribute__ ((__nothrow__));



extern size_t confstr (int __name, char *__buf, size_t __len) __attribute__ ((__nothrow__));


extern __pid_t getpid (void) __attribute__ ((__nothrow__));


extern __pid_t getppid (void) __attribute__ ((__nothrow__));




extern __pid_t getpgrp (void) __attribute__ ((__nothrow__));






extern __pid_t __getpgid (__pid_t __pid) __attribute__ ((__nothrow__));

extern __pid_t getpgid (__pid_t __pid) __attribute__ ((__nothrow__));




extern int setpgid (__pid_t __pid, __pid_t __pgid) __attribute__ ((__nothrow__));

extern int setpgrp (void) __attribute__ ((__nothrow__));

extern __pid_t setsid (void) __attribute__ ((__nothrow__));



extern __pid_t getsid (__pid_t __pid) __attribute__ ((__nothrow__));


extern __uid_t getuid (void) __attribute__ ((__nothrow__));


extern __uid_t geteuid (void) __attribute__ ((__nothrow__));


extern __gid_t getgid (void) __attribute__ ((__nothrow__));


extern __gid_t getegid (void) __attribute__ ((__nothrow__));




extern int getgroups (int __size, __gid_t __list[]) __attribute__ ((__nothrow__)) ;

extern int setuid (__uid_t __uid) __attribute__ ((__nothrow__));




extern int setreuid (__uid_t __ruid, __uid_t __euid) __attribute__ ((__nothrow__));



extern int seteuid (__uid_t __uid) __attribute__ ((__nothrow__));





extern int setgid (__gid_t __gid) __attribute__ ((__nothrow__));




extern int setregid (__gid_t __rgid, __gid_t __egid) __attribute__ ((__nothrow__));



extern int setegid (__gid_t __gid) __attribute__ ((__nothrow__));

extern __pid_t fork (void) __attribute__ ((__nothrow__));






extern __pid_t vfork (void) __attribute__ ((__nothrow__));



extern char *ttyname (int __fd) __attribute__ ((__nothrow__));



extern int ttyname_r (int __fd, char *__buf, size_t __buflen)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2))) ;



extern int isatty (int __fd) __attribute__ ((__nothrow__));




extern int ttyslot (void) __attribute__ ((__nothrow__));


extern int link (const char *__from, const char *__to)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2))) ;




extern int linkat (int __fromfd, const char *__from, int __tofd,
     const char *__to, int __flags)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2, 4))) ;



extern int symlink (const char *__from, const char *__to)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2))) ;




extern ssize_t readlink (const char *__restrict __path,
    char *__restrict __buf, size_t __len)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2))) ;



extern int symlinkat (const char *__from, int __tofd,
        const char *__to) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 3))) ;


extern ssize_t readlinkat (int __fd, const char *__restrict __path,
      char *__restrict __buf, size_t __len)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2, 3))) ;


extern int unlink (const char *__name) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));



extern int unlinkat (int __fd, const char *__name, int __flag)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2)));


extern int rmdir (const char *__path) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));



extern __pid_t tcgetpgrp (int __fd) __attribute__ ((__nothrow__));


extern int tcsetpgrp (int __fd, __pid_t __pgrp_id) __attribute__ ((__nothrow__));






extern char *getlogin (void);







extern int getlogin_r (char *__name, size_t __name_len) __attribute__ ((__nonnull__ (1)));



extern int setlogin (const char *__name) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));


extern char *optarg;

extern int optind;




extern int opterr;



extern int optopt;

extern int getopt (int ___argc, char *const *___argv, const char *__shortopts)
       __attribute__ ((__nothrow__));






extern int gethostname (char *__name, size_t __len) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));




extern int sethostname (const char *__name, size_t __len)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;



extern int sethostid (long int __id) __attribute__ ((__nothrow__)) ;





extern int getdomainname (char *__name, size_t __len)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;
extern int setdomainname (const char *__name, size_t __len)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;





extern int vhangup (void) __attribute__ ((__nothrow__));


extern int revoke (const char *__file) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;







extern int profil (unsigned short int *__sample_buffer, size_t __size,
     size_t __offset, unsigned int __scale)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));





extern int acct (const char *__name) __attribute__ ((__nothrow__));



extern char *getusershell (void) __attribute__ ((__nothrow__));
extern void endusershell (void) __attribute__ ((__nothrow__));
extern void setusershell (void) __attribute__ ((__nothrow__));





extern int daemon (int __nochdir, int __noclose) __attribute__ ((__nothrow__)) ;




extern int chroot (const char *__path) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;



extern char *getpass (const char *__prompt) __attribute__ ((__nonnull__ (1)));






extern int fsync (int __fd);



extern long int gethostid (void);


extern void sync (void) __attribute__ ((__nothrow__));





extern int getpagesize (void) __attribute__ ((__nothrow__)) __attribute__ ((__const__));




extern int getdtablesize (void) __attribute__ ((__nothrow__));





extern int truncate (const char *__file, __off_t __length)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;

extern int ftruncate (int __fd, __off_t __length) __attribute__ ((__nothrow__)) ;

extern int brk (void *__addr) __attribute__ ((__nothrow__)) ;





extern void *sbrk (intptr_t __delta) __attribute__ ((__nothrow__));

extern long int syscall (long int __sysno, ...) __attribute__ ((__nothrow__));

extern int fdatasync (int __fildes);

extern char *ctermid (char *__s) __attribute__ ((__nothrow__));



























struct mntent
  {
    char *mnt_fsname;
    char *mnt_dir;
    char *mnt_type;
    char *mnt_opts;
    int mnt_freq;
    int mnt_passno;
  };




extern FILE *setmntent (const char *__file, const char *__mode) __attribute__ ((__nothrow__));




extern struct mntent *getmntent (FILE *__stream) __attribute__ ((__nothrow__));



extern struct mntent *getmntent_r (FILE *__restrict __stream,
       struct mntent *__restrict __result,
       char *__restrict __buffer,
       int __bufsize) __attribute__ ((__nothrow__));



extern int addmntent (FILE *__restrict __stream,
        const struct mntent *__restrict __mnt) __attribute__ ((__nothrow__));


extern int endmntent (FILE *__stream) __attribute__ ((__nothrow__));



extern char *hasmntopt (const struct mntent *__mnt,
   const char *__opt) __attribute__ ((__nothrow__));








struct statfs
  {
    long int f_type;
    long int f_bsize;

    __fsblkcnt_t f_blocks;
    __fsblkcnt_t f_bfree;
    __fsblkcnt_t f_bavail;
    __fsfilcnt_t f_files;
    __fsfilcnt_t f_ffree;






    __fsid_t f_fsid;
    long int f_namelen;
    long int f_frsize;
    long int f_spare[5];
  };






extern int statfs (const char *__file, struct statfs *__buf)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1, 2)));

extern int fstatfs (int __fildes, struct statfs *__buf)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (2)));









struct passwd
{
  char *pw_name;
  char *pw_passwd;
  __uid_t pw_uid;
  __gid_t pw_gid;
  char *pw_gecos;
  char *pw_dir;
  char *pw_shell;
};








extern void setpwent (void);





extern void endpwent (void);





extern struct passwd *getpwent (void);

extern struct passwd *fgetpwent (FILE *__stream);







extern int putpwent (const struct passwd *__restrict __p,
       FILE *__restrict __f);





extern struct passwd *getpwuid (__uid_t __uid);





extern struct passwd *getpwnam (const char *__name);

extern int getpwent_r (struct passwd *__restrict __resultbuf,
         char *__restrict __buffer, size_t __buflen,
         struct passwd **__restrict __result);

extern int getpwuid_r (__uid_t __uid,
         struct passwd *__restrict __resultbuf,
         char *__restrict __buffer, size_t __buflen,
         struct passwd **__restrict __result);

extern int getpwnam_r (const char *__restrict __name,
         struct passwd *__restrict __resultbuf,
         char *__restrict __buffer, size_t __buflen,
         struct passwd **__restrict __result);

extern int fgetpwent_r (FILE *__restrict __stream,
   struct passwd *__restrict __resultbuf,
   char *__restrict __buffer, size_t __buflen,
   struct passwd **__restrict __result);









struct group
  {
    char *gr_name;
    char *gr_passwd;
    __gid_t gr_gid;
    char **gr_mem;
  };








extern void setgrent (void);






extern void endgrent (void);





extern struct group *getgrent (void);

extern struct group *fgetgrent (FILE *__stream);

extern struct group *getgrgid (__gid_t __gid);





extern struct group *getgrnam (const char *__name);

extern int getgrgid_r (__gid_t __gid, struct group *__restrict __resultbuf,
         char *__restrict __buffer, size_t __buflen,
         struct group **__restrict __result);





extern int getgrnam_r (const char *__restrict __name,
         struct group *__restrict __resultbuf,
         char *__restrict __buffer, size_t __buflen,
         struct group **__restrict __result);

extern int fgetgrent_r (FILE *__restrict __stream,
   struct group *__restrict __resultbuf,
   char *__restrict __buffer, size_t __buflen,
   struct group **__restrict __result);




extern int setgroups (size_t __n, const __gid_t *__groups) __attribute__ ((__nothrow__));

extern int getgrouplist (const char *__user, __gid_t __group,
    __gid_t *__groups, int *__ngroups);

extern int initgroups (const char *__user, __gid_t __group);







extern in_addr_t inet_addr (const char *__cp) __attribute__ ((__nothrow__));


extern in_addr_t inet_lnaof (struct in_addr __in) __attribute__ ((__nothrow__));



extern struct in_addr inet_makeaddr (in_addr_t __net, in_addr_t __host)
     __attribute__ ((__nothrow__));


extern in_addr_t inet_netof (struct in_addr __in) __attribute__ ((__nothrow__));



extern in_addr_t inet_network (const char *__cp) __attribute__ ((__nothrow__));



extern char *inet_ntoa (struct in_addr __in) __attribute__ ((__nothrow__));




extern int inet_pton (int __af, const char *__restrict __cp,
        void *__restrict __buf) __attribute__ ((__nothrow__));




extern const char *inet_ntop (int __af, const void *__restrict __cp,
    char *__restrict __buf, socklen_t __len)
     __attribute__ ((__nothrow__));






extern int inet_aton (const char *__cp, struct in_addr *__inp) __attribute__ ((__nothrow__));



extern char *inet_neta (in_addr_t __net, char *__buf, size_t __len) __attribute__ ((__nothrow__));




extern char *inet_net_ntop (int __af, const void *__cp, int __bits,
       char *__buf, size_t __len) __attribute__ ((__nothrow__));




extern int inet_net_pton (int __af, const char *__cp,
     void *__buf, size_t __len) __attribute__ ((__nothrow__));




extern unsigned int inet_nsap_addr (const char *__cp,
        unsigned char *__buf, int __len) __attribute__ ((__nothrow__));



extern char *inet_nsap_ntoa (int __len, const unsigned char *__cp,
        char *__buf) __attribute__ ((__nothrow__));


extern char **environ;




int klogctl(int type, char *b, int len);




char *dirname(char *path);



struct sysinfo {
 long uptime;
 unsigned long loads[3];
 unsigned long totalram;
 unsigned long freeram;
 unsigned long sharedram;
 unsigned long bufferram;
 unsigned long totalswap;
 unsigned long freeswap;
 unsigned short procs;
 unsigned short pad;
 unsigned long totalhigh;
 unsigned long freehigh;
 unsigned int mem_unit;
 char _f[20 - 2 * sizeof(long) - sizeof(int)];
};
int sysinfo(struct sysinfo* info);







typedef unsigned long uoff_t;

extern int *const bb_errno;





uint64_t bb_bswap_64(uint64_t x) ;

unsigned long long monotonic_ns(void) ;
unsigned long long monotonic_us(void) ;
unsigned long long monotonic_ms(void) ;
unsigned monotonic_sec(void) ;

extern void chomp(char *s) ;
extern void trim(char *s) ;
extern char *skip_whitespace(const char *) ;
extern char *skip_non_whitespace(const char *) ;
extern char *skip_dev_pfx(const char *tty_name) ;

extern char *strrstr(const char *haystack, const char *needle) ;


extern const char *bb_mode_string(mode_t mode) ;
extern int is_directory(const char *name, int followLinks, struct stat *statBuf) ;
enum {
 FILEUTILS_PRESERVE_STATUS = 1 << 0,
 FILEUTILS_DEREFERENCE = 1 << 1,
 FILEUTILS_RECUR = 1 << 2,
 FILEUTILS_FORCE = 1 << 3,
 FILEUTILS_INTERACTIVE = 1 << 4,
 FILEUTILS_MAKE_HARDLINK = 1 << 5,
 FILEUTILS_MAKE_SOFTLINK = 1 << 6,
 FILEUTILS_DEREF_SOFTLINK = 1 << 7,
 FILEUTILS_DEREFERENCE_L0 = 1 << 8,




};

extern int remove_file(const char *path, int flags) ;




extern int copy_file(const char *source, const char *dest, int flags) ;

enum {
 ACTION_RECURSE = (1 << 0),
 ACTION_FOLLOWLINKS = (1 << 1),
 ACTION_FOLLOWLINKS_L0 = (1 << 2),
 ACTION_DEPTHFIRST = (1 << 3),

 ACTION_QUIET = (1 << 5),
 ACTION_DANGLING_OK = (1 << 6),
};
typedef uint8_t recurse_flags_t;
extern int recursive_action(const char *fileName, unsigned flags,
 int (*fileAction)(const char *fileName, struct stat* statbuf, void* userData, int depth),
 int (*dirAction)(const char *fileName, struct stat* statbuf, void* userData, int depth),
 void* userData, unsigned depth) ;
extern int device_open(const char *device, int mode) ;
enum { GETPTY_BUFSIZE = 16 };
extern int xgetpty(char *line) ;
extern int get_console_fd_or_die(void) ;
extern void console_make_active(int fd, const int vt_num) ;
extern char *find_block_device(const char *path) ;

extern off_t bb_copyfd_eof(int fd1, int fd2) ;
extern off_t bb_copyfd_size(int fd1, int fd2, off_t size) ;
extern void bb_copyfd_exact_size(int fd1, int fd2, off_t size) ;


extern void complain_copyfd_and_die(off_t sz) __attribute__ ((__noreturn__)) ;
extern char bb_process_escape_sequence(const char **ptr) ;
char* strcpy_and_process_escape_sequences(char *dst, const char *src) ;






extern char *bb_get_last_path_component_strip(char *path) ;

extern char *bb_get_last_path_component_nostrip(const char *path) ;

int ndelay_on(int fd) ;
int ndelay_off(int fd) ;
int close_on_exec_on(int fd) ;
void xdup2(int, int) ;
void xmove_fd(int, int) ;


DIR *xopendir(const char *path) ;
DIR *warn_opendir(const char *path) ;

char *xmalloc_realpath(const char *path) __attribute__ ((malloc));
char *xmalloc_readlink(const char *path) __attribute__ ((malloc));
char *xmalloc_readlink_or_warn(const char *path) __attribute__ ((malloc));

char *xrealloc_getcwd_or_warn(char *cwd) ;

char *xmalloc_follow_symlinks(const char *path) __attribute__ ((malloc));


enum {

 BB_FATAL_SIGS = (int)(0
  + (1LL << 1)
  + (1LL << 2)
  + (1LL << 15)
  + (1LL << 13)
  + (1LL << 3)
  + (1LL << 6)
  + (1LL << 14)
  + (1LL << 26)
  + (1LL << 24)
  + (1LL << 25)
  + (1LL << 10)
  + (1LL << 12)
  + 0),
};
void bb_signals(int sigs, void (*f)(int)) ;



void bb_signals_recursive_norestart(int sigs, void (*f)(int)) ;

void signal_no_SA_RESTART_empty_mask(int sig, void (*handler)(int)) ;

void signal_SA_RESTART_empty_mask(int sig, void (*handler)(int)) ;
void wait_for_any_sig(void) ;
void kill_myself_with_sig(int sig) __attribute__ ((__noreturn__)) ;
void sig_block(int sig) ;
void sig_unblock(int sig) ;

int sigaction_set(int sig, const struct sigaction *act) ;

int sigprocmask_allsigs(int how) ;

extern smallint bb_got_signal;
void record_signo(int signo);


void xsetgid(gid_t gid) ;
void xsetuid(uid_t uid) ;
void xchdir(const char *path) ;
void xchroot(const char *path) ;
void xsetenv(const char *key, const char *value) ;
void bb_unsetenv(const char *key) ;
void bb_unsetenv_and_free(char *key) ;
void xunlink(const char *pathname) ;
void xstat(const char *pathname, struct stat *buf) ;
void xfstat(int fd, struct stat *buf, const char *errmsg) ;
int xopen(const char *pathname, int flags) ;
int xopen_nonblocking(const char *pathname) ;
int xopen3(const char *pathname, int flags, int mode) ;
int open_or_warn(const char *pathname, int flags) ;
int open3_or_warn(const char *pathname, int flags, int mode) ;
int open_or_warn_stdin(const char *pathname) ;
int xopen_stdin(const char *pathname) ;
void xrename(const char *oldpath, const char *newpath) ;
int rename_or_warn(const char *oldpath, const char *newpath) ;
off_t xlseek(int fd, off_t offset, int whence) ;
int xmkstemp(char *template) ;
off_t fdlength(int fd) ;

uoff_t get_volume_size_in_bytes(int fd,
                const char *override,
                unsigned override_units,
                int extend);

void xpipe(int filedes[2]) ;

struct fd_pair { int rd; int wr; };




typedef int8_t socktype_t;
typedef int8_t family_t;
struct BUG_too_small {
 char BUG_socktype_t_too_small[(0
   | SOCK_STREAM
   | SOCK_DGRAM
   | SOCK_RDM
   | SOCK_SEQPACKET
   | SOCK_RAW
   ) <= 127 ? 1 : -1];
 char BUG_family_t_too_small[(0
   | 0
   | 2
   | 10
   | 1

   | 17


   | 16



   ) <= 127 ? 1 : -1];
};


void parse_datestr(const char *date_str, struct tm *ptm) ;
time_t validate_tm_time(const char *date_str, struct tm *ptm) ;


int xsocket(int domain, int type, int protocol) ;
void xbind(int sockfd, struct sockaddr *my_addr, socklen_t addrlen) ;
void xlisten(int s, int backlog) ;
void xconnect(int s, const struct sockaddr *s_addr, socklen_t addrlen) ;
ssize_t xsendto(int s, const void *buf, size_t len, const struct sockaddr *to,
    socklen_t tolen) ;






void setsockopt_reuseaddr(int fd) ;
int setsockopt_broadcast(int fd) ;
int setsockopt_bindtodevice(int fd, const char *iface) ;

unsigned bb_lookup_port(const char *port, const char *protocol, unsigned default_port) ;
typedef struct len_and_sockaddr {
 socklen_t len;
 union {
  struct sockaddr sa;
  struct sockaddr_in sin;



 } u;
} len_and_sockaddr;
enum {
 LSA_LEN_SIZE = __builtin_offsetof (len_and_sockaddr, u),
 LSA_SIZEOF_SA = sizeof(
  union {
   struct sockaddr sa;
   struct sockaddr_in sin;



  }
 )
};

int xsocket_type(len_and_sockaddr **lsap, int sock_type) ;


int xsocket_stream(len_and_sockaddr **lsap) ;





int create_and_bind_stream_or_die(const char *bindaddr, int port) ;
int create_and_bind_dgram_or_die(const char *bindaddr, int port) ;




int create_and_connect_stream_or_die(const char *peer, int port) ;

int xconnect_stream(const len_and_sockaddr *lsa) ;

len_and_sockaddr *get_sock_lsa(int fd) __attribute__ ((malloc));

len_and_sockaddr *get_peer_lsa(int fd) __attribute__ ((malloc));





len_and_sockaddr* host2sockaddr(const char *host, int port) __attribute__ ((malloc));

len_and_sockaddr* xhost2sockaddr(const char *host, int port) __attribute__ ((malloc));
len_and_sockaddr* xdotted2sockaddr(const char *host, int port) __attribute__ ((malloc));

void set_nport(len_and_sockaddr *lsa, unsigned port) ;

int get_nport(const struct sockaddr *sa) ;

char* xmalloc_sockaddr2host(const struct sockaddr *sa) __attribute__ ((malloc));

char* xmalloc_sockaddr2host_noport(const struct sockaddr *sa) __attribute__ ((malloc));

char* xmalloc_sockaddr2hostonly_noport(const struct sockaddr *sa) __attribute__ ((malloc));

char* xmalloc_sockaddr2dotted(const struct sockaddr *sa) __attribute__ ((malloc));
char* xmalloc_sockaddr2dotted_noport(const struct sockaddr *sa) __attribute__ ((malloc));


struct hostent *xgethostbyname(const char *name) ;




void socket_want_pktinfo(int fd) ;
ssize_t send_to_from(int fd, void *buf, size_t len, int flags,
  const struct sockaddr *to,
  const struct sockaddr *from,
  socklen_t tolen) ;
ssize_t recv_from_to(int fd, void *buf, size_t len, int flags,
  struct sockaddr *from,
  struct sockaddr *to,
  socklen_t sa_size) ;


char *xstrdup(const char *s) __attribute__ ((malloc));
char *xstrndup(const char *s, int n) __attribute__ ((malloc));
void overlapping_strcpy(char *dst, const char *src) ;
char *safe_strncpy(char *dst, const char *src, size_t size) ;
char *strncpy_IFNAMSIZ(char *dst, const char *src) ;


int bb_putchar(int ch) ;

int bb_putchar_stderr(char ch) ;
char *xasprintf(const char *format, ...) __attribute__ ((format(printf, 1, 2))) __attribute__ ((malloc));

typedef struct uni_stat_t {
 unsigned byte_count;
 unsigned unicode_count;
 unsigned unicode_width;
} uni_stat_t;


const char* printable_string(uni_stat_t *stats, const char *str);



enum { PRINTABLE_META = 0x100 };
void fputc_printable(int ch, FILE *file) ;



void *malloc_or_warn(size_t size) __attribute__ ((malloc));
void *xmalloc(size_t size) __attribute__ ((malloc));
void *xzalloc(size_t size) __attribute__ ((malloc));
void *xrealloc(void *old, size_t size) ;






void* xrealloc_vector_helper(void *vector, unsigned sizeof_and_shift, int idx) ;


extern ssize_t safe_read(int fd, void *buf, size_t count) ;
extern ssize_t nonblock_safe_read(int fd, void *buf, size_t count) ;


extern ssize_t full_read(int fd, void *buf, size_t count) ;
extern void xread(int fd, void *buf, size_t count) ;
extern unsigned char xread_char(int fd) ;
extern ssize_t read_close(int fd, void *buf, size_t maxsz) ;
extern ssize_t open_read_close(const char *filename, void *buf, size_t maxsz) ;



extern char *xmalloc_reads(int fd, char *pfx, size_t *maxsz_p) ;

extern void *xmalloc_read(int fd, size_t *maxsz_p) __attribute__ ((malloc));

extern void *xmalloc_open_read_close(const char *filename, size_t *maxsz_p) __attribute__ ((malloc));

extern int open_zipped(const char *fname) ;
extern void *xmalloc_open_zipped_read_close(const char *fname, size_t *maxsz_p) __attribute__ ((malloc));

extern void *xmalloc_xopen_read_close(const char *filename, size_t *maxsz_p) __attribute__ ((malloc));

extern ssize_t safe_write(int fd, const void *buf, size_t count) ;


extern ssize_t full_write(int fd, const void *buf, size_t count) ;
extern void xwrite(int fd, const void *buf, size_t count) ;
extern void xwrite_str(int fd, const char *str) ;
extern ssize_t full_write1_str(const char *str) ;
extern ssize_t full_write2_str(const char *str) ;
extern void xopen_xwrite_close(const char* file, const char *str) ;


extern void xclose(int fd) ;


extern void xprint_and_close_file(FILE *file) ;

extern char *bb_get_chunk_from_file(FILE *file, int *end) ;
extern char *bb_get_chunk_with_continuation(FILE *file, int *end, int *lineno) ;

extern char *xmalloc_fgets_str(FILE *file, const char *terminating_string) __attribute__ ((malloc));

extern char *xmalloc_fgets_str_len(FILE *file, const char *terminating_string, size_t *maxsz_p) __attribute__ ((malloc));

extern char *xmalloc_fgetline_str(FILE *file, const char *terminating_string) __attribute__ ((malloc));

extern char *xmalloc_fgets(FILE *file) __attribute__ ((malloc));

extern char *xmalloc_fgetline(FILE *file) __attribute__ ((malloc));



void die_if_ferror(FILE *file, const char *msg) ;
void die_if_ferror_stdout(void) ;
int fflush_all(void) ;
void fflush_stdout_and_exit(int retval) __attribute__ ((__noreturn__)) ;
int fclose_if_not_stdin(FILE *file) ;
FILE* xfopen(const char *filename, const char *mode) ;

FILE* fopen_or_warn(const char *filename, const char *mode) ;

FILE* xfopen_stdin(const char *filename) ;
FILE* fopen_or_warn_stdin(const char *filename) ;
FILE* fopen_for_read(const char *path) ;
FILE* xfopen_for_read(const char *path) ;
FILE* fopen_for_write(const char *path) ;
FILE* xfopen_for_write(const char *path) ;
FILE* xfdopen_for_read(int fd) ;
FILE* xfdopen_for_write(int fd) ;

int bb_pstrcmp(const void *a, const void *b) ;
void qsort_string_vector(char **sv, unsigned count) ;





int safe_poll(struct pollfd *ufds, nfds_t nfds, int timeout_ms) ;

char *safe_gethostname(void) ;
char *safe_getdomainname(void) ;


char* str_tolower(char *str) ;

char *utoa(unsigned n) ;
char *itoa(int n) ;

char *utoa_to_buf(unsigned n, char *buf, unsigned buflen) ;
char *itoa_to_buf(int n, char *buf, unsigned buflen) ;

void smart_ulltoa4(unsigned long long ul, char buf[4], const char *scale) ;
void smart_ulltoa5(unsigned long long ul, char buf[5], const char *scale) ;







const char *make_human_readable_str(unsigned long long size,
  unsigned long block_size, unsigned long display_unit) ;

char *bin2hex(char *buf, const char *cp, int count) ;

char* hex2bin(char *dst, const char *str, int count) ;


void generate_uuid(uint8_t *buf) ;


struct suffix_mult {
 char suffix[4];
 unsigned mult;
};








unsigned long long xstrtoull_range_sfx(const char *str, int b, unsigned long long l, unsigned long long u, const struct suffix_mult *sfx) ; unsigned long long xstrtoull_range(const char *str, int b, unsigned long long l, unsigned long long u) ; unsigned long long xstrtoull_sfx(const char *str, int b, const struct suffix_mult *sfx) ; unsigned long long xstrtoull(const char *str, int b) ; unsigned long long xatoull_range_sfx(const char *str, unsigned long long l, unsigned long long u, const struct suffix_mult *sfx) ; unsigned long long xatoull_range(const char *str, unsigned long long l, unsigned long long u) ; unsigned long long xatoull_sfx(const char *str, const struct suffix_mult *sfx) ; unsigned long long xatoull(const char *str) ; long long xstrtoll_range_sfx(const char *str, int b, long long l, long long u, const struct suffix_mult *sfx) ; long long xstrtoll_range(const char *str, int b, long long l, long long u) ; long long xstrtoll(const char *str, int b) ; long long xatoll_range_sfx(const char *str, long long l, long long u, const struct suffix_mult *sfx) ; long long xatoll_range(const char *str, long long l, long long u) ; long long xatoll_sfx(const char *str, const struct suffix_mult *sfx) ; long long xatoll(const char *str) ;

static __inline__ unsigned long xstrtoul_range_sfx(const char *str, int b, unsigned long l, unsigned long u, const struct suffix_mult *sfx) { return xstrtoull_range_sfx(str, b, l, u, sfx); } static __inline__ unsigned long xstrtoul_range(const char *str, int b, unsigned long l, unsigned long u) { return xstrtoull_range(str, b, l, u); } static __inline__ unsigned long xstrtoul_sfx(const char *str, int b, const struct suffix_mult *sfx) { return xstrtoull_sfx(str, b, sfx); } static __inline__ unsigned long xstrtoul(const char *str, int b) { return xstrtoull(str, b); } static __inline__ unsigned long xatoul_range_sfx(const char *str, unsigned long l, unsigned long u, const struct suffix_mult *sfx) { return xatoull_range_sfx(str, l, u, sfx); } static __inline__ unsigned long xatoul_range(const char *str, unsigned long l, unsigned long u) { return xatoull_range(str, l, u); } static __inline__ unsigned long xatoul_sfx(const char *str, const struct suffix_mult *sfx) { return xatoull_sfx(str, sfx); } static __inline__ unsigned long xatoul(const char *str) { return xatoull(str); } static __inline__ long xstrtol_range_sfx(const char *str, int b, long l, long u, const struct suffix_mult *sfx) { return xstrtoll_range_sfx(str, b, l, u, sfx); } static __inline__ long xstrtol_range(const char *str, int b, long l, long u) { return xstrtoll_range(str, b, l, u); } static __inline__ long xstrtol(const char *str, int b) { return xstrtoll(str, b); } static __inline__ long xatol_range_sfx(const char *str, long l, long u, const struct suffix_mult *sfx) { return xatoll_range_sfx(str, l, u, sfx); } static __inline__ long xatol_range(const char *str, long l, long u) { return xatoll_range(str, l, u); } static __inline__ long xatol_sfx(const char *str, const struct suffix_mult *sfx) { return xatoll_sfx(str, sfx); } static __inline__ long xatol(const char *str) { return xatoll(str); }







 unsigned int xstrtou_range_sfx(const char *str, int b, unsigned int l, unsigned int u, const struct suffix_mult *sfx) ; unsigned int xstrtou_range(const char *str, int b, unsigned int l, unsigned int u) ; unsigned int xstrtou_sfx(const char *str, int b, const struct suffix_mult *sfx) ; unsigned int xstrtou(const char *str, int b) ; unsigned int xatou_range_sfx(const char *str, unsigned int l, unsigned int u, const struct suffix_mult *sfx) ; unsigned int xatou_range(const char *str, unsigned int l, unsigned int u) ; unsigned int xatou_sfx(const char *str, const struct suffix_mult *sfx) ; unsigned int xatou(const char *str) ; int xstrtoi_range_sfx(const char *str, int b, int l, int u, const struct suffix_mult *sfx) ; int xstrtoi_range(const char *str, int b, int l, int u) ; int xstrtoi(const char *str, int b) ; int xatoi_range_sfx(const char *str, int l, int u, const struct suffix_mult *sfx) ; int xatoi_range(const char *str, int l, int u) ; int xatoi_sfx(const char *str, const struct suffix_mult *sfx) ; int xatoi(const char *str) ;



uint32_t BUG_xatou32_unimplemented(void);
static __inline__ uint32_t xatou32(const char *numstr)
{
 if ((2147483647 * 2U + 1U) == 0xffffffff)
  return xatou(numstr);
 if ((9223372036854775807L * 2UL + 1UL) == 0xffffffff)
  return xatoul(numstr);
 return BUG_xatou32_unimplemented();
}

unsigned long long bb_strtoull(const char *arg, char **endp, int base) ;
long long bb_strtoll(const char *arg, char **endp, int base) ;


static __inline__
unsigned long bb_strtoul(const char *arg, char **endp, int base)
{ return bb_strtoull(arg, endp, base); }
static __inline__
long bb_strtol(const char *arg, char **endp, int base)
{ return bb_strtoll(arg, endp, base); }

 unsigned bb_strtou(const char *arg, char **endp, int base) ;
int bb_strtoi(const char *arg, char **endp, int base) ;

uint32_t BUG_bb_strtou32_unimplemented(void);
static __inline__
uint32_t bb_strtou32(const char *arg, char **endp, int base)
{
 if (sizeof(uint32_t) == sizeof(unsigned))
  return bb_strtou(arg, endp, base);
 if (sizeof(uint32_t) == sizeof(unsigned long))
  return bb_strtoul(arg, endp, base);
 return BUG_bb_strtou32_unimplemented();
}



double bb_strtod(const char *arg, char **endp) ;








int xatoi_positive(const char *numstr) ;


uint16_t xatou16(const char *numstr) ;





long xuname2uid(const char *name) ;
long xgroup2gid(const char *name) ;

unsigned long get_ug_id(const char *s, long (*xname2id)(const char *)) ;

struct bb_uidgid_t {
 uid_t uid;
 gid_t gid;
};

int get_uidgid(struct bb_uidgid_t*, const char*, int numeric_ok) ;

void xget_uidgid(struct bb_uidgid_t*, const char*) ;

void parse_chown_usergroup_or_die(struct bb_uidgid_t *u, char *user_group) ;
struct passwd* xgetpwnam(const char *name) ;
struct group* xgetgrnam(const char *name) ;
struct passwd* xgetpwuid(uid_t uid) ;
struct group* xgetgrgid(gid_t gid) ;
char* xuid2uname(uid_t uid) ;
char* xgid2group(gid_t gid) ;
char* uid2uname(uid_t uid) ;
char* gid2group(gid_t gid) ;
char* uid2uname_utoa(long uid) ;
char* gid2group_utoa(long gid) ;

const char* get_cached_username(uid_t uid) ;
const char* get_cached_groupname(gid_t gid) ;
void clear_username_cache(void) ;

enum { USERNAME_MAX_SIZE = 16 - sizeof(int) };

int execable_file(const char *name) ;
char *find_execable(const char *filename, char **PATHp) ;
int exists_execable(const char *filename) ;

int BB_EXECVP_or_die(char **argv) __attribute__ ((__noreturn__)) ;

pid_t xfork(void) ;


pid_t spawn(char **argv) ;
pid_t xspawn(char **argv) ;

pid_t safe_waitpid(pid_t pid, int *wstat, int options) ;
pid_t wait_any_nohang(int *wstat) ;

int wait4pid(pid_t pid) ;

int spawn_and_wait(char **argv) ;
struct nofork_save_area {
 jmp_buf die_jmp;
 const char *applet_name;
 uint32_t option_mask32;
 int die_sleep;
 uint8_t xfunc_error_retval;
 smallint saved;
};
void save_nofork_data(struct nofork_save_area *save) ;
void restore_nofork_data(struct nofork_save_area *save) ;

int run_nofork_applet(int applet_no, char **argv) ;
int run_nofork_applet_prime(struct nofork_save_area *old, int applet_no, char **argv) ;

enum {
 DAEMON_CHDIR_ROOT = 1,
 DAEMON_DEVNULL_STDIO = 2,
 DAEMON_CLOSE_EXTRA_FDS = 4,
 DAEMON_ONLY_SANITIZE = 8,
};

  enum { re_execed = 0 };

void

bb_daemonize_or_rexec(int flags)




 ;
void bb_sanitize_stdio(void) ;

int sanitize_env_if_suid(void) ;


char* single_argv(char **argv) ;
extern const char *const bb_argv_dash[];
extern const char *opt_complementary;






extern uint32_t option_mask32;
extern uint32_t getopt32(char **argv, const char *applet_opts, ...) ;


typedef struct llist_t {
 char *data;
 struct llist_t *link;
} llist_t;
void llist_add_to(llist_t **old_head, void *data) ;
void llist_add_to_end(llist_t **list_head, void *data) ;
void *llist_pop(llist_t **elm) ;
void llist_unlink(llist_t **head, llist_t *elm) ;
void llist_free(llist_t *elm, void (*freeit)(void *data)) ;
llist_t *llist_rev(llist_t *list) ;
llist_t *llist_find_str(llist_t *first, const char *str) ;

enum { wrote_pidfile = 0 };



enum {
 LOGMODE_NONE = 0,
 LOGMODE_STDIO = (1 << 0),
 LOGMODE_SYSLOG = (1 << 1) *




0

,
 LOGMODE_BOTH = LOGMODE_SYSLOG + LOGMODE_STDIO,
};
extern const char *msg_eol;
extern smallint logmode;
extern int die_sleep;
extern uint8_t xfunc_error_retval;
extern jmp_buf die_jmp;
extern void xfunc_die(void) __attribute__ ((__noreturn__)) ;
extern void bb_show_usage(void) __attribute__ ((__noreturn__)) ;
extern void bb_error_msg(const char *s, ...) __attribute__ ((format (printf, 1, 2))) ;
extern void bb_error_msg_and_die(const char *s, ...) __attribute__ ((noreturn, format (printf, 1, 2))) ;
extern void bb_perror_msg(const char *s, ...) __attribute__ ((format (printf, 1, 2))) ;
extern void bb_simple_perror_msg(const char *s) ;
extern void bb_perror_msg_and_die(const char *s, ...) __attribute__ ((noreturn, format (printf, 1, 2))) ;
extern void bb_simple_perror_msg_and_die(const char *s) __attribute__ ((__noreturn__)) ;
extern void bb_herror_msg(const char *s, ...) __attribute__ ((format (printf, 1, 2))) ;
extern void bb_herror_msg_and_die(const char *s, ...) __attribute__ ((noreturn, format (printf, 1, 2))) ;
extern void bb_perror_nomsg_and_die(void) __attribute__ ((__noreturn__)) ;
extern void bb_perror_nomsg(void) ;
extern void bb_info_msg(const char *s, ...) __attribute__ ((format (printf, 1, 2))) ;
extern void bb_verror_msg(const char *s, va_list p, const char *strerr) ;

int bb_cat(char** argv);

int echo_main(int argc, char** argv)

;
int printf_main(int argc, char **argv)

;
int test_main(int argc, char **argv)

;
int kill_main(int argc, char **argv)

;

int chown_main(int argc, char **argv)

;

int ls_main(int argc, char **argv)

;

int gunzip_main(int argc, char **argv)






;
int bunzip2_main(int argc, char **argv)






;





int create_icmp_socket(void) ;
int create_icmp6_socket(void) ;


struct aftype {
 const char *name;
 const char *title;
 int af;
 int alen;
 char* (*print)(unsigned char *);
 const char* (*sprint)(struct sockaddr *, int numeric);
 int (*input)( const char *bufp, struct sockaddr *);
 void (*herror)(char *text);
 int (*rprint)(int options);
 int (*rinput)(int typ, int ext, char **argv);

 int (*getmask)(char *src, struct sockaddr *mask, char *name);
};

struct hwtype {
 const char *name;
 const char *title;
 int type;
 int alen;
 char* (*print)(unsigned char *);
 int (*input)(const char *, struct sockaddr *);
 int (*activate)(int fd);
 int suppress_null_addr;
};
extern smallint interface_opt_a;
int display_interfaces(char *ifname) ;






const struct aftype *get_aftype(const char *name) ;
const struct hwtype *get_hwtype(const char *name) ;
const struct hwtype *get_hwntype(int type) ;



extern int find_applet_by_name(const char *name) ;

extern void run_applet_and_exit(const char *name, char **argv) ;
extern void run_applet_no_and_exit(int a, char **argv) __attribute__ ((__noreturn__)) ;


extern int match_fstype(const struct mntent *mt, const char *fstypes) ;
extern struct mntent *find_mount_point(const char *name, int subdir_too) ;

extern void erase_mtab(const char * name) ;
extern unsigned int tty_baud_to_value(speed_t speed) ;
extern speed_t tty_value_to_baud(unsigned int value) ;






extern int get_linux_version_code(void) ;

extern char *query_loop(const char *device) ;
extern int del_loop(const char *device) ;



extern int set_loop(char **devname, const char *file, unsigned long long offset) ;


char *bb_ask_stdin(const char * prompt) ;

char *bb_ask(const int fd, int timeout, const char * prompt) ;
int bb_ask_confirmation(void) ;

int bb_parse_mode(const char* s, mode_t* theMode) ;




enum {
 PARSE_COLLAPSE = 0x00010000,
 PARSE_TRIM = 0x00020000,

 PARSE_GREEDY = 0x00040000,
 PARSE_MIN_DIE = 0x00100000,

 PARSE_KEEP_COPY = 0x00200000 *




0

,






 PARSE_NORMAL = PARSE_COLLAPSE | PARSE_TRIM | PARSE_GREEDY,
};
typedef struct parser_t {
 FILE *fp;
 char *line;
 char *data;
 int lineno;
} parser_t;
parser_t* config_open(const char *filename) ;
parser_t* config_open2(const char *filename, FILE* (*fopen_func)(const char *path)) ;

int config_read(parser_t *parser, char **tokens, unsigned flags, const char *delims) ;


void config_close(parser_t *parser) ;





char *concat_path_file(const char *path, const char *filename) ;
char *concat_subpath_file(const char *path, const char *filename) ;
const char *bb_basename(const char *name) ;

char *last_char_is(const char *s, int c) ;


int bb_make_directory(char *path, long mode, int flags) ;

int get_signum(const char *name) ;
const char *get_signame(int number) ;
void print_signames(void) ;

char *bb_simplify_path(const char *path) ;

char *bb_simplify_abs_path_inplace(char *path) ;


extern void bb_do_delay(int seconds) ;
extern void change_identity(const struct passwd *pw) ;
extern void run_shell(const char *shell, int loginshell, const char *command, const char **additional_args) __attribute__ ((__noreturn__)) ;

extern void selinux_or_die(void) ;

extern void setup_environment(const char *shell, int flags, const struct passwd *pw) ;
extern int correct_password(const struct passwd *pw) ;




extern char *

pw_encrypt(const char *clear, const char *salt)




 ;
extern int obscure(const char *old, const char *newval, const struct passwd *pwdp) ;







extern int crypt_make_salt(char *p, int cnt, int rnd) ;






extern int

update_passwd(const char *filename,
 const char *username,
 const char *data)







 ;

int index_in_str_array(const char *const string_array[], const char *key) ;
int index_in_strings(const char *strings, const char *key) ;
int index_in_substr_array(const char *const string_array[], const char *key) ;
int index_in_substrings(const char *strings, const char *key) ;
const char *nth_string(const char *strings, int n) ;

extern void print_login_issue(const char *issue_file, const char *tty) ;
extern void print_login_prompt(void) ;

char *xmalloc_ttyname(int fd) __attribute__ ((malloc));

int get_terminal_width_height(int fd, unsigned *width, unsigned *height) ;

int tcsetattr_stdin_TCSANOW(const struct termios *tp) ;


int ioctl_or_perror(int fd, unsigned request, void *argp, const char *fmt,...) __attribute__ ((format (printf, 4, 5))) ;
int ioctl_or_perror_and_die(int fd, unsigned request, void *argp, const char *fmt,...) __attribute__ ((format (printf, 4, 5))) ;







int bb_ioctl_or_warn(int fd, unsigned request, void *argp) ;
int bb_xioctl(int fd, unsigned request, void *argp) ;



char *is_in_ino_dev_hashtable(const struct stat *statbuf) ;
void add_to_ino_dev_hashtable(const struct stat *statbuf, const char *name) ;
void reset_ino_dev_hashtable(void) ;


unsigned long long bb_makedev(unsigned int major, unsigned int minor) ;







enum {
 KEYCODE_UP = -2,
 KEYCODE_DOWN = -3,
 KEYCODE_RIGHT = -4,
 KEYCODE_LEFT = -5,
 KEYCODE_HOME = -6,
 KEYCODE_END = -7,
 KEYCODE_INSERT = -8,
 KEYCODE_DELETE = -9,
 KEYCODE_PAGEUP = -10,
 KEYCODE_PAGEDOWN = -11,

 KEYCODE_CTRL_UP = KEYCODE_UP & ~0x40,
 KEYCODE_CTRL_DOWN = KEYCODE_DOWN & ~0x40,
 KEYCODE_CTRL_RIGHT = KEYCODE_RIGHT & ~0x40,
 KEYCODE_CTRL_LEFT = KEYCODE_LEFT & ~0x40,

 KEYCODE_CURSOR_POS = -0x100,




 KEYCODE_BUFFER_SIZE = 16
};

int64_t read_key(int fd, char *buffer, int timeout) ;
void read_key_ungets(char *buffer, const char *str, unsigned len) ;

int read_line_input(const char* prompt, char* command, int maxsize) ;







enum { COMM_LEN = 16 };


struct smaprec {
 unsigned long mapped_rw;
 unsigned long mapped_ro;
 unsigned long shared_clean;
 unsigned long shared_dirty;
 unsigned long private_clean;
 unsigned long private_dirty;
 unsigned long stack;
 unsigned long smap_pss, smap_swap;
 unsigned long smap_size;
 unsigned long smap_start;
 char smap_mode[5];
 char *smap_name;
};





int

procps_read_smaps(pid_t pid, struct smaprec *total)





;

typedef struct procps_status_t {
 DIR *dir;

 uint8_t shift_pages_to_bytes;
 uint8_t shift_pages_to_kb;

 uint16_t argv_len;
 char *argv0;
 char *exe;

 unsigned long vsz, rss;
 unsigned long stime, utime;
 unsigned long start_time;
 unsigned pid;
 unsigned ppid;
 unsigned pgid;
 unsigned sid;
 unsigned uid;
 unsigned gid;





 unsigned tty_major,tty_minor;



 char state[4];



 char comm[COMM_LEN];




} procps_status_t;

enum {
 PSSCAN_PID = 1 << 0,
 PSSCAN_PPID = 1 << 1,
 PSSCAN_PGID = 1 << 2,
 PSSCAN_SID = 1 << 3,
 PSSCAN_UIDGID = 1 << 4,
 PSSCAN_COMM = 1 << 5,

 PSSCAN_ARGV0 = 1 << 7,
 PSSCAN_EXE = 1 << 8,
 PSSCAN_STATE = 1 << 9,
 PSSCAN_VSZ = 1 << 10,
 PSSCAN_RSS = 1 << 11,
 PSSCAN_STIME = 1 << 12,
 PSSCAN_UTIME = 1 << 13,
 PSSCAN_TTY = 1 << 14,
 PSSCAN_SMAPS = (1 << 15) *




0

,


 PSSCAN_ARGVN = (1 << 16) * (




0


    ||




0

 ||




0


    ||




0


    ||




0


    ),
 PSSCAN_CONTEXT = (1 << 17) *




0

,
 PSSCAN_START_TIME = 1 << 18,
 PSSCAN_CPU = (1 << 19) *




0

,
 PSSCAN_NICE = (1 << 20) *




0

,
 PSSCAN_RUIDGID = (1 << 21) *




0

,
 PSSCAN_TASKS = (1 << 22) *




0

,

 PSSCAN_STAT = PSSCAN_PPID | PSSCAN_PGID | PSSCAN_SID
                 | PSSCAN_COMM | PSSCAN_STATE
                 | PSSCAN_VSZ | PSSCAN_RSS
                 | PSSCAN_STIME | PSSCAN_UTIME | PSSCAN_START_TIME
                 | PSSCAN_TTY | PSSCAN_NICE
                 | PSSCAN_CPU
};

void free_procps_scan(procps_status_t* sp) ;
procps_status_t* procps_scan(procps_status_t* sp, int flags) ;


void read_cmdline(char *buf, int size, unsigned pid, const char *comm) ;
pid_t *find_pid_by_name(const char* procName) ;
pid_t *pidlist_reverse(pid_t *pidList) ;
int starts_with_cpu(const char *str) ;
unsigned get_cpu_count(void) ;


extern const char bb_uuenc_tbl_base64[];
extern const char bb_uuenc_tbl_std[];
void bb_uuencode(char *store, const void *s, int length, const char *tbl) ;
enum {
 BASE64_FLAG_UU_STOP = 0x100,

 BASE64_FLAG_NO_STOP_CHAR = 0x80,
};
void read_base64(FILE *src_stream, FILE *dst_stream, int flags);

typedef struct md5_ctx_t {
 uint8_t wbuffer[64];
 void (*process_block)(struct md5_ctx_t*) ;
 uint64_t total64;
 uint32_t hash[8];
} md5_ctx_t;
typedef struct md5_ctx_t sha1_ctx_t;
typedef struct md5_ctx_t sha256_ctx_t;
typedef struct sha512_ctx_t {
 uint64_t total64[2];
 uint64_t hash[8];
 uint8_t wbuffer[128];
} sha512_ctx_t;
void md5_begin(md5_ctx_t *ctx) ;
void md5_hash(md5_ctx_t *ctx, const void *data, size_t length) ;
void md5_end(md5_ctx_t *ctx, void *resbuf) ;
void sha1_begin(sha1_ctx_t *ctx) ;

void sha1_end(sha1_ctx_t *ctx, void *resbuf) ;
void sha256_begin(sha256_ctx_t *ctx) ;


void sha512_begin(sha512_ctx_t *ctx) ;
void sha512_hash(sha512_ctx_t *ctx, const void *buffer, size_t len) ;
void sha512_end(sha512_ctx_t *ctx, void *resbuf) ;

extern uint32_t *global_crc32_table;
uint32_t *crc32_filltable(uint32_t *tbl256, int endian) ;
uint32_t crc32_block_endian1(uint32_t val, const void *buf, unsigned len, uint32_t *crc_table) ;
uint32_t crc32_block_endian0(uint32_t val, const void *buf, unsigned len, uint32_t *crc_table) ;

typedef struct masks_labels_t {
 const char *labels;
 const int masks[];
} masks_labels_t;
int print_flags_separated(const int *masks, const char *labels,
  int flags, const char *separator) ;
int print_flags(const masks_labels_t *ml, int flags) ;

typedef struct bb_progress_t {
 off_t lastsize;
 unsigned lastupdate_sec;
 unsigned start_sec;
 smallint inited;
} bb_progress_t;

void bb_progress_init(bb_progress_t *p) ;
void bb_progress_update(bb_progress_t *p, const char *curfile,
   off_t beg_range, off_t transferred,
   off_t totalsize) ;

extern const char *applet_name;

extern const char bb_banner[];
extern const char bb_msg_memory_exhausted[];
extern const char bb_msg_invalid_date[];


extern const char bb_msg_unknown[];
extern const char bb_msg_can_not_create_raw_socket[];
extern const char bb_msg_perm_denied_are_you_root[];
extern const char bb_msg_you_must_be_root[];
extern const char bb_msg_requires_arg[];
extern const char bb_msg_invalid_arg[];
extern const char bb_msg_standard_input[];
extern const char bb_msg_standard_output[];


extern const char bb_hexdigits_upcase[];

extern const char bb_path_wtmp_file[];

extern const char bb_busybox_exec_path[];


extern const char bb_PATH_root_path[];



extern const int const_int_0;
extern const int const_int_1;



enum { COMMON_BUFSIZE = (8192 >= 256*sizeof(void*) ? 8192+1 : 256*sizeof(void*)) };
extern char bb_common_bufsiz1[COMMON_BUFSIZE];


struct globals;



extern struct globals *const ptr_to_globals;

extern const char bb_default_login_shell[];

static __inline__ int bb_ascii_isalnum(unsigned char a)
{
 unsigned char b = a - '0';
 if (b <= 9)
  return (b <= 9);
 b = (a|0x20) - 'a';
 return b <= 'z' - 'a';
}

static __inline__ int bb_ascii_isxdigit(unsigned char a)
{
 unsigned char b = a - '0';
 if (b <= 9)
  return (b <= 9);
 b = (a|0x20) - 'a';
 return b <= 'f' - 'a';
}

static __inline__ unsigned char bb_ascii_toupper(unsigned char a)
{
 unsigned char b = a - 'a';
 if (b <= ('z' - 'a'))
  a -= 'a' - 'A';
 return a;
}

static __inline__ unsigned char bb_ascii_tolower(unsigned char a)
{
 unsigned char b = a - 'A';
 if (b <= ('Z' - 'A'))
  a += 'a' - 'A';
 return a;
}


















enum {

  COMPRESS_MAGIC = 0x9d1f,
 GZIP_MAGIC = 0x8b1f,
 BZIP2_MAGIC = 'Z' * 256 + 'B',
 XZ_MAGIC1 = '7' * 256 + 0xfd,
 XZ_MAGIC2 = ((0 * 256 + 'Z') * 256 + 'X') * 256 + 'z',
 XZ_MAGIC1a = (('X' * 256 + 'z') * 256 + '7') * 256 + 0xfd,
 XZ_MAGIC2a = 0 * 256 + 'Z',

};

typedef struct file_header_t {
 char *name;
 char *link_target;




 off_t size;
 uid_t uid;
 gid_t gid;
 mode_t mode;
 time_t mtime;
 dev_t device;
} file_header_t;

struct hardlinks_t;

typedef struct archive_handle_t {

 unsigned ah_flags;


 int src_fd;


 char (*filter)(struct archive_handle_t *);

 llist_t *accept;

 llist_t *reject;

 llist_t *passed;


 file_header_t *file_header;


 void (*action_header)(const file_header_t *);


 void (*action_data)(struct archive_handle_t *);


 void (*seek)(int fd, off_t amount);


 off_t offset;

} archive_handle_t;

typedef struct tar_header_t {
 char name[100];
 char mode[8];
 char uid[8];
 char gid[8];
 char size[12];
 char mtime[12];
 char chksum[8];
 char typeflag;
 char linkname[100];





 char magic[8];
 char uname[32];
 char gname[32];
 char devmajor[8];
 char devminor[8];
 char prefix[155];
 char padding[12];
} tar_header_t;
struct BUG_tar_header {
 char c[sizeof(tar_header_t) == 512 ? 1 : -1];
};





typedef struct unpack_info_t {
 time_t mtime;
} unpack_info_t;

extern archive_handle_t *init_handle(void) ;

extern char filter_accept_all(archive_handle_t *archive_handle) ;
extern char filter_accept_list(archive_handle_t *archive_handle) ;
extern char filter_accept_list_reassign(archive_handle_t *archive_handle) ;
extern char filter_accept_reject_list(archive_handle_t *archive_handle) ;

extern void unpack_ar_archive(archive_handle_t *ar_archive) ;

extern void data_skip(archive_handle_t *archive_handle) ;
extern void data_extract_all(archive_handle_t *archive_handle) ;
extern void data_extract_to_stdout(archive_handle_t *archive_handle) ;
extern void data_extract_to_command(archive_handle_t *archive_handle) ;

extern void header_skip(const file_header_t *file_header) ;
extern void header_list(const file_header_t *file_header) ;
extern void header_verbose_list(const file_header_t *file_header) ;

extern char get_header_ar(archive_handle_t *archive_handle) ;
extern char get_header_cpio(archive_handle_t *archive_handle) ;
extern char get_header_tar(archive_handle_t *archive_handle) ;
extern char get_header_tar_gz(archive_handle_t *archive_handle) ;
extern char get_header_tar_bz2(archive_handle_t *archive_handle) ;
extern char get_header_tar_lzma(archive_handle_t *archive_handle) ;

extern void seek_by_jump(int fd, off_t amount) ;
extern void seek_by_read(int fd, off_t amount) ;

extern void data_align(archive_handle_t *archive_handle, unsigned boundary) ;
extern const llist_t *find_list_entry(const llist_t *list, const char *filename) ;
extern const llist_t *find_list_entry2(const llist_t *list, const char *filename) ;


typedef struct bunzip_data bunzip_data;
int start_bunzip(bunzip_data **bdp, int in_fd, const void *inbuf, int len) ;


int read_bunzip(bunzip_data *bd, char *outbuf, int len) ;
void dealloc_bunzip(bunzip_data *bd) ;

typedef struct inflate_unzip_result {
 off_t bytes_out;
 uint32_t crc;
} inflate_unzip_result;

 int inflate_unzip(inflate_unzip_result *res, off_t compr_size, int src_fd, int dst_fd) ;

 int unpack_xz_stream(int src_fd, int dst_fd) ;

 int unpack_lzma_stream(int src_fd, int dst_fd) ;

 int unpack_bz2_stream(int src_fd, int dst_fd) ;







 int unpack_gz_stream(int src_fd, int dst_fd) ;







 int unpack_gz_stream_with_info(int src_fd, int dst_fd, unpack_info_t *info) ;







 int unpack_Z_stream(int src_fd, int dst_fd) ;

 int unpack_bz2_stream_prime(int src_fd, int dst_fd) ;

char* append_ext(char *filename, const char *expected_ext) ;
int bbunpack(char **argv,







 int (*unpacker)(unpack_info_t *info),
     char* (*make_new_name)(char *filename, const char *expected_ext),
     const char *expected_ext
) ;


void open_transformer(int fd,







 int (*transformer)(int src_fd, int dst_fd)) ;


























struct ar_header {
 char name[16];
 char date[12];
 char uid[6];
 char gid[6];
 char mode[8];
 char size[10];
 char magic[2];
};












static void header_verbose_list_ar(const file_header_t *file_header)
{
 const char *mode = bb_mode_string(file_header->mode);
 char *mtime;

 mtime = ctime(&file_header->mtime);
 mtime[16] = ' ';
 memmove(&mtime[17], &mtime[20], 4);
 mtime[21] = '\0';
 printf("%s %u/%u%7"




"l"

"u %s %s\n", &mode[1],
   (int)file_header->uid, (int)file_header->gid,
   file_header->size,
   &mtime[4], file_header->name
 );
}

int ar_main(int argc, char **argv)






;
int ar_main(int argc __attribute__ ((__unused__)), char **argv)
{
 archive_handle_t *archive_handle;
 unsigned opt, t;

 archive_handle = init_handle();




 opt_complementary = "--:-1:p:t:x"






;
 opt = getopt32(argv, "voc""ptx"






);
 argv += optind;

 t = opt / (1 << 3);
 if (t & (t-1))
  bb_show_usage();

 if (opt & (1 << 3)) {
  archive_handle->action_data = data_extract_to_stdout;
 }
 if (opt & (1 << 4)) {
  archive_handle->action_header = header_list;
 }
 if (opt & (1 << 5)) {
  archive_handle->action_data = data_extract_all;
 }
 if (opt & (1 << 1)) {
  archive_handle->ah_flags |= (1 << 0);
 }
 if (opt & (1 << 0)) {
  archive_handle->action_header = header_verbose_list_ar;
 }



 archive_handle->src_fd = xopen(*argv++,
   (opt & (1 << 6))
    ? 02 | 0100
    : 00
 );

 if (*argv)
  archive_handle->filter = filter_accept_list;
 while (*argv) {
  llist_add_to_end(&archive_handle->accept, *argv++);
 }





 unpack_ar_archive(archive_handle);

 return 0;
}
