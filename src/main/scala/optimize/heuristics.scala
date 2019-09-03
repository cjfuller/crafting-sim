package optimize

import simulate.Simulate
import simulate.SimulationRunResult
import simulate.Stats
import types._

sealed trait Rule

object Heuristic {
  val possibleNoOps = Set(
    data.SharedAbilities.Observe,
    data.SharedAbilities.Rumination
  )
  def tryRemoveNoOps(
    generation: Vector[SimulationRunResult],
    initialState: CraftingState,
    temperature: Double,
    flag: Flag,
    numSims: Int
  ): Vector[SimulationRunResult] = {
    val nextGen = generation.map { simResult =>
      // TODO(colin): refactor out shared code with the optimizer
      val withoutPossibleNoOps =
        simResult.steps.filter(!possibleNoOps.contains(_))
      val states =
        Simulate.simulateN(initialState, withoutPossibleNoOps, flag, numSims)
      val maxNumberStepsExecuted = states.map(_.stepsExecuted).max
      val worst =
        Simulate
          .simulateN(initialState, withoutPossibleNoOps, Flag.WorstCase, 1)
          .head
      Stats.calculateRunResult(
        initialState.item,
        withoutPossibleNoOps.slice(0, maxNumberStepsExecuted),
        states,
        worst
      )
    }
    generation.zip(nextGen) map {
      case (last, next) =>
        Optimizer.maybeAccept(last, next, temperature)
    }
  }
}
