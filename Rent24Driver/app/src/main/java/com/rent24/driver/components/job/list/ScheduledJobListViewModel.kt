package com.rent24.driver.components.job.list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.gms.maps.model.LatLng
import com.rent24.driver.api.login.response.JobResponse
import com.rent24.driver.api.login.response.JobTrip
import com.rent24.driver.api.login.response.StatusBooleanResponse
import com.rent24.driver.components.home.ACTIVE_JOB_MAP_REQUEST
import com.rent24.driver.components.job.worker.JobActivateWorker
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

private const val TWO_HOURS = 2 * 60 * 60 * 1000
private const val OFFSET = 5000L
private const val STATUS_ASSIGNED = "assigned"
private const val STATUS_ACTIVATED = "activated"

class ScheduledJobListViewModel(application: Application) : CompletedJobListViewModel(application) {

    private val activeJobWorkerId by lazy { MutableLiveData<UUID>() }
    private val activeJobId by lazy { MutableLiveData<Int>().apply { value = 0 } }

    override fun updateTrips() {
        if (trips.value.isNullOrEmpty()) apiManager.scheduledTrips(this)
        else trips.postValue(trips.value)
    }

    override fun updateTrips(response: JobResponse) {
        super.updateTrips(response)
        if (!trips.value.isNullOrEmpty()) {
            if (trips.value?.any { STATUS_ACTIVATED == it.status } == false) {
                val trip = trips.value!!.find {
                    STATUS_ASSIGNED == it.status
                }
                if (null != trip) {
                    setJobActivationAlarm(trip)
                    trip.status = STATUS_ACTIVATED
                }
            }
        }
    }

    fun getActiveJobId(): LiveData<Int> = activeJobId

    fun updateTrips(response: StatusBooleanResponse) {
        if (response.success) {
            trips.value = trips.value?.filter {
                it.id == activeJobId.value
            }
        }
    }

    private fun getPickupLocation(): LatLng {
        val trip = (trips.value as List<JobTrip>)[0]
        return LatLng(trip.pickupLatitude ?: 0.0, trip.pickupLongitude ?: 0.0)
    }

    private fun setJobActivationAlarm(trip: JobTrip) {
        var duration = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(trip.startTime).time -
                TWO_HOURS
        if (duration < System.currentTimeMillis()) {
            duration = OFFSET
        } else {
            duration -= System.currentTimeMillis()
        }
        val work = OneTimeWorkRequestBuilder<JobActivateWorker>()
            .setInitialDelay(duration, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(Pair("message", "Tap to see details"), Pair("type", ACTIVE_JOB_MAP_REQUEST),
                Pair("pickup", getPickupLocation().let {
                    DoubleArray(2).apply {
                        this[0] = it.latitude
                        this[1] = it.longitude
                    }
                })))
            .addTag("Activation")
            .build()
        WorkManager.getInstance()
            .enqueue(work)
        activeJobWorkerId.value = work.id
        setActiveJobId(trip.id)
        updateJobStatus(trip.id)
    }

    private fun updateJobStatus(id: Int) {
        apiManager.updateJobStatus(mapOf(Pair("status", "jobstart"), Pair("jobid", id.toString())), this)
    }

    private fun setActiveJobId(id: Int) {
        activeJobId.value = id
    }
}
