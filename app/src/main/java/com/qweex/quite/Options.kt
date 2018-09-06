package com.qweex.quite

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.io.File
import java.util.*


class Options(act:MainActivity) {
    private var popupDialog: AlertDialog? = null
    private val filetypesSelector:AlertDialog

    internal var reverseSort:Boolean = false
    internal var aboutText:TextView
    internal var filetypesText:TextView

    fun updateFiletypesText() {
        var c = 0
        for (s in filetypesSelected)
            c += if (s == ".") 0 else 1
        filetypesText.text = "Select filetypes (" + Integer.toString(c) + ")"
    }

    init{

        val ll = LinearLayout(act)
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        ll.orientation = LinearLayout.VERTICAL
        val lpM = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lpM.setMargins(40, 40, 40, 40)

        aboutText = TextView(act)
        val reverseCheckbox = CheckBox(act)
        val recurseCheckbox = CheckBox(act)
        val sortOrderSpinner = Spinner(act)
        val filetypesItem = act.layoutInflater.inflate(android.R.layout.simple_list_item_1, null)
        filetypesText = (filetypesItem.findViewById(android.R.id.text1) as TextView)
        ll.addView(aboutText, lp)
        ll.addView(sortOrderSpinner, lp)
        ll.addView(reverseCheckbox, lpM)
        ll.addView(recurseCheckbox, lpM)
        ll.addView(filetypesItem, lp)

        aboutText.setPadding(40, 40, 40, 40)
        aboutText.setBackgroundColor(Color.parseColor("#FDFDF0"))

        val sorts = arrayOf<String>("Order by Name", "Order by Date", "Order by Size", "Order by Type", "Order Random")
        val adap = ArrayAdapter<String>(act, android.R.layout.simple_list_item_1, sorts)
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortOrderSpinner.adapter = adap
        var sel = 0
        if (sortOrder === dateDesc || sortOrder === dateAsc)
            sel = 1
        else if (sortOrder === sizeDesc || sortOrder === sizeAsc)
            sel = 2
        else if (sortOrder === typeDesc || sortOrder === typeAsc)
            sel = 3
        else if (sortOrder === RANDOM)
            sel = 4
        sortOrderSpinner.setSelection(sel)
        sortOrderSpinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent:AdapterView<*>, view:View, position:Int, id:Long) {
                val oldSortOrder = sortOrder
                when (position) {
                    0 -> {
                        sortOrder = if (reverseSort) nameDesc else nameAsc
                        return
                    }
                    1 -> {
                        sortOrder = if (reverseSort) dateDesc else dateAsc
                        return
                    }
                    2 -> {
                        sortOrder = if (reverseSort) sizeDesc else sizeAsc
                        return
                    }
                    3 -> {
                        sortOrder = if (reverseSort) typeDesc else typeAsc
                        return
                    }
                    4 -> sortOrder = RANDOM
                }
            }

            override fun onNothingSelected(parent:AdapterView<*>) {}
        }

        reverseCheckbox.isChecked = reverseSort
        reverseCheckbox.text = "Reverse Order"
        reverseCheckbox.setOnCheckedChangeListener { buttonView, isChecked -> reverseSort = isChecked }

        recurseCheckbox.isChecked = recurse
        recurseCheckbox.text = "Recurse"

        //fileTypes
        updateFiletypesText()
        val checkedFiletypes = BooleanArray(filetypesSelected.size)
        for (i in checkedFiletypes.indices)
        checkedFiletypes[i] = filetypesSelected[i] != "."
        val filetypesBuilder = AlertDialog.Builder(act)
        filetypesBuilder.setMultiChoiceItems(filetypes, checkedFiletypes, DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked -> filetypesSelected[which] = if (isChecked) filetypes[which] else "." })
        filetypesBuilder.setTitle("Select Filetypes")
        filetypesBuilder.setPositiveButton("Ok") { dialog, which -> updateFiletypesText() }
        filetypesSelector = filetypesBuilder.create()
        filetypesItem.setOnClickListener {
            Log.d("filetypesText", "showSelector")
            filetypesSelector.show()
        }

        val sv = ScrollView(act)
    sv.addView(ll, lp)

    val builder = AlertDialog.Builder(act)
    builder.setView(sv)
    builder.setPositiveButton("Apply", object:DialogInterface.OnClickListener {
        override fun onClick(dialog:DialogInterface, which:Int) {
            val currentUri = Uri.parse(
            ("file:" + act.qAdapter.files!![act.pager!!.getCurrentItem()].getPath()))
            Log.d("Options", "re-initializing adapter : " + currentUri.path!!)

         //act.getIntent().setData(currentUri);
         //act.initializeView();
         //act.initializeAdapter();

            recurse = recurseCheckbox.isChecked

            act.qAdapter.rescan()

//        if(quiteAdapter.fragments!=null) {
//            FragmentTransaction ft = quiteAdapter.fragmentManager.beginTransaction();
//            for (FragmentBase fb : quiteAdapter.fragments)
//                ft.remove(fb);
//            ft.commit();
//        }
//        act.getQAdapter().fragments = arrayOfNulls<FragmentBase>(0)
//        act.getQAdapter().notifyDataSetChanged()
//        act.getPager().setAdapter(null)
//        act.getPager().destroyDrawingCache()
//        act.getQAdapter().rescan()
//        act.getQAdapter().notifyDataSetChanged()
//
//        act.setQAdapter(QuiteAdapter(currentUri, act.getSupportFragmentManager(), act))
//        if (act.getQAdapter().getCount() === 0)
//        {
//        Toast.makeText(act, "No images in directory", Toast.LENGTH_SHORT).show()
//        act.finish()
//        return
//        }
//        act.getPager().setAdapter(act.getQAdapter())
         //*/
                /*
        Log.d("New Current", quiteAdapter.files[
                act.pager.getCurrentItem()
                ].getPath() + "!");
        act.pager.setAdapter(quiteAdapter);
        act.pager.setCurrentItem(
                act.lastSelected = quiteAdapter.getIndexOfStart()
        );
        */

        popupDialog!!.hide()
        // */
            }
    })
    popupDialog = builder.create()

    // Start slideshow w/ seconds | Stop slideshow

    }

     fun show(fragment:FragmentBase) {
         aboutText.text = fragment.about
         popupDialog!!.show()
    }

    companion object {
         var sortOrder:Comparator<File?>
         var recurse:Boolean = false
         var filetypes = arrayOfNulls<String>(
                (ImageFragment.filesHandled.size +
                GifFragment.filesHandled.size +
                VideoFragment.filesHandled.size))
        var filetypesSelected:Array<String?>

         var nameAsc:Comparator<File?> = Comparator { f1, f2 -> f1!!.path.compareTo(f2!!.path) }
         var nameDesc:Comparator<File?> = Comparator { f1, f2 -> f2!!.path.compareTo(f1!!.path) }
         var dateAsc:Comparator<File?> = Comparator { f1, f2 -> (f1!!.lastModified() - f2!!.lastModified()) as Int }
         var dateDesc:Comparator<File?> = Comparator { f1, f2 -> (f2!!.lastModified() - f1!!.lastModified()) as Int }
         var sizeAsc:Comparator<File?> = Comparator { f1, f2 -> (f1!!.getTotalSpace() - f2!!.getTotalSpace()) as Int }
         var sizeDesc:Comparator<File?> = Comparator { f1, f2 -> (f2!!.getTotalSpace() - f1!!.getTotalSpace()) as Int }
         var typeAsc:Comparator<File?> = Comparator { f1, f2 ->
             f1!!.name.substring(f1.name.lastIndexOf(".") + 1).compareTo(
                     f2!!.name.substring(f2.name.lastIndexOf(".") + 1))
         }
         var typeDesc:Comparator<File?> = Comparator { f1, f2 ->
             f2!!.name.substring(f2.name.lastIndexOf(".") + 1).compareTo(
                     f1!!.name.substring(f1.name.lastIndexOf(".") + 1))
         }
         var RANDOM:Comparator<File?> = Comparator { _, _ -> 0 }

        init{
            filetypesSelected = arrayOfNulls(filetypes.size)
            sortOrder = nameAsc
            var i = 0
            for (s in ImageFragment.filesHandled)
                filetypes[i++] = s
            for (s in GifFragment.filesHandled)
                filetypes[i++] = s
            for (s in VideoFragment.filesHandled)
                filetypes[i++] = s
            Arrays.sort(filetypes)
            for (j in filetypes.indices)
                filetypesSelected[j] = filetypes[j]
        }
    }

}
