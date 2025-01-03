package gitcommands

import datastructs.*
import java.io.File

def removeFile(currentDir: String, files: List[String]): Unit = {
    val index = new Index(currentDir)
    index.initializeIndex()

    index.initializeIndex()
    for (file <- files) {
        if (index.getIndex contains file) {
            var (oldhash, newhash) = index.getIndex(file)
            if (oldhash != newhash) {
                println("error: the following file has changes staged in the index: " + file)
                sys.exit(1)
            }
            File(file).delete()
            index.updateIndex(file, (oldhash, "null"))
        }
    }
}