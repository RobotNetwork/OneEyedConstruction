package tasks

import Config
import Constants.HOUSE_OPTIONS_WIDGET
import Constants.SETTINGS_TAB_CONTROL_WIDGET
import Task
import org.powbot.api.Condition
import org.powbot.api.Input
import org.powbot.api.Notifications
import org.powbot.api.Random
import org.powbot.api.rt4.*
import org.powbot.mobile.script.ScriptManager
import org.slf4j.LoggerFactory

class Butler(private val config: Config) : Task() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override var name: String = "Butler"

    override fun shouldExecute(): Boolean {
        val randChance = Random.nextBoolean()
        val plankCount = Inventory.stream().name(config.plankType).count().toInt()
        val planksToAvoidDowntime = (config.plankAmount * 2) + Random.nextInt(2, 6)

        val notEnoughToBuild = plankCount < config.plankAmount
        val shouldBeEfficient = plankCount < planksToAvoidDowntime

        return if (randChance) notEnoughToBuild else shouldBeEfficient
    }

    override fun execute() {
        callButlerIfNeeded()
        clickButlerIfNeeded()
        handleChatIfNeeded()
    }

    private fun callButlerIfNeeded() {
        if (!findButlerNpc().inViewport() || !findButlerNpc().valid() || findButlerNpc().distance().toInt() > 2 || !findButlerNpc().reachable()) {
            callButler()
            Condition.wait({ Chat.chatting() }, 100, 9)
        }
    }

    private fun clickButlerIfNeeded() {
        if (!Chat.chatting() && findButlerNpc().interact("Talk-to")) {
            Condition.wait({ Chat.chatting() }, 100, 9)
        }
    }

    private fun handleChatIfNeeded()  {
        if (Chat.chatting()) {
            handleChat()
        }
    }

    private fun handleChat() {
        logger.info("--Handling chat--")
        if (Chat.getChattingName() == "Demon butler") {
            config.butlerBanking = false
        }
        // message variants when talking to butler
        val needsMoney = "Master, if thou desirest my continued service,"
        val fetchPlank = "Fetch from bank: 25 x ${config.plankType}"
        val hasNoneLeft = "I have returned with what you asked me to"
        val noMorePlanksInBank = "My Lord, I cannot conjure items out of the air"

        val chatMessage = Chat.getChatMessage()
        if (Chat.chatting()) {
            setButlerTask()
        }

        if (chatMessage.contains(noMorePlanksInBank)) {
            Notifications.showNotification("No more planks in the bank!")
            logger.info("No more planks in the bank!")
            ScriptManager.stop()
        }

        if (chatMessage.contains(needsMoney)) {
            logger.info("Butler needs money, paying him")
            Chat.completeChat("Okay, here's 10,000 coins.", "Thanks")
            return
        }

        if ((chatMessage.contains(hasNoneLeft) && findButlerNpc().interact("Talk-to")) || Chat.stream().text(fetchPlank).first().valid()) {
            val canSendToBank = Condition.wait({ Chat.chatting() && Chat.stream().text(fetchPlank).first().valid() }, 350, 10)

            if (canSendToBank) {
                logger.info("Chat option to send to bank was visible or made visible by interacting with the butler.")
                if (Chat.continueChat(fetchPlank)) {
                    config.butlerBanking = true
                    return
                }
            }
        }
    }

    private fun callButler() {
        if (config.butlerBanking == true) return
        if (Game.tab(Game.Tab.SETTINGS)) {
            revealHouseOptionsWidget()
            if (Condition.wait { houseOptions().visible() }) {
                if (houseOptions().click() && Condition.wait { callServantOption().visible() }) {
                    // two clicks works, one doesn't, don't know why :)
                    callServantOption().click()
                    callServantOption().click()
                    Condition.wait({findButlerNpc().distanceTo(Players.local().tile()).toInt() <= 2 }, 250, 10)
                }
            }
        }
    }

    private fun setButlerTask() {
        val chatMsg = Chat.stream().textContains("Fetch from bank: 25 x ").first().text()
        logger.info(chatMsg)
        if (!chatMsg.contains(config.plankType)) {
            logger.info("Setting butler task...")
            Chat.completeChat("Something else...", "Go to the bank...",
                "Bring something from the bank", config.plankType + "s")
            Condition.wait({ Chat.pendingInput() }, 350, 20)
            if (Chat.pendingInput()) {
                Input.sendln("25")
            }
        }
        return
    }

    private fun revealHouseOptionsWidget() {
        val cog = SETTINGS_TAB_CONTROL_WIDGET
        if (cog.actions().contains("Controls") && cog.click()) {
            logger.info("tab not active, clicking tab")
        }
    }

    private fun houseOptions(): Component {
        return HOUSE_OPTIONS_WIDGET
    }

    private fun callServantOption(): Component {
        return Widgets.component(370, 22)
    }

    private fun findButlerNpc(): Npc {
        return Npcs.stream().name("Demon butler").first()
    }
}