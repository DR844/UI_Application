package com.executor.uiapplication.uitel

import android.app.Activity
import android.app.AlertDialog
import com.executor.uiapplication.R


class LoadingDialog(private val mActivity: Activity) {
    private lateinit var isDialog: AlertDialog
    fun startLoading() {
//        set View
        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_layout, null)
//        set Dialog
        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
    }

    fun isDismiss() {
        isDialog.dismiss()
    }
}
