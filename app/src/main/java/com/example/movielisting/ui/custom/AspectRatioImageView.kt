package com.example.movielisting.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.card.MaterialCardView

class AspectRatioImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val ratio = 1.4f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val ratioHeight = (measuredWidth * ratio).toInt()
        setMeasuredDimension(measuredWidth, ratioHeight)
        val lp = layoutParams
        lp.height = ratioHeight
        layoutParams = lp
    }
}