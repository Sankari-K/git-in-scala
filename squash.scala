import datastructs.*

def squashCommits(currentDir: String, number: Int, commitMessage: String): Unit = {
    // does nothing to current index/currently staged files
    var commit = new Commit(currentDir)
    commit.initializeCommit()
    var commitsToSquash = commit.listCommits.takeRight(number - 1)

    if (commitsToSquash.size == commit.listCommits.size) {
        println("The number of commits to squash is greater than commits made, please check the commit log and retry")
        sys.exit(1)
    }
    // var index = new Index(currentDir)
    var (hash, (_, _, _, _, index)) = commit.listCommits.takeRight(number).take(1).last
    // var (_, (_, _, _, _, prevIndex)) = (commit.listCommits.takeRight(number + 1).take(1).last)
    // index.indexMap = prevIndex.indexMap
    commit.removeCommit(hash)

    for ((hash, (message, username, email, timestamp, next_index)) <- commitsToSquash) {
        for ((file, (nextoldhash, nextnewhash)) <- next_index.getIndex) {
            var (oldhash, newhash) = index.getValueFromIndex(file)
            next_index.indexMap += (file -> (oldhash, nextnewhash))
        }
        for ((file, (oldhash, newhash)) <- index.getIndex) {
            if (!(next_index.getIndex contains file)) {
                next_index.indexMap += (file -> (oldhash, newhash))
            }
        }
        index.indexMap = next_index.indexMap
        commit.removeCommit(hash)
    }

    var commitHash = commit.getCommitHash(index)
    commit.addCommit(commitHash, index, commitMessage)
}