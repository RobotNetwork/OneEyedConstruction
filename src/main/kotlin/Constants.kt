import org.powbot.api.rt4.Widgets

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
    val SETTINGS_TAB_CONTROL_WIDGET = Widgets.component(116, 63)
    val HOUSE_OPTIONS_WIDGET = Widgets.component(116, 31)
    val CALL_SERVANT_WIDGET = Widgets.component(370, 22)

    // Objects
    const val REMMINTON_HOUSE_PORTAL = 15478
    const val POH_PORTAL_INSIDE = 4525
    const val POH_DUNGEON_ENTRANCE = 4529
}