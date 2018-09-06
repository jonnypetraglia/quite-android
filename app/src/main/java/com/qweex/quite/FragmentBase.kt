package com.qweex.quite

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import java.io.File
import java.util.*

abstract class FragmentBase : Fragment() {

    internal lateinit var context: Context
    lateinit var currentPath: String
    protected var type: String? = null

    val about: String
        get() {
            val file = File(currentPath)
            val lm = Date(file.lastModified())
            return (type + "\n" +
                    currentPath.substring(Environment.getExternalStorageDirectory().path.length + 1)
                            .replace("/".toRegex(), "/\u200B")
                    + "\n" +
                    android.text.format.Formatter.formatShortFileSize(getContext(), file.length()) + "\n" +
                    lm.toString())
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        context = container!!.context
        Log.d("Context:", context.toString() + "!")
        val v = initView()
        if (v.parent != null)
            return v.parent as View
        val rl = RelativeLayout(getContext())
        val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        rl.addView(v, lp)

        rl.id = R.id.frag_view
        rl.setBackgroundColor(-0x1000000)
        v.setBackgroundColor(0x00000000)

        Log.d("onCreateView", "$currentPath!")
        return rl
    }

    protected abstract fun initView(): View
    open fun pause() {}
    open fun unPause() {}

    companion object {

        @Throws(IllegalAccessException::class, java.lang.InstantiationException::class)
        fun newInstance(cls: Class<*>, filepath: String): FragmentBase {
            val frag = cls.newInstance() as FragmentBase
            frag.currentPath = filepath
            val args = Bundle()
            args.putString("file", filepath)
            frag.arguments = args
            return frag
        }
    }
}
