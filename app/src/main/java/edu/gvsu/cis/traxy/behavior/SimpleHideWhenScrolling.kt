package edu.gvsu.cis.traxy.behavior

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.ALPHA
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.jar.Attributes

class SimpleHideWhenScrolling(val context: Context, attrs: AttributeSet) :
    CoordinatorLayout.Behavior<FloatingActionButton>(context, attrs) {
    private var fabTarget: View? = null

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int
    ): Boolean {
        if (axes == View.SCROLL_AXIS_VERTICAL) {
            fabTarget = child
            return true
        } else return false
    }


    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        fabTarget?.alpha = 0f
    }

    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View
    ) {
//        fabTarget?.alpha = 1f
        fabTarget!!.animate()
            .alpha(1.0f)
            .setDuration(500)
            .setStartDelay(100)
            .start()
    }
}