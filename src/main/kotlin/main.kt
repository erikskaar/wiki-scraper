import kotlinx.coroutines.runBlocking
import java.net.URL
import kotlin.system.exitProcess

const val padding = "https://en.wikipedia.org"

val regex = "((?=\\/wiki\\/)([^:|?|#]*?)(?=\"))".toRegex()
var startTime: Long = 0
val endLink = URL("https://en.wikipedia.org/wiki/ESPN")
val startLink = "/wiki/England"
var visitedLinks = mutableSetOf<String>()
var nodeList = mutableListOf<BfsNode>()
val api = Api()

fun main(args: Array<String>) {

    api.initialize()
    val pathIsStored = api.checkIfPathAlreadyExists(startLink, endLink.path)
    if (pathIsStored) {
        api.close()
        exitProcess(0)
    }

    val startNode = BfsNode(
        parent = null, link = startLink, mutableSetOf(), mutableSetOf()
    )

    api.createNode(startNode)

    startTime = System.currentTimeMillis()
    nodeList.add(startNode)
    startNode.visitChild(startNode)
    startNode.getChildrenURLs(startNode.content)
    runBlocking {
        startNode.visitChildren()
    }
}
