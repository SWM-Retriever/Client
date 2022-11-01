package org.retriever.dailypet.util

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface

fun modifyOrientation(bitmap: Bitmap, image_absolute_path: String): Bitmap {
    val ei = ExifInterface(image_absolute_path)

    when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
        ExifInterface.ORIENTATION_ROTATE_90 -> {
            return rotate(bitmap, 90f)
        }
        ExifInterface.ORIENTATION_ROTATE_180 -> {
            return rotate(bitmap, 180f)
        }
        ExifInterface.ORIENTATION_TRANSVERSE -> {
            return rotate(bitmap, 270f)
        }
        ExifInterface.ORIENTATION_ROTATE_270 -> {
            return rotate(bitmap, 270f)
        }
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
            return flip(bitmap, true, vertical = false)
        }
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
            return flip(bitmap, false, vertical = true)
        }
        else -> {
            return bitmap
        }
    }
}

fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
    val matrix = Matrix()
    matrix.preScale(if (horizontal) (-1f) else 1f, if (vertical) (-1f) else 1f)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true);
}