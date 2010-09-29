#ifdef __CHECKER__
#define __force __attribute__((force))
#else 
# define __force
#endif




#if (!(defined(__LINUX_PREEMPT_H)) && !(defined(_ASM_GENERIC_BITOPS_EXT2_NON_ATOMIC_H_)) && !(defined(_ASM_GENERIC_BITOPS_LE_H_)) && defined(__KERNEL__) && !(defined(_LINUX_BYTEORDER_LITTLE_ENDIAN_H)) && !(defined(_LINUX_MMZONE_H)) && !(defined(__LINUX_GFP_H)) && !(defined(_ASM_X86_BYTEORDER_H)) && !(defined(__ASSEMBLY__)) && !(defined(_ASM_X86_BITOPS_H)) && !(defined(__LINUX_SPINLOCK_H)) && !(defined(_LINUX_BITOPS_H)) && !(defined(_LINUX_SWAB_H)) && !(defined(_LINUX_THREAD_INFO_H)) && !(defined(_LINUX_SLAB_H)) && !(defined(__GENERATING_BOUNDS_H)))
#define ___constant_swab16(x) ((__u16)( (((__u16)(x) & (__u16)0x00ffU) << 8) | (((__u16)(x) & (__u16)0xff00U) >> 8)))
#endif 


#if (!(defined(__LINUX_PREEMPT_H)) && !(defined(_ASM_GENERIC_BITOPS_EXT2_NON_ATOMIC_H_)) && !(defined(_ASM_GENERIC_BITOPS_LE_H_)) && defined(__KERNEL__) && !(defined(_LINUX_BYTEORDER_LITTLE_ENDIAN_H)) && !(defined(_LINUX_MMZONE_H)) && !(defined(__LINUX_GFP_H)) && !(defined(_ASM_X86_BYTEORDER_H)) && !(defined(__ASSEMBLY__)) && !(defined(_ASM_X86_BITOPS_H)) && !(defined(__LINUX_SPINLOCK_H)) && !(defined(_LINUX_BITOPS_H)) && !(defined(_LINUX_SWAB_H)) && !(defined(_LINUX_THREAD_INFO_H)) && !(defined(_LINUX_SLAB_H)) && !(defined(__GENERATING_BOUNDS_H)))
#define __swab16(x)				\
	(__builtin_constant_p((__u16)(x)) ?	\
	___constant_swab16(x) :			\
	__fswab16(x))
#endif 



#if (!(defined(__LINUX_PREEMPT_H)) && !(defined(_ASM_GENERIC_BITOPS_EXT2_NON_ATOMIC_H_)) && !(defined(_ASM_GENERIC_BITOPS_LE_H_)) && defined(__KERNEL__) && !(defined(_LINUX_BYTEORDER_LITTLE_ENDIAN_H)) && !(defined(_LINUX_MMZONE_H)) && !(defined(__LINUX_GFP_H)) && !(defined(_ASM_X86_BYTEORDER_H)) && !(defined(__ASSEMBLY__)) && !(defined(_ASM_X86_BITOPS_H)) && !(defined(__LINUX_SPINLOCK_H)) && !(defined(_LINUX_BITOPS_H)) && !(defined(_LINUX_THREAD_INFO_H)) && !(defined(_LINUX_SLAB_H)) && !(defined(__GENERATING_BOUNDS_H)))
#define __be16_to_cpu(x) __swab16((__force __u16)(__be16)(x))
#endif 


#if (!(defined(__LINUX_PREEMPT_H)) && !(defined(_ASM_GENERIC_BITOPS_EXT2_NON_ATOMIC_H_)) && !(defined(_ASM_GENERIC_BITOPS_LE_H_)) && defined(__KERNEL__) && !(defined(_LINUX_BYTEORDER_LITTLE_ENDIAN_H)) && !(defined(_LINUX_MMZONE_H)) && !(defined(__LINUX_GFP_H)) && !(defined(_ASM_X86_BYTEORDER_H)) && !(defined(__ASSEMBLY__)) && !(defined(_ASM_X86_BITOPS_H)) && !(defined(__LINUX_SPINLOCK_H)) && !(defined(_LINUX_BITOPS_H)) && !(defined(_LINUX_THREAD_INFO_H)) && !(defined(_LINUX_SLAB_H)) && !(defined(__GENERATING_BOUNDS_H)) && !(defined(_LINUX_BYTEORDER_GENERIC_H)))
#define be16_to_cpu __be16_to_cpu 
#endif


#if (!(defined(_LINUX_THREAD_INFO_H)) && !(defined(_ASM_GENERIC_BITOPS_LE_H_)) && defined(__KERNEL__) && !(defined(__LINUX_PREEMPT_H)) && !(defined(_LINUX_MMZONE_H)) && !(defined(__GENERATING_BOUNDS_H)) && !(defined(__ASSEMBLY__)) && !(defined(__LINUX_GFP_H)) && !(defined(_ASM_X86_BYTEORDER_H)) && !(defined(_LINUX_BYTEORDER_GENERIC_H)) && !(defined(_ASM_X86_BITOPS_H)) && !(defined(_ASM_GENERIC_BITOPS_EXT2_NON_ATOMIC_H_)) && !(defined(_LINUX_BYTEORDER_LITTLE_ENDIAN_H)) && !(defined(_LINUX_BITOPS_H)) && !(defined(_LINUX_SLAB_H)) && !(defined(__LINUX_SPINLOCK_H)))
#define cpu_to_be16  __cpu_to_be16
#endif 

#if (!(defined(_LINUX_THREAD_INFO_H)) && !(defined(_ASM_GENERIC_BITOPS_LE_H_)) && defined(__KERNEL__) && !(defined(__LINUX_PREEMPT_H)) && !(defined(_LINUX_MMZONE_H)) && !(defined(__GENERATING_BOUNDS_H)) && !(defined(__ASSEMBLY__)) && !(defined(__LINUX_GFP_H)) && !(defined(_ASM_X86_BYTEORDER_H)) && !(defined(_ASM_X86_BITOPS_H)) && !(defined(_ASM_GENERIC_BITOPS_EXT2_NON_ATOMIC_H_)) && !(defined(_LINUX_BYTEORDER_LITTLE_ENDIAN_H)) && !(defined(_LINUX_BITOPS_H)) && !(defined(_LINUX_SLAB_H)) && !(defined(__LINUX_SPINLOCK_H)))
#define __cpu_to_be16(x) ((__force __be16)__swab16((x)))
#endif 



static inline void be16_add_cpu(__be16 *var, u16 val)
{
	*var = cpu_to_be16(be16_to_cpu(*var) + val);
}