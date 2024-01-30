package helpers

import Config
import org.powbot.api.Condition
import org.powbot.api.rt4.Equipment
import org.powbot.api.rt4.Inventory
import org.powbot.api.rt4.Item
import org.powbot.api.rt4.Movement
import org.powbot.api.rt4.stream.item.EquipmentItemStream
import org.slf4j.LoggerFactory

class Player(private val config: Config) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    fun disableRun() {
        if (Movement.running() && Movement.running(false)) {
            logger.info("Disabling run...")
            Condition.wait({ !Movement.running() }, 150, 5)
        }
    }

    fun enableRun() {
        if (!Movement.running() && Movement.running(true)) {
            logger.info("Enabling run...")
            Condition.wait({ Movement.running() }, 150, 5)
        }
    }

    fun getItemToEquip() : Item {
        return Inventory.stream().name(config.itemToEquip).first()
    }

    fun getItemToUnequip() : EquipmentItemStream {
        return Equipment.stream().name(config.itemToEquip)
    }
}
