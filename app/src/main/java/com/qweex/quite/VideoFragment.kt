package com.qweex.quite

import android.view.View
import android.widget.MediaController
import android.widget.VideoView

class VideoFragment : FragmentBase() {
    internal var v: VideoView? = null
    internal lateinit var mc: MediaController

    public override fun initView(): View {
        if (v == null) {
            type = "Video"
            v = VideoView(getContext())
            v!!.id = R.id.content_view

            v!!.setOnLongClickListener((getActivity() as MainActivity).showOptions);

            mc = MediaController(getContext())
            //TODO: Make mc only show on long press (or preferably, swipe down)
            v!!.setMediaController(mc)
            //v.setZOrderOnTop(true);
            v!!.setOnCompletionListener { mp ->
                mp.seekTo(100)
                mp.start()
            }
        }
        v!!.setVideoPath("file://$currentPath")
        //TODO: Volume: http://stackoverflow.com/a/13398059

        v!!.seekTo(100) // loads the thumbnail without starting playing
        //use v.start() instead for autoplay
        return v as VideoView
    }

    override fun pause() {
        if (v != null) {
            v!!.pause()
            v!!.setMediaController(null)
        }
    }

    override fun unPause() {
        if (v != null) {
            //v.start();
            v!!.setMediaController(mc)
            mc.show()
        }

    }

    companion object {
        internal val filesHandled = arrayOf(".flv", ".gifv", ".mov", ".mp4", ".webm") as Array<String?>
    }
}
