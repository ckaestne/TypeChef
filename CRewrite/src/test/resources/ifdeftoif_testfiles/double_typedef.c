#if definedEx(CONFIG_LFS)
typedef unsigned long long uoff_t;
#endif


#if !definedEx(CONFIG_LFS)
typedef unsigned long uoff_t;
#endif

uoff_t get_volume_size_in_bytes(int fd, const char *override, unsigned override_units, int extend);

void main() {
}