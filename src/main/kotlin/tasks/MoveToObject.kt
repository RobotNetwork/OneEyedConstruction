package tasks

import Config
import Constants.POH_DUNGEON_ENTRANCE
import Task
import helpers.ConstructionHelpers
import helpers.Player
import org.powbot.api.Condition
import org.powbot.api.rt4.GameObject
import org.powbot.api.rt4.Movement
import org.powbot.api.rt4.Objects
import org.powbot.api.rt4.Players
import org.slf4j.LoggerFactory

class MoveToObject(private val config: Config) : Task() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override var name: String = "MoveToObject"
    private val consHelpers = ConstructionHelpers(config)
    private val playerHelpers = Player(config)

    override fun shouldExecute(): Boolean {
        val toGoto = walkWhere()
        return toGoto.distanceTo(Players.local().tile()) > 7
                && (toGoto.valid() || consHelpers.isOakDoorSelected())
    }

    override fun execute() {
        playerHelpers.enableRun()
        if (consHelpers.isOakDoorSelected()) {
            enterDungeon()
            return
        }
        walkToObjectTile()
    }

    private fun enterDungeon() {
        // enters the dungeon if Oak doors are selected
        val dungeon = Objects.stream().id(POH_DUNGEON_ENTRANCE).first()
        logger.info("Walking to and entering dungeon")
        if (dungeon.valid()) {
            Movement.walkTo(dungeon.tile())
            Condition.wait({ dungeon.distanceTo(Players.local().tile()) <= 3 }, 550, 15 )
            dungeon.interact("Enter")
        }
    }

    private fun walkWhere() : GameObject {
        // identifies where it walks to, the hotspot or the removable object
        val objectToRemove = consHelpers.getObjectToRemove()
        val hotspotObject = consHelpers.getHotspot()

        if (objectToRemove.valid()) {
            return objectToRemove
        } else if (hotspotObject.valid()) {
            return hotspotObject
        } else {
            logger.info("Cannot locate buildable object")
            return GameObject.Nil
        }
    }

    private fun walkToObjectTile() {
        val target = walkWhere()
        Movement.step(target.tile())
        logger.info("Hotspot we need to build at is further than 7 tiles away, moving player to it.")
        Condition.wait({ target.distance().toInt() < 3 }, 1000, 15)
    }
}