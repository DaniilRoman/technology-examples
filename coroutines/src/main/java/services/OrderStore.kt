package services

import entities.OrderResolution
import entities.Resolution
import java.util.*

class OrderStore {
    private val accepted = Stack<OrderResolution>()
    private val rejected = Stack<OrderResolution>()

    fun store(order: OrderResolution) {
        when (order.resolution) {
            Resolution.ACCEPT -> accepted.push(order)
            Resolution.REJECT -> rejected.push(order)
        }
    }

    fun getLatestRejected(): OrderResolution? {
        return popOrNull(rejected)
    }

    fun getLatestAccepted(): OrderResolution? {
        return popOrNull(accepted)
    }

    private fun popOrNull(stack: Stack<OrderResolution>): OrderResolution? {
        return if (stack.isNotEmpty()) {
            stack.pop()
        } else null
    }
}