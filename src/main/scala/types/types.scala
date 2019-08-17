package types

object TypeDefs {
  type EffectFn =
    (Effect, CraftingState, Ability) => (CraftingState, ModifierT)
  type PostEffectFn =
    (Effect, CraftingState, CraftingState, Ability) => (
      CraftingState,
      ModifierT
    )

}
import TypeDefs._

object Implicits {
  implicit def effectToPostEffect(f: EffectFn): PostEffectFn =
    (e, lastState, currState, ability) => f(e, currState, ability)
}
import Implicits._

sealed trait Condition
object Condition {
  case object Excellent extends Condition
  case object Good      extends Condition
  case object Normal    extends Condition
  case object Poor      extends Condition
}

sealed trait AbilityType
object AbilityType {
  case object CP         extends AbilityType
  case object Progress   extends AbilityType
  case object Quality    extends AbilityType
  case object Durability extends AbilityType
  case object Buff       extends AbilityType
  case object Special    extends AbilityType
}

sealed trait Flag
object Flag {
  case object Probabilistic extends Flag
  case object WorstCase     extends Flag
  case object BestCase      extends Flag
}

case class CharacterStats(
  craftsmanship: Int,
  control: Int,
  CP: Int,
  level: Int
) {
  def +(other: CharacterStats): CharacterStats = CharacterStats(
    this.craftsmanship + other.craftsmanship,
    this.control + other.control,
    this.CP + other.CP,
    this.level + other.level
  )
}

case class CraftedItem(
  characterLevel: Int,
  iLevel: Int,
  difficulty: Int,
  durability: Int,
  maxQuality: Int,
  name: String
)

sealed trait ModifierT

case class Modifier(
  successRate: Double,
  efficiency: Double,
  stats: CharacterStats,
  buffs: Map[String, Int]
) extends ModifierT {
  def +(other: Modifier): Modifier = Modifier(
    this.successRate + other.successRate,
    this.efficiency + other.efficiency,
    this.stats + other.stats,
    other.buffs ++ this.buffs.map {
      case (k: String, v: Int) =>
        (k, other.buffs.getOrElse(k, 0))
    }
  )
}
object Modifier {
  val NoModifier = Modifier(0.0, 0.0, CharacterStats(0, 0, 0, 0), Map())
  def successRateBoost(boost: Double) =
    Modifier(boost, 0.0, CharacterStats(0, 0, 0, 0), Map())
  def efficiencyBoost(boost: Double) =
    Modifier(0.0, boost, CharacterStats(0, 0, 0, 0), Map())
  def statsBoost(
    craftsmanship: Int = 0,
    control: Int = 0,
    CP: Int = 0,
    level: Int = 0
  ) =
    Modifier(0.0, 0.0, CharacterStats(craftsmanship, control, CP, level), Map())
  def buff(name: String, count: Int) =
    Modifier(0.0, 0.0, CharacterStats(0, 0, 0, 0), Map(name -> count))
}

object Keep extends ModifierT

case class Effect(
  preFn: EffectFn,
  postFn: PostEffectFn,
  duration: Int
)
object Effect {
  val NoEffectFn: EffectFn =
    (e: Effect, state: CraftingState, ability: Ability) =>
      (state, Modifier.NoModifier)
  val KeepEffectFn: EffectFn =
    (e: Effect, state: CraftingState, ability: Ability) => (state, Keep)
  val NoEffect = Effect(
    NoEffectFn,
    NoEffectFn,
    0
  )
}

case class CraftingState(
  stats: CharacterStats,
  item: CraftedItem,
  remainingCP: Int,
  progress: Int,
  quality: Int,
  durability: Int,
  activeEffects: List[Effect],
  stepsExecuted: Int,
  condition: Condition,
  modifiers: Map[Effect, Modifier] = Map()
) {
  def modifier: Modifier = modifiers.foldLeft(Modifier.NoModifier) {
    case (acc, (_, mod)) => acc + mod
  }

  def withEffectRemoved(e: Effect): CraftingState =
    copy(
      activeEffects = activeEffects.filterNot(_ == e),
      modifiers = modifiers.filterNot { case (k, v) => k == e }
    )
}

case class Ability(
  name: String,
  CPCost: Int,
  abilityType: AbilityType,
  efficiency: Double,
  successRate: Double,
  durabilityLoss: Int,
  requiredLevel: Int,
  onceOnly: Boolean = false,
  otherEffect: Effect = Effect.NoEffect,
  specialEffect: (CraftingState, Ability, Flag) => CraftingState = (s, a, f) =>
    s
)
