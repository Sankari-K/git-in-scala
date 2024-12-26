import datastructs.* 
import fileops.*
import java.nio.file.{Paths}
import scala.collection.immutable.Stream.Cons

def getStatus(currentDir: String): Unit = {
    val index = new Index(currentDir)
    index.initializeIndex()

    println(Console.WHITE + "changes to be committed: \n")
    for ((filepath, (oldhash, newhash)) <- index.getIndex) {
        if (oldhash == "null") {
            println(Console.GREEN + "new file: " + filepath)
        }
    }
    for ((filepath, (oldhash, newhash)) <- index.getIndex) {
        if (oldhash != newhash & oldhash != "null") {
            println(Console.RED + "modified: " + filepath)
        }
    }
    for ((filepath, (oldhash, newhash)) <- index.getIndex) {
        if (newhash == "null") {
            println(Console.YELLOW + "deleted file: " + filepath)
        }
    }
    println()
    println(Console.WHITE + "untracked files: \n")

    var existingFiles = listFilesInDirectory(Paths.get(currentDir)).getOrElse(List[String]())
    for (file <- existingFiles) {
        // file is not in INDEX
        if (!(index.getIndex contains file)) {
            println(Console.BLUE + file)
        }
    }
    println(Console.WHITE)
}