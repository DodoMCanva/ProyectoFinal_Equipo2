import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

class CustomTypefaceSpan(private val newType: Typeface?) : MetricAffectingSpan() {

    override fun updateDrawState(tp: TextPaint) {
        applyCustomTypeFace(tp, newType)
    }

    override fun updateMeasureState(tp: TextPaint) {
        applyCustomTypeFace(tp, newType)
    }

    private fun applyCustomTypeFace(paint: Paint, tf: Typeface?) {
        tf?.let {
            val oldStyle = paint.typeface?.style ?: 0
            val fake = oldStyle and it.style.inv()

            if (fake and Typeface.BOLD != 0) {
                paint.isFakeBoldText = true
            }

            if (fake and Typeface.ITALIC != 0) {
                paint.textSkewX = -0.25f
            }

            paint.typeface = it
        }
    }
}
