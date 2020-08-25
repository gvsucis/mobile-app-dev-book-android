package edu.gvsu.cis.traxy

import android.app.Application
import net.danlew.android.joda.JodaTimeAndroid

class TraxyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)
    }
}