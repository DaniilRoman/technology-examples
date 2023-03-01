package com.example.ksql.saga

import com.fasterxml.jackson.databind.ObjectMapper
import com.test.saga.SagaStatusSubscriber
import com.test.saga.service.EventAggregatorRepository
import com.test.saga.service.ServiceRuleDto
import com.test.saga.service.ServiceWorkerDto
import com.test.saga.service.WorkerName
import io.confluent.ksql.api.client.*
import io.confluent.ksql.api.client.exception.KsqlClientException
import mu.KotlinLogging
import org.apache.kafka.clients.admin.KafkaAdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.Exception
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.stream.Stream

internal class NewVersionTest {
    private val options: ClientOptions = ClientOptions.create()
        .setHost(KSQLDB_SERVER_HOST)
        .setPort(KSQLDB_SERVER_PORT)

    private val client = Client.create(options)
    private val kafkaAdmin = KafkaAdminClient.create(KAFKA_ADMIN_PROPERTIES)
    private val kafkaProducer = KafkaProducer(KAFKA_ADMIN_PROPERTIES, StringSerializer(), StringSerializer())

    private val log = KotlinLogging.logger {}

    private val repo = EventAggregatorRepository()

    val worker1 = ServiceWorkerDto(
        "service1",
        "worker1",
        "inputTopic1",
        "outputTopic1"
    )
    val worker2 = ServiceWorkerDto(
        "service2",
        "worker1",
        "inputTopic2",
        "outputTopic2"
    )
    val worker3 = ServiceWorkerDto(
        "service3",
        "worker1",
        "inputTopic3",
        "outputTopic3"
    )
    val rule = ServiceRuleDto(
        "rule_service",
        "ruleInputTopic",
        Arrays.asList(
            WorkerName("service1", "worker1"),
            WorkerName("service2", "worker1"),
            WorkerName("service3", "worker1")
        ),
        "ruleOutputTopic",
        "rule1"
    )

    @BeforeEach
    fun before() {
        repo.workers[worker1.serviceName+"_"+worker1.workerName] = worker1
        repo.workers[worker2.serviceName+"_"+worker2.workerName] = worker2
        repo.workers[worker3.serviceName+"_"+worker3.workerName] = worker3
    }

    @Test
    fun delete() {
//        deleteTopics(listOf(worker1, worker2, worker3))
//        createTmpTopics(listOf(worker1, worker2, worker3))
//        terminateQueries()
        clearEverything()
    }

    @Test
    fun create() {
        log.info { "================ Creating ksql streams ================" }
        genCreateStreamSql(rule)
//        createStreamSubscriberAndSubscribe(rule)


        log.info { "================ Creating ksql table ================" }
        createKTable(rule)
    }

    @Test
    fun statusesTest() {
        log.info("============   START   =============")

//        terminateQueries()
//        clearRule(rule)
//        clearWorkers(listOf(worker1, worker2, worker3))

//        deleteTopics(listOf(worker1, worker2, worker3))
//        createTmpTopics(listOf(worker1, worker2, worker3))

        createSubscriberAndSubscribe(worker1, rule)

        insertSampleData(rule)
        Thread.sleep(15000)
        log.info("============   END   =============")



        // create stream from topics
        // send final message to target topic
        // resend message from one topic to several ones

        // TESTS
        // register worker (just save in map)
        // register rule -> create stream from several topics -> create table for stream -> send message from table

        // send message from rule service
        // check to get response

    }

    private fun createTmpTopics(serviceWorkerDtos: List<ServiceWorkerDto>) {
        log.info { "============================= Creating topics started =============================" }
        kafkaAdmin.createTopics(serviceWorkerDtos.flatMap {
            listOf(
                NewTopic(it.inputTopic, 1, 1), NewTopic(it.outputTopic, 1, 1)
            )
        }).all().get()
        log.info { "============================== Creating topics ended ==============================" }
    }
    private fun deleteTopics(serviceWorkerDtos: List<ServiceWorkerDto>) {
        try {
            kafkaAdmin.deleteTopics(serviceWorkerDtos.flatMap { listOf(it.inputTopic, it.outputTopic) }).all().get()
        } catch (ex: Exception) {
            log.error("Skip", ex)
        }
    }

    fun createSubscriberAndSubscribe(workerDto: ServiceWorkerDto, rule: ServiceRuleDto): CompletableFuture<Void> {
        val statusSubscriber = SagaStatusSubscriber(rule)
        val streamQuery = "SELECT * FROM ${rule.getTableName()} EMIT CHANGES;"
        return client.streamQuery(streamQuery, PROPERTIES)
            .thenAccept { streamedQueryResult: StreamedQueryResult ->
                streamedQueryResult.subscribe(
                    statusSubscriber
                )
            }
            .whenComplete { _: Void, ex: Throwable ->
                log.error("Status events push query failed", ex)
            }
    }

    fun createStreamSubscriberAndSubscribe(rule: ServiceRuleDto): CompletableFuture<Void> {
        val statusSubscriber = SagaStatusSubscriber(rule)
        val streamQuery = "SELECT * FROM service1_worker1_stream EMIT CHANGES;"
        return client.streamQuery(streamQuery, PROPERTIES)
            .thenAccept { streamedQueryResult: StreamedQueryResult ->
                streamedQueryResult.subscribe(
                    statusSubscriber
                )
            }
            .whenComplete { _: Void, ex: Throwable ->
                log.error("Status events push query failed", ex)
            }
    }

    private fun createKTable(serviceRuleDto: ServiceRuleDto) {
        val tableCreateSql = genCreateTableSql(serviceRuleDto)
        execWithCatch(tableCreateSql)
    }

    private fun genCreateStreamSql(serviceRuleDto: ServiceRuleDto) {
        val workers = serviceRuleDto.workers.map {
            repo.workers[it.serviceName+"_"+it.workerName]!!
        }

        workers.forEach { w ->
            val partialStream = """
                 CREATE STREAM ${w.getStreamsName()} (request_id VARCHAR KEY, service VARCHAR, status VARCHAR)
                    WITH (KAFKA_TOPIC = '${w.outputTopic}',
                    VALUE_FORMAT = 'JSON',
                    PARTITIONS = 1);
            """.trimIndent()
            execWithCatch(partialStream)
        }

        execWithCatch("INSERT INTO ${workers[0].getStreamsName()} SELECT request_id, service, status FROM ${workers[1].getStreamsName()};")
        execWithCatch("INSERT INTO ${workers[0].getStreamsName()} SELECT request_id, service, status FROM ${workers[2].getStreamsName()};")

//        val sumupstream = """
//             CREATE TABLE ${serviceRuleDto.getStreamsName()} AS
//              SELECT s1.*, s2.*, s3.*
//              FROM ${workers[0].getStreamsName()} s1
//                INNER JOIN ${workers[1].getStreamsName()} s2 WITHIN 30 SECONDS ON s1.request_id = s2.request_id
//                INNER JOIN ${workers[2].getStreamsName()} s3 WITHIN 30 SECONDS ON s1.request_id = s3.request_id
//              EMIT CHANGES;
//        """.trimIndent()



//        val sumupstream = """
//             CREATE STREAM ${serviceRuleDto.getStreamsName()} AS
//              SELECT
//                s1.request_id,
//                s1.status,
//                s1.service
//              FROM ${workers[0].getStreamsName()} s1
//                INNER JOIN ${workers[1].getStreamsName()} s2 WITHIN 30 SECONDS ON s1.request_id = s2.request_id
//                INNER JOIN ${workers[2].getStreamsName()} s3 WITHIN 30 SECONDS ON s1.request_id = s3.request_id
//              GROUP BY request_id
//              EMIT CHANGES;
//        """.trimIndent()
//        execWithCatch(sumupstream)
    }

    private fun genCreateTableSql(serviceRuleDto: ServiceRuleDto): String {
        return """
                CREATE TABLE ${serviceRuleDto.getTableName()} AS
                  SELECT
                    request_id,
                    COLLECT_LIST(status) as statuses,
                    COLLECT_LIST(service) as services
                  FROM service1_worker1_stream
                  WINDOW TUMBLING (SIZE ${3} SECONDS)
                  GROUP BY request_id
                  HAVING COUNT(status)=3
                  EMIT CHANGES;
        """.trimIndent()
    }

    private fun execWithCatch(sql: String) {
        try {
            client!!.executeStatement(sql, PROPERTIES).get()
        } catch (ex: CompletionException) {
            if (ex.cause is KsqlClientException && ex.message?.contains("with the same name already exists") == true) {
                log.error("Duplicate: $sql")
                log.info("------------------------------------------------------------")
            } else {
                throw ex
            }
        }
    }

    private fun insertSampleDataStream(rule: ServiceRuleDto) {
        val data =
            Stream.of(
                KsqlObject().put("request_id", "1").put("service", "service_1")
                    .put("status", "SERVICE_APPROVED"),
                KsqlObject().put("request_id", "1").put("service", "service_2")
                    .put("status", "SERVICE_APPROVED"),
                KsqlObject().put("request_id", "1").put("service", "service_3")
                    .put("status", "SERVICE_APPROVED"),
                KsqlObject().put("request_id", "2").put("service", "service_1")
                    .put("status", "SERVICE_APPROVED"),
                KsqlObject().put("request_id", "2").put("service", "service_2")
                    .put("status", "SERVICE_REJECTED"),
                KsqlObject().put("request_id", "2").put("service", "service_3")
                    .put("status", "SERVICE_APPROVED")
            )
                .peek { s: KsqlObject ->
                    s.put("source_topic", "source_topic").put("target_topic", "target_topic")
                }

        data.map { row -> client.insertInto(rule.getStreamsName(), row) }.forEach { insertResult -> insertResult.get() }
    }

    data class KafkaOutputEvent(val request_id: Int, val service: String, val status: String) {

        fun toJson(): String = OBJECT_MAPPER.writeValueAsString(this)
        companion object {
            var OBJECT_MAPPER = ObjectMapper()
        }
    }

    private fun insertSampleData(rule: ServiceRuleDto) {
        Stream.of(
            ProducerRecord("outputTopic1", "1", KafkaOutputEvent(1, "service_1",  "SERVICE_APPROVED").toJson()),

            ProducerRecord("outputTopic1", "1", KafkaOutputEvent(1, "service_1",  "SERVICE_APPROVED").toJson()),

            ProducerRecord("outputTopic2", "1", KafkaOutputEvent(1, "service_2",  "SERVICE_APPROVED").toJson()),
            ProducerRecord("outputTopic2", "2", KafkaOutputEvent(2, "service_2",  "SERVICE_REJECTED").toJson()),

            ProducerRecord("outputTopic3", "1", KafkaOutputEvent(1, "service_3",  "SERVICE_APPROVED").toJson()),
            ProducerRecord("outputTopic3", "2", KafkaOutputEvent(2, "service_3",  "SERVICE_APPROVED").toJson()),


            ProducerRecord("outputTopic3", "3", KafkaOutputEvent(3, "service_3",  "SERVICE_APPROVED").toJson()),
            ProducerRecord("outputTopic2", "3", KafkaOutputEvent(3, "service_2",  "SERVICE_APPROVED").toJson()),
            ProducerRecord("outputTopic2", "3", KafkaOutputEvent(3, "service_1",  "SERVICE_APPROVED").toJson()),
        ).map { kafkaProducer.send(it) }.peek { log.info { "Sent" } }.forEach { it.get() }
    }


    private fun terminateQueries() {
//        client.listQueries()
//            .thenApply { queryInfos: List<QueryInfo> ->
//                queryInfos.forEach { log.info { it.id } }
//                queryInfos.forEach { client.executeStatement("TERMINATE ${it.id};") }
//                queryInfos.forEach { client.executeStatement("DROP QUERY ${it.id};") }
//            }
//            .join()
        client.listTables()
            .thenApply { queryInfos ->
                queryInfos.forEach { log.info { it.name } }
                queryInfos.forEach { client.executeStatement("TERMINATE ${it.name};") }
                queryInfos.forEach { client.executeStatement("DROP TABLE ${it.name};") }
            }
            .join()
//        client.listStreams()
//            .thenApply { queryInfos ->
//                queryInfos.forEach { log.info { it.name } }
//                queryInfos.forEach { client.executeStatement("TERMINATE ${it.name};") }
//                queryInfos.forEach { client.executeStatement("DROP STREAM ${it.name};") }
//            }
//            .join()
    }

    fun clearEverything() {
        terminateEverything()
        dropEverything()
    }

    private fun terminateEverything() {
        client.listTables()
            .thenApply { queryInfos ->
                queryInfos.forEach { log.info { it.name } }
                queryInfos.forEach { client.executeStatement("TERMINATE ${it.name};") }
            }
            .join()
        client.listStreams()
            .thenApply { queryInfos ->
                queryInfos.forEach { log.info { it.name } }
                queryInfos.forEach { client.executeStatement("TERMINATE ${it.name};") }
            }
            .join()
        client.listQueries()
            .thenApply { queryInfos: List<QueryInfo> ->
                queryInfos.forEach { log.info { it.id } }
                queryInfos.forEach { client.executeStatement("TERMINATE ${it.id};") }
            }
            .join()
    }

    private fun dropEverything() {
        client.listTables()
            .thenApply { queryInfos ->
                queryInfos.forEach { log.info { it.name } }
                queryInfos.forEach { client.executeStatement("DROP TABLE ${it.name} DELETE TOPIC;") }
            }
            .join()
        client.listStreams()
            .thenApply { queryInfos ->
                queryInfos.forEach { log.info { it.name } }
                queryInfos.forEach { client.executeStatement("DROP STREAM ${it.name} DELETE TOPIC;") }
            }
            .join()
    }

    private fun clearWorkers(workerDtos: List<ServiceWorkerDto>) {
        workerDtos.forEach {
            execWithCatch("DROP STREAM IF EXISTS ${it.getStreamsName()};")
        }
    }

    private fun clearRule(rule: ServiceRuleDto) {
        execWithCatch("DROP TABLE IF EXISTS ${rule.getTableName()};")
        execWithCatch("DROP STREAM IF EXISTS ${rule.getStreamsName()};")
//        client.listQueries()
//            .thenApply { queryInfos: List<QueryInfo> ->
//                queryInfos.stream()
//                    .filter { queryInfo: QueryInfo -> queryInfo.queryType == QueryType.PERSISTENT }
//                    .map { obj: QueryInfo -> obj.id }
//                    .findFirst()
//                    .orElseThrow { RuntimeException("Persistent query not found") }
//            }
//            .thenCompose { id: String ->
//                client.executeStatement(
//                    "TERMINATE $id;"
//                )
//            }
//            .thenCompose { result: ExecuteStatementResult? ->
//                client.executeStatement(
//                    "DROP TABLE status_events_table DELETE TOPIC;"
//                )
//            }
//            .thenCompose { result: ExecuteStatementResult? ->
//                client.executeStatement(
//                    "DROP STREAM status_events DELETE TOPIC;"
//                )
//            }
//            .join()
    }

    companion object {
        private const val KSQLDB_SERVER_HOST = "localhost"
        private const val KSQLDB_SERVER_PORT = 8088
        private const val TRANSACTIONAL_PREFIX = "events_aggregator_13"
        private val PROPERTIES = mapOf<String, Any>(Pair("auto.offset.reset", "latest"),
//            Pair(ProducerConfig.TRANSACTIONAL_ID_CONFIG, TRANSACTIONAL_PREFIX)
        )
        private val KAFKA_ADMIN_PROPERTIES = Properties().apply {
            this[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:29092"
            this[ProducerConfig.CLIENT_ID_CONFIG] = "testProducer"
            this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
            this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
//            this[ProducerConfig.TRANSACTIONAL_ID_CONFIG] = TRANSACTIONAL_PREFIX
        }
    }
}