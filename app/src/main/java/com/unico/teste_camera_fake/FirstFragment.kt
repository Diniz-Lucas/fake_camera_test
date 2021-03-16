package com.unico.teste_camera_fake

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_first.*
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private lateinit var mContext: Context
    private var mCurrentPhotoPath: String = ""

    companion object {
        var methodCall : String? = null
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_first, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mContext = view.context

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            openCamera()
//            openCameraX()
        }

    }

    private fun openCameraX() {

//        val newFragment = CameraSurface()
//        newFragment.setTargetFragment(this, 10)
//        //newFragment.show(fragmentManager!!, "dialog")

        val manager: FragmentManager? = fragmentManager
        val transaction: FragmentTransaction = manager!!.beginTransaction()
        transaction.replace(R.id.container, CameraX(), "tag")
        transaction.addToBackStack(null)
        transaction.commit()

    }

    private fun createImageFile(context: Context): File? {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun openCamera(){

        val photoURI = FileProvider.getUriForFile(
            mContext,
            mContext.applicationContext.packageName.toString() + ".provider",
            createImageFile(mContext)!!
        )

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

        startActivityForResult(cameraIntent, 100)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100){
            if(resultCode == -1){

                val imageStream = FileInputStream(mCurrentPhotoPath)

                val bmp = BitmapFactory.decodeStream(imageStream)

                if(bmp != null){

                    imageView.visibility
                    imageView.setImageBitmap(bmp)


                }else{
                    Toast.makeText(mContext, "result code not find", Toast.LENGTH_LONG).show()
                }
            }
        }else{
            Toast.makeText(mContext, "request code not find", Toast.LENGTH_LONG).show()
        }

    }


    //region Camera Permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != REQUEST_CODE_PERMISSIONS) {
            Toast.makeText(mContext, "Permissão acesso camera negada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPermission() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(mContext, it) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        if (!getPermission()) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                10
            )
        }
    }
    //endregion

}