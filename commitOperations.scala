package datastructs

import scala.io.Source
import java.io.{BufferedWriter, FileWriter, PrintWriter, File}
import scala.collection.mutable

class Commits(private val filePath: String) {
    // The dictionary to store commit hashes and their corresponding Index objects
    val commits: mutable.Map[String, Index] = mutable.Map()

    def addCommit(hash: String, index: Index): Unit = {
        commits(hash) = index
        exportCommits()
    }

    def getCommit(hash: String): Option[Index] = {
        if (hasCommit(hash)) commits.get(hash)
        else None
    }

    // Remove a commit by its hash TODO: find the use for it
    def removeCommit(hash: String): Unit = {
        if (hasCommit(hash)) commits.remove(hash)
        exportCommits()
    }

    def listCommits(): List[String] = {
        commits.keys.toList
    }

    def hasCommit(hash: String): Boolean = {
        commits.contains(hash)
    }

    def exportCommits(): Unit = {
        val writer = new PrintWriter(new File(filePath))
        try {
            for ((hash, index) <- commits) {
                val commitIndex = index.getIndex
                writer.write(s"[$hash]\n")
                for ((file, (oldHash, newHash)) <- commitIndex) {
                    writer.write(s"$file=$oldHash,$newHash\n")
                }
                writer.write("\n")
            }
        } finally {
            writer.close()
        }
    }

    def importCommits(): Unit = {
        val source = scala.io.Source.fromFile(filePath).getLines().toList
        var fileLine = 0
        while (fileLine < source.length) {
            val line = source(fileLine)

            if (line.startsWith("[")) {
                // detected section header
                val hash = line.stripPrefix("[").stripSuffix("]")
                val index = new Index(filePath + "/../INDEX")
                val indexData = scala.collection.mutable.Map.empty[String, (String, String)]
                fileLine = fileLine + 1

                while (source(fileLine) != "") {
                    val parts = source(fileLine).split("=").map(_.trim)
                    if (parts.length == 2) {
                    val file = parts(0)
                    val hashes = parts(1).split(",").map(_.trim)
                    indexData += (file -> (hashes(0), hashes(1)))
                    }
                    index.indexMap = indexData.toMap
                    commits(hash) = index
                    fileLine = fileLine + 1
                }
            }
            fileLine = fileLine + 1
      }
    }
}