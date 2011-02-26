//test case based on problems with
//arch_pfn_to_nid macro in mincore.c in linux
//problem was caused by repeated expansion of macro arguments when there were alternatives
//caused by incorrect push_source call with siblings


#define outer(x) whatever(x)

#ifdef A
#define ba(x) ab(x)
#else
#define ba(x) x
#endif
#ifdef B
#define ab(x) ba(x)
#else
#define ab(x) x
#endif



outer(ab(x))


x outer(outer(x)) x