import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session

class Api {
    private val driver = GraphDatabase.driver(
        "INSERT_NEO4J_URI",
        AuthTokens.basic("INSERT_NEO4J_USERNAME", "INSERT_NEO4J_PASSWORD")
    )
    lateinit var session: Session

    fun initialize() {
        try {
            session = driver.session()
            val result = session.run("MATCH (p) RETURN p")
            println("Successfully connected to ${result.consume().server().address()}")
        } catch (exception: Exception) {
            println("Could not connect: $exception")
        }
    }

    fun createNode(node: BfsNode) {
        try {
            if (node.parent != null) {
                val result = session.run(
                    "MERGE (p:Node {name: '${node.parent.link.split("/")[2]}'})" + "MERGE (n:Node {name: '${
                        node.link.split(
                            "/"
                        )[2]
                    }'})" + "MERGE (p)-[c:CONTAINS]->(n)\n" + "return c"
                )
            } else {
                val result = session.run("MERGE (n:Node {name: '${node.link.split("/")[2]}'}) return n")
            }
        } catch (exception: Exception) {
            println("Could not create node: $exception")
        }
    }

    fun checkIfPathAlreadyExists(startLink: String, endLink: String): Boolean {
        try {
            val query = "MATCH p = shortestpath((x:Node {name: '${
                startLink.split("/").last()
            }'})-[*]->(y:Node {name: '${
                endLink.split("/").last()
            }'})) \n" + "with reduce(output = [], a IN nodes(p) | output + a.name) as nodes\n" + "RETURN nodes"
            val result = session.run(query)
            val records = result.single().get(0)
            if (records.asList().size > 3) {
                println("Path is cached, but it too long. Looking for new path. Cached path:")
                records.asList().forEach {
                    if (it == records.asList().last()) print("$it \n") else print("$it -> ")
                }
                return false
            }
            return if (records.asList().size > 0) {
                println("Path exists: ")
                records.asList().forEach {
                    if (it == records.asList().last()) print("$it \n") else print("$it -> ")
                }
                true
            } else {
                println("Path does not exist")
                false
            }

        } catch (exception: Exception) {
            return false
        }
    }

    fun close() {
        driver.close()
        println("Closed connection")
    }
}
