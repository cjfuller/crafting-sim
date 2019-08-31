package optimize

import scala.util.Random

import com.typesafe.scalalogging.Logger

import simulate.{Simulate, SimulationRunResult, Stats}
import types._
import Util.doProbabilistically

object Optimizer {
  val logger = Logger("optimizer")

  def makeNextFromBest(
    result: SimulationRunResult,
    allAbilities: Vector[Ability]
  ): Vector[Ability] = Population.swapNeighboringAbilitiesRandomly(result.steps)

  def makeNextFromTopQuarter(
    result: SimulationRunResult,
    allResults: Vector[SimulationRunResult],
    allAbilities: Vector[Ability]
  ): Vector[Ability] = {
    // If there is still room to add another ability, we do it with 70% probability.
    if (result.stats.averageRemaningCP >= allAbilities.map(_.CPCost).min) {
      doProbabilistically {
        case p if p < 0.7 =>
          Population.addAbilityRandomly(
            result.steps,
            allAbilities,
            result.stats.averageRemaningCP.toInt
          )
        case _ =>
          Population.mutateOneAbilityRandomly(
            result.steps,
            allAbilities
          )
      }
    } else {
      doProbabilistically {
        case p if p < 0.5 =>
          Population.swapNeighboringAbilitiesRandomly(result.steps)
        case p if p < 0.6 && result.steps.length > 1 =>
          Population.removeOneAbilityRandomly(result.steps)
        case p if p < 0.8 =>
          Population.doCrossover(
            result.steps,
            allResults(Random.nextInt(allResults.length)).steps
          )
        case _ =>
          Population.mutateOneAbilityRandomly(
            result.steps,
            allAbilities
          )
      }
    }
  }

  def makeNext(
    result: SimulationRunResult,
    allResults: Vector[SimulationRunResult],
    allAbilities: Vector[Ability]
  ): Vector[Ability] = doProbabilistically {
    case p if p < 0.7 =>
      Population.doCrossover(
        result.steps,
        allResults(Random.nextInt(allResults.length)).steps
      )
    case p if p < 0.8 && result.steps.length > 1 =>
      Population.removeOneAbilityRandomly(result.steps)
    case _ =>
      Population.mutateOneAbilityRandomly(result.steps, allAbilities)
  }

  def maybeAccept(
    old: SimulationRunResult,
    candidate: SimulationRunResult,
    temperature: Double
  ): SimulationRunResult = {
    val scoreDiff = Scoring.score(old) - Scoring.score(candidate)
    true match {
      case _ if candidate.steps.length == 0 => old
      case _ if scoreDiff < 0               => candidate
      case _ =>
        doProbabilistically {
          case p if p < math.exp(-scoreDiff / temperature) => candidate
          case _                                           => old
        }
    }
  }

  def makeNextGeneration(
    // Assumed to be sorted by score, ascending
    lastGeneration: Vector[SimulationRunResult],
    allAbilities: Vector[Ability],
    initialState: CraftingState,
    regenNumSteps: Int,
    flag: Flag,
    numSims: Int,
    temperature: Double
  ): Vector[SimulationRunResult] = {
    val nextGenerationCandidate = lastGeneration.reverse.zipWithIndex map {
      case (result, i) =>
        i match {
          // Always just keep the best result we've found so far
          case 0 => makeNextFromBest(result, allAbilities)
          // Top quarter, we either add an ability or do a mutation
          case _ if i < lastGeneration.length / 4 =>
            makeNextFromTopQuarter(result, lastGeneration, allAbilities)
          // 25% - 87.5% we either do crossover with another random population
          // member, or two mutations
          case _ if i < lastGeneration.length * 7 / 8 =>
            makeNext(result, lastGeneration, allAbilities)
          // For the worst ones, we replace with a random ability set.
          case _ =>
            Population.generateOneRandom(
              result.steps.length,
              Vector(),
              None,
              allAbilities
            )
        }
    } map (Population.truncate) map { steps =>
      if (steps.length == 0) {
        Population.generateOneRandom(
          regenNumSteps,
          Vector(),
          None,
          allAbilities
        )
      } else {
        steps
      }
    } map { steps =>
      Population.filterNonCastable(steps, initialState.stats.CP)
    } map { steps =>
      val states                 = Simulate.simulateN(initialState, steps, flag, numSims)
      val maxNumberStepsExecuted = states.map(_.stepsExecuted).max
      val worst =
        Simulate.simulateN(initialState, steps, Flag.WorstCase, 1).head
      Stats.calculateRunResult(
        initialState.item,
        steps.slice(0, maxNumberStepsExecuted),
        states,
        worst
      )
    }

    lastGeneration.zip(nextGenerationCandidate) map {
      case (last, next) =>
        maybeAccept(last, next, temperature)
    }
  }

  def optimizeFully(
    allAbilities: Vector[Ability],
    initialState: CraftingState,
    flag: Flag,
    numSims: Int
  ): SimulationRunResult = {
    var temperature     = initialState.item.maxQuality / 2.0
    val populationSize  = 100
    val initialNumSteps = 20
    var currentGeneration =
      Population.generateRandom(
        populationSize,
        initialNumSteps,
        None,
        allAbilities
      ) map { steps =>
        Stats.calculateRunResult(
          initialState.item,
          steps,
          Simulate.simulateN(initialState, steps, flag, numSims),
          Simulate.simulateN(initialState, steps, Flag.WorstCase, 1).head
        )

      }

    while (temperature > 1) {
      currentGeneration = currentGeneration.sortBy(Scoring.score)
      val best = currentGeneration.last.steps
      logger.info(
        s"Current temperature is $temperature\n" +
          s"Best crafting sequence is ${util.util.formatAbilities(best)}"
      )
      currentGeneration = makeNextGeneration(
        currentGeneration,
        allAbilities,
        initialState,
        initialNumSteps,
        flag,
        numSims,
        temperature
      )
      temperature *= 0.97
    }

    currentGeneration.sortBy(Scoring.score).last
  }
}
