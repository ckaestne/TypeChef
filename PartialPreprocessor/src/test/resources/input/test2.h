#ifdef A
#define FUNC(x) (x)*(x)
#else
#define FUNC(x) (x)+(x)
#endif

int main() {
	int k = FUNC(3);
	return 0;
}
