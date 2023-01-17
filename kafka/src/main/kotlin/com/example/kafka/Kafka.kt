package com.example.kafka

import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.retry.annotation.Backoff
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.Transactional
import java.util.*


data class TestEntity(val name: String, val value: String)

@EnableKafka
@EnableScheduling
@EnableTransactionManagement
@Configuration
class KafkaConfig(
    @Value("\${spring.kafka.bootstrap-servers}") val bootstrapServers: String,
    @Value("\${custom-kafka.offset-reset}") val offsetReset: String,
) {

//    @Bean
//    fun handler(testKafkaTemplate: KafkaTemplate<String, TestEntity>): DefaultErrorHandler {
//        val bo = FixedBackOff(100L, 3L)
//        val myRecoverer = DeadLetterPublishingRecoverer(testKafkaTemplate)
//        return DefaultErrorHandler(myRecoverer, bo)
//    }

    @Bean
    fun testConsumerFactory(): DefaultKafkaConsumerFactory<String, TestEntity> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = offsetReset
        props[ConsumerConfig.ISOLATION_LEVEL_CONFIG] = "read_committed" // for producer transactions
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        props[JsonDeserializer.TRUSTED_PACKAGES] = "com.example.kafka"
        return DefaultKafkaConsumerFactory<String, TestEntity>(props)
    }

    @Bean
    fun testListenerContainerFactory(@Value("\${custom-kafka.concurrency}") concurrency: Int,
                                     testConsumerFactory: DefaultKafkaConsumerFactory<String, TestEntity>)
    : ConcurrentKafkaListenerContainerFactory<String, TestEntity> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, TestEntity>()
        factory.consumerFactory = testConsumerFactory
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
        factory.setConcurrency(concurrency)
        return factory
    }

    @Bean
    fun testProducerFactory(): DefaultKafkaProducerFactory<String, TestEntity> {
        val props: MutableMap<String, Any> = HashMap()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        // Try to produce until we succeed not to lose any data
        props[ProducerConfig.RETRIES_CONFIG] = Int.MAX_VALUE
        // Retry sending with backoff of 100ms, ensuring we do not spam kafka too much.
        props[ProducerConfig.RETRY_BACKOFF_MS_CONFIG] = 100
        // Ensure the Record is marked as produced only if kafka receives the Record
        props[ProducerConfig.ACKS_CONFIG] = "all"
        props[ProducerConfig.TRANSACTIONAL_ID_CONFIG] = "tx-" // for producer transactions
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        return DefaultKafkaProducerFactory<String, TestEntity>(props)
    }


    @Bean
    fun testKafkaTemplate(testProducerFactory: DefaultKafkaProducerFactory<String, TestEntity>): KafkaTemplate<String, TestEntity> {
        return KafkaTemplate(testProducerFactory)
    }

}

@Component
class KafkaProducer(@Value("\${custom-kafka.topic.name.test-output}") val outputTopic: String,
                    val testKafkaTemplate: KafkaTemplate<String, TestEntity>) {

    @Transactional
    fun send(payload: TestEntity) {
        println("====== sending payload='$payload' to topic='$outputTopic'")
        testKafkaTemplate.send(outputTopic, payload)
        throw RuntimeException("oooooppppppsssss when sending")
        println("====== sended")
    }

}

@Component
class KafkaConsumer {

    var isException = true

    @RetryableTopic(
        attempts = "3",
        backoff = Backoff(delay = 1000),
        kafkaTemplate = "testKafkaTemplate")
    @KafkaListener(
        topics = ["\${custom-kafka.topic.name.test-input}"],
        containerFactory = "testListenerContainerFactory",
        groupId = "\${custom-kafka.consumer-group-id}")
    fun receive(consumerRecord: ConsumerRecord<*, *>, acknowledgment: Acknowledgment) {
        println("received payload='{}' $consumerRecord")
        if (isException) {
            isException = false
            throw RuntimeException("oooopppppsssss")
        } else {
            isException = true
        }

        acknowledgment.acknowledge() // manually mark we read the message
    }

}

@Service
class KafkaService(val kafkaProducer: KafkaProducer) {
    @PostConstruct
    fun setup() {
//        kafkaProducer.send(TestEntity("test key 1", "test value 1"))
    }

    @Scheduled(fixedDelay = 3000)
    fun scheduledFun() {
        println("====== RUN ======")
        kafkaProducer.send(TestEntity("test key 1", "test value 1"))
    }
}

