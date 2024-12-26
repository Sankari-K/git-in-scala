import datastructs.Index
import datastructs.Commits
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
        if (checkIfRepo(currentDir)) {
            addFiles(currentDir, files)
        }
        else {
            println("fatal: not a wegit repository")
        }

        case "commit" :: message =>
        if (checkIfRepo(currentDir)) {
            ////
        }
        else {
            println("fatal: not a wegit repository")
        }

        case _ =>
        println("Usage:")
        println("  scala run *.scala -- init <directory>")
        println("  scala run *.scala -- add <file>")
  }
}
