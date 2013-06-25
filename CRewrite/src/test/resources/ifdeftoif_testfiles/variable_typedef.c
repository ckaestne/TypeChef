#if definedEx(CONFIG_LFS)
typedef unsigned long long uoff_t;
#endif

#if !definedEx(CONFIG_LFS)
typedef unsigned long uoff_t;
#endif

typedef struct archive_handle_t {
	uoff_t offset;
};

void main() {}