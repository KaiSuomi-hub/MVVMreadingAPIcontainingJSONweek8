package com.example.mvvm_reading_api_containing_json_week8.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.mvvm_reading_api_containing_json_week8.model.ExchangeRatesApi
import kotlinx.coroutines.launch
import kotlin.Exception

class ExchangeRatesViewModel : ViewModel() {
    var eurInput by mutableStateOf("")
    var gbp by mutableStateOf(0.0)
        private set
    var gbpRate: Float  = 0.0f
        private set
    var exchangeRatesUIState: ExchangeRatesUIState by mutableStateOf(ExchangeRatesUIState.Loading)
        private set

    fun changeEur(newValue: String) {
        eurInput = newValue
    }

    fun convert() {
        val euros = eurInput.toDoubleOrNull() ?: 0.0
        gbp = euros * gbpRate
    }

    private fun getExchangeRateForGBP() {
        viewModelScope.launch {
            var exchangeRatesApi: ExchangeRatesApi? = null
            try {
                exchangeRatesApi = ExchangeRatesApi.getInstance()
                val exchangeRates = exchangeRatesApi.getRates()
                gbpRate = exchangeRates.rates.GBP
                if (exchangeRates.success) {
                    gbpRate = exchangeRates.rates.GBP
                    exchangeRatesUIState = ExchangeRatesUIState.Success
                } else {
                    exchangeRatesUIState = ExchangeRatesUIState.Error
                }
            } catch (e: Exception) {
                gbpRate = 0.0f
                exchangeRatesUIState = ExchangeRatesUIState.Error
            }
        }
    }

    init {
        getExchangeRateForGBP()
    }
}

sealed interface ExchangeRatesUIState {
    object Loading : ExchangeRatesUIState
    object Success : ExchangeRatesUIState
    object Error : ExchangeRatesUIState
}