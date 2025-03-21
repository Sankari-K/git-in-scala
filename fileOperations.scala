package fileops

import java.nio.file.{Files, Path, Paths, StandardOpenOption}
import java.security.MessageDigest
import scala.util.Using
import scala.jdk.StreamConverters._
import java.util.zip.{DeflaterOutputStream, InflaterInputStream}
import java.io.{FileOutputStream, FileInputStream, File, BufferedReader, InputStreamReader}
import gitcommands.*

def computeFileHash(filePath: String, algorithm: String = "SHA-256"): String = {
    val bytes = Files.readAllBytes(Paths.get(filePath)) 
    val digest = MessageDigest.getInstance(algorithm).digest(bytes) 
    return digest.map("%02x".format(_)).mkString 
}

def listFilesInDirectory(directory: Path): Either[Throwable, List[String]] = {
    try {
      var fileList = Files.walk(directory).toScala(Seq)
        .filter(path => Files.isRegularFile(path) && !path.toString.contains(".wegit"))
        .map(path => directory.relativize(path).toString)
        .toList

      fileList = fileList.filter(path => !getIgnoredFiles(directory.toString()).contains(path))

      Right(fileList)
    } catch {
      case e: Throwable => Left(e)
  }
}

def addCompressedFile(inputFilePath: String, outputFilePath: String): Either[Throwable, Unit] = {
    try {
        val inputBytes = Files.readAllBytes(Paths.get(inputFilePath)) 

        val outputStream = new DeflaterOutputStream(new FileOutputStream(outputFilePath))
        try {
            outputStream.write(inputBytes)
        } finally {
            outputStream.close() 
        }

        Right(()) 
    } catch {
        case e: Throwable => Left(e) 
    }
}

def addDecompressedFile(inputFilePath: String, outputFilePath: String): Either[Throwable, Unit] = {
    try {
        val inputStream = new InflaterInputStream(new FileInputStream(inputFilePath))
        val outputStream = new FileOutputStream(outputFilePath)
        try {
            inputStream.transferTo(outputStream) 
        } finally {
            inputStream.close()
            outputStream.close()
        }
        Right(())
    } catch {
        case e: Throwable => Left(e)
    }
}

def deleteDirectory(file: File): Unit = {
    if (file.isDirectory) {
        file.listFiles.foreach(deleteDirectory)
    }
    if (file.exists && !file.delete) {
        throw new Exception(s"Unable to delete ${file.getAbsolutePath}")
    }
}

def getFileContent(filePath: String): List[String] = {
    val sourceFile = scala.io.Source.fromFile(filePath)
    val source = sourceFile.getLines().toList
    sourceFile.close()
    return source
}

def getDecompressedFileContent(filePath: String): List[String] = {
    val inputStream = new InflaterInputStream(new FileInputStream(filePath))
    val reader = new BufferedReader(new InputStreamReader(inputStream))
    try {
        Iterator.continually(reader.readLine()).takeWhile(_ != null).toList
    } finally {
        reader.close()
    }
}