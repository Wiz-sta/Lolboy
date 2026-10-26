package com.example.data.model

data class AiMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false,
    val category: String? = null
)

enum class MessageSender {
    USER,
    ASSISTANT
}

data class WeatherData(
    val temperature: Double,
    val condition: String,
    val city: String = "Dhaka",
    val windSpeed: Double = 12.0,
    val humidity: Int = 68,
    val iconEmoji: String = "☀️"
)
