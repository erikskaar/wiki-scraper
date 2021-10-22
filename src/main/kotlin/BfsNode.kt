import kotlinx.coroutines.*
import java.net.URL
import kotlin.system.exitProcess

class BfsNode(
    val parent: BfsNode?,
    val link: String,
    private var childLinks: MutableSet<String>,
    private var children: MutableSet<BfsNode>
) {
    var content: String = ""

    fun getChildrenURLs(content: String) {
        val matchingContent = regex.findAll(content)
        matchingContent.forEach { url ->
            isEndLink(url.value)
            if (url.value !in visitedLinks) {
                this.childLinks.add(url.value)
                visitedLinks.add(url.value)
            }
        }
    }

    suspend fun visitChildren() {
        coroutineScope {
            childLinks.forEach {
                launch(Dispatchers.IO) {
                    val child = BfsNode(parent = this@BfsNode, link = it, mutableSetOf(), mutableSetOf())
                    visitChild(child)
                    child.getChildrenURLs(child.content)
                    children.add(child)
                    nodeList.add(child)
                    //println("testNodeListLength: ${testNodeList.size}")
                    //println(Thread.currentThread().name)
                    listParents(child, it)
                }
            }
        }
        nodeList.removeFirst()
        nodeList.first().initialize()
    }

    fun visitChild(child: BfsNode) {
        try {
            val completeURL = URL(padding + child.link)
            val currentContent = completeURL.readText()
            child.content = currentContent
        } catch (e: Exception) {
            println("Skipped ${child.link}")
        }
    }

    private fun isEndLink(link: String) {
        if (URL(padding + link).toString().equals(endLink.toString(), ignoreCase = true)) {
            prettyPrintPath(link)
        }
    }

    private fun prettyPrintPath(link: String) {
        var newNode = this
        val parentList = mutableListOf(newNode.link)
        while (newNode.parent != null) {
            parentList.add(newNode.parent!!.link)
            newNode = newNode.parent!!
        }
        print("Path found!: ")
        parentList.asReversed().forEach {
            print("$it -> ")
        }
        print("$link\n")
        println("Time taken: ${(System.currentTimeMillis() - startTime) / 1000} seconds")
        exitProcess(0)
    }

    private fun listParents(node: BfsNode, link: String) {
        val parentList = mutableListOf<String>()
        var newNode = node
        var depth = 1
        while (newNode.parent != null) {
            parentList.add(newNode.parent!!.link)
            newNode = newNode.parent!!
            depth += 1
        }
        println("Depth: $depth - Parents: ${parentList.asReversed()} - $link")
    }

    private fun initialize() {
        runBlocking {
            visitChildren()
        }
    }

}