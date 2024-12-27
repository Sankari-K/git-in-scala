import datastructs.Index
import datastructs.Commit
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

        case _ =>
        println("Usage:")
        println("  scala run *.scala -- init <directory>")
        println("  scala run *.scala -- add <file>")
        println("  scala run *.scala -- commit <message>")
        println("  scala run *.scala -- status")
        println("  scala run *.scala -- log")
        println("  scala run *.scala -- restore <files>")
        println("  scala run *.scala -- restore--staged <files>")
        println("  scala run *.scala -- checkout <hash>")
  }
}
