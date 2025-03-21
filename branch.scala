package gitcommands

import datastructs.*
import fileops.*
import java.nio.file.{Path, Paths, Files, StandardCopyOption}
import java.io.File
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.time.LocalDateTime

def createNewBranch(currentDir: String, branch: String): Unit = {
    createBranch(currentDir, branch)
    println(s"created branch: ${branch}")
}

def showCurrentBranch(currentDir: String): Unit = {
    println(s"current branch: ${getCurrentBranch(currentDir)}")
}

def switchBranch(currentDir: String, branch: String): Unit = {
    safeSwitchBranch(currentDir, branch)
    println(s"switched to branch: ${branch}")
}

def checkoutBranch(currentDir: String, branch: String): Unit = {
    // creates a new branch and switches to it
    createBranch(currentDir, branch)
    switchBranchPointer(currentDir, branch)
    println(s"switched to branch: ${branch}")
}

def showAllBranches(currentDir: String): Unit = {
    for (branch <- getAllBranches(Paths.get(currentDir))) {
        if (branch == getCurrentBranch(currentDir)) {
            println(Console.GREEN + s"* ${branch}" + Console.WHITE)
        }
        else {
            println(s"  ${branch}")
        }
    }
}

def renameCurrentBranch(currentDir: String, newBranchName: String): Unit = {
    renameBranch(currentDir, getCurrentBranch(currentDir), newBranchName)
}

def renameBranch(currentDir: String, oldBranchName: String, newBranchName: String): Unit = {
    if (checkIfBranch(currentDir, newBranchName)) {
        println(s"fatal: ${newBranchName} branch already exists")
        sys.exit(1)
    }

    if (List("objs", "config", "head-name") contains newBranchName.toLowerCase()) {
        println("fatal: not a legal name for a branch")
        sys.exit(1)
    }

    if (!checkIfBranch(currentDir, oldBranchName)) {
        println(s"fatal: ${oldBranchName} doesn't exist")
        sys.exit(1)
    }

    val path = Paths.get(currentDir).toAbsolutePath()
    var oldBranchPath =  path.resolve(s".wegit/${{oldBranchName}}")
    var newBranchPath =  path.resolve(s".wegit/${newBranchName}")

    renameDirectory(oldBranchPath.toString(), newBranchPath.toString())

    if (oldBranchName == getCurrentBranch(currentDir)) {
        switchBranchPointer(currentDir, newBranchName)
        println(s"current branch renamed to '${newBranchName}'")
    }
    else {
        println(s"'${oldBranchName}' branch renamed to '${newBranchName}'")
    }
}

def deleteBranch(currentDir: String, branch: String): Unit = {
    if (branch == getCurrentBranch(currentDir)) {
        println("fatal: cannot delete branch you're currently on")
        sys.exit(1)
    }
    if (!checkIfBranch(currentDir, branch)) {
        println(s"fatal: no branch called '${branch}' exists")
        sys.exit(1)
    }

    val branchPath = Paths.get(currentDir).toAbsolutePath().resolve(".wegit").resolve(branch)

    deleteDirectory(new File(branchPath.toString()))
    println(s"deleted branch ${branch}")
}

def deleteInactiveBranches(currentDir: String, days: String): Unit = {
    for (branch <- getInactiveBranches(currentDir, days)) {
        deleteBranch(currentDir, branch)
    }
}

def getInactiveBranches(currentDir: String, days: String): List[String] = {
    var intDays = 0;
    try {
        intDays = days.toInt
    }
    catch {
        case _: Exception => {
            println("fatal: days must be an integer value")
            sys.exit(1)
        }
    }

    val currentBranch = getCurrentBranch(currentDir)
    var inactiveBranches: List[String] = List()

    for (branch <- getAllBranches(Paths.get(currentDir))) {

        if (branch != currentBranch) {
            var newCommits = new Commit(currentDir)
            switchBranchPointer(currentDir, branch)

            newCommits.initializeCommit()

            if (!newCommits.listCommits.isEmpty) {
                var (commithash, (message, name, email, timestamp, index)) = newCommits.listCommits.last
                val parts = timestamp.split(" ").drop(1).dropRight(1)
                timestamp = parts(0).capitalize + " " + parts.drop(1).mkString(" ")

                val formatter = DateTimeFormatter.ofPattern("MMMM d HH:mm:ss yyyy", Locale.ENGLISH)
                val formattedTimestamp = LocalDateTime.parse(timestamp, formatter)

                val now = LocalDateTime.now()
                val daysPassed = ChronoUnit.DAYS.between(formattedTimestamp, now)
                
                switchBranchPointer(currentDir, currentBranch)

                if (daysPassed > intDays) {
                    inactiveBranches = branch :: inactiveBranches
                }
            }
        }
    }
    return inactiveBranches
}

def showInactiveBranches(currentDir: String, days: String): Unit = {
    for (branch <- getInactiveBranches(currentDir, days)) {
        println(s"  ${branch}")
    }
}