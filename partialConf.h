#ifdef CONFIG_SMP
#define CONFIG_NR_CPUS 8
#else
#define CONFIG_NR_CPUS 1
#endif

//encode numeric parameter
#ifdef CONFIG_ZONE_DMA
#define CONFIG_ZONE_DMA_FLAG 1
#else
#define CONFIG_ZONE_DMA_FLAG 0
#endif

#define CONFIG_X86

//Defines a 'if' macro, which triggers a bug: The preprocessor incorrectly
//expands this macro within "#if".
#undef CONFIG_TRACE_BRANCH_PROFILING
//Needs header provided by the user:
#undef CONFIG_ACPI_CUSTOM_DSDT

//Is always true on x86. If it's false it causes an error in
//arch/x86/include/asm/paravirt.h.
#define CONFIG_TRACE_IRQFLAGS_SUPPORT

/////////////////////////////////////
//All the following macros are always false on x86, even if defined or anyhow included in the feature model:
#undef CONFIG_SBUS
#undef CONFIG_ATA_NONSTANDARD

#undef CONFIG_ARCH_HAS_ASYNC_TX_FIND_CHANNEL
#undef CONFIG_OF
#undef CONFIG_OF_DEVICE
#undef CONFIG_LEDS_GPIO_OF
#undef CONFIG_MISDN_HFCMULTI_8xx //Depends on 8xx
#undef CONFIG_MTD_XIP
#undef CONFIG_MVME16x_NET //Depends on MVME16x
#undef CONFIG_TULIP_DM910X //Depends on SPARC
#undef CONFIG_SSB_PCICORE_HOSTMODE //Depends on MIPS
#undef CONFIG_USB_EHCI_MXC
#undef CONFIG_USB_OHCI_HCD_PPC_OF //Depends on PPC_OF
#undef CONFIG_ADB_PMU 

//Second & last round of disabling:
#undef CONFIG_OF_GPIO
#undef CONFIG_PMAC_BACKLIGHT
#undef CONFIG_BVME6000_NET
#undef CONFIG_VME
#undef CONFIG_BVME6000
#undef CONFIG_USB_EHCI_HCD_PPC_OF
#undef CONFIG_XPS_USB_HCD_XILINX
/////////////////////////////////////

//Non-boolean features
#define AUTOCONF_INCLUDED
#define CONFIG_FRAME_WARN 1024
#define CONFIG_ARCH_DEFCONFIG "arch/x86/configs/i386_defconfig"
#define CONFIG_RCU_FANOUT 32
#define CONFIG_OUTPUT_FORMAT "elf32-i386"
#define CONFIG_DEFCONFIG_LIST "/lib/modules/$UNAME_RELEASE/.config"
#define CONFIG_PHYSICAL_ALIGN 0x1000000
#define CONFIG_IO_DELAY_TYPE_UDELAY 2
#define CONFIG_INPUT_MOUSEDEV_SCREEN_X 1024
#define CONFIG_INPUT_MOUSEDEV_SCREEN_Y 768
#define CONFIG_X86_L1_CACHE_SHIFT 5
#define CONFIG_EXTRA_FIRMWARE ""
#define CONFIG_PAGE_OFFSET 0xC0000000
#define CONFIG_PHYSICAL_START 0x1000000
#define CONFIG_DEFAULT_MMAP_MIN_ADDR 4096
#define CONFIG_SPLIT_PTLOCK_CPUS 4
#define CONFIG_IO_DELAY_TYPE_0X80 0
#define CONFIG_IO_DELAY_TYPE_0XED 1
#define CONFIG_ILLEGAL_POINTER_VALUE 0x0
#define CONFIG_X86_INTERNODE_CACHE_SHIFT 5
#define CONFIG_DEFAULT_IOSCHED "noop"
#define CONFIG_DEFAULT_IO_DELAY_TYPE 0
#define CONFIG_UEVENT_HELPER_PATH "/sbin/hotplug"
#define CONFIG_LOCALVERSION ""
#define CONFIG_INIT_ENV_ARG_LIMIT 32
#define CONFIG_IO_DELAY_TYPE_NONE 3
#define CONFIG_HZ 250
#define CONFIG_X86_MINIMUM_CPU_FAMILY 5
#define CONFIG_DEFAULT_SECURITY ""
#define CONFIG_BASE_SMALL 0
#define CONFIG_LOG_BUF_SHIFT 17
#undef CONFIG_PANEL_BOOT_MESSAGE

#define KBUILD_STR(s) #s
