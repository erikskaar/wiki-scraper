import kotlinx.coroutines.runBlocking
import java.net.URL

const val padding = "https://en.wikipedia.org"

val regex = "((?=\\/wiki\\/)([^:|?|#]*?)(?=\"))".toRegex()
var startTime: Long = 0
val endLink = URL("https://en.wikipedia.org/wiki/Barack_Obama")
var visitedLinks = mutableSetOf<String>()
var nodeList = mutableListOf<BfsNode>()

fun main(args: Array<String>) {

    val startNode = BfsNode(
        parent = null,
        link = "/wiki/Wikiracing",
        mutableSetOf(),
        mutableSetOf()
    )

    startTime = System.currentTimeMillis()
    nodeList.add(startNode)
    startNode.visitChild(startNode)
    startNode.getChildrenURLs(startNode.content)
    runBlocking {
        startNode.visitChildren()
    }
}

