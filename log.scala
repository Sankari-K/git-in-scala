package gitcommands

import datastructs.*

def getLog(currentDir: String): Unit = {
    var commit = new Commit(currentDir)
    commit.initializeCommit()

    for ((commitHash, (message, author, index)) <- commit.listCommits) {
        println(Console.YELLOW + "commit: " + commitHash)
        println(Console.MAGENTA + "author: " + author)
        println(Console.WHITE + "\t" + message)
        println()
    }
}