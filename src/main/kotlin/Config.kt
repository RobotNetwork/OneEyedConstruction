data class Config(
    val objToBuild: String,
    val id: Int,
    val builtID: Int,
    val plankType: String,
    val plankAmount: Int,
    val secondary: String?,
    var butlerBanking: Boolean?,
    var planksRemaining: Int?,
    var itemToEquip: String?,
) {

}