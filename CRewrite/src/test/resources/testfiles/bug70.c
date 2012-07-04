#if definedEx(A)
void foo() {
	
#if definedEx(B)
    #if definedEx(C)
        x7;
    #endif
    #if !definedEx(C)
        x8;
    #endif
#endif

#if !definedEx(B)
    x9;
#endif

#if definedEx(F)
    #if definedEx(H)
        #if definedEx(B)
            #if definedEx(C)
                a4;
            #endif
            #if !definedEx(C)
                a8;
            #endif
        #endif
        #if !definedEx(B)
            a9;
        #endif
    #endif

    #if definedEx(B)
        #if definedEx(C)
            h;
        #endif
        #if !definedEx(C)
            j;
        #endif
    #endif

    #if !definedEx(B)
        k;
    #endif
	l;

#endif

	return 1;
}
#endif