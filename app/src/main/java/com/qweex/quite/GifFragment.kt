package com.qweex.quite

import android.util.Log
import android.view.View
import android.webkit.WebView

class GifFragment : FragmentBase() {
    private var v: WebView? = null

    public override fun initView(): View {
        if (v == null) {
            type = "Gif"
            v = WebView(context)
            v!!.id = R.id.content_view
            v!!.setOnLongClickListener((activity as MainActivity).showOptions)
        }
        val x = "<html style='margin: 0; padding: 0; width: 100%; height: 100%;'>" +
                "<body style='margin: 0; padding: 0; width: 100%; height: 100%;'>" +

                "<div style='background-image: url(\"file://" + currentPath + "\");\n" +
                "    background-size: contain;\n" +
                "    background-repeat: no-repeat;\n" +
                "    background-position: center;" +
                "    width: 100%; height: 100%;" +
                "'></div>" +
                "</body></html>"

        Log.d("HTML", x)
        v!!.loadDataWithBaseURL("", x, "text/html", "utf-8", "")
        return v as WebView
    }

    override fun pause() {
        if (view != null)
            (view!!.findViewById(R.id.content_view) as WebView).onPause() //TODO: is this the best way?
    }

    override fun unPause() {
        if (view != null)
            (view!!.findViewById(R.id.content_view) as WebView).onResume() //TODO: is this the best way?
    }

    companion object {
        internal val filesHandled = arrayOf(".gif") as Array<String?>
    }

}
