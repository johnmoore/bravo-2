#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[]) {
    setuid(0);
    setgid(0);

    unsigned int i;
    size_t len = 0;
    char *_all_args, *all_args;

    for(i=1; i<argc; i++) {
        len += strlen(argv[i]);
    }

    _all_args = all_args = (char *)malloc(len+argc-1);

    for(i=1; i<argc; i++) {
        memcpy(_all_args, argv[i], strlen(argv[i]));
        _all_args += strlen(argv[i])+1;
        *(_all_args-1) = ' ';
    }
    *(_all_args-1) = 0;

    printf("All %d args: '%s'\n", argc, all_args);
    system(all_args);
}

