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

object Main {
  def itemsByClass(cls: String): Map[String, CraftedItem] = cls match {
    case "weaver"     => weaver.Items.allByName
    case "culinarian" => culinarian.Items.allByName
  }
  val abilitiesByClass = Map(
    "weaver"     -> weaver.Abilities.all,
    "culinarian" -> culinarian.Abilities.all
  )
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
        val character: CharacterStats = CharacterStats(
          craftsmanship = opts.craftsmanship.get,
          control = opts.control.get,
          CP = opts.cp.get,
          level = opts.level.get
        )
        val item = itemsByClass(opts.cls.get)
          .get(opts.item)
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
          abilitiesByClass(opts.cls.get).filter(_.requiredLevel <= 73),
          initialState,
          Flag.Probabilistic,
          20
        )
        println(
          f"Best result is: ${formatAbilities(best.steps)} with " +
            f"expected quality ${best.stats.averageQuality} and success rate ${best.stats.successRate}"
        )

        if (opts.showExample) {

          println("One simulation example:")
          Simulate
            .simulate(initialState, best.steps.toList, Flag.Probabilistic, true)

        }
        println(
          makeMacro(best.steps)
        )
    }
  }
}
