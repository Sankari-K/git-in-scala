// Use this function to a) compute the sha hash given the file directory, b) go through the os file structure and return all paths

package fileops

import java.nio.file.{Files, Path, Paths}
import java.security.MessageDigest
import scala.util.Using
import scala.jdk.StreamConverters._
import java.util.zip.{DeflaterOutputStream, InflaterInputStream}
import java.io.{FileOutputStream, FileInputStream}

def computeFileHash(filePath: String, algorithm: String = "SHA-256"): Either[Throwable, String] = {
  try {
    val bytes = Files.readAllBytes(Paths.get(filePath)) // Read the entire file into a byte array
    val digest = MessageDigest.getInstance(algorithm).digest(bytes) // Compute the hash
    Right(digest.map("%02x".format(_)).mkString) // Convert the hash to a hex string
  } catch {
    case e: Throwable => Left(e) // Return any errors
  }
}

def listFilesInDirectory(directory: Path): Either[Throwable, List[String]] = {
  try {
    val fileList = Files.walk(directory).toScala(Seq) // Recursively traverse the file structure
      .filter(Files.isRegularFile(_)) // Only include regular files
      .map(path => directory.relativize(path).toString) // Convert to paths relative to the directory
      .toList

    Right(fileList)
  } catch {
    case e: Throwable => Left(e) // Return any errors
  }
}

def compressFile(inputFilePath: String, outputFilePath: String): Either[Throwable, Unit] = {
   try {
    val inputBytes = Files.readAllBytes(Paths.get(inputFilePath)) // Read the file content as bytes

    // Write compressed data to the output file with .txt extension
    val outputStream = new DeflaterOutputStream(new FileOutputStream(outputFilePath))
    try {
      outputStream.write(inputBytes)
    } finally {
      outputStream.close() // Ensure resources are released
    }

    Right(()) // Return success
  } catch {
    case e: Throwable => Left(e) // Return the error
  }
}

def decompressFile(inputFilePath: String, outputFilePath: String): Either[Throwable, Unit] = {
  try {
    val inputStream = new InflaterInputStream(new FileInputStream(inputFilePath))
    val outputStream = new FileOutputStream(outputFilePath)
    try {
      inputStream.transferTo(outputStream) // Simpler way to copy all bytes from input to output
    } finally {
      inputStream.close() // Ensure input stream is closed
      outputStream.close() // Ensure output stream is closed
    }
    Right(()) // Return success
  } catch {
    case e: Throwable => Left(e) // Return the error
  }
}
