package com.example.tiktok_analog


import android.text.Editable
import android.text.TextWatcher
import android.view.View

// TODO: add textWatcher functionality
// https://stackoverflow.com/questions/38872546/edit-text-for-otp-with-each-letter-in-separate-positions

class GenericTextWatcher private constructor(private val view: View) : TextWatcher {
    override fun afterTextChanged(editable: Editable) {
        // TODO Auto-generated method stub
        val text = editable.toString()
        when (view.id) {
//            R.id.editText1 -> if (text.length == 1) et2.requestFocus()
//            R.id.editText2 -> if (text.length == 1) et3.requestFocus() else if (text.isEmpty()) et1.requestFocus()
//            R.id.editText3 -> if (text.length == 1) et4.requestFocus() else if (text.isEmpty()) et2.requestFocus()
//            R.id.editText4 -> if (text.isEmpty()) et3.requestFocus()
        }
    }

    override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        // TODO Auto-generated method stub
    }

    override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        // TODO Auto-generated method stub
    }

}