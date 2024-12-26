package gitcommands

import java.nio.file.{Files, Paths}

def initRepo(directoryPath: String): Either[Throwable, String] = {
    val path = Paths.get(directoryPath)
    try {
        if (!Files.exists(path)) {
          Files.createDirectories(path) 
        }

        val wegitPath = path.resolve(".wegit")
        if (!Files.exists(wegitPath)) {
          Files.createDirectory(wegitPath) 
          val objects = path.resolve(".wegit/objs")
          Files.createDirectory(objects) 
          Right((s".wegit directory created inside: $directoryPath"))

        } else {
          Right((s".wegit directory already exists inside: $directoryPath"))

        }
      } catch {
        case e: Throwable => Left(e) // Return the error if there was one
    }
}
