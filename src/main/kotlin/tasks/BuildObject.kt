package tasks

import Config
import Task
import helpers.ConstructionHelpers
import org.powbot.api.Condition
import org.powbot.api.rt4.*
import org.slf4j.LoggerFactory

class BuildObject(private val config: Config) : Task() {
    override var name: String = "BuildObject"
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val consHelpers = ConstructionHelpers(config)

    override fun shouldExecute(): Boolean {
        val hotspot = consHelpers.getHotspot()
        return hotspot.valid()
                && hotspot.distanceTo(Players.local().tile()) <= 3
                && consHelpers.hasMaterialsRequired()
    }

    override fun execute() {
//        consHelpers.angleCameraToObject()
        build()
    }

    private fun build() {
        val hotspot = consHelpers.getHotspot()
        if (consHelpers.isMythicalCapeSelected()) {
            hotspot.bounds(36, 48, -214, -134, -32, 29)
        }
        if (hotspot.interact("Build") && Condition.wait({ consHelpers.getBuildableInterfaceWidget().visible() }, 350, 9)) {
            logger.info("Building ${config.objToBuild}...")
            if (consHelpers.getBuildableInterfaceWidget().click() && Condition.wait({ consHelpers.isBuilt() }, 150, 15)) {
                logger.info("Done building")
            }
        }
    }
}