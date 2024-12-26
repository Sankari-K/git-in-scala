package fileops

import java.nio.file.{Files, Path, Paths, StandardOpenOption}
import java.security.MessageDigest
import scala.util.Using
import scala.jdk.StreamConverters._
import java.util.zip.{DeflaterOutputStream, InflaterInputStream}
import java.io.{FileOutputStream, FileInputStream}

def computeFileHash(filePath: String, algorithm: String = "SHA-256"): String = {
    val bytes = Files.readAllBytes(Paths.get(filePath)) 
    val digest = MessageDigest.getInstance(algorithm).digest(bytes) 
    return digest.map("%02x".format(_)).mkString 
}

def listFilesInDirectory(directory: Path): Either[Throwable, List[String]] = {
    try {
      val fileList = Files.walk(directory).toScala(Seq) 
        .filter(Files.isRegularFile(_))
        .map(path => directory.relativize(path).toString) 
        .toList

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
