import datastructs.* 
import fileops.*
import java.nio.file.{Paths}

def getStatus(currentDir: String): Unit = {
    val index = new Index(currentDir)
    index.initializeIndex()

    println("changes to be committed: \n")
    for ((filepath, (oldhash, newhash)) <- index.getIndex) {
        if (oldhash == "null") {
            println("new file: " + filepath)
        }
    }
    for ((filepath, (oldhash, newhash)) <- index.getIndex) {
        if (oldhash != newhash) {
            println("modified: " + filepath)
        }
    }
    for ((filepath, (oldhash, newhash)) <- index.getIndex) {
        if (newhash == "null") {
            println("deleted file: " + filepath)
        }
    }
    println()
    println("untracked files: \n")

    var existingFiles = listFilesInDirectory(Paths.get(currentDir)).getOrElse(List[String]())
    for (file <- existingFiles) {
        // file is not in INDEX
        if (!(index.getIndex contains file)) {
            println(file)
        }
    }
}