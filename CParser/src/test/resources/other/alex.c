#if defined(FA)
void A(){
#if defined(FA) && defined(FB)
int A_AND_B;
#else
int A_inner;
#endif
}
#endif

