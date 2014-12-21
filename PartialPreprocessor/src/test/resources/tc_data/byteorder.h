#ifdef __CHECKER__
#define __force __attribute__((force))
#else 
# define __force
#endif




#ifdef A
#define ___constant_swab16(x) ((__u16)( (((__u16)(x) & (__u16)0x00ffU) << 8) | (((__u16)(x) & (__u16)0xff00U) >> 8)))
#endif 


#ifdef B
#define __swab16(x)				\
	(__builtin_constant_p((__u16)(x)) ?	\
	___constant_swab16(x) :			\
	__fswab16(x))
#endif 



#ifdef C
#define __be16_to_cpu(x) __swab16((__force __u16)(__be16)(x))
#endif 


#ifdef D
#define be16_to_cpu __be16_to_cpu 
#endif


#ifdef E
#define cpu_to_be16  __cpu_to_be16
#endif 

#ifdef F
#define __cpu_to_be16(x) ((__force __be16)__swab16((x)))
#endif 



static inline void be16_add_cpu(__be16 *var, u16 val)
{
	*var = cpu_to_be16(be16_to_cpu(*var) + val);
}