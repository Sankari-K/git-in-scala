package gitcommands

import fileops.*
import datastructs.*
import java.nio.file.{Paths}
import javax.net.ssl.TrustManager

def addFiles(currentDir: String, files: List[String]): Unit = {
    var existingFiles = listFilesInDirectory(Paths.get(currentDir)).getOrElse(List[String]())
    val path = Paths.get(currentDir)

    val index = new Index(currentDir)
    index.initializeIndex()
    
    for (file <- files) {
        if (existingFiles contains file) {
            println(s"add file to repository: $file")
            
            // add file to index
            var (oldhash, newhash) = index.getValueFromIndex(file)
            index.updateIndex(file, (oldhash, computeFileHash(file)))

            // add to objs
            var objspath = path.resolve(".wegit").resolve("objs").resolve(computeFileHash(file))
            addCompressedFile(file, objspath.toString())
        }
        else {
            println(s"fatal: pathspec $file did not match any files")
        }
    }
}