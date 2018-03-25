package com.thehoick.photolandia

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.widget.EditText


class AlbumCreateDialogFragment : DialogFragment(), DialogInterface.OnDismissListener {
    val TAG = AlbumCreateDialogFragment::class.java.simpleName
    var newAlbumName: String? = null
    private var onDismissListener: DialogInterface.OnDismissListener? = null
    private var onShowListener: DialogInterface.OnShowListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val builder = AlertDialog.Builder(getActivity())
        builder.setTitle(getString(R.string.create_album))

        // Set up the input.
        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        // Set up the buttons.
        builder.setPositiveButton("Create Album") { dialog, which -> newAlbumName = input.text.toString() }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        return builder.create()
    }

    fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener) {
        this.onDismissListener = onDismissListener
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss(dialog);
    }

}
