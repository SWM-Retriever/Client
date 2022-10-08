package org.retriever.dailypet.util

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.view.ViewCompat

fun View.setViewBackgroundWithoutResettingPadding(@DrawableRes backgroundResId: Int) {
    val paddingBottom = this.paddingBottom
    val paddingStart = ViewCompat.getPaddingStart(this)
    val paddingEnd = ViewCompat.getPaddingEnd(this)
    val paddingTop = this.paddingTop
    setBackgroundResource(backgroundResId)
    ViewCompat.setPaddingRelative(this, paddingStart, paddingTop, paddingEnd, paddingBottom)
}

fun View.setViewBackgroundWithoutResettingPadding(background: Drawable) {
    val paddingBottom = this.paddingBottom
    val paddingStart = ViewCompat.getPaddingStart(this)
    val paddingEnd = ViewCompat.getPaddingEnd(this)
    val paddingTop = this.paddingTop
    ViewCompat.setBackground(this, background)
    ViewCompat.setPaddingRelative(this, paddingStart, paddingTop, paddingEnd, paddingBottom)
}