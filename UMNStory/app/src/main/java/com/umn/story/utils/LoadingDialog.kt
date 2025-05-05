package com.umn.story.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.umn.story.R

class LoadingDialog(context: Context) {

    private val dialog: Dialog = Dialog(context).apply {
        val view = LayoutInflater.from(context).inflate(R.layout.loading, null)
        setContentView(view)
        setCancelable(false)
    }

    fun show() {
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}
