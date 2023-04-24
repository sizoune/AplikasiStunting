package com.kominfotabalong.simasganteng

import android.app.Application
import com.jaredrummler.android.device.DeviceName
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class SiMasGantengApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DeviceName.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}

