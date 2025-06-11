package com.example.currency

case class AppConfig(
                      kafkaBootstrap: String,
                      kafkaTopic: String,
                      offsetsPath: String,
                      jdbcUrl: String,
                      jdbcUser: String,
                      jdbcPassword: String,
                      jdbcTable: String,
                      s3AccessKey: String,
                      s3SecretKey: String,
                      s3Endpoint: String
)

object AppConfig {
  def load(path: String = "application.properties"): AppConfig = {
    val props = new java.util.Properties()
    val in = getClass.getClassLoader.getResourceAsStream("application.properties")
    try props.load(in) finally in.close()

    AppConfig(
      kafkaBootstrap = props.getProperty("kafka.bootstrap.servers"),
      kafkaTopic     = props.getProperty("kafka.topic"),
      offsetsPath    = props.getProperty("kafka.offsets.file"),
      jdbcUrl        = props.getProperty("jdbc.url"),
      jdbcUser       = props.getProperty("jdbc.user"),
      jdbcPassword   = props.getProperty("jdbc.password"),
      jdbcTable      = props.getProperty("jdbc.table"),
      s3AccessKey    = props.getProperty("s3.access.key"),
      s3SecretKey    = props.getProperty("s3.secret.key"),
      s3Endpoint     = props.getProperty("s3.endpoint")
    )
  }
}
