🎯Cube Animation SDK
---
A powerful and customizable Android SDK for creating stunning 3D cube animation effects with images, buttons, and text overlays. Perfect for onboarding screens, image galleries, and interactive content displays.

✨ **Features**
---
3D Cube Animations - Smooth left/right cube flip transitions

Dynamic Content - Different text and buttons for each image

Auto Rotation - Automatic slideshow with configurable intervals

Touch Gestures - Swipe left/right for manual navigation

Fully Customizable - Colors, positions, sizes, and animations

Image Support - Load from resources or URLs (with Glide)

Event Listeners - Comprehensive callback system

🚀Quick Start
---
1. Add Dependency - v1.0.0
---
Add the SDK to your build.gradle:

gradle
dependencies {
   implementation("com.github.Muthuthiruj:CubeAnimateDynamicImage:v1.0.0")
}

2. Add to Layout
---
**XML Configuration:**

xml
<com.muthuthiruj.cube_animation_sdk.CubeAnimatingSdk
    android:id="@+id/cubeSdk"
    android:layout_width="match_parent"
    android:layout_height="300dp"
    
    <!-- Basic Configuration -->
    app:autoRotateInterval="3000"
    app:animationDuration="500"
    app:cubeCornerRadius="20dp"
    
    <!-- Button Configuration -->
    app:enableButton="true"
    app:buttonText="Get Started"
    app:buttonColor="@color/blue"
    app:buttonTextColor="@color/white"
    app:buttonPosition="bottom_end"
    
    <!-- Greeting Text Configuration -->
    app:greetingText="Welcome!"
    app:greetingTextColor="@color/white"
    app:greetingTextSize="18sp"
    app:greetingBackgroundColor="#80000000"
    
    <!-- Different texts for different images -->
    app:greetingTextForIndex0="🏔️ Discover Amazing Mountains"
    app:greetingTextForIndex1="🌄 Explore Natural Beauty"
    app:greetingTextForIndex2="🚀 Adventure Awaits You"
    app:greetingTextForIndex3="📸 Capture Memorable Moments"
    app:greetingTextForIndex4="❤️ Find Your Perfect Escape" />

    3. Initialize in Code
    ---
**Kotlin:**

class MainActivity : AppCompatActivity() {
    private lateinit var cubeSdk: CubeAnimatingSdk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        cubeSdk = findViewById(R.id.cubeSdk)
        setupCubeAnimatingSdk()
    }

    private fun setupCubeAnimatingSdk() {
        // Step 1: Load images
        cubeSdk.loadImagesFromResources(listOf(
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5
        ))

        // Step 2: Basic configuration
        cubeSdk.setRotationInterval(3000)
        cubeSdk.setAnimationDuration(500)
        cubeSdk.setSwipeEnabled(true)
        cubeSdk.setImageScaleType(ImageView.ScaleType.CENTER_CROP)

        // Step 3: Configure UI elements
        setupUIElements()

        // Step 4: Set up event listeners
        setupEventListeners()

        // Step 5: Start auto-rotation
        cubeSdk.startAutoRotation()
    }

    private fun setupUIElements() {
        // Button Configuration
        val buttonConfig = CubeAnimatingSdk.ButtonConfig().apply {
            enabled = true
            position = CubeAnimatingSdk.ButtonPosition.BOTTOM_END
            text = "Get Started"
            backgroundColor = Color.BLUE
            textColor = Color.WHITE
            textSize = 16f
            cornerRadius = 12f
            marginEnd = 60
            marginBottom = 60
            visibilityMode = CubeAnimatingSdk.ButtonVisibility.ON_SPECIFIC_INDICES
        }

        cubeSdk.setButtonConfig(buttonConfig)
        cubeSdk.setButtonOnIndices(0, 2, 4) // Show button only on images 0, 2, 4

        // Greeting Configuration
        val greetingConfig = CubeAnimatingSdk.GreetingConfig().apply {
            text = "Default Welcome Text"
            textSize = 18f
            textColor = Color.WHITE
            textStyle = CubeAnimatingSdk.TextStyle.BOLD
            position = CubeAnimatingSdk.GreetingPosition.TOP_START
            backgroundColor = Color.parseColor("#80000000")
            marginTop = 40
            marginStart = 40
            showOnMain = true
            showOnNext = true
        }

        cubeSdk.setGreetingConfig(greetingConfig)
        cubeSdk.setGreetingOnIndices(0, 1, 2, 3, 4) // Show greeting on all images

        // Set different texts for different images
        cubeSdk.setGreetingTextForIndex(0, "🏔️ Discover Amazing Mountains")
        cubeSdk.setGreetingTextForIndex(1, "🌄 Explore Natural Beauty")
        cubeSdk.setGreetingTextForIndex(2, "🚀 Adventure Awaits You")
        cubeSdk.setGreetingTextForIndex(3, "📸 Capture Memorable Moments")
        cubeSdk.setGreetingTextForIndex(4, "❤️ Find Your Perfect Escape")
    }

    private fun setupEventListeners() {
        cubeSdk.setOnButtonClickListener { index, image ->
            Toast.makeText(this, "Button clicked on image $index", Toast.LENGTH_SHORT).show()
            
            when (index) {
                0 -> startLogin()
                2 -> showSignUp()
                4 -> exploreFeatures()
            }
        }

        cubeSdk.setOnImageLoadListener { index, image ->
            Log.d("CubeSDK", "Image loaded: $index")
        }

        cubeSdk.setOnAnimationCompleteListener { currentIndex ->
            Log.d("CubeSDK", "Animation completed: $currentIndex")
        }

        cubeSdk.setOnImageClickListener { index, image, allImages ->
            Toast.makeText(this, "Image $index tapped", Toast.LENGTH_SHORT).show()
        }
    }
}

🎨Customization Options
---
Button Configuration
---
kotlin
val buttonConfig = CubeAnimatingSdk.ButtonConfig().apply {
    enabled = true
    position = CubeAnimatingSdk.ButtonPosition.BOTTOM_END // TOP_START, TOP_END, BOTTOM_START, BOTTOM_END, CENTER
    text = "Action"
    backgroundColor = Color.BLUE
    textColor = Color.WHITE
    textSize = 16f
    cornerRadius = 8f
    padding = 16
    margin = 8
    visibilityMode = CubeAnimatingSdk.ButtonVisibility.ON_SPECIFIC_INDICES // ALWAYS, ON_FIRST_IMAGE, ON_LAST_IMAGE, ON_SPECIFIC_INDICES, NEVER
}

**Greeting Configuration**
---
kotlin
val greetingConfig = CubeAnimatingSdk.GreetingConfig().apply {
    text = "Welcome!"
    textSize = 18f
    textColor = Color.WHITE
    textStyle = CubeAnimatingSdk.TextStyle.BOLD // NORMAL, BOLD, ITALIC, BOLD_ITALIC
    position = CubeAnimatingSdk.GreetingPosition.TOP_START // TOP_CENTER, TOP_END, CENTER, BOTTOM_START, BOTTOM_CENTER, BOTTOM_END
    backgroundColor = Color.parseColor("#80000000")
    padding = 16
    margin = 8
    showOnMain = true
    showOnNext = true
}

🔧 API Reference
---
Core Methods
---
Method	Description
loadImagesFromResources(resources: List<Int>)	Load images from drawable resources
loadImagesFromUrls(urls: List<String>)	Load images from URLs
addImageResource(resource: Int)	Add single image dynamically
addImageUrl(url: String)	Add single image URL dynamically
startAutoRotation()	Start automatic slideshow
stopAutoRotation()	Stop automatic slideshow
next()	Manually go to next image
previous()	Manually go to previous image

Configuration Methods
---
Method	Description
setRotationInterval(interval: Long)	Set auto-rotation interval (ms)
setAnimationDuration(duration: Long)	Set animation duration (ms)
setSwipeEnabled(enabled: Boolean)	Enable/disable swipe gestures
setImageScaleType(scaleType: ImageView.ScaleType)	Set image scale type
setCornerRadius(radius: Float)	Set container corner radius

UI Customization
---
Method	Description
setButtonConfig(config: ButtonConfig)	Configure button appearance
setGreetingConfig(config: GreetingConfig)	Configure greeting text
setButtonOnIndices(vararg indices: Int)	Show button on specific image indices
setGreetingOnIndices(vararg indices: Int)	Show greeting on specific image indices
setGreetingTextForIndex(index: Int, text: String)	Set different text for specific image
setGreetingTexts(texts: Map<Int, String>)	Set multiple texts at once

Event Listeners
---
Listener	Description
setOnButtonClickListener	Called when button is clicked
setOnImageLoadListener	Called when image loads
setOnAnimationCompleteListener	Called when animation completes
setOnImageClickListener	Called when image is tapped
setOnSwipeListener	Called when swipe gesture is detected

📋XML Attributes
---
Basic Attributes
<!-- Auto Rotation -->
app:autoRotateInterval="3000"

<!-- Animation Speed -->
app:animationDuration="500"

<!-- Swipe Gestures -->
app:swipeEnabled="true"

<!-- Container Styling -->
app:cubeCornerRadius="20dp"

<!-- Image Scaling -->
app:scaleType="centerCrop"

**Button Attributes**
xml
<!-- Enable/Disable Button -->
app:enableButton="true"

<!-- Button Text -->
app:buttonText="Action"

<!-- Button Styling -->
app:buttonColor="@color/blue"
app:buttonTextColor="@color/white"
app:buttonCornerRadius="8dp"

<!-- Button Text Size -->
app:buttonTextSize="16sp"

<!-- Button Position -->
app:buttonPosition="bottom_end"

**Greeting Text Attributes**
xml
app:greetingText="Welcome!"
app:greetingTextColor="@color/white"
app:greetingTextSize="18sp"
app:greetingBackgroundColor="#80000000"
app:greetingTextPosition="top_start"

<!-- Different texts for different images -->
app:greetingTextForIndex0="Text for image 0"
app:greetingTextForIndex1="Text for image 1"
app:greetingTextForIndex2="Text for image 2"
app:greetingTextForIndex3="Text for image 3"
app:greetingTextForIndex4="Text for image 4"

🎯 Use Cases
---
Onboarding Screens
kotlin
// Different messages for each onboarding step
cubeSdk.setGreetingTextForIndex(0, "Welcome to Our App!")
cubeSdk.setGreetingTextForIndex(1, "Discover Amazing Features")
cubeSdk.setGreetingTextForIndex(2, "Get Started Today")
cubeSdk.setButtonOnIndices(2) // Show button only on last screen


Image Sliders with Actions
kotlin
// Different buttons for different images
cubeSdk.setButtonOnIndices(0, 2, 4)
cubeSdk.setOnButtonClickListener { index, image ->
    when (index) {
        0 -> showProductDetails()
        2 -> addToCart()
        4 -> makePurchase()
    }
}

🔄 Lifecycle Management
---
kotlin
override fun onResume() {
    super.onResume()
    cubeSdk.startAutoRotation()
}

override fun onPause() {
    super.onPause()
    cubeSdk.stopAutoRotation()
}

override fun onDestroy() {
    super.onDestroy()
    // Clean up resources
}

🐛Troubleshooting
---
Common Issues
Images not loading

Check if images are added to drawable resources

Verify image URLs are accessible

Check internet permission for URL loading

Buttons not visible

Verify buttonConfig.enabled = true

Check setButtonOnIndices() includes current image index

Ensure visibilityMode is set correctly

Animation not smooth

Reduce image sizes if too large

Adjust setAnimationDuration() for smoother transitions

Check memory usage for large image sets

Logging
Enable debug logging to track SDK operations:

kotlin
// Check Logcat for "CubeSDK" tags
Log.d("CubeSDK", "Debug information")

📄 License
---
text
Copyright 2024 Cube Animation SDK

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
