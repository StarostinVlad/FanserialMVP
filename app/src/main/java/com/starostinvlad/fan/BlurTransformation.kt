package com.starostinvlad.fan

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.squareup.picasso.Transformation

class BlurTransformation(context: Context?) : Transformation {
    private val rs: RenderScript
    override fun transform(bitmap: Bitmap): Bitmap {
        return if (App.instance.isReview) {
            // Create another bitmap that will hold the results of the filter.
            val blurredBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

            // Allocate memory for Renderscript to work with
            val input = Allocation.createFromBitmap(rs, blurredBitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED)
            val output = Allocation.createTyped(rs, input.type)

            // Load up an instance of the specific script that we want to use.
            val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            script.setInput(input)

            // Set the blur radius
            script.setRadius(25f)

            // Start the ScriptIntrinisicBlur
            script.forEach(output)

            // Copy the output to the blurred bitmap
            output.copyTo(blurredBitmap)
            bitmap.recycle()
            blurredBitmap
        } else bitmap
    }

    override fun key(): String {
        return "blur"
    }

    init {
        rs = RenderScript.create(context)
    }
}