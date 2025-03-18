package gitcommands

import java.nio.file.{Files, Paths}
import java.io.PrintWriter
import java.io.File

def initRepo(directoryPath: String): Either[Throwable, String] = {
    val path = Paths.get(directoryPath)
    try {
        if (!Files.exists(path)) {
            Files.createDirectories(path)
        }

        val wegitPath = path.resolve(".wegit")
        checkIfRepo(directoryPath, s".wegit directory already exists inside: $directoryPath", true)
        Files.createDirectory(wegitPath)

        val objects = path.resolve(".wegit/objs")
        Files.createDirectory(objects)

        var branchref = path.resolve(".wegit").resolve("HEAD-NAME")
        Files.createFile(branchref)
        val writer = new PrintWriter(new File(branchref.toString()))
        writer.write(s"main\n")
        writer.close()

        var branch = path.resolve(".wegit/main")
        Files.createDirectory(branch)

        Right((s".wegit directory created inside: $directoryPath"))
    } catch {
        case e: Throwable => Left(e)
    }
}

def checkIfRepo(directoryPath: String, errorMessage: String, negation: Boolean): Unit = {
    val path = Paths.get(directoryPath)
    val wegitPath = path.resolve(".wegit")
    if (Files.exists(wegitPath) == negation) {
        println(errorMessage)
        sys.exit(1)
    }
}
