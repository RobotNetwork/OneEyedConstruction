enum class PlankType(val displayName: String) {
    OAK("Oak plank"),
    TEAK("Teak plank"),
    MAHOGANY("Mahogany plank"),
}

data class Buildable(
    val objectName: String,
    val objectHotspotID: Int,
    val builtObjectID: Int,
    val plankType: String,
    val plankAmount: Int,
    val secondary: String?,
)

object Constants {
    val buildables = listOf(
        Buildable("Oak door", 15329, 13345, PlankType.OAK.displayName, 10, null),
        Buildable("Oak larder", 15317, 13345, PlankType.OAK.displayName, 8, null),
        Buildable("Mythical cape", 15394, 31986, PlankType.TEAK.displayName, 3, "Mythical cape"),
        Buildable("Mahogany table", 15298, 13298, PlankType.MAHOGANY.displayName, 6, null),
    )

    // Widgets
    const val CONTROL_SETTINGS_TAB_PARENT = 116
    const val CONTROL_SETTINGS_TAB_COMPONENT_INDEX = 63
    const val HOUSE_OPTIONS = 31
    const val WIDGET_INDEX_HOUSE_OPTIONS_OPENED = 370
    const val CALL_SERVANT_WIDGET = 22
    const val PARENT_WIDGET_BUILD_INTERFACE = 458

    // Objects
    const val RIMMINGTON_HOUSE_PORTAL = 15478
    const val POH_PORTAL_INSIDE = 4525
    const val POH_DUNGEON_ENTRANCE = 4529

    // items
    const val ITEM_HAMMER = "Hammer"
    const val ITEM_SAW = "Saw"
    const val ITEM_CRYSTAL_SAW = "Crystal saw"

    // NPCs
    const val NPC_DEMON_BUTLER = "Demon butler"

    // butler chat variants
    const val NEEDS_MONEY = "Master, if thou desirest my continued service,"
    var FETCH_PLANK = "Fetch from bank: 25 x []" // "[]" is reserved for string replacement in the Butler class, dependent on object user selects to build
    const val HAS_NONE_LEFT = "I have returned with what you asked me to"
    const val NO_PLANKS_IN_BANK = "My Lord, I cannot conjure items out of the air"

}