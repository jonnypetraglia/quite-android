package com.qweex.quite

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast

import java.io.File

// GIF support? https://gist.github.com/felipecsl/6289457

class MainActivity : AppCompatActivity() {

    internal val BLACK = -0x1000000
    internal val PERM_CHECK = 9001
    internal val REQUEST_APP_SETTINGS = 1337

    internal var pager: ViewPager? = null
    internal lateinit var qAdapter: QuiteAdapter
    internal var lastSelected: Int = 0
    internal lateinit var optionsDialog: Options

    internal var goToSettings: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, which ->
        val myAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
        myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivityForResult(myAppSettings, REQUEST_APP_SETTINGS)
    }

    var showOptions: View.OnLongClickListener = View.OnLongClickListener {
        Log.d("showOptions", "onClickListener")
        optionsDialog.show(qAdapter.getItem(pager!!.currentItem) as FragmentBase)
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE) //TODO: Config

        val permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        Log.d("permissionCheck", permissionCheck.toString() + "!")
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            initializeView()
            initializeAdapter()
        } else {
            Log.d("permissionCheck", "requesting")
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERM_CHECK)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            initializeView()
            initializeAdapter()
        } else {
            AlertDialog.Builder(this)
                    .setMessage("You must allow access to local files")
                    .setPositiveButton("Open Settings", goToSettings)
                    .setNegativeButton(android.R.string.cancel) { dialog, which -> this@MainActivity.finish() }
                    .show()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && pager != null) {
            pager!!.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    protected fun initializeView() {
        pager = ViewPager(this)
        pager!!.setBackgroundColor(BLACK)
        pager!!.id = R.id.pager

        val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setContentView(pager, lp)
        (pager!!.parent as View).setBackgroundColor(BLACK)


        pager!!.foregroundGravity = Gravity.CENTER

        pager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                (qAdapter.getItem(lastSelected) as FragmentBase).pause()
                lastSelected = position //or if this don't work, pager.getCurrentItem();
                (qAdapter.getItem(lastSelected) as FragmentBase).unPause()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        //*/
    }

    protected fun initializeAdapter() {
        val uri: Uri?
        val intent = intent
        val action = intent.action

        if (action != null && action.compareTo(Intent.ACTION_VIEW) == 0)
            uri = intent.data
        else
            uri = Uri.fromFile(File(Environment.getExternalStorageDirectory().path + "/.system")) //TODO Config

        Log.d("URI", uri!!.path!! + "!")
        pager!!.adapter = null
        qAdapter = QuiteAdapter(uri, supportFragmentManager, this)
        if (qAdapter.count == 0) {
            Toast.makeText(this, "No images in directory", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        pager!!.adapter = qAdapter
        lastSelected = qAdapter.indexOfStart
        pager!!.currentItem = lastSelected
        Log.d("Starting", "at $lastSelected")

        optionsDialog = Options(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_APP_SETTINGS) {
            val permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permissionCheck == PermissionChecker.PERMISSION_GRANTED)
                initializeAdapter()
            else
                finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
