/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.harvard.bwh.shafieelab.apps.bio_luminescence.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import com.permissionx.guolindev.PermissionX
import edu.harvard.bwh.shafieelab.apps.bio_luminescence.MainActivity
import edu.harvard.bwh.shafieelab.apps.bio_luminescence.R
import edu.harvard.bwh.shafieelab.apps.bio_luminescence.databinding.ActivityMainCameraBinding
import java.io.File

const val KEY_EVENT_ACTION = "key_event_action"
const val KEY_EVENT_EXTRA = "key_event_extra"
private const val IMMERSIVE_FLAG_TIMEOUT = 500L

/**
 * Main entry point into our app. This app follows the single-activity pattern, and all
 * functionality is implemented in the form of fragments.
 */


interface BackPressRegistrar {
    fun registerHandler(handler: BackPressHandler)
    fun unregisterHandler(handler: BackPressHandler)
}

interface BackPressHandler {
    fun onBackPressed(): Boolean
}

class MainCameraActivity : AppCompatActivity(),BackPressRegistrar {

    private var registeredHandler: BackPressHandler? = null
    override fun registerHandler(handler: BackPressHandler) { registeredHandler = handler }
    override fun unregisterHandler(handler: BackPressHandler) { registeredHandler = null }


    private lateinit var activityMainBinding: ActivityMainCameraBinding
    lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainCameraBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // get intent from previous activity
        id = intent.getStringExtra("id").toString()
        Log.d("TAG", "Camera Called $id")

//        // Request camera permissions
//        PermissionX.init(this)
//            .permissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,  Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE)
//            .request { allGranted, grantedList, deniedList ->
//                if (allGranted) {
//                    Log.d("PermissionTAG", "All permissions are granted")
//                } else {
//                    Log.d("PermissionTAG", "These permissions are denied: $deniedList")
//                }
//            }

        // Set up the toolbar.
//        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_home_24)
        supportActionBar?.title = "Capture - Sow ID: $id"
        // setup supportActionBar home to on click listener


//        findNavController(R.id.fragment_container).setGraph(R.navigation.nav_graph, intent.extras)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment?
        val navController = navHostFragment!!.navController
        navController.setGraph(R.navigation.nav_graph, intent.extras)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        // Before setting full screen flags, we must wait a bit to let UI settle; otherwise, we may
        // be trying to set app to immersive mode before it's ready and the flags do not stick
        activityMainBinding.fragmentContainer.postDelayed({
            hideSystemUI()
        }, IMMERSIVE_FLAG_TIMEOUT)
    }

    /** When key down event is triggered, relay it via local broadcast so fragments can handle it */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                val intent = Intent(KEY_EVENT_ACTION).apply { putExtra(KEY_EVENT_EXTRA, keyCode) }
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            // Workaround for Android Q memory leak issue in IRequestFinishCallback$Stub.
            // (https://issuetracker.google.com/issues/139738913)
            finishAfterTransition()
        } else {
            super.onBackPressed()
            val it = Intent(this, MainActivity::class.java)
            startActivity(it)
            finish()
        }
    }

    companion object {

        /** Use external media if it is available, our app's file directory otherwise */
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, activityMainBinding.fragmentContainer).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
