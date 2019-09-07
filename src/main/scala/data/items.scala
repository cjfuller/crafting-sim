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

object ItemReader {
  def itemsByClass(cls: String): Map[String, CraftedItem] = cls match {
    case "weaver"        => weaver.Items.allByName
    case "culinarian"    => culinarian.Items.allByName
    case "carpenter"     => carpenter.Items.allByName
    case "alchemist"     => alchemist.Items.allByName
    case "armorer"       => armorer.Items.allByName
    case "blacksmith"    => blacksmith.Items.allByName
    case "goldsmith"     => goldsmith.Items.allByName
    case "leatherworker" => leatherworker.Items.allByName
    case _               => Map()
  }
}
