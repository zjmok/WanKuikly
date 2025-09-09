package org.example.wan.kuikly

import android.app.Application
import android.view.Gravity
import com.hjq.toast.Toaster

class KRApplication : Application() {

    init {
        application = this
    }

    companion object {
        lateinit var application: Application
    }

    override fun onCreate() {
        super.onCreate()

        initToaster()
    }

    private fun initToaster() {
        // 初始化 Toast 框架
        Toaster.init(this)
        Toaster.setGravity(
            Gravity.CENTER or Gravity.BOTTOM,
            0,
            200
        )

    }

}