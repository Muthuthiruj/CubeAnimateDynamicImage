**📦 CubeAnimatingSdk**

A lightweight, powerful Android View component designed to display a sequence of images (Drawables or URLs) with smooth, engaging 3D cube rotation and sliding animations. Perfect for dialogs, product carousels, and interactive headers.

✨ Features

3D Cube Rotation: Seamless transition between images using a visually appealing 3D cube effect.

Automatic Rotation: Configure speed and direction (including ping-pong mode).

Swipe Interaction: Users can manually navigate images via touch gestures.

Image Caching: Efficient memory management for loaded images.

High Customizability: Control animation duration, rotation interval, and image scaling (ScaleType).

Lifecycle Awareness: Easy integration with Android Activity/Fragment lifecycle for stopping and resuming animation.

🚀 Setup

**1. Installation**

This SDK is intended to be used as a library dependency. Replace the placeholder below with your actual dependency coordinates once published to Maven Central or a private repository.

Add the dependency to your module's build.gradle.kts (or build.gradle):

// NOTE: Replace this placeholder link with your actual Git repository reference or Maven coordinates
implementation 'com.Muthuthiruj:cube-animating-sdk:1.0.0' 


**2. XML Layout**

Add the CubeAnimatingSdk to your activity_main.xml (or any layout file). Note the app:cubeSdk ID used for accessing the view in Kotlin.

<com.Muthuthiruj.cubeanimatingsdk.CubeAnimatingSdk
    android:id="@+id/cubeSdk"
    android:layout_width="match_parent"
    android:layout_height="250dp" 
    android:layout_margin="16dp"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent" />


**💻 Usage**

1. Configure and Load Images (MainActivity.kt)

Use the following pattern to load images, configure the animation, and manage the lifecycle. This example uses View Binding, as demonstrated in the code history.

// Ensure you have R.drawable.mountain1, mountain2, etc., defined
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var cubeSdk: CubeAnimatingSdk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) 

        cubeSdk = binding.cubeSdk 
        setupCubeAnimatingSdk()
    }

    private fun setupCubeAnimatingSdk() {
        // Load image resources (list of @DrawableRes Ints)
        cubeSdk.loadImagesFromResources(listOf(
            R.drawable.mountain1,
            R.drawable.mountain2,
            R.drawable.mountain3,
            R.drawable.mountain4,
            R.drawable.mountain5
        ))

        // Configuration
        cubeSdk.setRotationInterval(2000) // Rotate every 2 seconds
        cubeSdk.setAnimationDuration(600) // Animation takes 600ms
        cubeSdk.setSwipeEnabled(true)
        cubeSdk.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
        
        // Start auto rotation immediately
        cubeSdk.startAutoRotation()
    }

    // 2. Lifecycle Management (CRITICAL)
    // Always stop and restart the rotation handler in lifecycle callbacks.

    override fun onResume() {
        super.onResume()
        cubeSdk.startAutoRotation() 
    }

    override fun onPause() {
        super.onPause()
        cubeSdk.stopAutoRotation()
    }
}


**⚙️ Configuration Methods**

You can customize the cube's behavior using the following setter methods:

Method

Type

Default

Description

setRotationInterval(Long)

Long

3000

Time (ms) between automatic rotations.

setAnimationDuration(Long)

Long

500

Duration (ms) of the cube animation/slide effect.

setSwipeEnabled(Boolean)

Boolean

true

Allows user to change images via swipe gestures.

setImageCaching(Boolean)

Boolean

true

Enables in-memory caching for smoother transitions.

setLoopImages(Boolean)

Boolean

true

If true, looping back from the last image to the first is enabled.

setPingPongMode(Boolean)

Boolean

true

If true, rotation reverses direction after reaching the end.

setImageScaleType(ImageView.ScaleType)

ScaleType

FIT_XY

Determines how the image fits within the cube face.

setImageSetName(String)

String

null

Optional descriptive name for debugging/logging.

startAutoRotation()

Unit

N/A

Starts the automatic rotation loop.

stopAutoRotation()

Unit

N/A

Stops the rotation loop immediately.
