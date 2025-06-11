package com.example.currency

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{DataFrame, functions}
import org.json4s.jackson.Serialization.writePretty
import org.json4s.jackson.{JsonMethods, Serialization}
import org.json4s.{DefaultFormats, Formats}
import org.slf4j.LoggerFactory

import java.io.{BufferedReader, BufferedWriter, InputStreamReader, OutputStreamWriter}
import java.net.URI

class KafkaOffsetsHelper(config: AppConfig) {
  implicit val formats: Formats = DefaultFormats
  private val logger = LoggerFactory.getLogger(this.getClass)

  private def getFileSystem(uri: String): FileSystem = {
    val conf = new Configuration()
    conf.set("fs.s3a.access.key", config.s3AccessKey)
    conf.set("fs.s3a.secret.key", config.s3SecretKey)
    conf.set("fs.s3a.endpoint", config.s3Endpoint)
    FileSystem.get(new URI(uri), conf)
  }

  def loadOffsets(): String = {
    val fs = getFileSystem(config.offsetsPath)
    val reader = new BufferedReader(new InputStreamReader(fs.open(new Path(config.offsetsPath))))
    try {
      val offsetsAsMap = JsonMethods.parse(reader).extract[Map[String, Map[String, Long]]]
      Serialization.write(offsetsAsMap)
    } finally {
      reader.close()
    }
  }

  def saveOffsets(df: DataFrame): Unit = {
    if (!df.isEmpty) {
      val offsets = df.selectExpr("topic", "partition", "offset")
        .groupBy("topic", "partition")
        .agg(functions.max("offset").alias("offset"))
        .collect()
        .groupBy(_.getAs[String]("topic"))
        .map { case (topic, rows) =>
          topic -> rows.map { r =>
            val partition = r.getAs[Int]("partition").toString
            val nextOffset = r.getAs[Long]("offset") + 1
            partition -> nextOffset
          }.toMap
        }

      val fs = getFileSystem(config.offsetsPath)
      val writer = new BufferedWriter(new OutputStreamWriter(fs.create(new Path(config.offsetsPath), true)))
      try {
        writer.write(writePretty(offsets))
      } finally {
        writer.close()
      }
    } else {
      logger.info("No new data — offsets not updated.")
    }
  }

}

