package com.Muthuthiruj.cubeanimatingsdk

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.muthuthiruj.cube_animation_sdk.CubeAnimatingSdk

class MainActivity : AppCompatActivity() {
    private lateinit var cubeSdk: CubeAnimatingSdk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cubeSdk = findViewById(R.id.cubeSdk)
        setupCubeAnimatingSdk()
    }

    private fun setupCubeAnimatingSdk() {
        // ✅ CORRECT USAGE for Drawable Resources
        cubeSdk.loadImagesFromResources(listOf(
            R.drawable.mountain1,
            R.drawable.mountain2,
            R.drawable.mountain3,
            R.drawable.mountain4,
            R.drawable.mountain5

        ))

        // Configuration
        cubeSdk.setRotationInterval(2000) // 3 seconds
        cubeSdk.setAnimationDuration(1500) // 500ms animation
        cubeSdk.setSwipeEnabled(true)
        cubeSdk.setImageCaching(true)
        // 🌟 Apply ScaleType and Image Set Name
        cubeSdk.setImageScaleType(ImageView.ScaleType.FIT_XY)
        cubeSdk.setImageSetName("Dialog Images")




        /*cubeSdk.setCornerRadius(40f)*/




        // Event listeners
        cubeSdk.setOnImageLoadListener { index, image ->
            Log.d("CubeSDK", "✅ Image loaded: index=$index, resourceId=${image}")
            Toast.makeText(this, "Image ${index + 1} loaded", Toast.LENGTH_SHORT).show()
        }

        cubeSdk.setOnAnimationCompleteListener { currentIndex ->
            Log.d("CubeSDK", "🔄 Animation completed, now showing: $currentIndex")
        }



        // Start auto rotation with ping-pong effect
        cubeSdk.startAutoRotation()
    }

    override fun onResume() {
        super.onResume()

        // ✅ CRITICAL: Restart auto rotation when returning to the activity
        cubeSdk.startAutoRotation()
    }

    override fun onPause() {
        super.onPause()
    }
}