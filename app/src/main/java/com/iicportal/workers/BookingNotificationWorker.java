package com.iicportal.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import com.iicportal.notifications.BookingNotification;

public class BookingNotificationWorker extends Worker {
    public BookingNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();

        BookingNotification bookingNotification = new BookingNotification(getApplicationContext());
        bookingNotification.sendNotification(inputData.getString("TITLE"), inputData.getString("MESSAGE"));

        return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
    }
}
