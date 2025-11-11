package com.example.healthbichito.data.wear

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WearListener : WearableListenerService() {

    companion object {
        private val _ritmoCardiaco = MutableStateFlow(0)
        val ritmoCardiaco = _ritmoCardiaco.asStateFlow()

        private val _pasos = MutableStateFlow(0)
        val pasos = _pasos.asStateFlow()

        private val _calorias = MutableStateFlow(0f)
        val calorias = _calorias.asStateFlow()
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MOBILE_RECEIVE", "WearListenerService iniciado")
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("MOBILE_RECEIVE", "onDataChanged activado")

        for (event in dataEvents) {

            if (event.type == DataEvent.TYPE_CHANGED &&
                event.dataItem.uri.path == "/health_data"
            ) {

                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap

                val heart = dataMap.getInt("heartRate")
                val steps = dataMap.getInt("steps")
                val calories = dataMap.getFloat("calories")

                Log.d("MOBILE_RECEIVE", "Ritmo: $heart | Pasos: $steps | Calorías: $calories")

                // ✅ Actualizar flows
                _ritmoCardiaco.value = heart
                _pasos.value = steps
                _calorias.value = calories
            }
        }
    }
}

