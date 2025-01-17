package com.example.smslistener

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate

class MainActivity : ReactActivity() {

  private val TAG = "MainActivity"
  private val PERMISSION_REQUEST_CODE = 1

  private fun checkSmsPermission(): Boolean {
    var resultReadSms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
    var resultReceiveSms = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
    return resultReadSms == PackageManager.PERMISSION_GRANTED && resultReceiveSms == PackageManager.PERMISSION_GRANTED
  }

  private fun requestSmsPermission() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
            Log.d(TAG, "Sms permissions needed. Please allow in your application settings.")
    } else {
      ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS), PERMISSION_REQUEST_CODE)
    }
  }

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  override fun getMainComponentName(): String = "SmsListener"

  /**
   * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
   * which allows you to enable New Architecture with a single boolean flags [fabricEnabled]
   */
  override fun createReactActivityDelegate(): ReactActivityDelegate =
      DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (!checkSmsPermission()) {
      requestSmsPermission()
    }
  }
}
