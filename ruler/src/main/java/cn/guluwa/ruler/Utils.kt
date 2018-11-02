package cn.guluwa.ruler

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.DisplayMetrics

/**
 * Created by guluwa on 2018/11/2.
 */
object Utils {

    //手机屏幕宽高
    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val metric = DisplayMetrics()
        getActivity(context).windowManager.defaultDisplay.getMetrics(metric)
        return metric
    }

    private fun getActivity(c: Context): Activity {
        var context = c
        // Gross way of unwrapping the Activity so we can get the FragmentManager
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        throw  IllegalStateException("The Context is not an Activity.")
    }

    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }
}