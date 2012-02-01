int main()
{
#ifdef usestubs
  set_debug_traps();
  breakpoint();
#endif
  L1  = fun1();
  return 0;
}
