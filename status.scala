package gitcommands

import datastructs.* 
import fileops.*
import java.nio.file.{Paths}
import scala.collection.immutable.Stream.Cons

def getStatus(currentDir: String): Unit = {
    val index = new Index(currentDir)
    index.initializeIndex()

    println(Console.WHITE + "changes to be committed: ")
    println("\t(use \"restore -- staged\" to unstage)")
    for ((filepath, (oldhash, newhash)) <- index.getIndex) {
        if (oldhash == "null" & newhash != "null") {
            println("\t" + Console.GREEN + "new file: " + filepath)
        }
    }
    for ((filepath, (oldhash, newhash)) <- index.getIndex) {
        if (oldhash != newhash & oldhash != "null" & newhash != "null") {
            println("\t" + Console.RED + "modified: " + filepath)
        }
    }
    for ((filepath, (oldhash, newhash)) <- index.getIndex) {
        if (newhash == "null") {
            println("\t" + Console.YELLOW + "deleted file: " + filepath)
        }
    }
    println()

    println(Console.WHITE + "changes not staged for commit: ")
    println("\t(use \"add\" to update what will be committed)")
    println("\t(use \"restore\" to discard changes in working directory)")
    var existingFiles = listFilesInDirectory(Paths.get(currentDir)).getOrElse(List[String]())
    for (file <- existingFiles) {
        if (index.getIndex contains file) {
            var (oldhash, newhash) = index.getIndex(file)
            if (newhash != computeFileHash(file)) {
                println("\t" + Console.RED + "modified: " + file)
            }
        }
    }
    println()


    println(Console.WHITE + "untracked files: ")
    println("\t(use \"add\" to include in what will be committed)")
    for (file <- existingFiles) {
        // file is not in INDEX
        if (!(index.getIndex contains file)) {
            println("\t" + Console.BLUE + file)
        }
    }
    println(Console.WHITE)
}