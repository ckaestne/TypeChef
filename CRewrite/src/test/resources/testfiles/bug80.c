void foo() {
	a;
#if definedEx(CONFIG_INET) && definedEx(CONFIG_SLIP_COMPRESSED)
	b;
#endif
	c;

	if (d)
		goto err_exit;
	e;
	if (f)
		goto err_exit;
#if definedEx(CONFIG_INET) && definedEx(CONFIG_SLIP_COMPRESSED)
	g;
	if (h)
		goto err_exit;
	if (i)
		goto err_exit;
#endif
	j;
	if (k) {
		goto err_exit;
	}
	l;
	m =
#if definedEx(CONFIG_X86_32)
0
#endif
#if !definedEx(CONFIG_X86_32)
1
#endif
;
	n =
#if definedEx(CONFIG_X86_32)
2
#endif
#if !definedEx(CONFIG_X86_32)
3
#endif
;
#if definedEx(CONFIG_INET) && definedEx(CONFIG_SLIP_COMPRESSED)
	o =
#if definedEx(CONFIG_X86_32)
4
#endif
#if !definedEx(CONFIG_X86_32)
5
#endif
;
	p =
#if definedEx(CONFIG_X86_32)
6
#endif
#if !definedEx(CONFIG_X86_32)
7
#endif
;
#if definedEx(CONFIG_SLIP_MODE_SLIP6)
	q;
#endif
#endif
    r;
	/* Cleanup */
err_exit:
#if definedEx(CONFIG_INET) && definedEx(CONFIG_SLIP_COMPRESSED)
	s;
	if (t)
		u;
#endif
	return v;
}
