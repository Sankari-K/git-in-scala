package gitcommands

import datastructs.*
import java.nio.file.{Path, Paths, Files, StandardCopyOption}
import java.io.File

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