package com.example.currency

import org.slf4j.LoggerFactory
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SparkSession}


object BatchProcessor {

  private val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("CurrencyRateBatchProcessor")
      .getOrCreate()

    try {
      val config = AppConfig.load()
      logger.info("starting reading from kafka....")
      val df = readFromKafka(spark, config)
      logger.info("starting process....")
      val processed = processRates(df)
      if (processed.head(1).nonEmpty) {
        logger.info("starting writing to db....")
        val writer = new JdbcWriter(config)
        writer.write(processed)
      } else {
        logger.info("No data received from Kafka — skipping write and offset save.")
      }


    } finally {
      spark.stop()
    }
  }

  private def readFromKafka(spark: SparkSession, config: AppConfig): DataFrame = {
    val schema = StructType(Seq(
      StructField("amount", DoubleType),
      StructField("base", StringType, nullable = false),
      StructField("date", StringType, nullable = false),
      StructField("rates", MapType(StringType, DoubleType), nullable = false)
    ))
    val kafkaOffsetsHelper = new KafkaOffsetsHelper(config)
    val kafkaStartingOffsets = kafkaOffsetsHelper.loadOffsets()

    val kafkaRawDf = spark.read
      .format("kafka")
      .option("kafka.bootstrap.servers", config.kafkaBootstrap)
      .option("subscribe", config.kafkaTopic)
      .option("startingOffsets", kafkaStartingOffsets)
      .load()

    logger.info("saving offsets to s3....")
    kafkaOffsetsHelper.saveOffsets(kafkaRawDf)

    kafkaRawDf.selectExpr("CAST(value AS STRING) as json")
      .select(from_json(col("json"), schema).as("data"))
      .select(
        col("data.base").as("currency_from"),
        to_date(col("data.date"), "yyyy-MM-dd").as("date"),
        explode(col("data.rates")).as(Seq("currency_to", "value")),
        current_timestamp().as("last_update_date")
      )
  }

  private def processRates(df: DataFrame): DataFrame = {
    df.filter(col("currency_from").isNotNull)
  }

}
