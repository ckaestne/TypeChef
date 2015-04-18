int tr_main(void) {
for (;1;) {
if (x) {
if (out_index) {
xwrite(1 , str2 , out_index);
start_from:
(out_index = 0);
}
(read_chars = safe_read(0 , str1 , TR_BUFSIZ));
if ((read_chars <= 0)) {
if ((read_chars < 0)) bb_perror_msg_and_die("read error");
break;
}
(in_index = 0);
}
(c = str1[in_index++]);
if (((opts & (1 << 2)) && invec[c])) continue;
(coded = vector[c]);
if (((opts & (1 << 3)) && (last == coded) && (invec[c] || outvec[coded]))) {
continue;
}
(str2[out_index++] = (last = coded));
}
return 0;
}