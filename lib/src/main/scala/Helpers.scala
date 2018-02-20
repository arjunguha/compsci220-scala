package hw

private[hw] object Helpers {

  /**
   * This flag is set to true if the program is running as a Google Cloud
   * Function.
   */
  val isCloudFunction: Boolean = {
    // This environment variable is not documented, so it may not be 100%
    // reliable.
    System.getenv().get("X_GOOGLE_FUNCTION_NAME") != null
  }

}
