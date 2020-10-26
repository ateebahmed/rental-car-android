package com.rent24.driver.components.job.list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
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
private const val STATUS_ASSIGNED = "2"
private const val STATUS_ACTIVATED = "3"

class ScheduledJobListViewModel(application: Application) : CompletedJobListViewModel(application) {

    private val activeJobWorkerId by lazy { MutableLiveData<UUID>() }
    private val activeJobId by lazy { MutableLiveData<Int>().apply { value = 0 } }

    override fun updateTrips() {
        if (trips.value.isNullOrEmpty()) apiManager.scheduledTrips(this)
        else trips.postValue(trips.value)
    }

    override fun updateTrips(response: JobResponse) {
        super.updateTrips(response)
        triggerNextJob()
    }

    fun getActiveJobId(): LiveData<Int> = activeJobId

    fun updateTrips(response: StatusBooleanResponse) {
        if (response.success == true) {
            trips.value = trips.value?.filter {
                it.id == activeJobId.value
            }
        }
    }

    fun refreshTrips() {
        apiManager.scheduledTrips(this)
    }

    fun updateTrip(id: Int) {
        apiManager.getJobDetail(this, id)
    }

    fun updateTrip(response: JobResponse) {
        if (true == response.success?.isNotEmpty()) {
            val trip = response.success[0]
            val index = trips.value?.indexOfFirst { trip.id == it.id } ?: -1
            if (-1 != index) {
                val mutableList = trips.value?.toMutableList() ?: mutableListOf()
                mutableList[index] = trip
                trips.value = mutableList
            }
        }
    }

    private fun triggerNextJob() {
        if (!trips.value.isNullOrEmpty()) {
            if (trips.value?.any { STATUS_ACTIVATED == it.status } == false) {
                val trip = trips.value!!.find { STATUS_ASSIGNED == it.status }
                if (null != trip) {
                    createJob(trip)
                    trip.status = STATUS_ACTIVATED
                }
            } else {
                val trip = trips.value!!.find { STATUS_ACTIVATED == it.status }
                if (null != trip) {
                    createJob(trip)
                }
            }
        }
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
                Pair("pickup", DoubleArray(2).apply {
                    this[0] = trip.pickupLatitude ?: 0.0
                    this[1] = trip.pickupLongitude ?: 0.0
                }), Pair("dropoff", DoubleArray(2).apply {
                    this[0] = trip.dropoffLatitude ?: 0.0
                    this[1] = trip.dropoffLongitude ?: 0.0
                }), Pair("jobId", trip.id.toString())))
            .addTag("Activation")
            .build()
        WorkManager.getInstance()
            .enqueue(work)
        activeJobWorkerId.value = work.id
    }

    private fun updateJobStatus(id: Int) {
        apiManager.updateJobStatus(mapOf(Pair("status", "jobstart"), Pair("jobid", id.toString())), this)
    }

    private fun setActiveJobId(id: Int) {
        activeJobId.value = id
    }

    private fun createJob(trip: JobTrip) {
        setJobActivationAlarm(trip)
        setActiveJobId(trip.id ?: 0)
        updateJobStatus(trip.id ?: 0)
    }
}
