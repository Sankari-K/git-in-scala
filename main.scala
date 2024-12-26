import datastructs.Index
import datastructs.Commits
import fileops.*

import java.nio.file.{Files, Path, Paths}


// Example usage
@main def main(): Unit = {
    // val filePath = ".gitignore"

    // computeFileHash(filePath) match {
    // case Right(hash) => println(s"File hash: $hash")
    // case Left(error) => println(s"Error: ${error.getMessage}")
    // }

    // -------------------
    // val currentDir = Paths.get(".") // Start from the current directory
    // listFilesInDirectory(currentDir) match {
    // case Right(files) =>
    //     files.foreach(println) // Print all file paths
    // case Left(error) =>
    //     println(s"Error: ${error.getMessage}")
    // }

    // ---------------

    // val inputPath = "./tryout/output.txt"
    // val outputPath = "./tryout/compressedoutput.txt"

    // decompressFile(inputPath, outputPath) match {
    // case Right(_) => println(s"File compressed successfully to $outputPath")
    // case Left(error) => println(s"Error: ${error.getMessage}")
    // }

    // -----------
    // val index = new Index("INDEX")

    // // Add some data to the index
    // index.updateIndex("file1", ("sha1hash1", "sha1hash2"))
    // index.updateIndex("file2", ("sha2hash1", "sha2hash2"))

    // // Clear the index and read it back from the file
    // index.readFromFile()

    // // Print the index to verify
    // println(index.getIndex)

    // val commits = new Commits("COMMITS")

    // // Create and add Index objects
    // val index1 = new Index("INDEX")

    // index1.updateIndex("file1", ("sha1hash1ahahah", "sha1hash2aahahah"))
    // index1.updateIndex("file2", ("sha2hash1aahahah", "sha2hash2aahaha"))

    // commits.addCommit("commitHash1", index)
    // commits.addCommit("commitHash2", index1)
    // val commitsins = new Commits("COMMITS")
    // commitsins.importCommits()

    // // Loop through all commits and print their content
    // println("Commits and their content:")
    // for ((hash, index) <- commitsins.commits) {
    //     println(s"Commit Hash: $hash")
    //     println("Files and Hashes:")
    //     for ((file, (oldHash, newHash)) <- index.getIndex) {
    //     println(s"  File: $file, Old Hash: $oldHash, New Hash: $newHash")
    //     }
    //     println()
    //     }
}
