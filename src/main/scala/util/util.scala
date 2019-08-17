package util

object Implicits {
  implicit def extendAny[T](v: T): AnyExt[T] = new AnyExt[T](v)
}

class AnyExt[T](v: T) {
  def also(f: (T) => Unit): T = {
    f(v)
    v
  }

  def let[U](f: (T) => U): U = f(v)
}
