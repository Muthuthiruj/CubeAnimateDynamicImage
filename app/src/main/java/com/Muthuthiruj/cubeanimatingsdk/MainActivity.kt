package com.Muthuthiruj.cubeanimatingsdk

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var cubeSdk: CubeAnimatingSdk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cubeSdk = findViewById(R.id.cubeSdk)

        // Set multiple images for cube rotation
        val images = listOf(
            R.drawable.himalayas,
            R.drawable.hotel_1,
            R.drawable.hotel2,
            R.drawable.hotel1,
            R.drawable.hotel2
        )
        cubeSdk.setImages(images)

        // Set listeners
        cubeSdk.setOnCubeClickListener {
            Toast.makeText(this, "Cube clicked! Manual rotation", Toast.LENGTH_SHORT).show()
            cubeSdk.animateCubeFlip() // Manual cube flip on click
        }

        cubeSdk.setOnLoginButtonClickListener {
            performLogin()
        }

        cubeSdk.setOnImageChangeListener { index ->
            // You can add logic here when image changes
            println("Cube rotated to image index: $index")
        }

        // Configure auto-rotation with cube effect
        cubeSdk.setAutoRotateInterval(3000) // 3 seconds between rotations
        cubeSdk.setAnimationDuration(800) // 800ms cube flip duration

        // Set cube direction (horizontal or vertical flip)
        cubeSdk.setCubeDirection(CubeAnimatingSdk.CubeDirection.HORIZONTAL)

        // Simulate user login after 15 seconds to stop auto-rotation
        Handler(Looper.getMainLooper()).postDelayed({
            cubeSdk.setUserLoggedIn(true)
            cubeSdk.setGreetingText("Welcome, John Doe!")
            Toast.makeText(this, "Auto-rotation stopped (user logged in)", Toast.LENGTH_LONG).show()
        }, 15000)

        // Demo: Change animation type after 8 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            cubeSdk.setCubeDirection(CubeAnimatingSdk.CubeDirection.VERTICAL)
            Toast.makeText(this, "Changed to vertical cube flip!", Toast.LENGTH_SHORT).show()
        }, 8000)
    }

    private fun performLogin() {
        // Your actual login logic here
        cubeSdk.setUserLoggedIn(true)
        cubeSdk.setGreetingText("Welcome, User!")
        cubeSdk.setLoginButtonText("Profile")
        Toast.makeText(this, "Login successful! Auto-rotation stopped.", Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        // Restart auto-rotation when activity resumes (if user not logged in)
        if (!cubeSdk.isUserLoggedIn()) {
            cubeSdk.startAutoRotation()
        }
    }

    override fun onPause() {
        super.onPause()
        // Stop auto-rotation when activity pauses to save resources
        cubeSdk.stopAutoRotation()
    }
}