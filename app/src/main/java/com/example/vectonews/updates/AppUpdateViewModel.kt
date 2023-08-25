package com.example.vectonews.updates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AppUpdateViewModel @Inject constructor(
    private val repository: AppUpdateRepository ): ViewModel() {
    companion object {
        private const val MY_APP_VERSION = "1"
    }


    private val _appUpdateLiveData = MutableLiveData<AppUpdate?>()
    val appUpdateLiveData: LiveData<AppUpdate?> get() = _appUpdateLiveData

    fun checkAppUpdate() {
        repository.getAppUpdate(
            onSuccess = { appUpdate ->
                val version = appUpdate?.version.toString()
                val myFiredate = appUpdate?.date_publish.toString()

                if (version != MY_APP_VERSION) {
                    val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                    val currentDate = Calendar.getInstance().time
                    val fireDate = dateFormat.parse(myFiredate)

                    if (fireDate != null && currentDate.after(fireDate)) {

                        _appUpdateLiveData.value = AppUpdate(appUpdate?.version, myFiredate, appUpdate?.url)

                    } else {
                        _appUpdateLiveData.value = AppUpdate(null, myFiredate, appUpdate?.url)

                    }
                }
            },
            onFailure = { exception ->
                _appUpdateLiveData.value = null
                // Handle failure, e.g., show an error message to the user
            }
        )
    }

}

