#ifdef A
#define __force __attribute__((force)) 
#endif

#ifdef B
#define if(cond...) __trace_if( (cond , ## __VA_ARGS__) ) 
#endif


#if defined C
#define ___constant_swab16(x) ((__u16)( (((__u16)(x) & (__u16)0x00ffU) << 8) | (((__u16)(x) & (__u16)0xff00U) >> 8)))
#endif 


#if defined D
#define __swab16(x)				\
	(__builtin_constant_p((__u16)(x)) ?	\
	___constant_swab16(x) :			\
	__fswab16(x))
#endif 



#if defined E
#define __be16_to_cpu(x) __swab16((__force __u16)(__be16)(x))
#endif 


#if defined F
#define be16_to_cpu __be16_to_cpu 
#endif


static inline void be16_add_cpu(__be16 *var, u16 val)
{
	*var = cpu_to_be16(be16_to_cpu(*var) + val);
	if (1) {}
}