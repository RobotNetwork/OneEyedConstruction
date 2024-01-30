package tasks

import Config
import Task
import helpers.ConstructionHelpers
import helpers.Player
import org.powbot.api.Condition
import org.powbot.api.Random
import org.powbot.api.rt4.*
import org.slf4j.LoggerFactory

class RemoveObject(private val config: Config) : Task() {
    override var name: String = "RemoveObject"
    private val consHelpers = ConstructionHelpers(config)
    private val playerHelpers = Player(config)

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun shouldExecute(): Boolean {
        val objectToRemove = consHelpers.getObjectToRemove()
        return objectToRemove.valid() && objectToRemove.distanceTo(Players.local().tile()) < 4
    }

    override fun execute() {
//        consHelpers.angleCameraToObject()
        playerHelpers.disableRun()
        equipItemIfNeeded()
        remove()
        unequipItemIfNeeded()
        if (Random.nextBoolean()) {
            // random chance to open/view inventory
            Game.tab(Game.Tab.INVENTORY)
            return
        }
    }

    private fun remove() {
        val objectToRemove = consHelpers.getObjectToRemove()
        if (consHelpers.isMythicalCapeSelected()) {
            objectToRemove.bounds(37,48,-204,-82,-24,22)
        }
        if (Chat.chatting()) {
            handleChat()
        }
        if (objectToRemove.interact("Remove") && Condition.wait({ Chat.chatting() }, 350, 15)) {
            handleChat()
        }
    }

    private fun equipItemIfNeeded() {
        // equips the user selected item
        val item = playerHelpers.getItemToEquip()
        if (!consHelpers.isMythicalCapeSelected() || (!Inventory.isFull() || !item.valid())) return

        if (Game.tab(Game.Tab.INVENTORY) && consHelpers.isBuilt() && Inventory.isFull()) {
            val action = if (item.actions().contains("Wear")) "Wear" else "Wield"
            if (item.interact(action)) {
                Condition.wait( { !item.valid() }, 300, 6)
                logger.info("Equipped item: $item")
            }
        }
    }

    private fun handleChat() {
        if (Chat.chatting() && Chat.continueChat("Yes")) {
            Condition.wait( { consHelpers.getHotspot().valid() }, 350, 6)
            return
        }
    }

    private fun unequipItemIfNeeded() {
        if (!consHelpers.isMythicalCapeSelected() || playerHelpers.getItemToEquip().valid()) return

        val itemToUnequip = playerHelpers.getItemToUnequip()

        if (!playerHelpers.getItemToEquip().valid() && Game.tab(Game.Tab.EQUIPMENT) && Condition.wait( { itemToUnequip.viewable().first().valid() }, 300, 6)) {
            if (!Inventory.isFull() && itemToUnequip.first().click()) {
                logger.info("Unequipping ${config.itemToEquip}")
                Condition.wait( { !itemToUnequip.first().valid() }, 300, 6)
            }
        }
    }
}