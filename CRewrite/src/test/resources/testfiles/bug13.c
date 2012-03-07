int test (int x, char *y, int z) {
  int b = 0;

  do {
    switch (f3 (x, y + b)) {
      case -1:
        if ((*f2 ()) == 4)
          continue;
        if (b == 0)
          return -1;
        else
          return b;

      default:
        b++;
    }
  } while (b < z);
  return b;
}
