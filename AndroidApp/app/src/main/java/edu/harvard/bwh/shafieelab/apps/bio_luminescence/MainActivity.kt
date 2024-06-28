package edu.harvard.bwh.shafieelab.apps.bio_luminescence

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.permissionx.guolindev.PermissionX
import edu.harvard.bwh.shafieelab.apps.bio_luminescence.camera.MainCameraActivity
import edu.harvard.bwh.shafieelab.apps.bio_luminescence.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.startExperiment.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAnchorView(R.id.fab)
//                .setAction("Action", null).show()



            val intent = Intent(this, MainCameraActivity::class.java)
            startActivity(intent)
            finish()
        }


        val prefs: SharedPreferences = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE)
        val ip =
            prefs.getString("ip", "raspberrypi") //"No name defined" is the default value.

        val threshold_val =prefs.getFloat("threshold", 0.0F) //"No name defined" is the default value.

        binding.ipAddress.text = Editable.Factory.getInstance().newEditable(ip)
        binding.ipAddress.addTextChangedListener(
            afterTextChanged = {
                val editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit()
                editor.putString("ip", binding.ipAddress.text.toString() )
                editor.apply()

            },
//            onTextChanged = {s, start, before, count->
//                TODO("DO your code")
//            },
//            beforeTextChanged = {s, start, before, count->
//                TODO("DO your code")
//            }
        )


        binding.threshold.text = Editable.Factory.getInstance().newEditable(threshold_val.toString())
        binding.threshold.addTextChangedListener(
            afterTextChanged = {

                try{

                    val editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit()
                    editor.putFloat("threshold", binding.threshold.text.toString().toFloat() )
                    editor.apply()


                }
                catch (e:Exception){
                    print(e)

        }


            },
//            onTextChanged = {s, start, before, count->
//                TODO("DO your code")
//            },
//            beforeTextChanged = {s, start, before, count->
//                TODO("DO your code")
//            }
        )


        PermissionX.init(this)
            .permissions( Manifest.permission.READ_EXTERNAL_STORAGE,  Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Log.d("PermissionTAG", "All permissions are granted")
                } else {
                    Log.d("PermissionTAG", "These permissions are denied: $deniedList")
                }
            }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}