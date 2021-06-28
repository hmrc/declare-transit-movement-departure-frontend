package object services {

  /**
    * Utility Function to collect when the condition is true
    * @param condition
    * @param func
    * @tparam T
    * @return Option[T]
    */
  def collectWhen[T](condition: Boolean)(func: => Option[T]): Option[T] = {
    val result = Option(condition) collect {
      case true => func
    }
    result.flatten
  }
}
