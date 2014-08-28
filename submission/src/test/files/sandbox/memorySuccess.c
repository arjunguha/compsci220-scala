#include <stdlib.h>
#include <stdio.h>

#define SIZE (1024)

int main(int argc, char** argv) {

  int* ptr = malloc(SIZE * sizeof(int));

  // A big malloc does not consume memory (but a really big malloc does!)
  if (ptr == NULL) {
    printf("[BAD] Allocation failed.\n");
    return 1;
  }

  for (int i = 0; i < SIZE; i++) {
    ptr[i] = 99;
  }

  printf("[GOOD] Using memory succeeded.\n");
  return 0;
}