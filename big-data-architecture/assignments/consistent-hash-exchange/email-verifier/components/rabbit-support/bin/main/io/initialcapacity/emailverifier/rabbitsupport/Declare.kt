package io.initialcapacity.emailverifier.rabbitsupport

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import java.net.URI

fun buildConnectionFactory(rabbitUrl: URI): ConnectionFactory =
    ConnectionFactory().apply {
        setUri(rabbitUrl)
    }

fun ConnectionFactory.declareAndBind(exchange: RabbitExchange, queue: RabbitQueue): Unit =
    useChannel {
        it.exchangeDeclare(exchange.name, exchange.type, false, false, null)
        it.queueDeclare(queue.name, false, false, false, null)
        it.queueBind(queue.name, exchange.name, exchange.bindingKey)
    }

fun <T> ConnectionFactory.useChannel(block: (Channel) -> T): T =
    newConnection().use { connection ->
        connection.createChannel()!!.use(block)
    }
