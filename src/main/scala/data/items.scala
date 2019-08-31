package data

import scala.language.dynamics
import scala.io.Source

import org.json4s._
import org.json4s.native.JsonMethods._

import types._

trait ItemReader extends Dynamic {
  val resourceName: String

  implicit val formats = DefaultFormats
  lazy val all: Map[String, CraftedItem] = {
    allByName.map { case (name, value) => reformatName(name) -> value }
  }
  lazy val allByName = {
    val itemJson = parse(Source.fromResource(resourceName).mkString) transformField {
      case ("baseLevel", x) => ("characterLevel", x)
      case ("level", x)     => ("iLevel", x)
      case ("name", x)      => ("name", x \ "en")
    }
    itemJson
      .extract[List[CraftedItem]]
      .map { item: CraftedItem =>
        item.name -> item
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