package helpers

import Config
import org.powbot.api.Condition
import org.powbot.api.rt4.Movement
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
}