int foo() {
	for (n = 0; n <= max_code; n++) {
		curlen = nextlen;
		nextlen = tree[n + 1].dl.len;
		if (++count < max_count && curlen == nextlen)
			continue;

		if (count < min_count) {
			(*((struct globals2*)(ptr_to_globals))).bl_tree[curlen].fc.freq += count;
		} else if (curlen != 0) {
			if (curlen != prevlen)
				(*((struct globals2*)(ptr_to_globals))).bl_tree[curlen].fc.freq++;
			(*((struct globals2*)(ptr_to_globals))).bl_tree[16].fc.freq++;
		} else if (count <= 10) {
			(*((struct globals2*)(ptr_to_globals))).bl_tree[17].fc.freq++;
		} else {
			(*((struct globals2*)(ptr_to_globals))).bl_tree[18].fc.freq++;
		}
		count = 0;
	}
}