package simulate

import scala.annotation.tailrec
import scala.util.Random
import scala.util.chaining._

import com.typesafe.scalalogging.Logger

import types._
import util.Implicits._
import types.AbilityType.Buff

object Simulate {
  val logger = Logger("simulation")
  def probabilityFactor(base: Double, flag: Flag): Double = {
    val r = new Random().nextDouble()
    return flag match {
      case Flag.BestCase if base > 0.0  => 1.0
      case Flag.WorstCase if base < 1.0 => 0.0
      case _ if r < base                => 1.0
      case _                            => 0.0
    }
  }

  def simulateProgress(
    curr: CraftingState,
    ability: Ability,
    flag: Flag
  ): CraftingState = {
    val progressIncrease =
      CraftingModel.baseProgressIncrease(
        curr.stats + curr.modifier.stats,
        curr.item
      ) *
        probabilityFactor(ability.successRate + curr.modifier.successRate, flag) *
        (ability.efficiency + curr.modifier.efficiency)
    return curr.copy(
      progress = curr.progress + math.round(progressIncrease).intValue()
    )
  }

  def simulateCP(
    curr: CraftingState,
    ability: Ability,
    _flag: Flag
  ): CraftingState = curr.copy(remainingCP = curr.remainingCP - ability.CPCost)

  def simulateDurability(
    curr: CraftingState,
    ability: Ability,
    _flag: Flag
  ): CraftingState =
    curr.copy(
      durability =
        math.min(curr.item.durability, curr.durability - ability.durabilityLoss)
    )

  def simulateQuality(
    curr: CraftingState,
    ability: Ability,
    flag: Flag
  ): CraftingState = {
    val qualityIncrease =
      CraftingModel.baseQualityIncrease(
        curr.stats + curr.modifier.stats,
        curr.item
      ) *
        probabilityFactor(ability.successRate + curr.modifier.successRate, flag) *
        (ability.efficiency + curr.modifier.efficiency)
    return curr.copy(
      quality = math.min(
        curr.quality + math.round(qualityIncrease).intValue(),
        curr.item.maxQuality
      )
    )
  }

  // TODO(colin): implement condition
  def simulateCondition(
    curr: CraftingState,
    ability: Ability,
    flag: Flag
  ): CraftingState = curr

  def decrementEffects(
    curr: CraftingState
  ): CraftingState = {
    val nextEffects = curr.activeEffects
      .filter(_.duration > 0)
      .map(x => x.copy(duration = x.duration - 1))
    val nextModifiers = curr.modifiers filter {
      case (u, m) => nextEffects.exists(e => e.id == u)
    }
    curr.copy(activeEffects = nextEffects, modifiers = nextModifiers)
  }

  def simulatePreEffects(
    curr: CraftingState,
    ability: Ability,
    flag: Flag
  ) = {
    curr.activeEffects.foldLeft(curr) { (state, effect) =>
      effect.preFn(effect, state, ability) match {
        case (newState, Keep) => newState
        case (newState, newMod: Modifier) => {
          val newModifiers = newState.modifiers + (effect.id -> newMod)
          newState.copy(modifiers = newModifiers)
        }
      }
    }
  }

  def simulatePostEffects(
    prev: CraftingState,
    curr: CraftingState,
    ability: Ability,
    flag: Flag
  ) = curr.activeEffects.foldLeft(curr) { (state, effect) =>
    effect.postFn(effect, prev, state, ability) match {
      case (newState, Keep) => newState
      case (newState, newMod: Modifier) => {
        val newModifiers = newState.modifiers + (effect.id -> newMod)
        newState.copy(modifiers = newModifiers)
      }
    }
  }

  def simulateOne(
    curr: CraftingState,
    ability: Ability,
    flag: Flag,
    debug: Boolean = false
  ): CraftingState = {
    var state = curr
    state = simulatePreEffects(state, ability, flag)
    val preState = state
    state =
      state.copy(activeEffects = ability.otherEffect :: state.activeEffects)
    state = ability.abilityType match {
      case AbilityType.Progress => simulateProgress(state, ability, flag)
      case AbilityType.Quality  => simulateQuality(state, ability, flag)
      case AbilityType.Special  => ability.specialEffect(state, ability, flag)
      case _                    => state
    }
    state = simulateCP(state, ability, flag)
    state = simulateDurability(state, ability, flag)
    state = simulateCondition(state, ability, flag)
    state = simulatePostEffects(preState, state, ability, flag)
    state = decrementEffects(state)
    state.copy(stepsExecuted = state.stepsExecuted + 1).also { s =>
      if (debug) {
        println(f"Casted ${ability.name}")
        s.printForDebug()
      }
    }
  }

  @tailrec
  def simulate(
    state: CraftingState,
    abilitySeries: List[Ability],
    flag: Flag,
    debug: Boolean = false
  ): CraftingState = abilitySeries match {
    case Nil => state
    case _ if state.durability <= 0 =>
      state also { s =>
        logger.info(
          s"Termining after step ${s.stepsExecuted} because item is at 0 durability."
        )
      }
    case _ if state.progress >= state.item.difficulty => state
    case ability :: rest if ability.CPCost > state.remainingCP =>
      simulate(
        state also { _ =>
          logger.warn(s"Insufficient CP to use ability ${ability}. Skipping.")
        },
        rest,
        flag,
        debug
      )
    case ability :: rest
        // TODO(colin): unified "can cast" mechanism for abilities
        // TODO(colin): extract inner quiet out of weaver
        if ability.name == "Byregot's Blessing" && data.weaver.Abilities
          .countInnerQuiet(state) == 0 =>
      simulate(
        state also { _ =>
          logger.warn("Skipping Byregot's Blessing due to no IQ stacks.")
        },
        rest,
        flag,
        debug
      )
    case ability :: rest =>
      simulate(simulateOne(state, ability, flag, debug), rest, flag, debug)
  }

  def simulateN(
    state: CraftingState,
    abilitySeries: Vector[Ability],
    flag: Flag,
    n: Int,
    debug: Boolean = false
  ): List[CraftingState] =
    (0 until n)
      .map(_ => simulate(state, abilitySeries.toList, flag, debug))
      .toList

}
