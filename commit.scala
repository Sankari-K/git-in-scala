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
    if (!commit.hasCommit(commitHash)) {
        commit.addCommit(commitHash, index, message)
    }

    // now have to update my index
    for ((key, (oldhash, newhash)) <- index.getIndex) {
        index.updateIndex(key, (newhash, newhash))
    }
}