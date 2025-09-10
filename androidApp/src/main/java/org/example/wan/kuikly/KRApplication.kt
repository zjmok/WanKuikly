package org.example.wan.kuikly

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import com.hjq.toast.Toaster
import com.hjq.toast.style.BlackToastStyle
import com.hjq.toast.style.LocationToastStyle

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
        Toaster.init(this)
        Toaster.setStyle(object : BlackToastStyle() {
            override fun getBackgroundDrawable(context: Context): Drawable {
                return (super.getBackgroundDrawable(context) as GradientDrawable).apply {
                    setCornerRadius(
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            999f,
                            context.resources.displayMetrics
                        )
                    )
                }
            }
        }.let {
            LocationToastStyle(it, Gravity.CENTER or Gravity.BOTTOM, 0, 200, 0f, 0f)
        })
    }

}