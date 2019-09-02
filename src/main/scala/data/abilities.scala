package data

import scala.util.Random
import simulate.CraftingModel
import simulate.Simulate
import types._
import types.Implicits._
import java.util.UUID

object SharedAbilities {
  val INNER_QUIET = "Inner Quiet"
  def countInnerQuiet(state: CraftingState): Int =
    state.modifier.buffs.getOrElse(INNER_QUIET, 0)

  def addInnerQuietBuffs(state: CraftingState, n: Int): CraftingState = {
    val curr     = countInnerQuiet(state)
    val numToAdd = math.min(curr + n, 11) - curr
    val newIQMod = state.modifiers
      .getOrElse(InnerQuiet.otherEffect.id, Modifier.NoModifier) +
      Modifier.buff(INNER_QUIET, numToAdd)
    state
      .copy(
        modifiers = state.modifiers + (InnerQuiet.otherEffect.id -> newIQMod)
      )
  }

  val BasicSynthesis = Ability(
    name = "Basic Synthesis",
    CPCost = 0,
    abilityType = AbilityType.Progress,
    efficiency = 1.0,
    successRate = 0.9,
    durabilityLoss = 10,
    requiredLevel = 1
  )

  val BasicTouch = Ability(
    name = "Basic Touch",
    CPCost = 18,
    abilityType = AbilityType.Quality,
    efficiency = 1.0,
    successRate = 0.7,
    durabilityLoss = 10,
    requiredLevel = 5
  )

  val MastersMend = Ability(
    name = "Master's Mend",
    CPCost = 92,
    abilityType = AbilityType.Durability,
    efficiency = 0.0,
    successRate = 1.0,
    durabilityLoss = -30,
    requiredLevel = 7
  )

  val SteadyHand: Ability = Ability(
    name = "Steady Hand",
    CPCost = 22,
    abilityType = AbilityType.Buff,
    efficiency = 0.0,
    successRate = 1.0,
    durabilityLoss = 0,
    requiredLevel = 9,
    otherEffect = Effect(
      id = UUID.randomUUID(),
      preFn = (e, state, ability) =>
        ability match {
          case SteadyHand  => (state.withEffectRemoved(e), Modifier.NoModifier)
          case SteadyHand2 => (state.withEffectRemoved(e), Modifier.NoModifier)
          case _           => (state, Modifier.successRateBoost(0.2))
        },
      postFn = Effect.NoEffectFn,
      duration = 5
    )
  )

  val InnerQuiet: Ability = Ability(
    name = "Inner Quiet",
    CPCost = 18,
    abilityType = AbilityType.Buff,
    efficiency = 0.0,
    successRate = 1.0,
    durabilityLoss = 0,
    requiredLevel = 11,
    otherEffect = Effect(
      id = UUID.randomUUID(),
      preFn = (e, curr, ability) =>
        ability match {
          case ByregotsBlessing =>
            (
              curr,
              Modifier.efficiencyBoost(
                0.2 * countInnerQuiet(curr)
              )
            )
          case InnerQuiet =>
            (curr.withEffectRemoved(e), Keep)
          case _ => (curr, Keep)
        },
      postFn = (e, prev, curr, ability) =>
        ability match {
          case ByregotsBlessing =>
            (curr.withEffectRemoved(e), Modifier.NoModifier)
          case _ if curr.quality > prev.quality => {
            (
              curr,
              addInnerQuietBuffs(curr, 1).modifiers
                .getOrElse(InnerQuiet.otherEffect.id, Modifier.NoModifier) +
                Modifier.statsBoost(
                  control =
                    math.round(math.floor(0.2 * curr.stats.control)).toInt
                )
            )
          }
          case _ => (curr, Keep)
        },
      1000000
    )
  )

  val Observe = Ability(
    name = "Observe",
    CPCost = 7,
    abilityType = AbilityType.Buff,
    efficiency = 0.0,
    successRate = 1.0,
    durabilityLoss = 0,
    requiredLevel = 13,
    otherEffect = Effect(
      id = UUID.randomUUID(),
      preFn = (e, curr, ability) =>
        ability match {
          case FocusedSynthesis => (curr, Modifier.successRateBoost(1.0))
          case FocusedTouch     => (curr, Modifier.successRateBoost(1.0))
          case _                => (curr, Modifier.NoModifier)
        },
      postFn = Effect.NoEffectFn,
      1
    )
  )

  val SteadyHand2: Ability = Ability(
    name = "Steady Hand II",
    CPCost = 25,
    abilityType = AbilityType.Buff,
    efficiency = 0.0,
    successRate = 1.0,
    durabilityLoss = 0,
    requiredLevel = 37,
    onceOnly = false,
    otherEffect = Effect(
      id = UUID.randomUUID(),
      preFn = (e, state, ability) =>
        ability match {
          case SteadyHand  => (state.withEffectRemoved(e), Modifier.NoModifier)
          case SteadyHand2 => (state.withEffectRemoved(e), Modifier.NoModifier)
          case _           => (state, Modifier.successRateBoost(0.3))
        },
      postFn = Effect.NoEffectFn,
      duration = 5
    )
  )

  val ByregotsBlessing = Ability(
    name = "Byregot's Blessing",
    CPCost = 24,
    abilityType = AbilityType.Quality,
    efficiency = 1.0,
    successRate = 0.9,
    durabilityLoss = 10,
    requiredLevel = 50
  )

  val FocusedSynthesis = Ability(
    name = "Focused Synthesis",
    CPCost = 5,
    abilityType = AbilityType.Progress,
    efficiency = 2.0,
    successRate = 0.5,
    durabilityLoss = 10,
    requiredLevel = 67
  )

  val FocusedTouch = Ability(
    name = "Focused Touch",
    CPCost = 18,
    abilityType = AbilityType.Quality,
    efficiency = 1.5,
    successRate = 0.5,
    durabilityLoss = 10,
    requiredLevel = 68
  )

  val StandardTouch = Ability(
    name = "Standard Touch",
    CPCost = 32,
    abilityType = AbilityType.Quality,
    efficiency = 1.25,
    successRate = 0.8,
    durabilityLoss = 10,
    requiredLevel = 18
  )

  val GreatStrides = Ability(
    name = "Great Strides",
    CPCost = 32,
    abilityType = AbilityType.Buff,
    efficiency = 0.0,
    successRate = 1.0,
    durabilityLoss = 0,
    requiredLevel = 21,
    onceOnly = true,
    otherEffect = Effect(
      id = UUID.randomUUID(),
      preFn = (e, curr, ability) =>
        if (ability.name contains "Touch") {
          (curr, Modifier.efficiencyBoost(ability.efficiency)),
        } else {
          (curr, Modifier.NoModifier)
        },
      postFn = (e, prev, curr, ability) =>
        if (ability.name contains "Touch") {
          (
            curr.withEffectRemoved(e),
            Modifier.NoModifier
          )
        } else {
          (curr, Keep)
        },
      duration = 3
    )
  )

  val MastersMend2 = Ability(
    name = "Master's Mend II",
    CPCost = 160,
    abilityType = AbilityType.Durability,
    efficiency = 0.0,
    successRate = 1.0,
    durabilityLoss = -60,
    requiredLevel = 25
  )

  val StandardSynthesis = Ability(
    name = "Standard Synthesis",
    CPCost = 15,
    abilityType = AbilityType.Progress,
    efficiency = 1.5,
    successRate = 0.9,
    durabilityLoss = 10,
    requiredLevel = 31
  )

  val BrandOfTheElements = Ability(
    name = "Brand of the Elements",
    CPCost = 6,
    abilityType = AbilityType.Progress,
    efficiency = 1.0,
    successRate = 0.9,
    durabilityLoss = 10,
    requiredLevel = 37
  )

  val NameOfTheElements = Ability(
    name = "Name of the Elements",
    CPCost = 15,
    abilityType = AbilityType.Buff,
    efficiency = 0.0,
    successRate = 1.0,
    durabilityLoss = 0,
    requiredLevel = 37,
    onceOnly = true,
    otherEffect = Effect(
      id = UUID.randomUUID(),
      preFn = (e, curr, ability) =>
        ability match {
          case BrandOfTheElements =>
            (
              curr,
              Modifier.efficiencyBoost(
                CraftingModel
                  .nameOfXProgressMultiplier(curr) * ability.efficiency
              )
            )
          case _ => (curr, Modifier.NoModifier)
        },
      postFn = Effect.NoEffectFn,
      duration = 5
    )
  )

  val AdvancedTouch = Ability(
    name = "Advanced Touch",
    CPCost = 48,
    abilityType = AbilityType.Quality,
    efficiency = 1.5,
    successRate = 0.9,
    durabilityLoss = 10,
    requiredLevel = 43
  )

  // TODO(colin): only available depending on condition.
  val PreciseTouch = Ability(
    name = "Precise Touch",
    CPCost = 18,
    abilityType = AbilityType.Quality,
    efficiency = 1.0,
    successRate = 0.7,
    durabilityLoss = 10,
    requiredLevel = 53,
    otherEffect = Effect(
      id = UUID.randomUUID(),
      preFn = Effect.NoEffectFn,
      postFn = (e, prev, curr, ability) => {
        // Technically we could be at max quality and the action could still
        // have succeeded, so we'd want to increase the stack. But at max
        // quality, Inner Quiet stacks aren't really useful anyway.
        if (prev.quality < curr.quality) {
          (SharedAbilities.addInnerQuietBuffs(curr, 1), Modifier.NoModifier)
        } else {
          (curr, Modifier.NoModifier)
        }
      },
      duration = 0
    )
  )

  val TrainedEye = Ability(
    name = "Trained Eye",
    CPCost = 250,
    abilityType = AbilityType.Special,
    efficiency = 1.0,
    successRate = 1.0,
    durabilityLoss = 0,
    requiredLevel = 80,
    specialEffect = (state, ability, flag) => {
      val qualityGain =
        if (state.stepsExecuted == 0 && (state.stats.level - state.item.characterLevel) >= 10) {
          state.item.maxQuality * 0.5
        } else {
          0
        }

      state.copy(
        progress = state.progress + math.round(qualityGain).intValue()
      )
    }
  )

  val TrainedInstinct = Ability(
    name = "Trained Instinct",
    CPCost = 250,
    abilityType = AbilityType.Special,
    efficiency = 1.0,
    successRate = 1.0,
    durabilityLoss = 0,
    requiredLevel = 80,
    specialEffect = (state, ability, flag) => {
      val qualityGain =
        if (state.stepsExecuted == 0 && (state.stats.level - state.item.characterLevel) >= 10) {
          state.item.maxQuality * (new Random().nextDouble() * 0.7 + 0.3)
        } else {
          0
        }

      state.copy(
        progress = state.progress + math.round(qualityGain).intValue()
      )
    }
  )

  val HastyTouch2 = Ability(
    name = "Hasty Touch II",
    CPCost = 5,
    abilityType = AbilityType.Quality,
    efficiency = 1.0,
    successRate = 0.6,
    durabilityLoss = 10,
    requiredLevel = 61
  )

  val CarefulSynthesis3 = Ability(
    name = "Careful Synthesis III",
    CPCost = 7,
    abilityType = AbilityType.Progress,
    efficiency = 1.5,
    successRate = 1.0,
    durabilityLoss = 10,
    requiredLevel = 62
  )

  val RapidSynthesis2 = Ability(
    name = "Rapid Synthesis II",
    CPCost = 12,
    abilityType = AbilityType.Progress,
    efficiency = 3.0,
    successRate = 0.6,
    durabilityLoss = 10,
    requiredLevel = 63
  )

  val PatientTouch = Ability(
    name = "Patient Touch",
    CPCost = 6,
    abilityType = AbilityType.Quality,
    efficiency = 1.0,
    successRate = 0.5,
    durabilityLoss = 10,
    requiredLevel = 64,
    otherEffect = Effect(
      id = UUID.randomUUID(),
      preFn = Effect.NoEffectFn,
      postFn = (e, prev, curr, ability) => {
        if (prev.quality < curr.quality) {
          (SharedAbilities.addInnerQuietBuffs(curr, 1), Modifier.NoModifier)
        } else {
          val numToLose = SharedAbilities
            .countInnerQuiet(curr) / 2 // Rounds towards 0
          (
            SharedAbilities.addInnerQuietBuffs(curr, -numToLose),
            Modifier.NoModifier
          )
        }
      },
      duration = 0
    )
  )

  val Manipulation2: Ability = Ability(
    name = "Manipulation II",
    CPCost = 96,
    abilityType = AbilityType.Buff,
    efficiency = 0.0,
    successRate = 1.0,
    durabilityLoss = 0,
    requiredLevel = 65,
    otherEffect = Effect(
      id = UUID.randomUUID(),
      preFn = (e, curr, ability) => {
        if (ability == Manipulation2) {
          (curr.withEffectRemoved(e), Modifier.NoModifier)
        } else {
          (curr, Modifier.NoModifier)
        }
      },
      postFn = (e, prev, curr, ability) => {
        if (e.duration == 8 || curr.durability == 0) {
          // Hack not to restore durability until the next step.
          (curr, Modifier.NoModifier)
        } else {
          (
            curr.copy(
              durability = math.min(curr.item.durability, curr.durability + 5)
            ),
            Modifier.NoModifier
          )
        }
      },
      duration = 8
    )
  )

  val PrudentTouch = Ability(
    name = "Prudent Touch",
    CPCost = 21,
    abilityType = AbilityType.Quality,
    efficiency = 1.0,
    successRate = 0.7,
    durabilityLoss = 5,
    requiredLevel = 66
  )

  val PreparatoryTouch = Ability(
    name = "Preparatory Touch",
    CPCost = 36,
    abilityType = AbilityType.Quality,
    efficiency = 2.0,
    successRate = 0.7,
    durabilityLoss = 20,
    requiredLevel = 71,
    otherEffect = Effect(
      id = UUID.randomUUID(),
      preFn = Effect.NoEffectFn,
      postFn = (e, prev, curr, ability) => {
        // TODO(colin): does it always increase stacks, or just on success?
        if (prev.quality < curr.quality) {
          (SharedAbilities.addInnerQuietBuffs(curr, 1), Modifier.NoModifier)
        } else {
          (curr, Modifier.NoModifier)
        }
      },
      duration = 0
    )
  )

  val RapidSynthesis3 = Ability(
    name = "Rapid Synthesis III",
    CPCost = 24,
    abilityType = AbilityType.Progress,
    efficiency = 6.0,
    successRate = 0.6,
    durabilityLoss = 20,
    requiredLevel = 72,
    otherEffect = Effect(
      id = UUID.randomUUID(),
      // TODO(colin): what's the actual efficiency penalty? I presume it doesn't
      // matter as it likely doesn't make sense to use this at < 20 durability?
      preFn = (e, curr, ability) =>
        if (curr.durability < 20) {
          (curr, Modifier.efficiencyBoost(-6.0))
        } else {
          (curr, Modifier.NoModifier)
        },
      postFn = Effect.NoEffectFn,
      duration = 0
    )
  )

  val DelicateSynthesis = Ability(
    name = "Delicate Synthesis",
    CPCost = 32,
    abilityType = AbilityType.Special,
    efficiency = 1.0,
    successRate = 1.0,
    durabilityLoss = 10,
    requiredLevel = 76,
    specialEffect = (state, ability, flag) =>
      Simulate.simulateProgress(
        Simulate.simulateQuality(state, ability, flag),
        ability,
        flag
      )
  )

  // TODO(colin): only when Good or Excellent
  val IntensiveSynthesis = Ability(
    name = "IntenstiveSynthesis",
    CPCost = 12,
    abilityType = AbilityType.Progress,
    efficiency = 3.0,
    successRate = 0.8,
    durabilityLoss = 10,
    requiredLevel = 78
  )

}
