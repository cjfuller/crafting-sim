package target

sealed trait Target
object Unix    extends Target
object Windows extends Target
object Server  extends Target

object GlobalTarget {
  var assembleTarget: Target    = Unix
  var mainClass: Option[String] = Some("craftingsim.Main")
}
