package com.qweex.quite;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

public class QuiteAdapter extends FragmentPagerAdapter {
    File dir;
    File[] files;
    int indexOfStart = 0;
    String startingFile;

    FragmentBase[] fragments;

    public QuiteAdapter(Uri target, FragmentManager fm, Context c) {
        super(fm);
        dir = target.getScheme().startsWith("content")
                ? new File(getRealPathFromUri(c, target))
                : new File(target.getPath());
        Log.d("dir", dir.getAbsolutePath() + "+" + dir.isFile() + "+" + dir.isDirectory());
        if(dir.isFile()) {
            startingFile = dir.getName();
            dir = dir.getParentFile();
            Log.d("Starting File", startingFile + " : " + dir.toString());
        }
        Log.d("Dir", dir.toString());
        files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return handlesFile(name, ImageFragment.filesHandled) ||
                        handlesFile(name, VideoFragment.filesHandled) ||
                        handlesFile(name, GifFragment.filesHandled);
            }
        });
        if(files==null) {
            files = new File[]{};
        }
        sort();
        fragments = new FragmentBase[files.length];
        try {
            for (int position = 0; position < files.length; position++) {
                String fullPath = files[position].getPath(); // = dir + "/" + files[position].getName();
                if (handlesFile(files[position].getPath(), ImageFragment.filesHandled))
                    fragments[position] = FragmentBase.newInstance(ImageFragment.class, fullPath);
                if (handlesFile(files[position].getPath(), VideoFragment.filesHandled))
                    fragments[position] = FragmentBase.newInstance(VideoFragment.class, fullPath);
                if (handlesFile(files[position].getPath(), GifFragment.filesHandled))
                    fragments[position] = FragmentBase.newInstance(GifFragment.class, fullPath);
            }
        }
        catch(Exception e) {}
    }


    public void sort() {
        if(Options.sortOrder==Options.RANDOM) {
        //    Collections.shuffle(files);
        } else
        Arrays.sort(files, Options.sortOrder);
        for(int position = 0; position < files.length; position ++)
            if (files[position].equals(startingFile))
                indexOfStart = position;
    }

    @Override
    public int getCount() {
        if(files==null)
            return 0;
        return files.length;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("Get Item", position + "!");
        return fragments[position];
    }

    //TODO: There's GOT to be a way to write this better
    boolean handlesFile(String filename, String[] handled) {
        for(String h : handled) {
            if(filename.toLowerCase().endsWith(h))
                return true;
        }
        return false;
    }

    public int getIndexOfStart() { return indexOfStart; }


    private static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            Log.d("WAAAA", contentUri + " " + proj + " " + cursor);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
