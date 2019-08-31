package data.culinarian

import scala.language.dynamics
import scala.io.Source

import org.json4s._
import org.json4s.native.JsonMethods._

import data.ItemReader
import types._

object Items extends ItemReader {
  val resourceName = "Culinarian.json"
}
