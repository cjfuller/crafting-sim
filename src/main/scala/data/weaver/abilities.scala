package data.weaver
import data.SharedAbilities
import types._
import types.Implicits._
import simulate.Simulate

import scala.util.chaining._
import java.util.UUID

object Abilities {
  val CarefulSynthesis = Ability(
    name = "Careful Synthesis",
    CPCost = 0,
    abilityType = AbilityType.Progress,
    efficiency = 0.9,
    successRate = 1.0,
    durabilityLoss = 10,
    requiredLevel = 15
  )

  val CarefulSynthesis2 = Ability(
    name = "Careful Synthesis II",
    CPCost = 0,
    abilityType = AbilityType.Progress,
    efficiency = 1.2,
    successRate = 1.0,
    durabilityLoss = 10,
    requiredLevel = 50
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

  // TODO(colin): implement initial preparations
  // val InitialPreparations = ...
  // TODO(colin): implement reuse

  val all: Vector[Ability] = Vector(
    SharedAbilities.BasicSynthesis,
    SharedAbilities.BasicTouch,
    SharedAbilities.MastersMend,
    SharedAbilities.SteadyHand,
    SharedAbilities.InnerQuiet,
    SharedAbilities.Observe,
    CarefulSynthesis,
    SharedAbilities.StandardTouch,
    SharedAbilities.GreatStrides,
    SharedAbilities.MastersMend2,
    SharedAbilities.StandardSynthesis,
    // TODO(colin): figure out bug with Brand/Name and reenable
    //SharedAbilities.BrandOfTheElements,
    //SharedAbilities.NameOfTheElements,
    SharedAbilities.SteadyHand2,
    SharedAbilities.AdvancedTouch,
    CarefulSynthesis2,
    SharedAbilities.ByregotsBlessing,
    HastyTouch2,
    CarefulSynthesis3,
    RapidSynthesis2,
    PatientTouch,
    Manipulation2,
    PrudentTouch,
    SharedAbilities.FocusedSynthesis,
    SharedAbilities.FocusedTouch,
    PreparatoryTouch,
    RapidSynthesis3,
    DelicateSynthesis,
    SharedAbilities.TrainedEye,
    SharedAbilities.TrainedInstinct
  )
}
