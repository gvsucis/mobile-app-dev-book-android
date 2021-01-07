package edu.gvsu.cis.traxy.webservice

import edu.gvsu.cis.traxy.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapServices {
    @GET("data/2.5/weather")
    suspend fun getWeatherAt(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
//        @Query("appid") apiKey: String,
    ): OWMWeather
}


object OpenWeatherMap {
    private const val BASE_URL = "https://api.openweathermap.org/"

    val apiKeyInjector = Interceptor {
        it.request().run {
            val newUrl = url.newBuilder()
                .addQueryParameter("appid", BuildConfig.OWM_API_KEY)
                .addQueryParameter("units", "imperial")
                .build()
            val newRequest = newBuilder().url(newUrl).build()
            it.proceed(newRequest)
        }
    }

    val httpClient = OkHttpClient().newBuilder().addInterceptor(apiKeyInjector).build()

    private fun getRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val apiService = getRetrofit().create(OpenWeatherMapServices::class.java)
}