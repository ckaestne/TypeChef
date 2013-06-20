struct outer_struct {
	struct {
		/* Position in dec_index() */
		enum {
			MISSING
		} missing_enum;
	} missing_struct;
}

void main() {}