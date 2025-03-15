package gitcommands

import datastructs.*

def commitFiles(currentDir: String, message: String): Unit = {
    var commit = new Commit(currentDir)
    var index = new Index(currentDir)

    if (!index.isIndexInitialized()) {
        println("on branch master\n")
        println("no commits yet\n")
        println("nothing to commit (create/copy files and use 'git add' to track)\n")
        sys.exit(1)
    }

    index.initializeIndex()
    
    // make a screenshot of the current index in the COMMIT file
    commit.initializeCommit()

    var commitHash = commit.getCommitHash(index)
    if (stagedChangesPresent(currentDir, commit, index)) {
        commit.addCommit(commitHash, index, message)
    }
    else {
        println("error: there are no staged changes to commit")
        sys.exit(1)
    }

    updateIndexAfterCommit(index)

    println(s"[main $commitHash] $message")
}

def ammendMessage(currentDir: String, message: String): Unit = {
    var commit = new Commit(currentDir)
    commit.initializeCommit()

    try {
        var (lastCommitHash, lastCommitValue) = commit.listCommits.last
        var (_, _, _, _, index) = lastCommitValue
        commit.addCommit(lastCommitHash, index, message)

        println(s"[main $lastCommitHash] $message")
    }
    catch {
        case _: Exception => println("fatal: You have nothing to amend.")
    }
}

def ammendCommit(currentDir: String, message: Option[String]): Unit = {
    // fetch the latest commit
    var commit = new Commit(currentDir)
    var index = new Index(currentDir)

    commit.initializeCommit()
    index.initializeIndex()

    try {
        var (lastCommitHash, lastCommitValue) = commit.listCommits.last
        var (lastCommitMessage, _, _, _, lastIndex) = lastCommitValue

        for ((file, (oldhash, newhash)) <- index.getIndex) {
            var (lastOldhash, lastNewhash) = lastIndex.getValueFromIndex(file)
                index.updateIndex(file, (lastOldhash, newhash)) 
        }
        for ((file, (lastOldhash, lastNewhash)) <- lastIndex.getIndex) {
            if (!(index.getIndex contains file)) {
                index.updateIndex(file, (lastOldhash, lastNewhash))
            }
        }

        commit.removeCommit(lastCommitHash)

        var commitHash = commit.getCommitHash(index)

        message match {
            case Some(x) => {
                commit.addCommit(commitHash, index, x)
                println(s"[main $commitHash] $x")
            }
            case None =>{
                commit.addCommit(commitHash, index, lastCommitMessage)
                println(s"[main $commitHash] $lastCommitMessage")
            }
        }

        updateIndexAfterCommit(index)
    }
    catch {
        case _: Exception => println("fatal: You have nothing to amend.")
    }
}

def stagedChangesPresent(currentDir: String, commit: Commit, index: Index): Boolean = {
    var commitHash = commit.getCommitHash(index)

    if (commit.hasCommit(commitHash)) {
        return false;
    }

    var filesModified = false

    for ((filepath, (oldhash, newhash)) <- index.getIndex) {
        if (oldhash != newhash) {
            filesModified = true
        }
    }
    return filesModified
}

def updateIndexAfterCommit(index: Index): Unit = {
    for ((key, (_, newhash)) <- index.getIndex) {
        if (newhash == "null") { // skip adding the deleted files to index
            index.removeFromIndex(key)
        }
        else {
            index.updateIndex(key, (newhash, newhash))
        }
    }
}