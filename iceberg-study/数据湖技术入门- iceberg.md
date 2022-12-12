
# 数据湖技术入门 - iceberg

##  如何查看 iceberg 的数据组织结构

[avro-tools](https://repo1.maven.org/maven2/org/apache/avro/avro-tools/1.11.1/avro-tools-1.11.1.jar) 下载

``` shell
java -jar /path/to/avro-tools.jar tojson /path/of/avrofile
```

建立 iceberg 表, 元数据保存在 hadoop

``` sql
create  table test_iceberg_tbl5(
id int,
name string,
age int
)
partitioned by (dt string)
stored by 'org.apache.iceberg.mr.hive.HiveIcebergStorageHandler'
location 'hdfs://192.168.44.105/iceberg_data/default/test_iceberg_tbl5'
tblproperties ('iceberg.catalog'='hadoop');
```


``` sql
create  table flink_iceberg_tbl(
id int,
name string,
age int,
loc string 
)
stored by 'org.apache.iceberg.mr.hive.HiveIcebergStorageHandler'
location 'hdfs://hadoop105:8020/flink_iceberg/icebergdb/flink_iceberg_tbl'
tblproperties ('iceberg.catalog'='location_based_table');
```



``` xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <flink.version>1.3.5</flink.version>
    <hadoop.version>3.1.3</hadoop.version>
  </properties>

  <dependencies>

    <!-- https://mvnrepository.com/artifact/com.alibaba.ververica/ververica-connector-iceberg -->
<!--    <dependency>-->
<!--      <groupId>com.alibaba.ververica</groupId>-->
<!--      <artifactId>ververica-connector-iceberg</artifactId>-->
<!--      <version>1.13-vvr-4.0.7</version>-->
<!--    </dependency>-->

    <!-- https://mvnrepository.com/artifact/org.apache.flink/flink-table-planner -->
    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-table-planner_2.11</artifactId>
      <version>1.13.5</version>
      <scope>test</scope>
    </dependency>


    <!-- https://mvnrepository.com/artifact/org.apache.iceberg/iceberg-flink-runtime -->
    <dependency>
      <groupId>org.apache.iceberg</groupId>
      <artifactId>iceberg-flink-runtime</artifactId>
      <version>0.12.1</version>
    </dependency>

    <!-- https://mvnrepository.com/artifact/org.apache.flink/flink-java -->
    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-java</artifactId>
      <version>1.13.5</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.apache.flink/flink-clients -->
    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-clients_2.11</artifactId>
      <version>1.13.5</version>
    </dependency>

    <!-- https://mvnrepository.com/artifact/org.apache.flink/flink-streaming-scala -->
    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-streaming-scala_2.11</artifactId>
      <version>1.13.5</version>
      <scope>provided</scope>
    </dependency>

    <!-- https://mvnrepository.com/artifact/org.apache.flink/flink-connector-kafka -->
    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-connector-kafka_2.11</artifactId>
      <version>1.13.5</version>
    </dependency>

    <!-- https://mvnrepository.com/artifact/org.apache.flink/flink-connector-base -->
    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-connector-base</artifactId>
      <version>1.13.5</version>
    </dependency>

    <!-- https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-client -->
    <dependency>
      <groupId>org.apache.hadoop</groupId>
      <artifactId>hadoop-client</artifactId>
      <version>3.1.3</version>
      <scope>provided</scope>
    </dependency>

    <!-- https://mvnrepository.com/artifact/org.apache.flink/flink-runtime-web -->
<!--    <dependency>-->
<!--      <groupId>org.apache.flink</groupId>-->
<!--      <artifactId>flink-runtime-web_2.11</artifactId>-->
<!--      <version>1.13.5</version>-->
<!--      <scope>test</scope>-->
<!--    </dependency>-->

    <!-- https://mvnrepository.com/artifact/org.apache.flink/flink-table-common -->
    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-table-common</artifactId>
      <version>1.13.5</version>
      <scope>provided</scope>
    </dependency>

    <!-- https://mvnrepository.com/artifact/org.apache.flink/flink-table-planner-blink -->
    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-table-planner-blink_2.11</artifactId>
      <version>1.13.5</version>
<!--      <scope>test</scope>-->
    </dependency>



    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
```

