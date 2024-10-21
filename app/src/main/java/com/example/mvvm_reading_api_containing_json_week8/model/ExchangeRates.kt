package com.example.mvvm_reading_api_containing_json_week8.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import android.util.Log
import okhttp3.HttpUrl

// Data class for currency rates
data class Rates(
    var GBP: Float,
    var USD: Float,
)

// Updated to use String for date instead of Date
data class ExchangeRate(
    var success: Boolean,
    var base: String,
    var date: String,  // Changed from Date to String
    var rates: Rates,
)
val apiKey = System.getenv("API_KEY") ?: "default_api_key_if_not_set"

// Interceptor to append the API key to every request
class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl: HttpUrl = originalRequest.url

        // Add API key as query parameter
        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("apikey", apiKey)
            .build()

        // Build a new request with the updated URL
        val newRequest = originalRequest.newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }
}

const val BASE_URL = "https://api.exchangerate.host/"

interface ExchangeRatesApi {
    @GET("latest")
    suspend fun getRates(): ExchangeRate

    companion object {
        var exchangeRatesService: ExchangeRatesApi? = null

        fun getInstance(): ExchangeRatesApi {
            if (exchangeRatesService == null) {
                // Logging interceptor for debugging HTTP requests and responses
                val loggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

                // OkHttpClient with both the API Key Interceptor and Logging Interceptor
                val client = OkHttpClient.Builder()
                    .addInterceptor(ApiKeyInterceptor())
                    .addInterceptor(loggingInterceptor)
                    .build()

                // Build Retrofit instance
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create()) // Convert JSON to Kotlin objects
                    .build()

                // Create the API service
                exchangeRatesService = retrofit.create(ExchangeRatesApi::class.java)
            }
            return exchangeRatesService!!
        }
    }
}