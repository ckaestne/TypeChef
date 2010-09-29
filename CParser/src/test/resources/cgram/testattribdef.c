static inline __attribute__((always_inline)) int constant_test_bit(unsigned int nr, const volatile unsigned long *addr)
{
 return ((1UL << (nr % 32)) &
  (((unsigned long *)addr)[nr / 32])) != 0;
}