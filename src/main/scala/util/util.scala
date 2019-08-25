package util

import types._

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

package object util {
  val MAX_MACRO_SIZE = 15
  def formatAbilities(abilities: Vector[Ability]): String =
    "\n" + abilities.map(_.name).mkString("\n") + "\n"

  def macroStep(ability: Ability): String =
    s"""|/ac "${ability.name}" <wait.3>"""

  def maybeSoundEffectMacro(abilities: Vector[Ability], idx: Int): String =
    if (abilities.length < MAX_MACRO_SIZE) {
      f"/echo Macro $idx complete <se.7>"
    } else {
      ""
    }

  def makeSingleMacro(abilities: Vector[Ability], idx: Int = 1): String =
    s"""|
  |
  |macro $idx
  |
  ${abilities.map(macroStep).mkString("\n")}
  |${maybeSoundEffectMacro(abilities, idx)}
  |
  """.stripMargin

  def makeMacro(abilities: Vector[Ability], idx: Int = 1): String =
    if (abilities.length <= MAX_MACRO_SIZE) {
      makeSingleMacro(abilities, idx)
    } else {
      val (curr, next) = abilities.splitAt(MAX_MACRO_SIZE - 1)
      makeSingleMacro(curr) + makeMacro(next, idx + 1)
    }
}
