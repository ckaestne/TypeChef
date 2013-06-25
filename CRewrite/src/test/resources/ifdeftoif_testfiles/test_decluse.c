#ifdef A
signed
#endif
int i;

#ifdef A
int
#else
long
#endif
j;

#ifdef A
int k;
#endif
#ifndef A
long k;
#endif

void main() {
	i = 5;
	j = 5;
	k = 5;
}