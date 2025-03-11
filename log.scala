package gitcommands

import datastructs.*

def getLog(currentDir: String): Unit = {
    var commit = new Commit(currentDir)
    commit.initializeCommit()

    if (commit.listCommits.isEmpty) {
        println("fatal: your current branch 'master' does not have any commits yet")
        sys.exit(1)
    }
    for ((commitHash, (message, author, email, timestamp, index)) <- commit.listCommits) {
        println(Console.YELLOW + "commit " + commitHash)
        println(Console.MAGENTA + "author: " + author + s" <$email>")
        println(Console.WHITE + "date:   " + timestamp)
        println()
        println(Console.WHITE + "\t" + message)
        println()
    }
}