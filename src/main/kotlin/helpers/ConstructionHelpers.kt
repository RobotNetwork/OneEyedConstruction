package helpers

import Config
import Constants.POH_PORTAL_INSIDE
import org.powbot.api.rt4.*
import org.slf4j.LoggerFactory

class ConstructionHelpers(private val config: Config) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getHotspot(): GameObject {
        // construction hotspot
        return Objects.stream().id(config.id).first()
    }

    fun getObjectToRemove(): GameObject {
        return Objects.stream().id(config.builtID).first()
    }

    fun isBuilt(): Boolean {
        return getObjectToRemove().valid()
    }

    fun isMythicalCapeSelected(): Boolean {
        return config.objToBuild == "Mythical cape"
    }

    fun isOakDoorSelected() : Boolean {
        return config.objToBuild == "Oak door"
    }

    fun getIndoorPOHPortal(): GameObject {
        return Objects.stream().id(POH_PORTAL_INSIDE).first()
    }

    fun hasMaterialsRequired(): Boolean {
        return if (isMythicalCapeSelected()) {
            Inventory.stream().name(config.secondary).first().valid() && hasPlanksRequired() && hasSawAndHammer()
        } else {
            hasPlanksRequired() && hasSawAndHammer()
        }
    }

    private fun hasPlanksRequired(): Boolean {
        return Inventory.stream().name(config.plankType).count().toInt() >= config.plankAmount
    }

    private fun hasSawAndHammer(): Boolean {
        // TODO: stop script if no hammer or saw present
        val hammer = Inventory.stream().name("Hammer").first().valid()
        val saw = Inventory.stream().nameContains("Saw", "Crystal saw").first().valid()
        return saw && hammer
    }

    fun getBuildableInterfaceWidget(): Component {
        // interface widget for the thing being built, identified by name
        return Components.stream().widget(458).nameContains(config.objToBuild).first()
    }

    fun angleCameraToObject() {
        val buildable = getObjectToRemove()
        if (!buildable.inViewport()) {
            logger.info("Adjusting camera")
            Camera.turnTo(buildable)
        }
    }

//    private fun angleCameraToObject(objectID: Int) {
//        Camera.angleToLocatable()
//    }
}