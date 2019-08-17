package craftingsim

import _root_.types._
import data.weaver
import simulate.Simulate

object Main extends App {
  val character: CharacterStats = CharacterStats(
    craftsmanship = 900,
    control = 900,
    CP = 471,
    level = 70
  )
  val initialState = CraftingState(
    stats = character,
    item = weaver.Items.SergeHood,
    remainingCP = character.CP,
    progress = 0,
    quality = 0,
    durability = weaver.Items.KudzuTunicofAiming.durability,
    activeEffects = List(),
    condition = Condition.Normal,
    stepsExecuted = 0
  )
  import weaver.Abilities._
  val sims = Simulate.simulateN(
    initialState,
    List(
      InnerQuiet,
      SteadyHand,
      HastyTouch2,
      PatientTouch,
      BasicTouch,
      SteadyHand,
      HastyTouch2,
      PrudentTouch,
      HastyTouch2,
      HastyTouch2,
      HastyTouch2,
      MastersMend2,
      CarefulSynthesis2,
      CarefulSynthesis3,
      CarefulSynthesis2,
      CarefulSynthesis3,
      CarefulSynthesis2,
      CarefulSynthesis2,
      MastersMend2,
      CarefulSynthesis2,
      CarefulSynthesis2,
      CarefulSynthesis2,
      CarefulSynthesis2,
      CarefulSynthesis2,
      CarefulSynthesis2,
      CarefulSynthesis2
    ),
    Flag.Probabilistic,
    1000
  )
  println(sims)

  println(
    simulate.Stats
      .calculateCraftingStats(simulate.Stats.calculateSummaires(sims))
  )
}
