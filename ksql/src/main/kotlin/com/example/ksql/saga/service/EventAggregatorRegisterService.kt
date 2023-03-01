package com.test.saga.service

import io.confluent.ksql.api.client.Client
import io.confluent.ksql.api.client.ClientOptions
import io.confluent.ksql.api.client.QueryInfo
import io.confluent.ksql.api.client.exception.KsqlClientException
import mu.KotlinLogging
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import java.lang.Exception
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

data class ServiceWorkerDto(
    val serviceName: String,
    val workerName: String,
    val inputTopic: String,
    val outputTopic: String,
    val partitions: Int = 1
) {
    fun getStreamsName() = "${serviceName}_${workerName}_stream"

    fun key() = "${serviceName}_$workerName"
}

data class WorkerName(val serviceName: String, val workerName: String) {
    fun key() = "${serviceName}_$workerName"
}
data class ServiceRuleDto(
    val serviceName: String,
    val ruleName: String,

    val workers: List<WorkerName>,

    val inputTopic: String,
    val outputTopic: String,

    val timeout: Int = 30 // seconds
) {
    fun getStreamsName() = "${serviceName}_${ruleName}_stream"
    fun getTableName() = "${serviceName}_${ruleName}_table"

    fun key() = "${serviceName}_$ruleName"
}

class EventAggregatorRepository() {

    val workers: MutableMap<String, ServiceWorkerDto> = mutableMapOf()
    val rules: MutableMap<String, ServiceRuleDto> = mutableMapOf()

    fun save(worker: ServiceWorkerDto) = workers.put(worker.key(), worker)

    fun workerExists(workerName: WorkerName) = workers[workerName.key()] != null

    fun save(rule: ServiceRuleDto) = rules.put(rule.key(), rule)

    fun getWorker(workerName: WorkerName): ServiceWorkerDto = workers[workerName.key()]!!

}

class KafkaConfig {
    companion object {
        const val KSQLDB_SERVER_HOST = "localhost"
        const val KSQLDB_SERVER_PORT = 8088
        const val TRANSACTIONAL_PREFIX = "events_aggregator_13"
        val KAFKA_STREAMS_PROPERTIES = mapOf<String, Any>(Pair("auto.offset.reset", "latest"),
//            Pair(ProducerConfig.TRANSACTIONAL_ID_CONFIG, TRANSACTIONAL_PREFIX)
        )
        val KAFKA_ADMIN_PROPERTIES = Properties().apply {
            this[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:29092"
            this[ProducerConfig.CLIENT_ID_CONFIG] = "testProducer"
            this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
            this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
//            this[ProducerConfig.TRANSACTIONAL_ID_CONFIG] = TRANSACTIONAL_PREFIX
        }
    }
}

fun clientOptions(): ClientOptions {
    return ClientOptions.create()
        .setHost(KafkaConfig.KSQLDB_SERVER_HOST)
        .setPort(KafkaConfig.KSQLDB_SERVER_PORT)
}

class EventAggregatorRegisterService(options: ClientOptions, private val repo: EventAggregatorRepository) {

    private val log = KotlinLogging.logger {}
    private val client = Client.create(options)

    fun registerWorker(worker: ServiceWorkerDto) {
        repo.save(worker)
        createWorkerStream(worker)
    }

    fun registerRule(rule: ServiceRuleDto) {
        if (rule.workers.any { !repo.workerExists(it) }) {
            throw IllegalArgumentException("Configuration is not correct. Set up all workers first")
        }
        repo.save(rule)
        createRuleKTable(rule)
    }

    fun clearEverything() {
//        terminateEverything() // TODO
        dropEverything()
    }

//    private fun terminateEverything() {
//        val r = client.listTables();
//        r.whenComplete {}
//        .thenApply { queryInfos ->
//            queryInfos.forEach { log.info { it.name } }
//            queryInfos.forEach { client.executeStatement("TERMINATE ${it.name};") }
//        }
//            .thenApply {
//                client.listStreams()
//                    .thenApply { queryInfos ->
//                        queryInfos.forEach { log.info { it.name } }
//                        queryInfos.forEach { client.executeStatement("TERMINATE ${it.name};") }
//                    }
//            }.thenApply {
//                client.listQueries()
//                    .thenApply { queryInfos: List<QueryInfo> ->
//                        queryInfos.forEach { log.info { it.id } }
//                        queryInfos.forEach { client.executeStatement("TERMINATE ${it.id};") }
//                    }
//                    .join()
//            }
//
//    }


//    private fun terminateEverything() {
//        val r = client.listTables();
//            .thenApply { queryInfos ->
//                queryInfos.forEach { log.info { it.name } }
//                queryInfos.forEach { client.executeStatement("TERMINATE ${it.name};") }
//            }
//            .thenApply {
//                client.listStreams()
//                    .thenApply { queryInfos ->
//                        queryInfos.forEach { log.info { it.name } }
//                        queryInfos.forEach { client.executeStatement("TERMINATE ${it.name};") }
//                    }
//            }.thenApply {
//                client.listQueries()
//                    .thenApply { queryInfos: List<QueryInfo> ->
//                        queryInfos.forEach { log.info { it.id } }
//                        queryInfos.forEach { client.executeStatement("TERMINATE ${it.id};") }
//                    }
//                    .join()
//            }
//
//    }

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

    private fun createWorkerStream(worker: ServiceWorkerDto) {
        execWithCatch(
            createWorkerStreamSql(worker))
    }

    private fun createRuleKTable(serviceRuleDto: ServiceRuleDto) {
        val workers = serviceRuleDto.workers.map { repo.getWorker(it) }

        // CREAT RULE STREAM
        execWithCatch(
            copyStreamSql(from=workers[0].getStreamsName(), to=serviceRuleDto.getStreamsName()))


        // INSERT WORKER STREAMS TO RULE STREAM
        workers.stream().skip(1).forEach {
            execWithCatch(
                insertStreamSql(from=it.getStreamsName(), to=serviceRuleDto.getStreamsName()))
        }

        // CREATE RULE KTABLE
        val tableCreateSql = createTableSql(serviceRuleDto)
        execWithCatch(tableCreateSql)
    }

    private fun copyStreamSql(from: String, to: String) =
        """
            CREATE STREAM $to AS
              SELECT * FROM $from
              EMIT CHANGES;
        """.trimIndent()

    private fun createWorkerStreamSql(worker: ServiceWorkerDto) =
        """
         CREATE STREAM ${worker.getStreamsName()} (request_id VARCHAR KEY, service VARCHAR, status VARCHAR, payload VARCHAR)
            WITH (KAFKA_TOPIC = '${worker.outputTopic}',
            VALUE_FORMAT = 'JSON',
            PARTITIONS = ${worker.partitions});
        """.trimIndent()

    private fun insertStreamSql(from: String, to: String) =
        "INSERT INTO $to SELECT * FROM $from;"


    private fun createTableSql(serviceRuleDto: ServiceRuleDto) =
        """
            CREATE TABLE ${serviceRuleDto.getTableName()} AS
              SELECT
                request_id,
                COLLECT_LIST(status) as statuses,
                COLLECT_LIST(service) as services,
                COLLECT_LIST(payload) as payloads
              FROM ${serviceRuleDto.getStreamsName()}
              WINDOW TUMBLING (SIZE ${serviceRuleDto.timeout} SECONDS)
              GROUP BY request_id
              HAVING COUNT(status)=${serviceRuleDto.workers.size}
              EMIT CHANGES;
        """.trimIndent()

    private fun execWithCatch(sql: String) {
        try {
            client!!.executeStatement(sql, KafkaConfig.KAFKA_STREAMS_PROPERTIES)
        } catch (ex: CompletionException) {
            if (ex.cause is KsqlClientException && ex.message?.contains("with the same name already exists") == true) {
                log.error("Duplicate: $sql")
                log.info("------------------------------------------------------------")
            } else {
                throw ex
            }
        }
    }

}