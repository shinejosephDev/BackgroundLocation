package com.sample.backgroundlocation.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sample.backgroundlocation.db.*
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val locationDao: LocationDao = AppDatabase.getDatabase(application).locationDao()

    lateinit var locations: MutableLiveData<List<LocationData>>
    lateinit var locationsDateRange: MutableLiveData<List<LocationData>>


    fun getLocations(): LiveData<List<LocationData>> {
        if (!::locations.isInitialized) {
            locations = MutableLiveData()

            viewModelScope.launch {
                locations.postValue(locationDao.getAll())
            }

        }
        return locations
    }

    fun getLocationsTimeRange(start: Long, end :Long): LiveData<List<LocationData>> {
        if (!::locationsDateRange.isInitialized) {
            locationsDateRange = MutableLiveData()

            viewModelScope.launch {
                locationsDateRange.postValue(locationDao.fetchAllData(start,end))
            }

        }
        return locationsDateRange
    }
}