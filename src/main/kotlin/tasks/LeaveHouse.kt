package tasks

import Config
import Constants.POH_PORTAL_INSIDE
import Task
import helpers.ConstructionHelpers
import helpers.Player
import org.powbot.api.Condition
import org.powbot.api.rt4.*
import org.slf4j.LoggerFactory

class LeaveHouse(private val config: Config) : Task() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override var name: String = "Leave house"
    private val consHelpers = ConstructionHelpers(config)
    private val playerHelpers = Player(config)

    override fun shouldExecute(): Boolean {
        return Inventory.isFull()
                && consHelpers.isBuilt()
                && playerHelpers.getItemToUnequip().first().valid() // item is equipped
    }

    override fun execute() {
        val exitPortal = Objects.stream().id(POH_PORTAL_INSIDE).first()
        moveToPortal(exitPortal)
        exit(exitPortal)
        playerHelpers.enableRun()
        dropItemIfNeeded()
    }

    private fun dropItemIfNeeded() {
        val pohPortal = consHelpers.getIndoorPOHPortal()
        if (pohPortal.valid() && pohPortal.inViewport()) {
            if (Inventory.stream().name(config.plankType).first().interact("Drop")) {
                logger.info("Item dropped")
            }
        }
    }

    private fun exit(portal: GameObject) {
        if (portal.inViewport() && portal.interact("Enter")) {
            Condition.wait { !portal.valid() }
        }
    }

    private fun moveToPortal(portal: GameObject) {
        if (portal.distance().toInt() > 7 && portal.valid()) {
            Movement.step(portal.tile())
            Condition.wait( { portal.distance().toInt() < 2 }, 1200, 40)
        }
    }
}