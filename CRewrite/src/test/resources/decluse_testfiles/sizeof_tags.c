typedef unsigned int		uint32_t;
typedef long unsigned int size_t;
void *xzalloc(size_t size)  __attribute__ ((malloc));
typedef struct {
	uint32_t tag; /* 4 byte tag */
	uint32_t type; /* 4 byte type */
	uint32_t offset; /* 4 byte offset */
	uint32_t count; /* 4 byte count */
} rpm_index;

static rpm_index **rpm_gettags(int fd, int *num_tags)
{
	rpm_index **tags = xzalloc(200 * sizeof(tags[0]));
	return tags;
}

void main() {}