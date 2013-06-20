struct sockaddr
  {
    char sa_data[14];
  };
typedef unsigned short int	uint16_t;
typedef uint16_t in_port_t;
struct sockaddr_in
  {
  unsigned char sin_zero[sizeof (struct sockaddr) - sizeof (in_port_t)];
};
void main() {}