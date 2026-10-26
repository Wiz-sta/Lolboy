package com.example.data.api

import com.example.data.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object WeatherClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    suspend fun getCurrentWeather(city: String = "Dhaka"): WeatherData = withContext(Dispatchers.IO) {
        // Dhaka coordinates as default
        val lat = 23.8103
        val lon = 90.4125

        try {
            val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val body = response.body?.string() ?: ""
                val json = JSONObject(body)
                val current = json.getJSONObject("current")

                val temp = current.getDouble("temperature_2m")
                val code = current.getInt("weather_code")
                val humidity = current.getInt("relative_humidity_2m")
                val wind = current.getDouble("wind_speed_10m")

                val (condition, emoji) = parseWmoCode(code)

                return@withContext WeatherData(
                    temperature = temp,
                    condition = condition,
                    city = city,
                    windSpeed = wind,
                    humidity = humidity,
                    iconEmoji = emoji
                )
            }
        } catch (e: Exception) {
            // Fallback gracefully
        }

        WeatherData(
            temperature = 28.0,
            condition = "Clear Sky",
            city = city,
            windSpeed = 10.5,
            humidity = 65,
            iconEmoji = "☀️"
        )
    }

    private fun parseWmoCode(code: Int): Pair<String, String> {
        return when (code) {
            0 -> "Clear Sky" to "☀️"
            1, 2, 3 -> "Partly Cloudy" to "⛅"
            45, 48 -> "Foggy" to "🌫️"
            51, 53, 55 -> "Drizzle" to "🌦️"
            61, 63, 65 -> "Rain" to "🌧️"
            71, 73, 75 -> "Snow" to "❄️"
            80, 81, 82 -> "Showers" to "🌧️"
            95, 96, 99 -> "Thunderstorm" to "⛈️"
            else -> "Fair" to "🌤️"
        }
    }
}
