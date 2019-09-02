package optimize

import scala.annotation.tailrec
import scala.util.chaining._
import scala.util.Random

import types._

object Population {

  def allowedAbilities(
    curr: Vector[Ability],
    allAbilities: Vector[Ability]
  ): Vector[Ability] =
    allAbilities.filter(a => !a.onceOnly || !curr.contains(a))

  @tailrec
  def generateOneRandom(
    ofLength: Int,
    curr: Vector[Ability],
    heuristsics: Option[Vector[Rule]],
    allAbilities: Vector[Ability]
  ): Vector[Ability] =
    if (ofLength == 0) {
      curr
    } else {
      val allowed = allowedAbilities(curr, allAbilities)
      generateOneRandom(
        ofLength - 1,
        curr :+ allowed(
          Random.nextInt(allowed.length)
        ),
        heuristsics,
        allAbilities
      )
    }

  def generateRandom(
    n: Int,
    initialLength: Int,
    heuristics: Option[Vector[Rule]],
    allAbilities: Vector[Ability]
  ): Vector[Vector[Ability]] = {
    (0 until n)
      .map(
        _ =>
          generateOneRandom(initialLength, Vector(), heuristics, allAbilities) ++ allAbilities
            .filter(_.name == "Inner Quiet")
      )
      .toVector
  }

  def swapForProgressAbility(
    currAbilities: Vector[Ability],
    allAbilities: Vector[Ability]
  ): Vector[Ability] = {
    val firstQuality =
      currAbilities.indexWhere(_.abilityType == AbilityType.Quality)
    if (firstQuality == -1) {
      // Should already have checked that this case isn't happening in the
      // caller, but do something sensible as a fallback anyway.
      swapTwoAbilitiesRandomly(currAbilities)
    } else {
      val progressAbilities =
        allAbilities.filter(_.abilityType == AbilityType.Progress)
      val choice = progressAbilities(
        new Random().nextInt(progressAbilities.length)
      )
      println(
        s"Swapping ${currAbilities(firstQuality).name} for ${choice.name}"
      )
      currAbilities updated (firstQuality, choice)
    }
  }

  def addAbilityRandomly(
    currAbilities: Vector[Ability],
    allAbilities: Vector[Ability],
    maxCPCost: Int
  ): Vector[Ability] = {
    val allowedAbilities = Population
      .allowedAbilities(currAbilities, allAbilities)
      .filter(a => a.CPCost <= maxCPCost)
    val newAbility     = allowedAbilities(Random.nextInt(allowedAbilities.length))
    val insertionPoint = Random.nextInt(currAbilities.length + 1)
    currAbilities.slice(0, insertionPoint) ++ Vector(newAbility) ++ currAbilities
      .slice(insertionPoint, currAbilities.length)
  }

  def swapTwoAbilities(
    currAbilities: Vector[Ability],
    swap0: Int,
    swap1: Int
  ): Vector[Ability] = {

    val elem0 = currAbilities(swap0)
    val elem1 = currAbilities(swap1)
    currAbilities updated (swap0, elem1) updated (swap1, elem0)
  }

  def swapTwoAbilitiesRandomly(
    currAbilities: Vector[Ability]
  ): Vector[Ability] = swapTwoAbilities(
    currAbilities,
    Random.nextInt(currAbilities.length),
    Random.nextInt(currAbilities.length)
  )

  def doCrossover(
    curr: Vector[Ability],
    other: Vector[Ability]
  ): Vector[Ability] = {
    val minLen          = math.min(curr.length, other.length)
    val crossoverPoint  = Random.nextInt(minLen)
    val lhs :: rhs :: _ = Random.shuffle(List(curr, other))
    val lhsOnceOnly     = lhs.filter(_.onceOnly).toSet
    lhs.slice(0, crossoverPoint) ++ rhs
      .slice(crossoverPoint, other.length)
      .filter(!lhsOnceOnly.contains(_))
  }

  def mutateOneAbilityRandomly(
    curr: Vector[Ability],
    allAbilities: Vector[Ability]
  ): Vector[Ability] = {
    val allowed = allowedAbilities(curr, allAbilities)
    curr updated (
      Random.nextInt(curr.length),
      allowed(Random.nextInt(allowed.length))
    )
  }

  def swapNeighboringAbilitiesRandomly(
    curr: Vector[Ability]
  ): Vector[Ability] = {
    val pos = Random.nextInt(curr.length - 1)
    swapTwoAbilities(curr, pos, pos + 1)
  }

  def removeOneAbilityRandomly(
    curr: Vector[Ability]
  ): Vector[Ability] = {
    val idx = Random.nextInt(curr.length)
    curr.slice(0, idx) ++ curr.slice(idx + 1, curr.length)
  }

  // If the last action is not a progress-increasing action, there's no point,
  // so we should truncate the steps to make room for new things.
  def truncate(
    curr: Vector[Ability]
  ): Vector[Ability] = {
    curr.slice(
      0,
      curr.lastIndexWhere(
        a =>
          a.abilityType == AbilityType.Progress || a.abilityType == AbilityType.Special
      ) + 1
    )
  }

  def filterNonCastable(
    curr: Vector[Ability],
    maxCP: Int
  ): Vector[Ability] = {
    var remainingCP = maxCP
    curr filter { a =>
      if (a.CPCost <= remainingCP) {
        remainingCP -= a.CPCost
        true
      } else {
        false
      }
    }
  }

}
