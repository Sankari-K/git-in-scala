package datastructs

import scala.io.Source
import java.io.File
import scala.collection.mutable
import java.io.PrintWriter

class Index(private val filePath: String) {
  // The main index as a Map where keys are strings and values are (old, new) tuples of SHA hashes
  var indexMap: Map[String, (String, String)] = Map()

  // Load the index into the data structure
  def readFromFile(): Unit = {
    val source = Source.fromFile(filePath)
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
  def writeToFile(): Unit = {
    val writer = new PrintWriter(new File(filePath))
    // val writer = new BufferedWriter(new FileWriter(filePath))
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
    writeToFile()
  }

  def removeFromIndex(key: String): Unit = {
    indexMap = indexMap - key
    writeToFile()
  }
}