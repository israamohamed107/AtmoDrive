package com.israa.atmodrive.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import retrofit2.HttpException
import java.io.IOException

fun Exception.explain():ResponseState.Failure{
     return when (this) {
        is IOException -> {
            ResponseState.Failure("There is no internet connection")
        }

        is HttpException -> {
            val code = this.code()
            ResponseState.Failure("HttpException  code $code")
        }
        else -> {
//           println("NetworkState__Error ${e.localizedMessage}")
            ResponseState.Failure(this.localizedMessage)
        }
    }

}
fun Fragment.showToast(text:String){
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}

fun Fragment.hideKeyboard() {
    val inputManager: InputMethodManager = activity
        ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    // check if no view has focus:
    val currentFocusedView = activity?.currentFocus
    if (currentFocusedView != null) {
        inputManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}