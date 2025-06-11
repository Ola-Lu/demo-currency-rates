# Project Overview

This project demonstrates the interaction of the following technologies:

1. **Kafka** – a distributed event streaming platform used to handle large volumes of real-time data.  
2. **Spark** – an open-source distributed computing framework designed for big data processing and fast, in-memory computations.  
3. **Airflow** – a workflow orchestration tool used to programmatically author, schedule, and monitor data pipelines.

---

## Flow Diagram

You can find a schema of the data flow in the file:  
📄 `Schema_of_the_flow.drawio`

---

## Demo Video

The demonstration video is available in:  
🎥 `demo.mkv`

It was recorded using **OBS Studio**, and the audio was enhanced using **Audacity**.

> 🔊 **Note:** The audio quality is still suboptimal even after improvement. Please increase the volume as needed, and kindly ignore minor sound issues.

---

## Requirements

To run this project, you will need:

- **Kafka server**  
  _Version used:_ `kafka_2.13-3.9.1`  
  _Java version:_ `17`

- **Apache Spark**  
  _Version used:_ `3.5.5`  
  _Scala version:_ `2.12.18`  
  _Java version:_ `17`

- **Apache Airflow**  
  pip install "apache-airflow==2.9.0" \
    --constraint "https://raw.githubusercontent.com/apache/airflow/constraints-2.9.0/constraints-3.10.txt"

  pip install apache-airflow-providers-apache-spark==4.1.1
  
- **PostgreSQL**
- **AWS S3 account** (A free-tier account is sufficient)  

