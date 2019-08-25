package simulate

import types._

case class ResultSummary(success: Boolean, quality: Int, remainingCP: Int)

case class CraftingStats(
  successRate: Double,
  minQuality: Int,
  maxQuality: Int,
  averageQuality: Double,
  averageRemaningCP: Double,
  n: Int
)

object Stats {
  def isSuccess(state: CraftingState): Boolean =
    state.progress > state.item.difficulty

  def calculateSummaries(result: List[CraftingState]): List[ResultSummary] =
    result map { r =>
      ResultSummary(isSuccess(r), r.quality, r.remainingCP)
    }

  def calculateCraftingStats(summaries: List[ResultSummary]) = CraftingStats(
    successRate = summaries.filter(_.success).size * 1.0 / summaries.size,
    minQuality = summaries.map(_.quality).min,
    maxQuality = summaries.map(_.quality).max,
    averageQuality = summaries.map(_.quality).sum * 1.0 / summaries.size,
    averageRemaningCP = summaries.map(_.remainingCP).sum * 1.0 / summaries.size,
    n = summaries.size
  )

  def calculateRunResult(
    item: CraftedItem,
    steps: Vector[Ability],
    result: List[CraftingState]
  ) =
    SimulationRunResult(
      item,
      steps,
      calculateCraftingStats(calculateSummaries(result))
    )
}

case class SimulationRunResult(
  item: CraftedItem,
  steps: Vector[Ability],
  stats: CraftingStats
)
