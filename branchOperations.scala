package datastructs

import scala.io.Source
import java.nio.file.{Paths, Files, Path, StandardCopyOption}
import java.io.PrintWriter
import java.io.File
import java.nio.file.FileAlreadyExistsException
import scala.jdk.StreamConverters._
import fileops.*

def getCurrentBranch(filePath: String): String = {
    val path = Paths.get(filePath)
    var branchPath = path.resolve(".wegit").resolve("HEAD-NAME")
    val source = Source.fromFile(branchPath.toString())
    return source.getLines().next()
}

def createBranch(filePath: String, branchName: String): Unit = {
    val path = Paths.get(filePath)

    // find current branch
    var currentBranch = getCurrentBranch(filePath)

    // create directory for new branch
    var branch = path.resolve(s".wegit/${branchName}")

    // make sure such a branch doesn't exist already
    try {
        Files.createDirectory(branch)
    }
    catch {
        case _:
            Exception => {
                if (List("objs", "config", "head-name") contains branchName.toLowerCase()) {
                    println("fatal: not a legal name for a branch.")
                }
                else {
                    println("fatal: branch already exists.")
                }
            }
        sys.exit(1)
    }

    // initialize new branch with contents (index, commits) from current branch
    var currentIndex = new Index(filePath)
    currentIndex.initializeIndex()

    var newIndex = new Index(filePath)

    var currentCommits = new Commit(filePath)
    currentCommits.initializeCommit()

    var newCommits = new Commit(filePath)

    switchBranchPointer(filePath, branchName)

    newIndex.initializeIndex()
    newCommits.initializeCommit()

    newIndex.indexMap = currentIndex.getIndex
    newCommits.commits = currentCommits.listCommits

    newIndex.writeToIndex()
    newCommits.exportCommits(newCommits.getCommitPath().toString())

    switchBranchPointer(filePath, currentBranch)
}

def safeSwitchBranch(filePath: String, branchName: String): Unit = {
    // make sure no unstaged changes
    var index = new Index(filePath)
    index.initializeIndex()

     for ((file, (oldhash, newhash)) <- index.getIndex) {

        if (newhash != computeFileHash(file)) {
            abortSwitch()
        }
    }

    // make sure such a branch exists
    if (!checkIfBranch(filePath, branchName)) {
        println("fatal: no such branch exists.")
        sys.exit(1)
    }

    // make sure we're not already on that branch
    if (branchName == getCurrentBranch(filePath)) {
        println(s"fatal: already on branch ${branchName}")
        sys.exit(1)
    }

    // overwrite files step 1: remove objects in the current index
    for ((filepath, (oldhash, newhash)) <- index.getIndex) {
        val path = Paths.get(filepath)
        if (Files.exists(path)) {
            Files.delete(path)
        }
    }

    // change contents of HEAD-NAME (move to other branch)
    switchBranchPointer(filePath, branchName)

    // overwrite files step 2: add objects to look like the new index
    val newIndex = new Index(filePath)
    newIndex.initializeIndex()

    for ((filepath, (oldhash, newhash)) <- newIndex.getIndex) {
        var objspath = Paths.get(filePath).resolve(".wegit").resolve("objs").resolve(newhash)
        addDecompressedFile(objspath.toString(), filepath)
    }
}

def switchBranchPointer(filePath: String, branchName: String): Unit = {
    // change contents of HEAD-NAME
    val path = Paths.get(filePath)
    var branchRef = path.resolve(".wegit").resolve("HEAD-NAME")

    val writer = new PrintWriter(new File(branchRef.toString()))
    writer.write(s"${branchName}\n")
    writer.close()
}

def getAllBranches(filePath: Path): List[String] = {
    // iterate over .wegit
    val wegitPath = filePath.resolve(".wegit")
    val objsPath = wegitPath.resolve("objs").toString()

    var fileList = Files.walk(wegitPath).toScala(Seq)
      .filter(path => Files.isDirectory(path) && !(path.toString().equals(objsPath)))
      .map(path => wegitPath.relativize(path).toString)
      .filterNot(_ == "") // refers to ".wegit" itself
      .toList
    return fileList
}

def checkIfBranch(filePath: String, branch: String): Boolean = {
    return (getAllBranches(Paths.get(filePath)) contains branch)
}

def abortSwitch(): Unit = {
    println("fatal: there are unstaged changes, please stage or commit them before switching branches.")
    sys.exit(1)
}

// def deleteRecursively(file: File): Unit = {
//     if (file.isDirectory) {
//         file.listFiles.foreach(x => x.delete())
//     }
//     file.delete()
//     if (file.exists && !file.delete) {
//         throw new Exception(s"Unable to delete ${file.getAbsolutePath}")
//     }
// }

def renameDirectory(sourcePath: String, targetPath: String): Unit = {
    val source = Paths.get(sourcePath)
    val target = Paths.get(targetPath)

    if (Files.exists(source) && Files.isDirectory(source)) {
        Files.move(source, target, StandardCopyOption.ATOMIC_MOVE)
    } else {
        println(s"Error: Source directory does not exist or is not a directory.")
    }
}