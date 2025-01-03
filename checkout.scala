package gitcommands

import datastructs.*
import fileops.* 
import java.nio.file.{Files, Paths}

def checkoutCommit(currentDir: String, commitHash: String): Unit = {
    val path = Paths.get(currentDir)

    val index = new Index(currentDir)
    index.initializeIndex()
    var commit = new Commit(currentDir)
    commit.initializeCommit()

    // abort checkout if there are unmodified changes
    // check the diff b/w index and current state of files 
    // check the diff in index to check for uncommitted files

    for ((file, (oldhash, newhash)) <- index.getIndex) {

        if (newhash != computeFileHash(file)) {
            abortCheckout()
        }

        if (oldhash != newhash) {
            abortCheckout()
        }
    }

    // modify current index to look like that particular commit
    // step 1: remove objects in the current index
    for ((filepath, (oldhash, newhash)) <- index.getIndex) {
        val path = Paths.get(filepath)
        if (Files.exists(path)) {
            Files.delete(path)
        }
    }
    
    // step 2: add objects from specified checkout index
    commit.getCommit(commitHash) match
        case Some(i) => {
            val newIndex = new Index(currentDir)
            val (message, tempIndex) = i
            newIndex.indexMap = tempIndex.indexMap

            newIndex.writeToIndex()

            for ((filepath, (oldhash, newhash)) <- newIndex.getIndex) {
                var objspath = path.resolve(".wegit").resolve("objs").resolve(newhash)
                addDecompressedFile(objspath.toString(), filepath)
            }
        }
        case None => nonLegalCheckout()    
}

def nonLegalCheckout(): Unit = {
    println("error: commithash did not match any file(s) known to git")
    println("aborting checkout...")
    sys.exit(1)
}

def abortCheckout(): Unit = {
    println("error: your local changes would be overwritten by checkout")
    println("please commit your changes")
    println("aborting checkout...")
    sys.exit(1)
}