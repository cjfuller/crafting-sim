package optimize

import simulate.SimulationRunResult

object Scoring {
  // TODO(colin): different scoring functions for:
  // 1. if we don't care about 100% success
  // 2. if we care about max / min quality instead
  def score(srr: SimulationRunResult): Double =
    if (srr.stats.successRate == 1.0 && srr.worstCaseStats.successRate == 1.0) {
      srr.stats.averageQuality
    } else {
      -srr.item.maxQuality + srr.stats.averageQuality
    }
}
