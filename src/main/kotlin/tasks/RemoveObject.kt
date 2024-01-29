package tasks

import Config
import Task
import helpers.ConstructionHelpers
import org.powbot.api.Condition
import org.powbot.api.Random
import org.powbot.api.rt4.*
import org.powbot.api.rt4.stream.item.EquipmentItemStream
import org.slf4j.LoggerFactory

class RemoveObject(private val config: Config) : Task() {
    override var name: String = "RemoveObject"
    private val consHelpers = ConstructionHelpers(config)

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun shouldExecute(): Boolean {
        val objectToRemove = consHelpers.getObjectToRemove()
        return objectToRemove.valid() && objectToRemove.distanceTo(Players.local().tile()) < 4
    }

    override fun execute() {
//        consHelpers.angleCameraToObject()
        disableRun()
        equipItemIfNeeded()
        remove()
        unequipItemIfNeeded()
        if (Random.nextBoolean()) {
            // random chance to open/view inventory
            Game.tab(Game.Tab.INVENTORY)
            return
        }
    }

    private fun disableRun() {
        if (Movement.running() && Movement.running(false)) {
            logger.info("Enabling run...")
            Condition.wait({!Movement.running()}, 250, 6)
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
        val item = getItemToEquip()
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
        if (!consHelpers.isMythicalCapeSelected() || getItemToEquip().valid()) return

        val itemToUnequip = getItemToUnequip()

        if (!getItemToEquip().valid() && Game.tab(Game.Tab.EQUIPMENT) && Condition.wait( { itemToUnequip.viewable().first().valid() }, 300, 6)) {
            if (!Inventory.isFull() && itemToUnequip.first().click()) {
                logger.info("Unequipping ${config.itemToEquip}")
                Condition.wait( { !itemToUnequip.first().valid() }, 300, 6)
            }
        }
    }

    private fun getItemToEquip() : Item {
        return Inventory.stream().name(config.itemToEquip).first()
    }

    private fun getItemToUnequip() : EquipmentItemStream {
        return Equipment.stream().name(config.itemToEquip)
    }
}