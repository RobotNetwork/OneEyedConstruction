package tasks

import Config
import Constants.POH_PORTAL_INSIDE
import Constants.REMMINTON_HOUSE_PORTAL
import Task
import helpers.ConstructionHelpers
import org.powbot.api.Condition
import org.powbot.api.rt4.*
import org.powbot.api.rt4.stream.item.EquipmentItemStream
import org.slf4j.LoggerFactory

class LeaveHouse(private val config: Config) : Task() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override var name: String = "Leave house"
    private val consHelpers = ConstructionHelpers(config)

    override fun shouldExecute(): Boolean {
        return Inventory.isFull()
                && consHelpers.isBuilt()
                && getItemToUnequip(config.itemToEquip).first().valid() // item is equipped
    }

    override fun execute() {
        val exitPortal = Objects.stream().id(POH_PORTAL_INSIDE).first()
        moveToPortal(exitPortal)
        exit(exitPortal)
        enableRun()
        dropItemIfNeeded()
    }

    private fun enableRun() {
        if (!Movement.running() && Movement.running(true)) {
            logger.info("Enabling run...")
            Condition.wait({Movement.running()}, 250, 6)
        }
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
            Condition.wait { Objects.stream().id(REMMINTON_HOUSE_PORTAL).first().valid() }
        }
    }

    private fun moveToPortal(portal: GameObject) {
        Movement.step(portal.tile())
        Condition.wait( { portal.distance().toInt() < 2 }, 1000, 20)
    }

    private fun getItemToUnequip(itemName: String?) : EquipmentItemStream {
        return Equipment.stream().name(itemName)
    }
}