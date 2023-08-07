package com.example.timerapp

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ResetWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        // Reset the state of MyWorker here
        // For example, you can reset any flags or variables used in MyWorker
        return Result.success()
    }
}
