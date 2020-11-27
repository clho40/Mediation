package com.hmscl.mediationdemo

import android.content.Context
import android.widget.Toast

class Utils {
    companion object {
        fun showToast(context: Context,msg: String,length: Int = Toast.LENGTH_SHORT) {
            Toast.makeText(context,msg,length).show()
        }
    }
}