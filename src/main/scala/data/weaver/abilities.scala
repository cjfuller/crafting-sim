package data.weaver
import data.SharedAbilities
import types._
import types.Implicits._

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
    SharedAbilities.TrainedInstinct
  )
}
