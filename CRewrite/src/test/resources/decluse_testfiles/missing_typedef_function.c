typedef unsigned int		missing;
struct rc_dec {
	missing range;
}
static void  rc_reset(struct rc_dec *rc)
{
	rc->range = (missing)-1;
}

void main() {}