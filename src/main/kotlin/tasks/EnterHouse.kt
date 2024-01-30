package tasks

import Constants.RIMMINGTON_HOUSE_PORTAL
import Task
import org.powbot.api.Condition
import org.powbot.api.rt4.GameObject
import org.powbot.api.rt4.Objects
import org.slf4j.LoggerFactory

class EnterHouse : Task() {
    override var name = "Entering house"
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun shouldExecute(): Boolean {
        return pohPortal().inViewport()
    }

    override fun execute() {
        logger.info("Entering POH")
        enter()
    }

    private fun enter() {
        if (pohPortal().interact("Build mode")) {
            Condition.wait( { !pohPortal().valid() }, 350, 5)
        }
    }

    private fun pohPortal() : GameObject {
        return Objects.stream().id(RIMMINGTON_HOUSE_PORTAL).first()
    }
}