
//import com.sun.org.slf4j.internal.LoggerFactory
//import org.slf4j.LoggerFactory

abstract class Task() {
//    val logger = LoggerFactory.getLogger(this::class.java)
    open var name: String = ""

    abstract fun shouldExecute(): Boolean
    abstract fun execute()
}