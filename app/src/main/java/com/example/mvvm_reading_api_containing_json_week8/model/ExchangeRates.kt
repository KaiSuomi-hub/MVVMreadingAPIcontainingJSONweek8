package com.example.mvvm_reading_api_containing_json_week8.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.Date

data class Rates(
    var GBP: Float,
    var USD: Float,
)

data class ExchangeRate(

    var success: Boolean,
    var base: String,
    var date: Date,
    var rates: Rates,
)

const val BASE_URL = "https://api.exchangerate.host/"

interface ExchangeRatesApi {
    @GET("latest")
    suspend fun getRates(): ExchangeRate

    companion object {
      var exchangeRatesService: ExchangeRatesApi? = null
        fun getInstance(): ExchangeRatesApi {
            if (exchangeRatesService == null) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ExchangeRatesApi::class.java)

            }
            return exchangeRatesService!!
        }
    }

}
