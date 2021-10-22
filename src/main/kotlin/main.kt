import java.io.File
import java.net.URL

const val padding = "https://no.wikipedia.org"

val regex = "((?=\\/wiki\\/)([^:|?|#]*?)(?=\"))".toRegex()

val endLink = URL("https://no.wikipedia.org/wiki/Jens_Stoltenberg")
var completeNodeSet = mutableSetOf<Node>()
var nodeList = mutableListOf<Node>()
var visitedLinks = mutableSetOf<String>()
var totalLinksChecked = 0
val fileName = "src/main/resources/output.json"
val outputFile = File(fileName)

fun main(args: Array<String>) {

    val mainNode = Node(
        parent = null,
        link = "/wiki/Rita_Ottervik",
        mutableSetOf(),
        mutableSetOf()
    )

    nodeList.add(mainNode)
    mainNode.initialize()

}

