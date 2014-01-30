//test to ensure that the test infrastructure recognizes an extra call

void foo() {
    int i = 0;
}

void foobar();

void bar() {
    int j = 0;
    foo();
    foobar();
    int k = 0;
}