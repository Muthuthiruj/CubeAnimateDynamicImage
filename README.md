#Cube Animation SDK
A powerful, customizable Android SDK for creating stunning 3D cube animations with images. Perfect for image galleries, onboarding screens, product showcases, and interactive image displays.

#🚀 **Features**
3D Cube Animations - Smooth 3D cube flip transitions

Dual Control - Both XML attributes and programmatic API

Auto Rotation - Automatic image cycling with configurable intervals

Swipe Gestures - Touch-based navigation between images

Customizable Corners - Rounded corners with adjustable radius

Multiple Scale Types - All Android ImageView scale types supported

Image Loading - Support for both drawable resources and URL images

Event Listeners - Comprehensive callback system

Memory Efficient - Optimized with Glide for smooth performance

📦 Installation
Add Dependency
gradle
dependencies {
    implementation 'com.muthuthiruj:cube-animation-sdk:1.0.0'
}
Required Dependencies
gradle
dependencies {
    implementation 'androidx.cardview:cardview:1.0.0'
    implementation 'com.github.bumptech.glide:glide:4.14.2'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.14.2'
}
🎯 Quick Start
XML Usage
xml
<com.muthuthiruj.cube_animation_sdk.CubeAnimatingSdk
    android:id="@+id/cubeSdk"
    android:layout_width="300dp"
    android:layout_height="300dp"
    
    <!-- Basic Images -->
    app:mainImage="@drawable/image1"
    app:nextImage="@drawable/image2"
    
    <!-- Corner Styling -->
    app:cubeCornerRadius="16dp"
    
    <!-- Animation Controls -->
    app:autoRotateInterval="3000"
    app:animationDuration="500"
    app:swipeEnabled="true"
    app:autoRotate="true"
    app:loopImages="true"
    
    <!-- Scale Type -->
    app:scaleType="centerCrop"
    
    android:background="@android:color/transparent" />
Programmatic Usage
kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var cubeSdk: CubeAnimatingSdk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        cubeSdk = findViewById(R.id.cubeSdk)
        setupCubeAnimatingSdk()
    }

    private fun setupCubeAnimatingSdk() {
        // Load images
        cubeSdk.loadImagesFromResources(listOf(
            R.drawable.image1, R.drawable.image2, R.drawable.image3
        ))

        // Configuration
        cubeSdk.setRotationInterval(3000)
        cubeSdk.setAnimationDuration(500)
        cubeSdk.setSwipeEnabled(true)
        cubeSdk.setImageCaching(true)
        
        // Styling
        cubeSdk.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
        cubeSdk.setCornerRadiusInDp(16f)
        cubeSdk.setImageSetName("Nature Gallery")

        // Event listeners
        cubeSdk.setOnImageLoadListener { index, image ->
            Toast.makeText(this, "Image ${index + 1} loaded", Toast.LENGTH_SHORT).show()
        }

        cubeSdk.setOnAnimationCompleteListener { currentIndex ->
            Log.d("CubeSDK", "Now showing image: $currentIndex")
        }

        // Start animation
        cubeSdk.startAutoRotation()
    }
}
⚙️ Configuration
XML Attributes
Attribute	Type	Default	Description
mainImage	reference	-	Initial main image
nextImage	reference	-	Initial next image
cubeCornerRadius	dimension	0dp	Corner radius for containers
autoRotateInterval	integer	3000	Auto-rotation interval in ms
animationDuration	integer	500	Animation duration in ms
swipeEnabled	boolean	true	Enable/disable swipe gestures
autoRotate	boolean	true	Enable/disable auto rotation
loopImages	boolean	true	Loop images continuously
scaleType	enum	centerCrop	Image scaling behavior
Scale Types
center - No scaling, center in view

centerCrop - Scale uniformly, crop if needed

centerInside - Scale to fit inside, no cropping

fitCenter - Scale to fit, maintain aspect ratio

fitStart - Scale to fit, align top/left

fitEnd - Scale to fit, align bottom/right

fitXY - Stretch to fill, may distort

matrix - Use image matrix for custom scaling

🔧 Advanced Usage
Corner Radius Control
kotlin
// Set corner radius in dp (recommended)
cubeSdk.setCornerRadiusInDp(24f)

// Set corner radius in pixels
cubeSdk.setCornerRadius(72f)

// Remove corners (rectangular)
cubeSdk.removeCornerRadius()

// Get current corner radius
val currentRadius = cubeSdk.getCornerRadiusInDp()
Image Management
kotlin
// Load from URLs
cubeSdk.loadImagesFromUrls(listOf(
    "https://example.com/image1.jpg",
    "https://example.com/image2.jpg"
))

// Add images dynamically
cubeSdk.addImageResource(R.drawable.new_image)
cubeSdk.addImageUrl("https://example.com/new_image.jpg")

// Clear all images
cubeSdk.clearImages()

// Manual navigation
cubeSdk.next()      // Go to next image
cubeSdk.previous()  // Go to previous image
Animation Control
kotlin
// Start/stop auto rotation
cubeSdk.startAutoRotation()
cubeSdk.stopAutoRotation()

// Configure animation
cubeSdk.setRotationInterval(5000)  // 5 seconds
cubeSdk.setAnimationDuration(1000) // 1 second animation

// Enable/disable features
cubeSdk.setSwipeEnabled(false)
cubeSdk.setLoopImages(false)
Event Listeners
kotlin
cubeSdk.setOnImageLoadListener { index, image ->
    // Called when an image is loaded
    Log.d("CubeSDK", "Image $index loaded: $image")
}

cubeSdk.setOnImageErrorListener { index, image, error ->
    // Called when image loading fails
    Log.e("CubeSDK", "Error loading image $index", error)
}

cubeSdk.setOnAnimationCompleteListener { currentIndex ->
    // Called when animation completes
    Log.d("CubeSDK", "Animation complete, now showing: $currentIndex")
}

cubeSdk.setOnSwipeListener { direction ->
    // Called when user swipes
    val directionText = if (direction == CubeAnimation.DIRECTION_LEFT) "left" else "right"
    Log.d("CubeSDK", "User swiped $directionText")
}

cubeSdk.setOnImageClickListener { index, currentImage, allImages ->
    // Called when image is clicked
    Toast.makeText(this, "Clicked image $index", Toast.LENGTH_SHORT).show()
}
🏗️ Architecture
Key Components
CubeAnimatingSdk - Main custom view class

CubeAnimation - Handles 3D animation logic

CubeAnimationConfig - Configuration data class

LayoutCubeAnimationBinding - View binding for layout

Core Architecture Flow
text
XML/Programmatic Input
        ↓
handleAttributes() ← 🌟 Processes all configurations
        ↓
Image Loading (Glide/Resources)
        ↓
Gesture Detection & Touch Handling
        ↓
Animation System (CubeAnimation)
        ↓
State Management & Event Callbacks
🔍 Deep Dive: handleAttributes Method
Purpose
The handleAttributes() method is the core initialization engine that processes all XML attributes and applies them to the SDK configuration. It bridges the gap between XML declarations and runtime behavior.

Implementation Details
kotlin
private fun handleAttributes(attrs: AttributeSet?) {
    attrs?.let {
        // Obtain styled attributes from XML
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CubeAnimatingSdk,  // References attrs.xml
            0,  // Default style attribute
            0   // Default style resource
        )

        try {
            // 🌟 CORNER RADIUS
            cornerRadius = typedArray.getDimension(
                R.styleable.CubeAnimatingSdk_cubeCornerRadius,
                0f  // Default: no corner radius
            )

            // 🌟 IMAGE RESOURCES
            val mainImageRes = typedArray.getResourceId(
                R.styleable.CubeAnimatingSdk_mainImage,
                -1  // Default: no image
            )
            val nextImageRes = typedArray.getResourceId(
                R.styleable.CubeAnimatingSdk_nextImage,
                -1
            )

            // 🌟 ANIMATION TIMING
            val autoRotateInterval = typedArray.getInt(
                R.styleable.CubeAnimatingSdk_autoRotateInterval,
                3000  // Default: 3 seconds
            )
            val animationDuration = typedArray.getInt(
                R.styleable.CubeAnimatingSdk_animationDuration,
                500   // Default: 500ms
            )

            // 🌟 BEHAVIOR CONTROLS
            val swipeEnabled = typedArray.getBoolean(
                R.styleable.CubeAnimatingSdk_swipeEnabled,
                true  // Default: enabled
            )
            val autoRotate = typedArray.getBoolean(
                R.styleable.CubeAnimatingSdk_autoRotate,
                true
            )
            val loopImages = typedArray.getBoolean(
                R.styleable.CubeAnimatingSdk_loopImages,
                true
            )

            // 🌟 SCALE TYPE MAPPING
            val scaleTypeIndex = typedArray.getInt(
                R.styleable.CubeAnimatingSdk_scaleType,
                1  // Default: CENTER_CROP
            )
            val scaleType = when (scaleTypeIndex) {
                0 -> ImageView.ScaleType.CENTER
                1 -> ImageView.ScaleType.CENTER_CROP
                // ... other mappings
                else -> ImageView.ScaleType.CENTER_CROP
            }

            // 🌟 APPLY CONFIGURATIONS
            if (mainImageRes != -1 && nextImageRes != -1) {
                loadImagesFromResources(listOf(mainImageRes, nextImageRes))
            }

            setRotationInterval(autoRotateInterval.toLong())
            setAnimationDuration(animationDuration.toLong())
            setSwipeEnabled(swipeEnabled)
            config.autoRotate = autoRotate
            config.loopImages = loopImages
            setImageScaleType(scaleType)

            Log.d("CubeSDK", "📝 XML attributes applied successfully")

        } finally {
            // 🌟 CRITICAL: Always recycle typed array
            typedArray.recycle()
        }
    }
}
Why handleAttributes is Called First
kotlin
private fun initView(attrs: AttributeSet?) {
    binding = LayoutCubeAnimationBinding.inflate(LayoutInflater.from(context), this, true)
    
    // 🌟 MUST BE CALLED FIRST - Processes XML before any initialization
    handleAttributes(attrs)
    
    setupGestureDetector()
    setupTouchListeners()
    
    // Rest of initialization...
    applyCornerRadius()
}
Reasoning:

XML Precedence - XML attributes should override default values

Early Configuration - Images and timing need to be set before display

Consistent State - Ensures SDK starts in configured state

Performance - Prevents double initialization

Attribute Processing Order
Read XML attributes from AttributeSet

Extract values with proper type conversion

Apply configurations to internal state

Initialize components with final configuration

Start services like auto-rotation

🎨 Customization Examples
Different Corner Styles
kotlin
// Rounded corners (material design)
cubeSdk.setCornerRadiusInDp(8f)

// Pill-shaped (for square containers)
cubeSdk.setCornerRadiusInDp(50f)

// Sharp corners (modern look)
cubeSdk.removeCornerRadius()
Animation Styles
kotlin
// Slow, smooth transitions
cubeSdk.setAnimationDuration(1000)
cubeSdk.setRotationInterval(5000)

// Fast, dynamic transitions  
cubeSdk.setAnimationDuration(300)
cubeSdk.setRotationInterval(2000)
Responsive Design
xml
<!-- Different configurations for different screen sizes -->

<!-- values/dimens.xml (Phones) -->
<dimen name="cube_corner_radius">16dp</dimen>
<dimen name="cube_size">300dp</dimen>

<!-- values-sw600dp/dimens.xml (Tablets) -->
<dimen name="cube_corner_radius">24dp</dimen>
<dimen name="cube_size">400dp</dimen>
🔧 Troubleshooting
Common Issues
XML attributes not working

Ensure handleAttributes(attrs) is called in initView

Check attrs.xml for correct attribute definitions

Images not loading

Verify image resources exist

Check internet permission for URL images

Review Glide configuration

Animation performance

Reduce image sizes for better performance

Use appropriate scale types

Consider enabling image caching

Debug Mode
Enable detailed logging to troubleshoot:

kotlin
// Check current configuration
val configSummary = cubeSdk.getConfigSummary()
Log.d("CubeSDK", configSummary)

// Monitor animation states
cubeSdk.setOnAnimationCompleteListener { currentIndex ->
    Log.d("CubeSDK", "Animation completed → Index: $currentIndex")
}
📚 Best Practices
Performance
Use appropriately sized images

Enable image caching for URL images

Consider using WebP format for better compression

UX
Set appropriate auto-rotation intervals (3-5 seconds recommended)

Provide visual feedback for user interactions

Ensure touch targets are adequately sized

Maintenance
Use dimension resources for consistent styling

Implement proper error handling

Test on various screen sizes and densities

🤝 Contributing
We welcome contributions! Please see our Contributing Guidelines for details.

📄 License
This project is licensed under the MIT License - see the LICENSE file for details.

🆘 Support
📧 Email: support@muthuthiruj.com

🐛 Issue Tracker

📖 Documentation

Cube Animation SDK - Transform your image displays with stunning 3D animations! 🎭✨
