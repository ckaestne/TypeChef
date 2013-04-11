#include "Sampling/src/test/resources/testfiles/testHeader.h"

int main(int argc, char** argv) {
    #ifdef CONFIG_A
    int i = 0;
    #else
    int i = 1;
    #endif
    return i;
}