package com.example.currency

import org.apache.spark.sql.DataFrame
import java.util.Properties

class JdbcWriter(config: AppConfig) {

  private val connectionProperties = new Properties()
  connectionProperties.put("user", config.jdbcUser)
  connectionProperties.put("password", config.jdbcPassword)

  def write(df: DataFrame): Unit = {
    Class.forName("org.postgresql.Driver")
    df.write
      .mode("append")
      .option("driver", "org.postgresql.Driver")
      .jdbc(
        config.jdbcUrl,
        config.jdbcTable,
        connectionProperties
      )
  }
}

