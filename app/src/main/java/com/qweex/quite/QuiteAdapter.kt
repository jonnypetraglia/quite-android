package com.qweex.quite

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.util.Log
import java.io.File
import java.util.*

class QuiteAdapter(target: Uri, internal var fragmentManager: FragmentManager, c: Context) : FragmentPagerAdapter(fragmentManager) {
    internal var dir: File
    internal var files: ArrayList<File> = arrayListOf()
    var indexOfStart = 0
        internal set
    internal lateinit var startingFile: String

    internal lateinit var fragments: Array<FragmentBase?>

    init {
        dir = if (target.scheme!!.startsWith("content"))
            File(getRealPathFromUri(c, target))
        else
            File(target.path!!)
        Log.d("dir", dir.absolutePath + "+" + dir.isFile + "+" + dir.isDirectory)
        if (dir.isFile) {
            startingFile = dir.name
            dir = dir.parentFile
            Log.d("Starting File", startingFile + " : " + dir.toString())
        }
        Log.d("Dir", dir.toString())
        rescan()
    }

    private fun scanFolder(directory : File) {
        val foldersToScan : ArrayList<File> = arrayListOf()
        Log.d("rescan", "scan folder: " + directory.toString() + "...")
        val filesListed : Array<File> = directory.listFiles { subdir, name ->
            Log.d("rescan", "scanning file: " + name + "=" + handlesFile(name, Options.filetypesSelected))
            if(File(subdir,name).isDirectory and Options.recurse)
                foldersToScan.add(File(subdir, name))
            handlesFile(name, Options.filetypesSelected)
        }
        for(file in filesListed)
            files.add(file)
        for(fldr in foldersToScan)
            scanFolder(fldr)
    }

    fun rescan() {
        files.clear()
        scanFolder(dir)

        for (s in Options.filetypesSelected)
            Log.d("rescan", "filetype: " + s)

        if (Options.sortOrder === Options.RANDOM) {
            //    Collections.shuffle(files);
        } else {
            Collections.sort(files, Options.sortOrder)
            Log.d("rescan", "sort order: " + Options.sortOrder)
        }
        Log.d("rescan", "files count: " + files.size)
        for (f in files)
            Log.d("rescan", "file: " + f!!.name)
        fragments = arrayOfNulls(size = this.files.size)
        try {
            for (position in files.indices) {
                val fullPath = files[position].path // = dir + "/" + files[position].getName();
                if (handlesFile(files[position].path, ImageFragment.filesHandled))
                    fragments[position] = FragmentBase.newInstance(ImageFragment::class.java, fullPath)
                if (handlesFile(files[position].path, VideoFragment.filesHandled))
                    fragments[position] = FragmentBase.newInstance(VideoFragment::class.java, fullPath)
                if (handlesFile(files[position].path, GifFragment.filesHandled))
                    fragments[position] = FragmentBase.newInstance(GifFragment::class.java, fullPath)

                if (files[position].name == startingFile)
                    indexOfStart = position
            }
        } catch (e: Exception) {
        }
        this.notifyDataSetChanged()

    }

    override fun getCount(): Int {
        return files.size
    }

    override fun getItem(position: Int): FragmentBase? {
        Log.d("Get Item", position.toString() + "!")
        return fragments[position]
    }

    //TODO: There's GOT to be a way to write this better
    internal fun handlesFile(filename: String, handled: Array<String?>): Boolean {
        for (h in handled) {
            if (filename.toLowerCase().endsWith(h!!))
                return true
        }
        return false
    }


    private fun getRealPathFromUri(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }


    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}
