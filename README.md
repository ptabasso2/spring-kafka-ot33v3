## spring-kafka-ot33v3


A detailed step-by-step tutorial on how to implement an Apache Kafka Consumer and Producer using Spring Kafka and Spring Boot.

**_Preliminary tasks and first time steps_**

Install Kafka on mac OSX (High Sierra)

```
COMP10619:Kafka pejman.tabassomi$ brew install kafka
```

Start Zookeeper

```
COMP10619:Kafka pejman.tabassomi$ zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties
```

Start kafka server

```
COMP10619:Kafka pejman.tabassomi$ kafka-server-start /usr/local/etc/kafka/server.properties
```

Create a Topic (Optional no longer needed since change #1) 

```
COMP10619:Kafka pejman.tabassomi$ kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic users
```


**_Administration tasks_** 

Start Zookeeper

```
COMP10619:Kafka pejman.tabassomi$ zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties
```

Start kafka server

```
COMP10619:Kafka pejman.tabassomi$ kafka-server-start /usr/local/etc/kafka/server.properties
```

List all Topics

```
COMP10619:Kafka pejman.tabassomi$ kafka-topics --list --zookeeper localhost:2181
```

Check if data is landing in kafka

```
COMP10619:Kafka pejman.tabassomi$ kafka-console-consumer --bootstrap-server localhost:9092 --topic users --from-beginning
```


**_Spin up the Datadog Agent (Provide your API key  to the  below command)_** 


```
COMP10619:Kafka pejman.tabassomi$ DOCKER_CONTENT_TRUST=1 docker run -d --rm --name datadog_agent7 -h datadog \ 
-v /var/run/docker.sock:/var/run/docker.sock:ro -v /proc/:/host/proc/:ro -v /sys/fs/cgroup/:/host/sys/fs/cgroup:ro \
-p 8126:8126 -p 8125:8125/udp -e DD_API_KEY=<Api key to enter> -e DD_APM_ENABLED=true \
-e DD_APM_NON_LOCAL_TRAFFIC=true -e DD_PROCESS_AGENT_ENABLED=true -e DD_DOGSTATSD_NON_LOCAL_TRAFFIC="true" \ 
-e DD_LOG_LEVEL=debug datadog/agent:7
```


**_Start the spring boot app_**

```
COMP10619:spring-kafka-ot33v3 pejman.tabassomi$ java -Ddd.service=springkafka -Ddd.tags=env:datadoghq.com -jar build/libs/spring-kafka-ot33v3.jar
```


**_Run the tests_**

```
COMP10619:Kafka pejman.tabassomi$ curl localhost:8080/test
```

<br>

| # | Changes
|----------|---------
| 1 | Automatic topic creation (NewTopic)
| 2 | Cleaning out code (tracing parts)
| 3 | Dual bean definitions (Jaeger & Datadog) and conditional dependency injection + @Primary  
| 4 | Updating opentracing kafka spring, kafka client, spring framework & dd-trace-ot
| 5 | Updating gradle version => 5.6.2
| 6 | Updating the project JDK (12)
| 7 | Using port 8080 instead 9093
