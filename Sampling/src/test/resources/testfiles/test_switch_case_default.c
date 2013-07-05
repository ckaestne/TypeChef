int foo(int i) { return 2*i; }

int main(int argc, char** argv) {
    int k;
    switch (k) {
        default: foo(3*k);
        default: foo(4*k);
    }
    return 0;
}