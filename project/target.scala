package target

sealed trait Target
object Unix    extends Target
object Windows extends Target

object GlobalTarget {
  var assembleTarget: Target = Unix
}
