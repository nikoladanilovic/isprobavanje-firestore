package hr.ferit.nikoladanilovic.tryoutfirestorebaza

import android.app.Application

class TryoutFirestoreBaza : Application() {
    companion object{
        lateinit var application: TryoutFirestoreBaza
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}