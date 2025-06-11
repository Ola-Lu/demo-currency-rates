import psycopg2
import boto3
import csv
import os
from tempfile import NamedTemporaryFile

def extract_and_upload():
    # --- PostgreSQL config ---
    pg_config = {
        "host": "localhost",
        "port": 5432,
        "dbname": "demo",
        "user": "your_db_user",
        "password": "your_user_db_password"
    }

    # --- S3 config ---
    s3_bucket = "spark-batch-demo"
    s3_key = "max_currency_values.csv"
    aws_access_key = "YOUR_ACCESS_KEY"
    aws_secret_key = "YOUR_SECRET_KEY"

    # --- Query to extract max currency values ---
    query = """
            SELECT currency_from, currency_to, MAX(value) AS max_value
            FROM currency_rates
            GROUP BY currency_from, currency_to;
            """

    # --- Connect to PostgreSQL and fetch data ---
    with psycopg2.connect(**pg_config) as conn, conn.cursor() as cur:
        cur.execute(query)
        rows = cur.fetchall()

    # --- Write to temp CSV ---
    with NamedTemporaryFile(mode='w', delete=False, suffix='.csv') as temp_file:
        writer = csv.writer(temp_file)
        writer.writerow(["currency_from", "currency_to", "max_value"])
        writer.writerows(rows)
        temp_file_path = temp_file.name

    # --- Upload to S3 ---
    s3 = boto3.client(
        's3',
        aws_access_key_id=aws_access_key,
        aws_secret_access_key=aws_secret_key,
    )
    s3.upload_file(temp_file_path, s3_bucket, s3_key)

    # --- Cleanup ---
    os.remove(temp_file_path)
