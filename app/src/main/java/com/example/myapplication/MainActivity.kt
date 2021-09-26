package com.example.myapplication

import android.app.AlertDialog
import android.graphics.Bitmap
import android.media.FaceDetector
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.wonderkiln.camerakit.*
import dmax.dialog.SpotsDialog
import com.google.mlkit.*
import com.google.mlkit.vision.face.FaceDetection


class MainActivity : AppCompatActivity() {
    lateinit var waitDialog : AlertDialog
    lateinit var cameraView: CameraView
    lateinit var graphic_Overlay: GraphicOverlay

    override fun onResume() {
        super.onResume()
        cameraView.start()
    }

    override fun onPause() {
        super.onPause()
        cameraView.stop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraView = findViewById(R.id.cameraView)
        graphic_Overlay = findViewById(R.id.graphic_Overlay)
        waitDialog = SpotsDialog.Builder().setContext(this).setMessage("Please Wait...").setCancelable(false).build()
        val uploadButton :Button = findViewById(R.id.UploadButton)
        uploadButton.setOnClickListener {
            cameraView.start()
            cameraView.captureImage()
            graphic_Overlay.clear()
            cameraView.addCameraKitListener(object:CameraKitEventListener{
                override fun onEvent(p0: CameraKitEvent?) {

                }

                override fun onError(p0: CameraKitError?) {

                }

                override fun onImage(p0: CameraKitImage?) {
                    waitDialog.show()

                    var bitmap = p0?.bitmap
                    bitmap= Bitmap.createScaledBitmap(bitmap!!,cameraView.width,cameraView.height,false)
                    cameraView.stop()
                    detectFace(bitmap)
                }

                override fun onVideo(p0: CameraKitVideo?) {

                }

            })
        }
        }


    private fun detectFace(bitmap: Bitmap){
        val image = InputImage.fromBitmap(bitmap,0)
        val options = FaceDetectorOptions.Builder().build()
        val detector = FaceDetection.getClient(options)
        detector.process(image)
            .addOnSuccessListener { result->
                var count = 0
                for (face in result){
                    val bounds = face.boundingBox
                    val rectOverlay = RectOverlay( graphic_Overlay , bounds)
                    graphic_Overlay.add(rectOverlay)
                    count++
                }
                waitDialog.dismiss()
                Toast.makeText(this, "Detected text", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { e-> Toast.makeText(this,e.localizedMessage, Toast.LENGTH_SHORT).show() }
    }


}