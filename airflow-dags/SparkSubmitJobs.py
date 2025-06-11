from airflow.operators.python import PythonOperator
from airflow.providers.apache.spark.operators.spark_submit import SparkSubmitOperator
from airflow.decorators import dag
from datetime import datetime

from ExtractService import extract_and_upload


@dag(
    schedule_interval="*/4 * * * *",
    start_date=datetime(2025, 6, 10),
    catchup=False,
    tags=["currency", "spark"],
    dag_id = "currency_rates_dag_id",
    is_paused_upon_creation=False
)
def currency_batch_pipeline():

    run_spark_batch = SparkSubmitOperator(
        task_id="run_spark_batch",
        application="/absolute/path/to/spark/app",
        name="CurrencyBatchJob",
        java_class="com.example.currency.BatchProcessor",
        conn_id="spark_local",
        conf={
            "spark.hadoop.fs.s3a.access.key": "YOUR_ACCESS_KEY",
            "spark.hadoop.fs.s3a.secret.key": "YOUR_SECRET_KEY",
            "spark.hadoop.fs.s3a.endpoint": "https://s3.eu-north-1.amazonaws.com",
            "spark.driver.extraJavaOptions": "--add-exports java.base/sun.nio.ch=ALL-UNNAMED --add-exports java.base/sun.util.calendar=ALL-UNNAMED -Dlog4j.configurationFile=log4j2.xml",
            "spark.executor.extraJavaOptions": "--add-exports java.base/sun.nio.ch=ALL-UNNAMED --add-exports java.base/sun.util.calendar=ALL-UNNAMED -Dlog4j.configurationFile=log4j2.xml"
        }
    )

    extract_max_to_s3 = PythonOperator(
        task_id="extract_max_to_s3",
        python_callable=extract_and_upload
    )

    run_spark_batch >> extract_max_to_s3

currency_batch_pipeline_dag = currency_batch_pipeline()
