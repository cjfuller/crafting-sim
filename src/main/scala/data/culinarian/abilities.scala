package data.culinarian

import types._
import data.SharedAbilities

object Abilities {

  val HastyTouch = Ability(
    name = "Hasty Touch",
    CPCost = 0,
    abilityType = AbilityType.Quality,
    efficiency = 1.0,
    successRate = 0.5,
    durabilityLoss = 10,
    requiredLevel = 15
  )

  val MuscleMemory = Ability(
    name = "Muscle Memory",
    CPCost = 6,
    abilityType = AbilityType.Special,
    efficiency = 1.0,
    successRate = 1.0,
    durabilityLoss = 10, // TODO(colin): verify durability loss
    requiredLevel = 54,
    specialEffect = (state, ability, flag) => {
      val progressGain = if (state.stepsExecuted == 0) {
        math.min(1000, state.item.difficulty * 0.33)
      } else {
        0
      }

      state.copy(
        progress = state.progress + math.round(progressGain).intValue()
      )
    }
  )

  val all: Vector[Ability] = Vector(
    SharedAbilities.BasicSynthesis,
    SharedAbilities.BasicTouch,
    SharedAbilities.MastersMend,
    SharedAbilities.SteadyHand,
    SharedAbilities.InnerQuiet,
    SharedAbilities.Observe,
    HastyTouch,
    SharedAbilities.StandardTouch,
    SharedAbilities.GreatStrides,
    SharedAbilities.MastersMend2,
    SharedAbilities.StandardSynthesis,
    SharedAbilities.SteadyHand2,
    // TODO(colin): figure out bug with Brand/Name and reenable
    //SharedAbilities.BrandOfTheElements,
    //SharedAbilities.NameOfTheElements,
    SharedAbilities.AdvancedTouch,
    MuscleMemory,
    SharedAbilities.HastyTouch2,
    SharedAbilities.CarefulSynthesis3,
    SharedAbilities.RapidSynthesis2,
    SharedAbilities.PatientTouch,
    SharedAbilities.Manipulation2,
    SharedAbilities.PrudentTouch,
    SharedAbilities.FocusedSynthesis,
    SharedAbilities.FocusedTouch,
    SharedAbilities.PreparatoryTouch,
    SharedAbilities.RapidSynthesis3,
    SharedAbilities.DelicateSynthesis,
    SharedAbilities.TrainedEye,
    SharedAbilities.TrainedInstinct,
    // TODO(colin): temporary cross-class ability hack; implement cross-class
    // abilities and disable.
    data.weaver.Abilities.CarefulSynthesis,
    data.weaver.Abilities.CarefulSynthesis2
  )
}
