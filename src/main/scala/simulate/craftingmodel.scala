package simulate

import scala.math
import types._

// The formulas in this file are taken from https://github.com/doxxx/ffxiv-craft-opt-web/blob/master/app/js/ffxivcraftmodel.js
// No idea how they were derived.
// TODO(colin): figure out derivation and verify?

object CraftingModel {
  def baseCrafterILeveL(stats: CharacterStats): Int = stats.level match {
    case 51 => 120
    case 52 => 125
    case 53 => 130
    case 54 => 133
    case 55 => 136
    case 56 => 139
    case 57 => 142
    case 58 => 145
    case 59 => 148
    case 60 => 150
    case 61 => 260
    case 62 => 265
    case 63 => 270
    case 64 => 273
    case 65 => 276
    case 66 => 279
    case 67 => 282
    case 68 => 285
    case 69 => 288
    case 70 => 290
    case 71 => 390
    case 72 => 395
    case 73 => 400
    case 74 => 403
    case 75 => 406
    case 76 => 409
    case 77 => 412
    case 78 => 415
    case 79 => 418
    case 80 => 420
    case _  => stats.level
  }

  def extraProgressPenality(iLevel: Int): Double = iLevel match {
    case 180       => -0.02
    case 210 | 220 => -0.035
    case 250       => -0.04
    case 320       => -0.02
    case 350       => -0.035
    case _         => 0.0
  }

  def baseProgressIncrease(stats: CharacterStats, item: CraftedItem): Double = {
    val crafterILevel = baseCrafterILeveL(stats)
    val baseProgress = stats.level match {
      case 80 =>
        1.3089353e-5 * math.pow(stats.craftsmanship, 2) +
          0.13336899 * stats.craftsmanship +
          0.48898
      case l if l > 70 =>
        1.834712812e-5 * math.pow(stats.craftsmanship, 2) +
          0.1892751 * stats.craftsmanship -
          1.232056 -
          (420 - crafterILevel) * stats.craftsmanship / 1750.0
      case l if l > 60 =>
        1.834712812e-5 * math.pow(stats.craftsmanship, 2) +
          0.1904074773 * stats.craftsmanship +
          1.544103837
      case _ => 0.214959 * stats.craftsmanship + 1.6
    }
    val levelDifference = crafterILevel - item.iLevel
    val levelAdjustment = levelDifference match {
      case d if d > 0 =>
        0.05 * math.max(math.min(levelDifference, 5), 0) +        // 0-5 levels different
          0.02 * math.max(math.min(levelDifference - 5, 10), 0) + // 5-15 levels different
          0.01 * math.max(math.min(levelDifference - 15, 5), 0) + // 15-20 levels different
          0.0006 * math.max(levelDifference - 20, 0)              // >20 levels different
      case d if d < 0 && stats.level > 70 =>
        0.0265 * math.max(levelDifference, -10)
      case d if d < 0 => 0.025 * math.max(levelDifference, -10)
      case _          => 0.0
    }
    val extraLevelPenality = if (levelDifference < 0 && stats.level <= 70) {
      extraProgressPenality(item.iLevel)
    } else {
      0
    }

    return baseProgress *
      (1 + math.floor(levelAdjustment * 100) / 100.0) * // round to nearest percent
      (1 + extraLevelPenality)
  }

  def baseQualityIncrease(stats: CharacterStats, item: CraftedItem): Double = {
    val baseQuality = if (stats.level == 80) {
      1.5210374e-5 * math.pow(stats.control, 2) +
        0.14993291 * stats.control +
        14.77637
    } else {
      3.46e-5 * math.pow(stats.control, 2) +
        0.3514 * stats.control +
        34.66
    }

    val recipeLevelPenality = item.iLevel match {
      case i if i >= 340 => -0.11 - 0.002 * (i - 340)
      case i if i >= 310 => -0.10 - 0.002 * (i - 310)
      case i if i >= 300 => -0.09 - 0.002 * (i - 300)
      case i if i >= 245 => -0.08 - 0.002 * (i - 245)
      case i if i >= 200 => -0.07 - 0.002 * (i - 200)
      case i if i >= 180 => -0.06 - 0.002 * (i - 180)
      case i if i >= 160 => -0.05 - 0.002 * (i - 160)
      case i if i >= 90  => -0.03 - 0.002 * (i - 90)
      case i if i >= 50  => -0.02 - 0.002 * i
      case i             => -0.00015 * i + 0.005
    }

    val levelDifference = baseCrafterILeveL(stats) - item.iLevel
    val levelDifferencePenalty = if (levelDifference < 0) {
      0.05 * math.max(levelDifference, -10.0)
    } else {
      0.0
    }

    return baseQuality * (1 + recipeLevelPenality) * (1 + levelDifferencePenalty)
  }

  def nameOfXProgressMultiplier(state: CraftingState): Double = {
    val percentComplete = math.floor(
      state.progress / state.item.difficulty * 100
    ) / 100
    math.max(-2 * percentComplete + 2, 0.1)
  }
}
