/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0  (the "License");
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

package edu.harvard.bwh.shafieelab.apps.bio_luminescence.camera.fragments

import android.annotation.SuppressLint
import android.content.*
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import edu.harvard.bwh.shafieelab.apps.bio_luminescence.R
import edu.harvard.bwh.shafieelab.apps.bio_luminescence.camera.*
import edu.harvard.bwh.shafieelab.apps.bio_luminescence.databinding.FragmentCameraBinding
import okhttp3.*
import okio.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit


/** Helper type alias used for analysis use case callbacks */

/**
 * Main fragment for this app. Implements all camera operations including:
 * - Viewfinder
 * - Photo taking
 * - Image analysis
 */


class CameraFragment : Fragment() {

    private var _fragmentCameraBinding: FragmentCameraBinding? = null

    private val fragmentCameraBinding get() = _fragmentCameraBinding!!

//    private var cameraUiContainerBinding: CameraUiContainerBinding? = null
//private val BASE_URL = "http://raspberrypi:5000"
private lateinit var BASE_URL:String;

    private lateinit var outputDirectory: File
    private lateinit var currentDate:String

    private lateinit var sharedPref: SharedPreferences

    var Warning = false
    var VideoFile: File? = null
    private var FirstAlert = true

    private lateinit var brightness: String
    private val args: CameraFragmentArgs by navArgs()

    private lateinit var extdir: File

    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService

    private fun callHome() {
        val it = Intent(requireActivity(), MainCameraActivity::class.java)
        startActivity(it)
        requireActivity().finish()
    }

    private val backPressHandler = object : BackPressHandler {
        override fun onBackPressed(): Boolean {
//            showClosingWarning()
            callHome()
            return false
        }
    }

    override fun onStop() {
        (activity as? BackPressRegistrar)?.unregisterHandler(backPressHandler)
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        (activity as? BackPressRegistrar)?.registerHandler(backPressHandler)

    }

//    override fun onDestroyView() {
//        _fragmentCameraBinding = null
//        super.onDestroyView()
//        // Shut down our background executor
////        cameraExecutor.shutdown()
//    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE)!!

        val prefs: SharedPreferences = requireActivity().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE)
        val ip =
            prefs.getString("ip", "raspberrypi") //"No name defined" is the default value.

        BASE_URL = "http://$ip:5000"

        Toast.makeText(requireActivity().applicationContext, BASE_URL, Toast.LENGTH_LONG).show()

        val date = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        currentDate = dateFormat.format(date)

        val appContext = context?.applicationContext
        extdir = requireContext().getExternalFilesDir(null)?.let {
            File(it, "/"+ appContext!!.resources.getString(R.string.app_dir_name)
                     +"/"+currentDate+ "/").apply { mkdirs() } }!!


        val mediaDir = requireContext().externalMediaDirs.firstOrNull()?.let {
            File(it, appContext!!.resources.getString(R.string.app_name)).apply { mkdirs() } }

        _fragmentCameraBinding = FragmentCameraBinding.inflate(inflater, container, false)

        Log.d(TAG, "onCreateView: $extdir")

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()



        Thread {
            //Do some Network Request

            callPi("run_servo")
//            downloadImage()

            requireActivity().runOnUiThread {
                //Update UI
            }
        }.start()



//
//        val client = OkHttpClient()
//
//        val request: Request = Request.Builder()
//            .url("https://www.vogella.com/index.html")
//            .build()


//        val retrofit = Retrofit.Builder()
//            .addConverterFactory(ScalarsConverterFactory.create())
//            .client(okHttpClient)
//            .baseUrl(BASE_URL)
//            .build()
        return fragmentCameraBinding.root
    }

    private val countDownTimer =  object : CountDownTimer(30000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            requireActivity().runOnUiThread {
                fragmentCameraBinding.timeCount.text = "seconds remaining: " + millisUntilFinished / 1000
                var current_percent = 100*(30 - (millisUntilFinished / 1000))/ 30
                fragmentCameraBinding.progressText.text = "$current_percent %"
                fragmentCameraBinding.circularProgressView.setProgress(current_percent.toFloat())

            }
            // logic to set the EditText could go here
        }

        override fun onFinish() {
            requireActivity().runOnUiThread {

                fragmentCameraBinding.timeCount.text = "DONE!"
            }
        }
    }

    private fun callPi(mode:String) {

        requireActivity().runOnUiThread {
            fragmentCameraBinding.title.text = "$mode Started"
            fragmentCameraBinding.status.text = ""

        }


        val formBody: RequestBody = MultipartBody.Builder()
            .addFormDataPart("ID", "")
            .build()

        val request: Request = Request.Builder()
            .url("$BASE_URL/$mode")
            .post(formBody)
            .build()



        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()

        countDownTimer.start()
//        Log.d("TIMER", "Started")
//
//        TimeUnit.SECONDS.sleep((5))
//        Log.d("TIMER", "5 sec")


        try {
            // Do something with the response.
            val response = client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: okio.IOException) {
                    print(e.message)
                    countDownTimer.cancel()
                }
                override fun onResponse(call: Call, response: Response) {



                    val response_val = response.body!!.string()
                    print("MESSAGE: "+response.message+"\n")
                    print("RESPONSE:" + response_val+"\n")
                    print("RESPONSE:$response\n")
//                    print("XXX \n" + response.body)


                    if (response_val == "servo done"){
                        countDownTimer.cancel()
                        callPi("capture_image")
                    }
                    else if (response_val == "image captured"){
                        countDownTimer.cancel()

                        callPi("calculate_intensity")


                    }

                    else if ("Brightness:" in response_val){

                       brightness = response_val.replace("Brightness:","")

                        downloadImage()
                    }
                }
            })
//            editTextResponse.text = response.toString()

        } catch (e: java.io.IOException) {
//            editTextResponse.text = e.toString()
            e.printStackTrace()
        }



//        client.newCall(request).execute().use { response ->
//            if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//            println(response.body!!.string())
//
//
//        }
    }


     private fun downloadImage() {
        val request: Request = Request.Builder()
            .url("$BASE_URL/download_image")
            .build()

         requireActivity().runOnUiThread {
             fragmentCameraBinding.title.text = "download_image Started"
             fragmentCameraBinding.status.text = ""

         }

         val progressListener: ProgressListener = object : ProgressListener {
            var firstUpdate = true
            override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                if (done) {
                    println("completed")
                    requireActivity().runOnUiThread {
                        fragmentCameraBinding.status.text = ""

                        fragmentCameraBinding.progressText.text = "100 %"
                        fragmentCameraBinding.circularProgressView.setProgress(100F)

                    }
                } else {
                    if (firstUpdate) {
                        firstUpdate = false
                        if (contentLength == -1L) {
                            println("content-length: unknown")
                        } else {
                            System.out.format("content-length: %d\n", contentLength)
                        }
                    }
                    println(bytesRead)
                    if (contentLength != -1L) {
                        System.out.format("%d%% done\n", 100 * bytesRead / contentLength)
                        activity!!.runOnUiThread {
                            fragmentCameraBinding.progressText.text = (100 * bytesRead / contentLength).toString() +"%"
                            fragmentCameraBinding.circularProgressView.setProgress((100 * bytesRead / contentLength).toFloat())
                        }
                    }
                }
            }
        }
        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .addNetworkInterceptor(Interceptor { chain: Interceptor.Chain ->
                val originalResponse = chain.proceed(chain.request())
                originalResponse.newBuilder()
                    .body(ProgressResponseBody(originalResponse.body!!, progressListener))
                    .build()
            })
            .build()
         countDownTimer.start()




        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful){
                requireActivity().runOnUiThread {
                Toast.makeText(requireContext(),"Download Failed!!",Toast.LENGTH_SHORT).show();
                Toast.makeText(requireContext(),"Unexpected code $response",Toast.LENGTH_LONG).show();
                }
            }
            else {
//            println(response.body!!.string())


                countDownTimer.cancel()

//                val imgFileData = response.body?.byteStream()
                val imgFileData: InputStream = response.body!!.byteStream()

                //At this point you can do something with the pdf data
                //Below I add it to internal storage

                if (imgFileData != null) {
                    val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)

                    try {
//                        requireContext().openFileOutput(photoFile.toString(), Context.MODE_PRIVATE).use { output ->
//                            output.write(imgFileData.readBytes())
//                        }
                        try {
                            if (!photoFile.exists()) {
                                photoFile.createNewFile()
                            }
                            val fos = FileOutputStream(photoFile)
                            fos.write(imgFileData.readBytes())
                            fos.close()
                        } catch (e: Exception) {
                            Log.e(TAG, e.message!!)
                        }


                            val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy__HH_mm_ss")
                        val curDateTime = current.format(formatter)

                        val numFiles = extdir.listFiles()?.size

                        // New file name with username, curDayNum, curDateTime and .jpg
                        val newFileName = curDateTime + "___" + numFiles + ".jpg"
                        val newFile = File(extdir, newFileName)
                        Log.d(TAG, "New file name: $newFileName")
                        // copy file to external storage
                        newFile.let { sourceFile ->
                            photoFile.copyTo(newFile)
//                            sourceFile.delete()
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }




                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(),"Download Successfully!!",Toast.LENGTH_SHORT).show();

                    // Only navigate when the gallery has photos
                    if (true == extdir.listFiles()?.isNotEmpty()) {
                        Navigation.findNavController(
                            requireActivity(), R.id.fragment_container
                        ).navigate(
                            CameraFragmentDirections
                                .actionCameraToGallery(extdir.absolutePath, brightness)
                        )
                    }
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE) ?: return

        // Determine the output directory
        outputDirectory = MainCameraActivity.getOutputDirectory(requireContext())
    }

    companion object {

        private const val TAG = "CameraXBasic"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".png"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
                File(baseFolder, SimpleDateFormat(format, Locale.US)
                        .format(System.currentTimeMillis()) + extension)
    }


    private class ProgressResponseBody(
        private val responseBody: ResponseBody,
        private val progressListener: ProgressListener
    ) :
        ResponseBody() {
        private var bufferedSource: BufferedSource? = null
        override fun contentType(): MediaType? {
            return responseBody.contentType()
        }

        override fun contentLength(): Long {
            return responseBody.contentLength()
        }

        override fun source(): BufferedSource {
            if (bufferedSource == null) {
                bufferedSource = source(responseBody.source()).buffer()
            }
            return bufferedSource as BufferedSource
        }

        private fun source(source: Source): Source {
            return object : ForwardingSource(source) {
                var totalBytesRead = 0L

                @Throws(IOException::class)
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                    progressListener.update(
                        totalBytesRead,
                        responseBody.contentLength(),
                        bytesRead == -1L
                    )
                    return bytesRead
                }
            }
        }
    }

    internal interface ProgressListener {
        fun update(bytesRead: Long, contentLength: Long, done: Boolean)
    }

}
