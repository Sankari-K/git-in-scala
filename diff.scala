package gitcommands

import scala.collection.mutable
import datastructs.Index
import java.nio.file.Paths
import java.nio.file.Files
import fileops.{getFileContent, getDecompressedFileContent}

def findFileDiff(currentDir: String, filename: String): Unit = {
    val path = Paths.get(currentDir)

    // find difference b/w working tree(+) wrt index(-)
    // assumes file exists in both working tree and index
    if (!Files.exists(path.resolve(filename))) {
        println("file doesn't exist/has been removed")
        sys.exit(1)
    }

    var index = new Index(currentDir)
    index.initializeIndex()

    var workingTreeContent: List[String] = List()
    var indexedContent: List[String] = List()

    // get file in index
    if ((index.getIndex contains filename)) {
        var (oldhash, newhash) = index.getIndex(filename)
        // no need to do a null check for newhash since the file by definition exists in the working tree so no way newhash will be null
        indexedContent = getDecompressedFileContent(path.resolve(".wegit").resolve("objs").resolve(newhash).toString())
    }

    // get file in working tree
    workingTreeContent = getFileContent(path.resolve(filename).toString())

    computeDiff(indexedContent, workingTreeContent)
}

def findStagedFileDiff(currentDir: String, filename: String): Unit = {
    val path = Paths.get(currentDir)

    // find difference b/w index(+) wrt latest commit(-)

    var index = new Index(currentDir)
    index.initializeIndex()

    var indexedContent: List[String] = List()
    var commitContent: List[String] = List()

    // get file in index
    if ((index.getIndex contains filename)) {
        var (oldhash, newhash) = index.getIndex(filename)
        if (newhash != "null") {
            indexedContent = getDecompressedFileContent(path.resolve(".wegit").resolve("objs").resolve(newhash).toString())
        }
        if (oldhash != "null") {
            commitContent = getDecompressedFileContent(path.resolve(".wegit").resolve("objs").resolve(oldhash).toString())
        }
    }

    computeDiff(commitContent, indexedContent)
}

def computeDiff(originalLines: List[String], modifiedLines: List[String]): Unit = {
    val lcs = findLCS(originalLines, modifiedLines)

    var i = 0
    var j = 0

    while (i < originalLines.length || j < modifiedLines.length) {
        if (i < originalLines.length && j < modifiedLines.length && originalLines(i) == modifiedLines(j)) {
            println(s"  ${originalLines(i)}")
            i += 1
            j += 1
        } 
        else if (i < originalLines.length && !lcs.contains((i, j))) {
            println(Console.RED + s"- ${originalLines(i)}" + Console.RESET)
            i += 1
        } 
        else if (j < modifiedLines.length && !lcs.contains((i, j))) {
            println(Console.GREEN + s"+ ${modifiedLines(j)}" + Console.RESET)
            j += 1
        } 
        else {
            println(Console.RED + s"- ${originalLines(i)}" + Console.RESET)
            println(Console.GREEN + s"+ ${modifiedLines(j)}" + Console.RESET)
            i += 1
            j += 1
        }
    }
}

def findLCS(a: List[String], b: List[String]): Set[(Int, Int)] = {
    val dp = Array.fill(a.length + 1, b.length + 1)(0)

    for (i <- 1 to a.length; j <- 1 to b.length) {
        dp(i)(j) =
        if (a(i - 1) == b(j - 1)) dp(i - 1)(j - 1) + 1
        else Math.max(dp(i - 1)(j), dp(i)(j - 1))
    }

    val lcsSet = mutable.Set[(Int, Int)]()
    var i = a.length
    var j = b.length

    while (i > 0 && j > 0) {
        if (a(i - 1) == b(j - 1)) {
            lcsSet.add((i - 1, j - 1))
            i -= 1
            j -= 1
        } 
        else if (dp(i - 1)(j) > dp(i)(j - 1)) {
            i -= 1
        } 
        else {
            j -= 1
        }
    }
    return lcsSet.toSet
}