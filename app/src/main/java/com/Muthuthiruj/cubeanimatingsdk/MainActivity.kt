package com.Muthuthiruj.cubeanimatingsdk

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.muthuthiruj.cube_animation_sdk.CubeAnimatingSdk

class MainActivity : AppCompatActivity() {
    private lateinit var cubeSdk: CubeAnimatingSdk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cubeSdk = findViewById(R.id.cubeSdk)

        // ✅ Wait for the view to be fully laid out before setup
        cubeSdk.post {
            setupCubeAnimatingSdk()
        }
    }

    private fun setupCubeAnimatingSdk() {
        Log.d("CubeSDK", "🚀 Starting SDK configuration...")

        // ✅ Step 1: Load images first
        cubeSdk.loadImagesFromResources(listOf(
            R.drawable.mountain1,
            R.drawable.mountain2,
            R.drawable.mountain3,
            R.drawable.mountain4,
            R.drawable.mountain5
        ))

        // ✅ Step 2: Basic configuration
        cubeSdk.setRotationInterval(3000)
        cubeSdk.setAnimationDuration(1500)
        cubeSdk.setSwipeEnabled(true)
        cubeSdk.setImageCaching(true)
        cubeSdk.setImageScaleType(ImageView.ScaleType.FIT_XY)
        cubeSdk.setImageSetName("Mountain Images")
        cubeSdk.setCornerRadius(0f)

        // ✅ Step 3: Configure UI elements (Buttons & Text)
        setupUIElements()

        // ✅ Step 4: Set up listeners
        setupEventListeners()

        // ✅ Step 5: Start rotation AFTER everything is configured
        Handler(Looper.getMainLooper()).postDelayed({
            cubeSdk.startAutoRotation()
            Log.d("CubeSDK", "🎯 CubeAnimatingSdk setup completed successfully - Auto rotation started")
        }, 100) // Small delay to ensure everything is bound
    }

    private fun setupUIElements() {
        Log.d("CubeSDK", "🔄 Setting up UI elements...")

        // ===== BUTTON CONFIGURATION =====
        val buttonConfig = CubeAnimatingSdk.ButtonConfig().apply {
            enabled = true
            position = CubeAnimatingSdk.ButtonPosition.BOTTOM_END
            text = "Get Started"
            backgroundColor = ContextCompat.getColor(this@MainActivity, R.color.white)
            textColor = ContextCompat.getColor(this@MainActivity, R.color.white)
            textSize = 16f
            cornerRadius = 12f
            padding = 20
            width = RelativeLayout.LayoutParams.WRAP_CONTENT
            height = RelativeLayout.LayoutParams.WRAP_CONTENT
            visibilityMode = CubeAnimatingSdk.ButtonVisibility.ON_SPECIFIC_INDICES
            showOnMain = true
            showOnNext = false
        }

        cubeSdk.setButtonConfig(buttonConfig)
        cubeSdk.setButtonOnIndices(0, 2, 4)

        // ===== GREETING CONFIGURATION =====
        val greetingConfig = CubeAnimatingSdk.GreetingConfig().apply {
            text = "Discover Amazing Content"
            textSize = 18f
            textColor = ContextCompat.getColor(this@MainActivity, R.color.white)
            textStyle = CubeAnimatingSdk.TextStyle.BOLD
            position = CubeAnimatingSdk.GreetingPosition.TOP_START
            backgroundColor = Color.parseColor("#80000000")
            padding = 16
            marginTop=10
            marginStart=10
            showOnMain = true
            showOnNext = true
        }

        cubeSdk.setGreetingConfig(greetingConfig)
        cubeSdk.setGreetingOnIndices(0, 1)

        Log.d("CubeSDK", "✅ UI elements configured")
    }

    private fun setupEventListeners() {
        Log.d("CubeSDK", "🔄 Setting up event listeners...")

        cubeSdk.setOnButtonClickListener(object : CubeAnimatingSdk.OnButtonClickListener {
            override fun onButtonClick(index: Int, image: Any?) {
                Log.d("CubeSDK", "🎯 Button clicked: index=$index")
                Toast.makeText(this@MainActivity, "Button clicked on image $index", Toast.LENGTH_SHORT).show()

                when (index) {
                    0 -> startLogin()
                    1 -> showSpecialOffer()
                    2 -> showSignUp()
                    3 -> learnMore()
                    4 -> exploreFeatures()
                    else -> showDefaultAction()
                }
            }
        })

        cubeSdk.setOnImageLoadListener(object : CubeAnimatingSdk.OnImageLoadListener {
            override fun onImageLoad(index: Int, image: Any?) {
                Log.d("CubeSDK", "✅ Image loaded: index=$index")
                // Remove toast to avoid delays
            }
        })

        cubeSdk.setOnAnimationCompleteListener(object : CubeAnimatingSdk.OnAnimationCompleteListener {
            override fun onAnimationComplete(currentIndex: Int) {
                Log.d("CubeSDK", "🔄 Animation completed: index=$currentIndex")
                // Remove toast to avoid delays
            }
        })

        cubeSdk.setOnImageClickListener(object : CubeAnimatingSdk.OnImageClickListener {
            override fun onImageClick(index: Int, image: Any?, allImages: List<Any>?) {
                Toast.makeText(this@MainActivity, "Image $index tapped", Toast.LENGTH_SHORT).show()
            }
        })

        Log.d("CubeSDK", "✅ Event listeners configured")
    }

    // ===== BUTTON CLICK HANDLERS =====
    private fun startLogin() {
        Toast.makeText(this, "🔐 Starting Login Process...", Toast.LENGTH_LONG).show()
    }

    private fun showSpecialOffer() {
        Toast.makeText(this, "🎁 Showing Special Offers...", Toast.LENGTH_LONG).show()
    }

    private fun showSignUp() {
        Toast.makeText(this, "📝 Opening Sign Up Screen...", Toast.LENGTH_LONG).show()
    }

    private fun learnMore() {
        Toast.makeText(this, "📚 Showing More Information...", Toast.LENGTH_LONG).show()
    }

    private fun exploreFeatures() {
        Toast.makeText(this, "🚀 Exploring Amazing Features...", Toast.LENGTH_LONG).show()
    }

    private fun showDefaultAction() {
        Toast.makeText(this, "Default action", Toast.LENGTH_SHORT).show()
    }

    // ===== ACTIVITY LIFECYCLE =====
    override fun onResume() {
        super.onResume()
        cubeSdk.startAutoRotation()
        Log.d("CubeSDK", "🔄 Auto rotation restarted")
    }

    override fun onPause() {
        super.onPause()

        Log.d("CubeSDK", "⏸️ Auto rotation stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CubeSDK", "🧹 Resources cleaned up")
    }
}