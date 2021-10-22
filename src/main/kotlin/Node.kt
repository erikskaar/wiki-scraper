import com.google.gson.Gson
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.system.exitProcess

class Node(val parent: Node?, val link: String, var childLinks: MutableSet<String>, var children: MutableSet<Node>) {
    private lateinit var data: String

    private fun createChildNodes() {
        childLinks.forEach { childLink ->
            children.add(
                Node(
                    parent = this,
                    link = childLink,
                    mutableSetOf(),
                    mutableSetOf()
                )
            )
        }
    }

    private fun getChildrenURL(data: String): MutableSet<String> {
        val matchingData = regex.findAll(data)
        val urlList = mutableListOf<String>()

        matchingData.forEach { word ->
            urlList.add(word.value)
        }
        return urlList.toMutableSet()
    }

    private fun setChildURLs() {
        childLinks = getChildrenURL(data)
        childLinks.forEach { clink ->
            if (URL(padding + clink) == endLink) callbackFromChild(mutableListOf(clink))
        }
    }

    private fun visitLinkAndSetData(link: String) {
        try {
            val completeURL = URL(padding + link)
            val currentData = completeURL.readText()
            data = currentData
        } catch (e: Exception) {
            println("Skipped $link")
            skipNode()
        }
    }

    private fun skipNode() {
        nodeList.removeFirst()
        nodeList[0].initialize()
    }

    private fun callbackFromChild(path: MutableList<String>) {
        path.add(link)
        if (this.parent != null) {
            parent.callbackFromChild(path)
        } else {
            path.asReversed().forEach { node ->
                run {
                    if (node != path.first()) print("$node -> ") else print("$node \n")
                }
            }
            writeToFile()
            exitProcess(0)
        }

    }

    private fun writeToFile() {
        var topNode = this
        while (topNode.parent != null) {
            topNode = topNode.parent!!
        }
        val gsonContent = Gson().toJson(topNode.childLinks)
        outputFile.writeText(gsonContent)
    }

    private fun listParentsV2(node: Node) {
        val newList = mutableListOf<String>()
        var newNode = node
        var currentAmount = 0
        while (newNode.parent != null) {
            newList.add(newNode.parent!!.link)
            newNode = newNode.parent!!
            currentAmount += 1
        }
        print("Total links checked: $totalLinksChecked - Depth: $currentAmount - Parents: ${newList.asReversed()} - ")
    }

    private fun checkIfCorrectLinkElseContinue() {
        listParentsV2(this)
        print("Current site: ${URLDecoder.decode(link, StandardCharsets.UTF_8.toString())}\n")
        visitedLinks.add(link)
        if (URL(padding + link) == endLink) {
            callbackFromChild(mutableListOf())
        } else {
            visitLinkAndSetData(link)
            setChildURLs()
            createChildNodes()
            this.data = ""
            children.forEach { child ->

                if (child.link !in visitedLinks) {
                    nodeList.add(child)
                    totalLinksChecked += 1
                }
            }
            nodeList.removeFirst()
            completeNodeSet.add(this)
            nodeList[0].initialize()
        }
    }

    fun initialize() {
        checkIfCorrectLinkElseContinue()
    }
}