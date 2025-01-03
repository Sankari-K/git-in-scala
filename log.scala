package gitcommands

import datastructs.*

def getLog(currentDir: String): Unit = {
    var commit = new Commit(currentDir)
    commit.initializeCommit()

    for ((commitHash, (message, index)) <- commit.listCommits) {
        println(Console.YELLOW + "commit: " + commitHash)
        println(Console.WHITE + "message:" + message)
        println()
    }
}