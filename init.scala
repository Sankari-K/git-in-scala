package gitcommands

import java.nio.file.{Files, Paths}

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
        Right((s".wegit directory created inside: $directoryPath"))
    } catch {
        case e: Throwable => Left(e)
    }
}

def checkIfRepo(directoryPath: String, errorMessage: String, isInitFlag: Boolean): Unit = {
    val path = Paths.get(directoryPath)
    val wegitPath = path.resolve(".wegit")
    if (Files.exists(wegitPath) == isInitFlag) {
        println(errorMessage)
        sys.exit(1)
    }
}
