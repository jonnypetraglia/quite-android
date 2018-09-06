package com.qweex.quite

import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView

class ImageFragment : FragmentBase() {
    internal var v: ImageView? = null

    public override fun initView(): View {
        if (v == null) {
            type = "Image"
            v = ImageView(context)
            v!!.id = R.id.content_view
            v!!.setOnLongClickListener((activity as MainActivity).showOptions)
        }
        val myBitmap = BitmapFactory.decodeFile(currentPath)
        v!!.setImageBitmap(myBitmap)
        return v as ImageView
    }

    companion object {
        internal val filesHandled = arrayOf(".jpg", ".jpeg", ".png") as Array<String?>
    }
}
