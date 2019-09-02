package craftingsim

import _root_.types._
import data.culinarian
import data.weaver
import optimize.Optimizer
import simulate.Simulate
import simulate.Stats
import _root_.util.util._

import org.backuity.clist._

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

  var item = arg[String](description = "item to craft")
}

case class StatsRequest(
  val level: Int,
  val craftsmanship: Int,
  val control: Int,
  val cp: Int
)

case class OptimizeRequest(
  val cls: String,
  val stats: StatsRequest,
  val item: String
)

object Main {
  val abilitiesByClass = Map(
    "weaver"     -> weaver.Abilities.all,
    "culinarian" -> culinarian.Abilities.all
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
    val best = Optimizer.optimizeFully(
      abilitiesByClass(req.cls)
        .filter(_.requiredLevel <= character.level),
      initialState,
      Flag.Probabilistic,
      20
    )
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
    (output :+ makeMacro(best.steps)).mkString("\n")
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
          item = opts.item
        )
        println(run(req, includeExample = opts.showExample))
    }
  }
}
