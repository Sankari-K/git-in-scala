import datastructs.*
import fileops.*
import gitcommands.*

import java.nio.file.{Files, Path, Paths}

@main def main(args: String*): Unit = {
    val currentDir = Paths.get(".").toAbsolutePath.toString

    args.toList match {
        case "init" :: Nil =>
        initRepo(currentDir) match {
            case Right(message) => println(s"$message")
            case Left(error) => println(s"Error: ${error.getMessage}")
        }

        case "init" :: dir :: Nil =>
        initRepo(dir) match {
            case Right(message) => println(s"$message")
            case Left(error) => println(s"Error: ${error.getMessage}")
        }
        
        // case "add" :: Nil | "add" :: "." :: Nil=>
        // addFiles(currentDir, "")

        case "add" :: files =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        addFiles(currentDir, files)

        case "commit" :: message :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        commitFiles(currentDir, message)

        case "amend--message" :: message :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        ammendMessage(currentDir, message)

        case "amend--commit" :: message :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        ammendCommit(currentDir, Option(message))

        case "amend--commit" :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        ammendCommit(currentDir, None)

        case "squash" :: number :: message :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        squashCommits(currentDir, number, message)

        case "status" :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        getStatus(currentDir)

        case "log" :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        getLog(currentDir)

        case "restore" :: files =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        restoreFiles(currentDir, files)

        case "restore--staged" :: files =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        restoreStagedFiles(currentDir, files)

        case "checkout" :: hash :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        checkoutCommit(currentDir, hash)

        case "rm" :: files =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        removeFile(currentDir, files)

        case "config" :: attribute :: value =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        setConfig(currentDir, attribute, value)

        case "create" :: "branch" :: branchName :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        createNewBranch(currentDir, branchName)

        case "checkout" :: "branch" :: branchName :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        checkoutBranch(currentDir, branchName)

        case "switch" :: "branch" :: branchName :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        switchBranch(currentDir, branchName)

        case "rename" :: "branch" :: branchName :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        renameCurrentBranch(currentDir, branchName)

        case "rename" :: "branch" :: oldBranchName :: newBranchName :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        renameBranch(currentDir, oldBranchName, newBranchName)

        case "delete" :: "branch" :: branchName :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        deleteBranch(currentDir, branchName)

        case "delete" :: "branch" :: "since" :: days :: "days" :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        deleteInactiveBranches(currentDir, days)

        case "show" :: "branch" :: "since" :: days :: "days" :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        showInactiveBranches(currentDir, days)

        case "branch" :: "show-current" :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        showCurrentBranch(currentDir)

        case "branch" :: "show-all" :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        showAllBranches(currentDir)

        case "diff" :: file :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        findFileDiff(currentDir, file)

        case "diff--staged" :: file :: Nil =>
        checkIfRepo(currentDir, "fatal: not a wegit repository", false)
        findStagedFileDiff(currentDir, file)

        case "pls-work" :: Nil =>
        plsWork()

        case _ =>
        println("Usage:")
        println("  scala run *.scala -- init <directory>")
        println("  scala run *.scala -- add <file>")
        println("  scala run *.scala -- commit <message>")
        println("  scala run *.scala -- amend--message <message>")
        println("  scala run *.scala -- amend--commit <message>")
        println("  scala run *.scala -- amend--commit")
        println("  scala run *.scala -- squash <number of commits> <commit message>")
        println("  scala run *.scala -- status")
        println("  scala run *.scala -- log")
        println("  scala run *.scala -- restore <files>")
        println("  scala run *.scala -- restore--staged <files>")
        println("  scala run *.scala -- checkout <hash>")
        println("  scala run *.scala -- rm <file>")
        println("  scala run *.scala -- config <key> <value>")
        println("  scala run *.scala -- create branch <branch name>")
        println("  scala run *.scala -- checkout branch <branch name>")
        println("  scala run *.scala -- switch branch <branch name>")
        println("  scala run *.scala -- rename branch <new branch name>")
        println("  scala run *.scala -- rename branch <old branch name> <new branch name>")
        println("  scala run *.scala -- delete branch <branch name>")
        println("  scala run *.scala -- delete branch since <days> days")
        println("  scala run *.scala -- show branch since <days> days")
        println("  scala run *.scala -- branch show-current")
        println("  scala run *.scala -- branch show-all")
        println("  scala run *.scala -- pls-work")
  }
}
