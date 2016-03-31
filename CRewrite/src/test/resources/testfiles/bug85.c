void foo() {
  a;
  #ifdef A
  if (b) {
    while (c) {
        d;
    }
  }
  #else
  {
    while (e) {
        f;
    }
  }
  #endif
  g;
}
