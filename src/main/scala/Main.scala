package craftingsim

import _root_.types._
import data.{
  weaver,
  culinarian,
  carpenter,
  alchemist,
  armorer,
  blacksmith,
  goldsmith,
  leatherworker
}
import optimize.Optimizer
import optimize.Scoring
import scalacache._
import scalacache.caffeine._
import scalacache.modes.try_._
import scala.util.{Try, Success}
import simulate.Simulate
import simulate.Stats
import _root_.util.util._

import org.backuity.clist._
import com.github.benmanes.caffeine.cache.Caffeine

class CLI extends Command(description = "run a crafting optimizer") {
  var showExample =
    opt[Boolean](abbrev = "e", description = "show an example simulation run?")
  var craftsmanship =
    opt[Option[Int]](description = "your crafter's craftsmanship")
  var control =
    opt[Option[Int]](description = "your crafter's control")
  var cp =
    opt[Option[Int]](description = "your crafter's CP")
  var level =
    opt[Option[Int]](description = "your crafter's level")
  var cls =
    opt[Option[String]](description = "your crafter's class (lower case)")
  var crossClass =
    opt[Option[String]](
      description = "comma-delimited list of cross-class abilities to include"
    )

  var item = arg[String](description = "item to craft")
}

case class StatsRequest(
  val level: Int,
  val craftsmanship: Int,
  val control: Int,
  val cp: Int
) {
  def key(): String =
    s"StatsRequest!${level}:${craftsmanship}:${control}:${cp}"
}

case class OptimizeRequest(
  val cls: String,
  val stats: StatsRequest,
  val item: String,
  val crossClass: List[String]
) {
  def key(): String =
    s"""OptimizeRequest!${cls}::${stats.key}::${item}::${crossClass.mkString(
      ","
    )}"""
}

case class CachedResult(
  val score: Double,
  val content: String
)

object Main {
  val underlyingCaffeineCache = Caffeine
    .newBuilder()
    .maximumSize(1000L)
    .build[String, Entry[CachedResult]]
  implicit val caffeineCache: Cache[CachedResult] =
    CaffeineCache(underlyingCaffeineCache)

  val abilitiesByClass = Map(
    "weaver"        -> weaver.Abilities.all,
    "culinarian"    -> culinarian.Abilities.all,
    "carpenter"     -> carpenter.Abilities.all,
    "alchemist"     -> alchemist.Abilities.all,
    "armorer"       -> armorer.Abilities.all,
    "blacksmith"    -> blacksmith.Abilities.all,
    "goldsmith"     -> goldsmith.Abilities.all,
    "leatherworker" -> leatherworker.Abilities.all
  )

  def run(req: OptimizeRequest, includeExample: Boolean = false): String = {
    val character: CharacterStats = CharacterStats(
      craftsmanship = req.stats.craftsmanship,
      control = req.stats.control,
      CP = req.stats.cp,
      level = req.stats.level
    )
    val item = data.ItemReader
      .itemsByClass(req.cls)
      .get(req.item)
      .orElse({ throw new Exception("Item not found") })
      .get

    val initialState = CraftingState(
      stats = character,
      item = item,
      remainingCP = character.CP,
      progress = 0,
      quality = 0,
      durability = item.durability,
      activeEffects = List(),
      condition = Condition.Normal,
      stepsExecuted = 0
    )
    val allAbilities = abilitiesByClass.values.flatten
    val crossClass = req.crossClass
      .flatMap({ name =>
        allAbilities.filter(_.name == name).take(1)
      })
      .map(_.copy(requiredLevel = 1))
    val availableAbilities = (abilitiesByClass(req.cls)
      .filter(_.requiredLevel <= character.level)) ++ crossClass
    val best = Optimizer.optimizeFully(
      availableAbilities,
      initialState,
      Flag.Probabilistic,
      20
    )
    val bestScore = Scoring.score(best)
    val cached    = get(req.key)
    cached match {
      case Success(Some(CachedResult(score, content))) if score > bestScore =>
        return content
      case _ => ()
    }

    var output = Vector(
      f"Best result is: ${formatAbilities(best.steps)} with " +
        f"expected quality ${best.stats.averageQuality} and success rate ${best.stats.successRate}"
    )

    if (includeExample) {
      // TODO(colin): implement returning the debug example
      println("One simulation example:")
      Simulate
        .simulate(initialState, best.steps.toList, Flag.Probabilistic, true)
    }
    val resultString = (output :+ makeMacro(best.steps)).mkString("\n")
    put(req.key)(CachedResult(bestScore, resultString), ttl = None)
    resultString
  }
  def main(args: Array[String]) {
    Cli.parse(args).withCommand(new CLI) {
      case opts =>
        if (List(
              opts.craftsmanship,
              opts.control,
              opts.cp,
              opts.level,
              opts.cls
            ).exists(_ == None)) {
          throw new Exception(
            "You must provide all of craftsmanship, control, cp, class, and level."
          )
        }
        val req = OptimizeRequest(
          cls = opts.cls.get,
          stats = StatsRequest(
            level = opts.level.get,
            control = opts.control.get,
            cp = opts.cp.get,
            craftsmanship = opts.craftsmanship.get
          ),
          item = opts.item,
          crossClass =
            opts.crossClass.map(_.split(",").toList).getOrElse(List())
        )
        println(run(req, includeExample = opts.showExample))
    }
  }
}
