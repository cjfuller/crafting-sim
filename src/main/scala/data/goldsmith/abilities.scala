package data.goldsmith

import types._
import data.SharedAbilities

object Abilities {
  val all: Vector[Ability] = Vector(
    SharedAbilities.BasicSynthesis,
    SharedAbilities.BasicTouch,
    SharedAbilities.MastersMend,
    SharedAbilities.SteadyHand,
    SharedAbilities.InnerQuiet,
    SharedAbilities.Observe,
    SharedAbilities.StandardTouch,
    SharedAbilities.GreatStrides,
    SharedAbilities.MastersMend2,
    SharedAbilities.StandardSynthesis,
    SharedAbilities.SteadyHand2,
    // TODO(colin): figure out bug with Brand/Name and reenable
    //SharedAbilities.BrandOfTheElements,
    //SharedAbilities.NameOfTheElements,
    SharedAbilities.AdvancedTouch,
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
