package com.example.healthbichito.wear

import android.util.Log
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WearListener : WearableListenerService(), DataClient.OnDataChangedListener {

    companion object {
        private val _ritmoCardiaco = MutableStateFlow(0)
        val ritmoCardiaco = _ritmoCardiaco.asStateFlow()

        private val _pasos = MutableStateFlow(0)
        val pasos = _pasos.asStateFlow()

        private val _calorias = MutableStateFlow(0f)
        val calorias = _calorias.asStateFlow()
    }

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
        for (event in dataEventBuffer) {
            if (event.type == DataEvent.TYPE_CHANGED) {

                val item = event.dataItem
                if (item.uri.path == "/health_data") {

                    val dataMap = DataMapItem.fromDataItem(item).dataMap

                    val hr = dataMap.getInt("heartRate", 0)
                    val steps = dataMap.getInt("steps", 0)
                    val kcal = dataMap.getFloat("calories", 0f)

                    Log.d("WearListener", "Datos recibidos: HR=$hr, pasos=$steps, calorías=$kcal")

                    _ritmoCardiaco.value = hr
                    _pasos.value = steps
                    _calorias.value = kcal
                }
            }
        }
    }
}

