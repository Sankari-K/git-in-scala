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

    for ((key, (_, newhash)) <- index.getIndex) {
        if (newhash == "null") { // skip adding the deleted files to index
            index.removeFromIndex(key)
        }
        else {
            index.updateIndex(key, (newhash, newhash))
        }
    }

    println(s"[main $commitHash] $message")
}

def ammendCommit(currentDir: String, message: String): Unit = {
    var commit = new Commit(currentDir)
    commit.initializeCommit()

    try {
        var (lastCommitHash, lastCommitValue) = commit.listCommits.last
        var (_, _, _, _, index) = lastCommitValue
        commit.addCommit(lastCommitHash, index, message)
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