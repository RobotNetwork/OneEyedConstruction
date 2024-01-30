package tasks

import Constants.POH_PORTAL_INSIDE
import Constants.RIMMINGTON_HOUSE_PORTAL
import Task
import org.powbot.api.Condition
import org.powbot.api.rt4.Chat
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
        handleChat()
    }

    private fun enter() {
        if (pohPortal().interact("Build mode")) {
            Condition.wait( { Chat.chatting() }, 350, 5)
        }
    }

    private fun handleChat() {
        if (Chat.chatting() && Chat.completeChat("Build mode")) {
            Condition.wait( { exitPortal().valid() }, 3000, 5)
        }
    }

    private fun exitPortal() : GameObject {
        return Objects.stream().id(POH_PORTAL_INSIDE).first()
    }

    private fun pohPortal() : GameObject {
        return Objects.stream().id(RIMMINGTON_HOUSE_PORTAL).first()
    }
}