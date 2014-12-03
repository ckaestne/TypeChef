#define BLK_MAX_CDB 12
#define MAX_COMMAND_SIZE 12

#if (10 > BLK_MAX_CDB)
   never
#endif

#if (BLK_MAX_CDB < 10)
  never2
#endif

#if (16 < BLK_MAX_CDB)
  never3
#endif

#if (MAX_COMMAND_SIZE > BLK_MAX_CDB)
  never5
#endif


#if (0 > BLK_MAX_CDB2)
   never
#endif
