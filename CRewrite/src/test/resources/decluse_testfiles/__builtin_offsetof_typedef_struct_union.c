typedef struct len_and_sockaddr {
	union {
		int i;
	} u;
} len_and_sockaddr;
enum {
	LSA_LEN_SIZE = __builtin_offsetof (len_and_sockaddr, u)
};

void main(){}