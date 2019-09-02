package data.carpenter

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
    SharedAbilities.Rumination,
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
    SharedAbilities.TrainedInstinct,
    // TODO(colin): temporary cross-class ability hack; implement cross-class
    // abilities and disable.
    data.weaver.Abilities.CarefulSynthesis,
    data.weaver.Abilities.CarefulSynthesis2
  )
}
