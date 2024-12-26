import datastructs.*
import fileops.*
import java.nio.file.{Paths}

def restoreFiles(currentDir: String, files: List[String]): Unit = {
    val index = new Index(currentDir)
    index.initializeIndex()

    for (file <- files) {
        if (index.getIndex contains file) {
            var (_, newhash) = index.getValueFromIndex(file)

            val path = Paths.get(currentDir)
            var objspath = path.resolve(".wegit").resolve("objs").resolve(newhash)

            println(file + "   " + objspath)
            addDecompressedFile(objspath.toString(), file)

        }
    }
    // for ((filepath, (oldhash, newhash)) <- index.getIndex) {
    //     println(filepath + "|" + oldhash + "|" + newhash)
    // }

    println("hello")
}

def restoreStagedFiles(currentDir: String, files: List[String]): Unit = {
    val index = new Index(currentDir)
    index.initializeIndex()

    for (file <- files) {
        index.removeFromIndex(file)
    }
}