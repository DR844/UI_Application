package com.executor.uiapplication.uitel

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import com.executor.uiapplication.R


class LoadingDialog(private val foContext: Context) {
    private lateinit var moDialog: Dialog
    fun startLoading() {
//        set View
    /*    val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_layout, null)
//        set Dialog
        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()*/
        moDialog = Dialog(foContext)
        val inflate = LayoutInflater.from(foContext).inflate(R.layout.loading_layout, null)
        moDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        moDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        moDialog.setContentView(inflate)
        moDialog.setCancelable(false)
        moDialog.show()
//            dialog.window!!.setBackgroundDrawable(
//                    ColorDrawable(Color.TRANSPARENT))
    }

    fun isDismiss() {
        moDialog.dismiss()
    }
}
