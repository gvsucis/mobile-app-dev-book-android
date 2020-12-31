package edu.gvsu.cis.traxy.webservice

data class WeatherDescription(
    val description: String,
    val icon: String,
)

data class WeatherData(
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double,
)

data class OWMWeather(
    val weather: List<WeatherDescription>,
    val main: WeatherData,
)