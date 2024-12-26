package datastructs

import scala.io.Source
import java.io.File
import scala.collection.mutable
import java.io.PrintWriter
import java.nio.file.{Paths, Files, Path}


class Index(val filePath: String) {
    // The main index as a Map where keys are strings and values are (old, new) tuples of SHA hashes
    var indexMap: Map[String, (String, String)] = Map()

    def getIndexPath(): Path = {
        val path = Paths.get(filePath).toAbsolutePath()
        return path.resolve("INDEX")
    }

    def initializeIndex(): Unit = {
        val indexPath = getIndexPath()
        if (!Files.exists(indexPath)) {
        Files.createFile(indexPath)
        }
        readFromIndex(indexPath.toString())
    }

    // Load the index into the data structure
    def readFromIndex(indexPath: String): Unit = {
        val source = Source.fromFile(indexPath)
        try {
            indexMap = source.getLines().foldLeft(Map.empty[String, (String, String)]) { (acc, line) =>
            val parts = line.split(":")
            if (parts.length == 3) {
                val key = parts(0)
                val oldhash = parts(1)
                val newhash = parts(2)
                acc + (key -> (oldhash, newhash))
            } else {
                acc
            }
          }
        } finally {
            source.close()
      }
    }

    // Method to write the index data structure back to a file
    def writeToIndex(): Unit = {
        var indexPath = getIndexPath().toString()
        val writer = new PrintWriter(new File(indexPath))
        try {
            for ((key, (hash1, hash2)) <- indexMap) {
            writer.write(s"$key:$hash1:$hash2\n")
            }
        } finally {
            writer.close()
        }
    }

    // Method to get the whole index structure
    def getIndex: Map[String, (String, String)] = indexMap

    def updateIndex(key: String, value: (String, String)): Unit = {
        indexMap = indexMap + (key -> value)
        writeToIndex()
    }

    def removeFromIndex(key: String): Unit = {
        indexMap = indexMap - key
        writeToIndex()
    }

    def getValueFromIndex(key: String): (String, String) = {
        if (indexMap contains key) {
            indexMap(key)
        }
        else {
            ("null", "null")
        }
      }
    }