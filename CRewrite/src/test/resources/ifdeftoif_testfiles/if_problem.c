typedef long unsigned int size_t;
extern void bb_error_msg_and_die(const char *s, ...) __attribute__((noreturn, format (printf, 1, 2)));
#if definedEx(CONFIG_FEATURE_LOADFONT_PSF2)
struct psf2_header {
  unsigned char magic[4];
  unsigned int version;
  unsigned int headersize;
  unsigned int flags;
  unsigned int length;
  unsigned int charsize;
  unsigned int height;
  unsigned int width;
} ;
#endif
#if definedEx(CONFIG_FEATURE_LOADFONT_PSF2)
enum  {
  PSF2_MAGIC0 = 0x72,
  PSF2_MAGIC1 = 0xb5,
  PSF2_MAGIC2 = 0x4a,
  PSF2_MAGIC3 = 0x86,
  PSF2_HAS_UNICODE_TABLE = 0x01,
  PSF2_MAXVERSION = 0,
  PSF2_STARTSEQ = 0xfe,
  PSF2_SEPARATOR = 0xff
} ;
#endif
enum  {
  PSF1_MAGIC0 = 0x36,
  PSF1_MAGIC1 = 0x04,
  PSF1_MODE512 = 0x01,
  PSF1_MODEHASTAB = 0x02,
  PSF1_MODEHASSEQ = 0x04,
  PSF1_MAXMODE = 0x05,
  PSF1_STARTSEQ = 0xfffe,
  PSF1_SEPARATOR = 0xffff
} ;
struct psf1_header {
  unsigned char magic[2];
  unsigned char mode;
  unsigned char charsize;
} ;
static void do_load(int fd, unsigned char *buffer, size_t len)  {
  int height;
  int width =  8;
  int charsize;
  int fontsize =  256;
  int has_table =  0;
  unsigned char *font =  buffer;
  unsigned char *table;
  
  #if definedEx(CONFIG_FEATURE_LOADFONT_RAW)
  if (((len >= sizeof(struct psf1_header  )) && ((((struct psf1_header  *) buffer)->magic[0] == PSF1_MAGIC0) && (((struct psf1_header  *) buffer)->magic[1] == PSF1_MAGIC1)))) {
    if ((((struct psf1_header  *) buffer)->mode > PSF1_MAXMODE)) bb_error_msg_and_die("unsupported psf file mode");  
    if ((((struct psf1_header  *) buffer)->mode & PSF1_MODE512)) (fontsize = 512);  
    if ((((struct psf1_header  *) buffer)->mode & PSF1_MODEHASTAB)) (has_table = 1);  
    (height = (charsize = ((struct psf1_header  *) buffer)->charsize));
    (font += sizeof(struct psf1_header  ));
  } 
  #if (definedEx(CONFIG_FEATURE_LOADFONT_PSF2) && definedEx(CONFIG_FEATURE_LOADFONT_RAW))
  
  else if (((len >= sizeof(struct psf2_header  )) && ((((struct psf2_header  *) buffer)->magic[0] == PSF2_MAGIC0) && (((struct psf2_header  *) buffer)->magic[1] == PSF2_MAGIC1) && (((struct psf2_header  *) buffer)->magic[2] == PSF2_MAGIC2) && (((struct psf2_header  *) buffer)->magic[3] == PSF2_MAGIC3)))) {
    if ((((struct psf2_header  *) buffer)->version > PSF2_MAXVERSION)) bb_error_msg_and_die("unsupported psf file version");  
    (fontsize = ((struct psf2_header  *) buffer)->length);
    if ((((struct psf2_header  *) buffer)->flags & PSF2_HAS_UNICODE_TABLE)) (has_table = 2);  
    (charsize = ((struct psf2_header  *) buffer)->charsize);
    (height = ((struct psf2_header  *) buffer)->height);
    (width = ((struct psf2_header  *) buffer)->width);
    (font += ((struct psf2_header  *) buffer)->headersize);
  }
  #endif
  
  
  else if ((len == 9780)) {
    (charsize = (height = 16));
    (font += 40);
  }
  
  else if (((len & 0377) == 0)) {
    (charsize = (height = (len / 256)));
  } 
  else {
    bb_error_msg_and_die("input file: bad length or unsupported font type");
  }
  #endif
  
  
  #if (!definedEx(CONFIG_FEATURE_LOADFONT_RAW) && definedEx(CONFIG_FEATURE_LOADFONT_PSF2))
  if (((len >= sizeof(struct psf1_header  )) && ((((struct psf1_header  *) buffer)->magic[0] == PSF1_MAGIC0) && (((struct psf1_header  *) buffer)->magic[1] == PSF1_MAGIC1)))) {
    if ((((struct psf1_header  *) buffer)->mode > PSF1_MAXMODE)) bb_error_msg_and_die("unsupported psf file mode");  
    if ((((struct psf1_header  *) buffer)->mode & PSF1_MODE512)) (fontsize = 512);  
    if ((((struct psf1_header  *) buffer)->mode & PSF1_MODEHASTAB)) (has_table = 1);  
    (height = (charsize = ((struct psf1_header  *) buffer)->charsize));
    (font += sizeof(struct psf1_header  ));
  } 
  else if (((len >= sizeof(struct psf2_header  )) && ((((struct psf2_header  *) buffer)->magic[0] == PSF2_MAGIC0) && (((struct psf2_header  *) buffer)->magic[1] == PSF2_MAGIC1) && (((struct psf2_header  *) buffer)->magic[2] == PSF2_MAGIC2) && (((struct psf2_header  *) buffer)->magic[3] == PSF2_MAGIC3)))) {
    if ((((struct psf2_header  *) buffer)->version > PSF2_MAXVERSION)) bb_error_msg_and_die("unsupported psf file version");  
    (fontsize = ((struct psf2_header  *) buffer)->length);
    if ((((struct psf2_header  *) buffer)->flags & PSF2_HAS_UNICODE_TABLE)) (has_table = 2);  
    (charsize = ((struct psf2_header  *) buffer)->charsize);
    (height = ((struct psf2_header  *) buffer)->height);
    (width = ((struct psf2_header  *) buffer)->width);
    (font += ((struct psf2_header  *) buffer)->headersize);
  } 
  else {
    bb_error_msg_and_die("input file: bad length or unsupported font type");
  }
  #endif
  
  
  #if (!definedEx(CONFIG_FEATURE_LOADFONT_RAW) && !definedEx(CONFIG_FEATURE_LOADFONT_PSF2))
  if (((len >= sizeof(struct psf1_header  )) && ((((struct psf1_header  *) buffer)->magic[0] == PSF1_MAGIC0) && (((struct psf1_header  *) buffer)->magic[1] == PSF1_MAGIC1)))) {
    if ((((struct psf1_header  *) buffer)->mode > PSF1_MAXMODE)) bb_error_msg_and_die("unsupported psf file mode");  
    if ((((struct psf1_header  *) buffer)->mode & PSF1_MODE512)) (fontsize = 512);  
    if ((((struct psf1_header  *) buffer)->mode & PSF1_MODEHASTAB)) (has_table = 1);  
    (height = (charsize = ((struct psf1_header  *) buffer)->charsize));
    (font += sizeof(struct psf1_header  ));
  }  
  else {
    bb_error_msg_and_die("input file: bad length or unsupported font type");
  }
  #endif
  
  (table = (font + (fontsize * charsize)));
  (buffer += len);
  if (((table > buffer) || ((! has_table) && (table != buffer)))) bb_error_msg_and_die("input file: bad length");
  if (has_table) 
  #if !definedEx(CONFIG_FEATURE_LOADFONT_PSF2)
  do_loadtable(fd , table , (buffer - table) , fontsize);
  #else
  do_loadtable(fd , table , (buffer - table) , fontsize , (has_table - 1));
  #endif
    
}

void main() {}