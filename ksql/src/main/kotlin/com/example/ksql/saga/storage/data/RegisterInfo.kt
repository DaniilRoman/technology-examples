package com.test.saga.storage.data

class RegisterInfo {

}

data class SagaConnection(val listeningSourceTopic: String, val servicesCount: Int,
                          val services: List<String>, val targetTopics: List<String>)