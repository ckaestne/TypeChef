//#ifndef pr_fmt
#define pr_fmt(fmt) fmt
//#endif

//From Linux 2.6.33.3, include/net/ip_vs.h:
#define __KERNEL__
#ifdef __KERNEL__
#define pr_err(fmt, ...) \
        printk(KERN_ERR pr_fmt(fmt), ##__VA_ARGS__)
#ifdef IP_VS_DEBUG
#define IP_VS_ERR_BUF(msg...)						\
	do {								\
		char ip_vs_dbg_buf[160];				\
		int ip_vs_dbg_idx = 0;					\
		pr_err(msg);						\
	} while (0)
#define IP_VS_DBG_ADDR(af, addr)					\
	ip_vs_dbg_addr(af, ip_vs_dbg_buf,				\
		       sizeof(ip_vs_dbg_buf), addr,			\
		       &ip_vs_dbg_idx)
#else	/* NO DEBUGGING at ALL */
#define IP_VS_ERR_BUF(msg...)  do {} while (0)
#endif

static inline void ip_vs_control_del(struct ip_vs_conn *cp)
{
	struct ip_vs_conn *ctl_cp = cp->control;
	if (!ctl_cp) {
		IP_VS_ERR_BUF("request control DEL for uncontrolled: "
			      "%s:%d to %s:%d\n",
			      IP_VS_DBG_ADDR(cp->af, &cp->caddr),
			      ntohs(cp->cport),
			      IP_VS_DBG_ADDR(cp->af, &cp->vaddr),
			      ntohs(cp->vport));

	}
}
#endif
