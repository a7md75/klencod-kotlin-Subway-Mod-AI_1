package com.klencod.subwaymod

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast

class FloatingWidgetService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var controlPanel: View? = null
    private var isPanelVisible = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // نفخ واجهة الأيقونة العائمة
        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null)

        val LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        // إعدادات خصائص النافذة العائمة للأيقونة
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 100
        params.y = 100

        // إضافة الأيقونة إلى الشاشة
        windowManager?.addView(floatingView, params)

        // تفعيل سحب وإفلات الأيقونة العائمة بحرية
        val floatingIcon = floatingView?.findViewById<View>(R.id.root_icon)
        floatingIcon?.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        // إذا كانت الحركة خفيفة، اعتبرها ضغطة لفتح/إغلاق اللوحة
                        val XDiff = (event.rawX - initialTouchX).toInt()
                        val YDiff = (event.rawY - initialTouchY).toInt()
                        if (Math.abs(XDiff) < 10 && Math.abs(YDiff) < 10) {
                            toggleControlPanel()
                        }
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(floatingView, params)
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun toggleControlPanel() {
        Toast.makeText(this, "تم النقر على الأيقونة العائمة بنجاح", Toast.LENGTH_SHORT).show()
        // هنا يتم فتح لوحة التحكم أو تبديل حالتها في المراحل القادمة
    }

    override fun onDestroy() {
        super.onDestroy()
        if (floatingView != null) {
            windowManager?.removeView(floatingView)
        }
    }
}
