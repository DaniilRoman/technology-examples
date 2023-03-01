package com.test.saga.service.example

import com.test.saga.config.AppConfig.KSQLDB_SERVER_HOST
import com.test.saga.config.AppConfig.KSQLDB_SERVER_PORT
import com.test.saga.storage.data.SagaConnection
import io.confluent.ksql.api.client.Client
import io.confluent.ksql.api.client.ClientOptions

class ServiceRegister(val serviceRegistryRepository: ServiceRegistryRepository) {

    val PROPERTIES = mapOf("auto.offset.reset" to "earliest")
    val options = ClientOptions.create()
            .setHost(KSQLDB_SERVER_HOST)
            .setPort(KSQLDB_SERVER_PORT)

    private var client = Client.create(options)


    fun registerService(serviceRegisterInfo: ServiceRegisterInfo) {
        val sagaConnection = serviceRegistryRepository.upsertNewService(serviceRegisterInfo)

        val tableQuery = constructCreateTableQuery(sagaConnection.listeningSourceTopic, sagaConnection.servicesCount)

        createKsqlTable(sagaConnection.listeningSourceTopic, tableQuery)

//        val obj = Json.decodeFromString<AggregatedStatus>(string)
    }

    private fun createKsqlTable(listeningSourceTopic: String, table: String) {
        val createTableFuture = client.executeStatement(table, PROPERTIES)
        createTableFuture.join()
    }


}

class ServiceRegistryRepository {
    fun upsertNewService(serviceRegisterInfo: ServiceRegisterInfo): SagaConnection {
        val tableExists = isTableAlreadyExist(serviceRegisterInfo.listeningSourceTopic)

        val sagaConnection = if (tableExists) {
            SagaConnection("", 0, listOf(), listOf())
        } else {
            SagaConnection(
                    serviceRegisterInfo.listeningSourceTopic, 1,
                    listOf(serviceRegisterInfo.serviceName),
                    listOf(serviceRegisterInfo.targetTopic)
            )
        }

        return sagaConnection
    }

    private fun isTableAlreadyExist(listeningSourceTopic: String): Boolean {
        TODO("Not yet implemented")
    }

}


fun constructCreateTableQuery(sourceTopic: String, servicesCount: Int): String {

    return """
        CREATE TABLE ${sourceTopic}_TABLE 
            as SELECT 
                correlationId,
                COLLECT_LIST(status) as statuses,
                COLLECT_LIST(sourceService) as sourceServices,
                COLLECT_LIST(targetTopic) as targetTopics 
              FROM $sourceTopic
         WINDOW TUMBLING (SIZE 30 SECONDS)
         GROUP BY correlationId
         EMIT CHANGES
         LIMIT $servicesCount;
    """.trimIndent()
}


data class ServiceRegisterInfo(val serviceName: String, val listeningSourceTopic: String, val targetTopic: String)
