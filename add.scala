package gitcommands

import fileops.*
import datastructs.*
import java.nio.file.{Paths}
import javax.net.ssl.TrustManager

def addFiles(currentDir: String, files: List[String]): Unit = {
    var existingFiles = listFilesInDirectory(Paths.get(currentDir)).getOrElse(List[String]())
    val index = new Index("INDEX")
    index.readFromIndex()

    for (file <- files) {
        if (existingFiles contains file) {
            println(s"add file to repository: $file")

            var (oldhash, newhash) = index.getValueFromIndex(file)
            index.updateIndex(file, (oldhash, computeFileHash(file)))
        }
        else {
            println(s"fatal: pathspec $file did not match any files")
        }
    }
}