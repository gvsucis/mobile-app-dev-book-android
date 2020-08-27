package edu.gvsu.cis.traxy

import android.app.Application
import android.os.Bundle
import com.google.android.libraries.places.api.Places
import net.danlew.android.joda.JodaTimeAndroid

class TraxyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)
        Places.initialize(this, BuildConfig.MY_API_KEY);
    }
}