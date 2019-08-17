package data.weaver

import scala.language.dynamics
import scala.io.Source

import org.json4s._
import org.json4s.native.JsonMethods._

import types._

object Items extends Dynamic {
  implicit val formats = DefaultFormats
  lazy val all: Map[String, CraftedItem] = {
    val itemJson = parse(Source.fromResource("Weaver.json").mkString) transformField {
      case ("baseLevel", x) => ("characterLevel", x)
      case ("level", x)     => ("iLevel", x)
      case ("name", x)      => ("name", x \ "en")
    }
    itemJson
      .extract[List[CraftedItem]]
      .map { item: CraftedItem =>
        reformatName(item.name) -> item
      }
      .toMap
  }
  def reformatName(name: String): String =
    name
      .replaceAllLiterally(" ", "")
      .replaceAllLiterally("'", "")
      .replaceAllLiterally("(", "")
      .replaceAllLiterally(")", "")
      .replaceAllLiterally("-", "")

  def selectDynamic(name: String): CraftedItem = all(name)
}
