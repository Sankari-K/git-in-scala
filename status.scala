package gitcommands

import datastructs.* 
import fileops.*
import java.nio.file.{Paths, Files}
import scala.collection.immutable.Stream.Cons

def getStatus(currentDir: String): Unit = {
    val index = new Index(currentDir)

    if (!index.isIndexInitialized()) {
        println("on branch master\n")
        println("no commits yet\n")
        println("nothing to commit (create/copy files and use 'git add' to track)\n")
        sys.exit(1)
    }
    
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
    for ((file, (oldhash, newhash)) <- index.getIndex) {
        // newhash is checked with null to make sure file still exists
        // if it doesn't exist, it would be a deleted file anyway
        if (newhash != "null") {
            if (newhash != computeFileHash(file)) {
                println("\t" + Console.RED + "modified: " + file)
            }
        }
    }
    println()

    println(Console.WHITE + "untracked files: ")
    println("\t(use \"add\" to include in what will be committed)")
    var existingFiles = listFilesInDirectory(Paths.get(currentDir)).getOrElse(List[String]())
    for (file <- existingFiles) {
        // file is not in INDEX
        if (!(index.getIndex contains file)) {
            println("\t" + Console.BLUE + file)
        }
    }
    println(Console.WHITE)
}