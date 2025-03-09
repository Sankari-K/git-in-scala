package gitcommands

import java.nio.file.{Files, Paths}
import java.io.FileWriter
import java.io.File
import scala.collection.mutable

def setConfig(currentDir: String, attribute: String, value: List[String]): Unit = {

    if (attribute == "" || value.isEmpty) {
        println("error: both the config key and value must be specified")
        sys.exit(1)
    }

    val configs = importConfig(currentDir)
    configs(attribute) = value.mkString(" ")
    exportConfig(currentDir, configs)
}

def getConfig(currentDir: String, attribute: String): String = {
    var config = importConfig(currentDir)
    if (config.contains(attribute)) {
        return config(attribute)
    }
    println(s"error: please set up config for attribute: $attribute")
    sys.exit(1)
}

def importConfig(currentDir: String): mutable.Map[String, String] = {
    val configs: mutable.Map[String, String] = mutable.Map()

    val configPath = Paths.get(currentDir).toAbsolutePath().resolve(".wegit").resolve("CONFIG")

    if (!Files.exists(configPath)) {
        return mutable.Map()
    }

    val source = scala.io.Source.fromFile(configPath.toString()).getLines().toList

    var fileLine = 0
    while (fileLine < source.length) {
        val parts = source(fileLine).split("=").map(_.trim)
        configs += (parts(0) -> parts(1))
       
        fileLine += 1
    }
    return configs
}

def exportConfig(currentDir: String, config: mutable.Map[String, String]) = {
    val configPath = Paths.get(currentDir).toAbsolutePath().resolve(".wegit").resolve("CONFIG")

    if (!Files.exists(configPath)) {
        Files.createFile(configPath)
    }
    val source = scala.io.Source.fromFile(configPath.toString()).getLines().toList

    val writer = new FileWriter(new File(configPath.toString()))
        try {
            for ((key, value) <- config) {
                writer.write(s"$key=$value\n")
            }
        } finally {
            writer.close()
        }
}