import datastructs.Index
import datastructs.Commits
import fileops.*
import gitcommands.*

import java.nio.file.{Files, Path, Paths}

@main def main(args: String*): Unit = {
    args.toList match {
        case "init" :: Nil =>
        val currentDir = Paths.get(".").toAbsolutePath.toString
        initRepo(currentDir) match {
            case Right(message) => println(s"$message")
            case Left(error) => println(s"Error: ${error.getMessage}")
        }

        case "init" :: dir :: Nil =>
        initRepo(dir) match {
            case Right(message) => println(s"$message")
            case Left(error) => println(s"Error: ${error.getMessage}")
        }
        
        case "add" :: file :: Nil =>
        println(s"Adding file to repository: $file")
        // Call your "add" function here
        // addFileToRepo(file)
        
        case _ =>
        println("Usage:")
        println("  scala run *.scala -- init <directory>")
        println("  scala run *.scala -- add <file>")
  }
}
