package gitcommands

import java.nio.file.Paths

def getIgnoredFiles(currentDir: String): List[String] = {
    // assumes .ignore is always in the root directory
    // can't add a file if it is in .ignore
    val gitignorePath = Paths.get(currentDir).toAbsolutePath().resolve(".ignore");
    try {
        val source = scala.io.Source.fromFile(gitignorePath.toString()).getLines().toList

        var ignoredFiles = List[String]()
        var fileLine = 0
        while (fileLine < source.length) {
            ignoredFiles = ignoredFiles :+ source(fileLine)
            fileLine += 1
        }
        return ignoredFiles
    }
    catch {
        case _: Exception => return List()
    }
}