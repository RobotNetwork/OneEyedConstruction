abstract class Task() {
    open var name: String = ""

    abstract fun shouldExecute(): Boolean
    abstract fun execute()
}