package nikendo.com.instagrammapp.views

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ScrollView
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class KeyboardAwareScrollView(context: Context, attrs: AttributeSet) : ScrollView(context, attrs),
        KeyboardVisibilityEventListener {

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        isFillViewport = true
        isVerticalScrollBarEnabled = false

        KeyboardVisibilityEvent.setEventListener(context as Activity, this)
    }

    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if (isKeyboardOpen) {
            scrollTo(0, bottom)
        } else {
            scrollTo(0, top)
        }
    }
}