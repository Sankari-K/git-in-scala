package datastructs

import scala.io.Source
import java.io.{BufferedWriter, FileWriter, PrintWriter, File}
import java.nio.file.{Paths, Path, Files}
import scala.collection.mutable
import java.security.MessageDigest
import gitcommands.*
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

class Commit(val filePath: String) {
    // The dictionary to store commit hashes and their corresponding Index objects
    val commits: mutable.LinkedHashMap[String, (String, String, String, String, Index)] = mutable.LinkedHashMap()
    
    def getCommitPath(): Path = {
        val path = Paths.get(filePath).toAbsolutePath()
        return path.resolve(".wegit").resolve("COMMIT")
    }

    def initializeCommit(): Unit = {
        val indexPath = getCommitPath()
        if (!Files.exists(indexPath)) {
            Files.createFile(indexPath)
        }
        importCommits(indexPath.toString())
    }

    def getCommitHash(index: Index): String = {
        val serializedMap = index.indexMap.toSeq.sorted.map { 
            case (key, value) => s"$key=$value" 
        }.mkString("&")

        val sha1Digest = MessageDigest.getInstance("SHA-1")
        val hashBytes = sha1Digest.digest(serializedMap.getBytes("UTF-8"))
        hashBytes.map("%02x".format(_)).mkString
    }

    def addCommit(hash: String, index: Index, message: String): Unit = {
        commits(hash) = (message, getConfig(filePath, "username"), getConfig(filePath, "email"), getTimeStamp(), index)
        exportCommits(getCommitPath().toString())
    }

    def getCommit(hash: String): Option[(String, String, String, String, Index)] = {
        if (hasCommit(hash)) commits.get(hash)
        else None
    }

    // Remove a commit by its hash TODO: find the use for it
    def removeCommit(hash: String, message: String): Unit = {
        if (hasCommit(hash)) commits.remove(hash)
        exportCommits(getCommitPath().toString())
    }

    def listCommits: mutable.Map[String, (String, String, String, String, Index)] = commits
    
    def hasCommit(hash: String): Boolean = {
        commits.contains(hash)
    }

    def exportCommits(filePath: String): Unit = {
        val writer = new PrintWriter(new File(filePath))
        try {
            for ((hash, (message, author, email, timestamp, index)) <- commits) {
                val commitIndex = index.getIndex
                writer.write(s"[$hash]\n")
                writer.write(s"$message\n")
                writer.write(s"$author\n")
                writer.write(s"$email\n")
                writer.write(s"$timestamp\n")
                for ((file, (oldHash, newHash)) <- commitIndex) {
                    writer.write(s"$file=$oldHash,$newHash\n")
                }
                writer.write("\n")
            }
        } finally {
            writer.close()
        }
    }

    def importCommits(filePath: String): Unit = {
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
                val message = source(fileLine)
                fileLine = fileLine + 1
                val author = source(fileLine)
                fileLine = fileLine + 1
                val email = source(fileLine)
                fileLine = fileLine + 1
                val timestamp = source(fileLine)
                fileLine = fileLine + 1

                while (source(fileLine) != "") {
                    val parts = source(fileLine).split("=").map(_.trim)
                    if (parts.length == 2) {
                        val file = parts(0)
                        val hashes = parts(1).split(",").map(_.trim)
                        indexData += (file -> (hashes(0), hashes(1)))
                    }
                    index.indexMap = indexData.toMap
                    commits(hash) = (message, author, email, timestamp, index)
                    fileLine = fileLine + 1
                }
            }
            fileLine = fileLine + 1
        }
    }

    def getTimeStamp(): String = {
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val currentDateTime: LocalDateTime = LocalDateTime.now()

        val day = currentDateTime.getDayOfWeek().toString().toLowerCase()
        val month = currentDateTime.getMonth().toString().toLowerCase()
        val date = currentDateTime.getDayOfMonth()
        val time = timeFormatter.format(currentDateTime)
        val year = currentDateTime.getYear()
        val timezone = "+0530"

        return s"$day $month $date $time $year $timezone" 
    }
}