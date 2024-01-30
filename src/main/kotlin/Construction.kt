import org.powbot.api.event.InventoryItemActionEvent
import org.powbot.api.rt4.Game
import org.powbot.api.rt4.walking.model.Skill
import org.powbot.api.script.*
import org.powbot.api.script.paint.PaintBuilder
import org.powbot.mobile.script.ScriptManager
import tasks.*

@ScriptManifest(
    name = "OneEyedConstruction",
    description = "Builds stuff",
    version = "0.0.1",
    category = ScriptCategory.Construction,
    author = "Cyclop"
)

@ScriptConfiguration.List(
    [
        ScriptConfiguration(
            name = "Object to build",
            description = "",
            optionType = OptionType.STRING,
            allowedValues = ["Oak door", "Oak larder", "Mythical cape", "Teak table", "Mahogany table"],
            defaultValue = "Mythical cape",
        ),
        ScriptConfiguration(
            name = "Item to equip",
            description = "Long press -> `Use` on the item you wish to equip",
            optionType = OptionType.INVENTORY_ACTIONS,
        )
    ]
)

class Construction : AbstractScript() {
    private var tasks = ArrayList<Task>()

    override fun poll() {
        if (Game.loggedIn()) {
            for (task in tasks) {
                if (task.shouldExecute()) {
                    logger.info("Executing task ${task.name}")
                    task.execute()
                }
            }
        }
    }

    override fun onStart() {
        addPaint()
        val toBuild = getOption<String>("Object to build")
        val toEquip = getOption<ArrayList<InventoryItemActionEvent>>("Item to equip")[0].name
        val config = buildConfig(toBuild, toEquip)
        tasks.add(EnterHouse())
        tasks.add(MoveToObject(config))
        tasks.add(RemoveObject(config))
        tasks.add(BuildObject(config))
        tasks.add(Butler(config))
        tasks.add(LeaveHouse(config))
    }

    private fun buildConfig(toBuild: String, toEquip: String): Config {
        val build = Constants.buildables.first { it.objectName == toBuild }
        return Config(
            build.objectName,
            build.objectHotspotID,
            build.builtObjectID,
            build.plankType,
            build.plankAmount,
            build.secondary,
            butlerBanking = null,
            planksRemaining = null,
            itemToEquip = toEquip
        )
    }

    override fun onStop() {
        ScriptManager.stop()
    }

    private fun addPaint() {
        val paint = PaintBuilder.newBuilder()
            .trackSkill(Skill.Construction)
            .x(50)
            .y(50)
            .build()
        addPaint(paint)
    }
}

fun main() {
//    ScriptUploader().uploadAndStart(
//        "OneEyedConstruction",
//        "Main",
//        "emulator-5554",
//        portForward = true,
//    )
    Construction().startScript()
}