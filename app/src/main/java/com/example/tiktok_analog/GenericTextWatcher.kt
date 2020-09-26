package com.example.tiktok_analog


import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText

// TODO: add textWatcher functionality
// https://stackoverflow.com/questions/38872546/edit-text-for-otp-with-each-letter-in-separate-positions

class GenericTextWatcher(private val rootView: View, private val editText: EditText) : TextWatcher {

    override fun afterTextChanged(editable: Editable) {

        val text = editable.toString()
        when (editText.id) {
            R.id.et1 -> if (text.length == 1) {
                rootView.findViewById<EditText>(R.id.et2).requestFocus()
            }
            R.id.et2 -> if (text.length == 1) {
                rootView.findViewById<EditText>(R.id.et3).requestFocus()
            } else if (text.isEmpty()) {
                rootView.findViewById<EditText>(R.id.et1).requestFocus()
            }
            R.id.et3 -> if (text.length == 1) {
                rootView.findViewById<EditText>(R.id.et4).requestFocus()
            } else if (text.isEmpty()) {
                rootView.findViewById<EditText>(R.id.et2).requestFocus()
            }
            R.id.et4 -> if (text.isEmpty()) {
                rootView.findViewById<EditText>(R.id.et3).requestFocus()
            }
        }
    }

    override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        // TODO Auto-generated method stub
    }

    override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        // TODO Auto-generated method stub
    }

}