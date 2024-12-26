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

        case _ =>
        println("Usage:")
        println("  scala run *.scala -- init <directory>")
        println("  scala run *.scala -- add <file>")
        println("  scala run *.scala -- commit <message>")
  }
}
