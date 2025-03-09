package gitcommands

import datastructs.*

def commitFiles(currentDir: String, message: String): Unit = {
    var commit = new Commit(currentDir)
    var index = new Index(currentDir)

    index.initializeIndex()

    if (index.indexMap == Map()) {
        println("wegit expects an INDEX file to exist. please do an add before committing.")
        return
    }
    
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

// def ammendCommit(currentDir: String, message: String): Unit = {
//     var commit = new Commit(currentDir)
//     commit.initializeCommit()
//     println(commit.listCommits)
// }

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