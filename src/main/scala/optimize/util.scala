package optimize

import scala.util.Random

object Util {
  def doProbabilistically[T](f: (Double) => T): T =
    f(Random.nextDouble())
}
