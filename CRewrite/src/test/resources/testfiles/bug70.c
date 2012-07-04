#if definedEx(A)
void foo() {

#if !definedEx(CONFIG_SMP) && definedEx(CONFIG_TREE_RCU)
#if definedEx(CONFIG_PREEMPT)

#if definedEx(CONFIG_DEBUG_VM)

#if definedEx(CONFIG_BUG)
do { if (__builtin_expect(!!(!(((current_thread_info()->preempt_count) & ~0x10000000) !=
#if !definedEx(CONFIG_BLOCK) && definedEx(CONFIG_HIGHMEM) && definedEx(CONFIG_PREEMPT) && definedEx(CONFIG_LOCK_KERNEL) || definedEx(CONFIG_BLOCK) && definedEx(CONFIG_PREEMPT) && definedEx(CONFIG_LOCK_KERNEL)
(get_current()->lock_depth >= 0)
#endif
#if !definedEx(CONFIG_BLOCK) && definedEx(CONFIG_HIGHMEM) && definedEx(CONFIG_PREEMPT) && !definedEx(CONFIG_LOCK_KERNEL) || definedEx(CONFIG_BLOCK) && definedEx(CONFIG_PREEMPT) && !definedEx(CONFIG_LOCK_KERNEL)
1
#endif
)), 0))
#if definedEx(CONFIG_BUG) && definedEx(CONFIG_DEBUG_BUGVERBOSE)
do { asm volatile("1:\tud2\n" ".pushsection __bug_table,\"a\"\n"
#if definedEx(CONFIG_X86_32) && definedEx(CONFIG_BUG) && definedEx(CONFIG_DEBUG_BUGVERBOSE)
"2:\t.long 1b, %c0\n"
#endif
#if !definedEx(CONFIG_X86_32) && definedEx(CONFIG_BUG) && definedEx(CONFIG_DEBUG_BUGVERBOSE)
"2:\t.long 1b - 2b, %c0 - 2b\n"
#endif
 "\t.word %c1, 0\n" "\t.org 2b+%c2\n" ".popsection" : : "i" ("/local/joliebig/TypeChef-LinuxAnalysis/linux-2.6.33.3/include/linux/pagemap.h"), "i" (137), "i" (sizeof(struct bug_entry))); __builtin_unreachable(); } while (0)
#endif
#if definedEx(CONFIG_BUG) && !definedEx(CONFIG_DEBUG_BUGVERBOSE)
do { asm volatile("ud2"); __builtin_unreachable(); } while (0)
#endif
; } while(0)
#endif
#if !definedEx(CONFIG_BUG)
do { if (!(((current_thread_info()->preempt_count) & ~0x10000000) !=
#if !definedEx(CONFIG_BLOCK) && definedEx(CONFIG_HIGHMEM) && definedEx(CONFIG_PREEMPT) && definedEx(CONFIG_LOCK_KERNEL) || definedEx(CONFIG_BLOCK) && definedEx(CONFIG_PREEMPT) && definedEx(CONFIG_LOCK_KERNEL)
(get_current()->lock_depth >= 0)
#endif
#if !definedEx(CONFIG_BLOCK) && definedEx(CONFIG_HIGHMEM) && definedEx(CONFIG_PREEMPT) && !definedEx(CONFIG_LOCK_KERNEL) || definedEx(CONFIG_BLOCK) && definedEx(CONFIG_PREEMPT) && !definedEx(CONFIG_LOCK_KERNEL)
1
#endif
)) ; } while(0)
#endif

#endif
#if !definedEx(CONFIG_DEBUG_VM)
do { } while (0)
#endif
;
#endif
	/*
	 * Preempt must be disabled here - we rely on rcu_read_lock doing
	 * this for us.
	 *
	 * Pagecache won't be truncated from interrupt context, so if we have
	 * found a page in the radix tree here, we have pinned its refcount by
	 * disabling preempt, and hence no need for the "speculative get" that
	 * SMP requires.
	 */

#if definedEx(CONFIG_DEBUG_VM)

#if definedEx(CONFIG_BUG)
do { if (__builtin_expect(!!(page_count(page) == 0), 0))
#if definedEx(CONFIG_BUG) && definedEx(CONFIG_DEBUG_BUGVERBOSE)
do { asm volatile("1:\tud2\n" ".pushsection __bug_table,\"a\"\n"
#if definedEx(CONFIG_X86_32) && definedEx(CONFIG_BUG) && definedEx(CONFIG_DEBUG_BUGVERBOSE)
"2:\t.long 1b, %c0\n"
#endif
#if !definedEx(CONFIG_X86_32) && definedEx(CONFIG_BUG) && definedEx(CONFIG_DEBUG_BUGVERBOSE)
"2:\t.long 1b - 2b, %c0 - 2b\n"
#endif
 "\t.word %c1, 0\n" "\t.org 2b+%c2\n" ".popsection" : : "i" ("/local/joliebig/TypeChef-LinuxAnalysis/linux-2.6.33.3/include/linux/pagemap.h"), "i" (148), "i" (sizeof(struct bug_entry))); __builtin_unreachable(); } while (0)
#endif
#if definedEx(CONFIG_BUG) && !definedEx(CONFIG_DEBUG_BUGVERBOSE)
do { asm volatile("ud2"); __builtin_unreachable(); } while (0)
#endif
; } while(0)
#endif
#if !definedEx(CONFIG_BUG)
do { if (page_count(page) == 0) ; } while(0)
#endif

#endif
#if !definedEx(CONFIG_DEBUG_VM)
do { } while (0)
#endif
;
	atomic_inc(&page->_count);

#endif
#if !definedEx(CONFIG_SMP) && !definedEx(CONFIG_TREE_RCU) || definedEx(CONFIG_SMP)
	if (__builtin_expect(!!(!get_page_unless_zero(page)), 0)) {
		/*
		 * Either the page has been freed, or will be freed.
		 * In either case, retry here and the caller should
		 * do the right thing (see comments above).
		 */
		return 0;
	}
#endif

	return 1;
}
