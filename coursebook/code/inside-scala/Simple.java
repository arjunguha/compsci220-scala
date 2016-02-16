class Simple {

  static int factorial(int n, int result) {
    if (n == 0) {
      return result;
    }
    else {
      return factorial(n - 1, result * n);
    }
  }

  public static void main(String[] args) {
    System.out.println("Hello, world!");
     System.out.println(factorial(5, 1));
  }

}