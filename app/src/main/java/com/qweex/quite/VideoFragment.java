package com.qweex.quite;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoFragment extends FragmentBase {
    static final String[] filesHandled = new String[] {".flv", ".gifv", ".mov", ".mp4", ".webm"};
    VideoView v;
    MediaController mc;

    @Override
    public View initView() {
        if(v==null) {
            v = new VideoView(context);
            v.setId(R.id.content_view);
            //v.setOnLongClickListener(((MainActivity)getActivity()).showOptions);

            mc = new MediaController(getContext());
            //TODO: Make mc only show on long press (or preferably, swipe down)
            v.setMediaController(mc);
            //v.setZOrderOnTop(true);
            v.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.seekTo(100);
                    mp.start();
                }
            });
        }
        v.setVideoPath("file://" + currentPath);
        //TODO: Volume: http://stackoverflow.com/a/13398059

        v.seekTo(100); // loads the thumbnail without starting playing
            //use v.start() instead for autoplay
        return v;
    }

    @Override
    public void pause() {
        if(v!=null) {
            v.pause();
            v.setMediaController(null);
        }
    }

    @Override
    public void unPause() {
        if(v!=null) {
            //v.start();
            v.setMediaController(mc);
            mc.show();
        }

    }
}
