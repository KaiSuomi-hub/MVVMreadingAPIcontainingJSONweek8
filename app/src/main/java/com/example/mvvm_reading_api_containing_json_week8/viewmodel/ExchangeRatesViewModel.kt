package com.example.mvvm_reading_api_containing_json_week8.viewmodel
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.mvvm_reading_api_containing_json_week8.model.ExchangeRatesApi
import kotlinx.coroutines.launch
import kotlin.Exception

class ExchangeRatesViewModel: ViewModel() {
    var eurInput by mutableStateOf(value = "")
    var gbp by mutableStateOf(value = 0.0)
        private set
    var gbpRate by mutableStateOf(value = 0.0)
        private set
    var ExchangeRatesUIState by mutableStateOf<ExchangeRatesUIState>(ExchangeRatesUIState.Loading)
        private set
    fun changeEur(newValue: String) {
        eurInput = newValue
    }

    fun convert() {
        val euros = eurInput.toDoubleOrNull() ?: 0.0
        gbp = euros * 0.9
    }
}
private fun getExchangeRateForGBP() {
    viewModelScope.launch() {
    var exchangeRatesApi: ExchangeRatesApi? = null
    try {
        exchangeRatesApi = ExchangeRatesApi.getInstance()
        val exchangeRates = exchangeRatesApi!!.getRates()
        gbpRate = exchangeRates.rates.GBP
        if(exchangeRates.success) {
        gbpRate = exchangeRates.rates.GBP
        ExchangeRatesUIState = ExchangeRatesUIState.Success
        } else {
        ExchangeRatesUIState = ExchangeRatesUIState.Error
        }
    } catch (e: Exception) {
        gbpRate = 0.0
        ExchangeRatesUIState = ExchangeRatesUIState.Error
    }

}

init {
    getExchangeRateForGBP()
}

fun convert() {
    val euros = eurInput.toDoubleOrNull() ?: 0.0
    gbp = euros * gbpRate
}

sealed interface ExchangeRatesUIState {
    object Loading : ExchangeRatesUIState
    object Success: ExchangeRatesUIState
    object Error: ExchangeRatesUIState
}