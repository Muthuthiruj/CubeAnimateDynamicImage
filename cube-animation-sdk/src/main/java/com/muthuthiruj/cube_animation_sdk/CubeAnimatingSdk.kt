package com.muthuthiruj.cube_animation_sdk

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.animation.Animation
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.muthuthiruj.cube_animation_sdk.databinding.LayoutCubeAnimationBinding



class CubeAnimatingSdk @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var binding: LayoutCubeAnimationBinding
    private lateinit var gestureDetector: GestureDetector
    private var cornerRadius: Float = 0f
    // Image collections
    private val imageUrls = mutableListOf<String>()
    private val imageResources = mutableListOf<Int>()

    // Configuration
    private var config = CubeAnimationConfig()

    // State management
    private var currentImageIndex = 0
    private var isAnimating = false
    private var isForwardSequence = true
    private var autoRotationHandler = Handler(Looper.getMainLooper())
    private var lastSwipeTime = 0L
    private val SWIPE_DEBOUNCE_DELAY = 500L

    // Image loading placeholders
    @DrawableRes
    private var placeholderRes: Int = android.R.drawable.ic_menu_gallery
    @DrawableRes
    private var errorRes: Int = android.R.drawable.ic_menu_gallery

    // Listeners
    private var onImageLoadListener: OnImageLoadListener? = null
    private var onImageErrorListener: OnImageErrorListener? = null
    private var onAnimationCompleteListener: OnAnimationCompleteListener? = null
    private var onSwipeListener: OnSwipeListener? = null
    private var onImageClickListener: OnImageClickListener? = null

    // Add these properties to your existing class properties
    private var buttonConfig = ButtonConfig()
    private var greetingConfig = GreetingConfig()
    private var buttonOnIndices = mutableSetOf<Int>()
    private var greetingOnIndices = mutableSetOf<Int>()
    private var onButtonClickListener: OnButtonClickListener? = null

    private var currentScaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER // 🌟 Default ScaleType
    private var imageSetName: String = "" // To store the descriptive name


    // Define the interface for the click listener
    interface OnImageClickListener {
        fun onImageClick(index: Int, image: Any?, allImages: List<Any>?)
    }

    // Auto rotation runnable - INFINITE LOOP
    private val autoRotationRunnable = object : Runnable {
        override fun run() {
            if (config.autoRotate && !isAnimating && hasMultipleImages()) {
                onAutoSlide()
            }
            // Always post next execution for infinite loop
            autoRotationHandler.postDelayed(this, config.rotationInterval)
        }
    }

    data class ButtonConfig(
        var enabled: Boolean = false,
        var position: ButtonPosition = ButtonPosition.BOTTOM_END,
        var text: String = "Action",
        var backgroundColor: Int = Color.BLUE,
        var textColor: Int = Color.WHITE,
        var textSize: Float = 16f,

        // ✅ Enhanced Padding Options
        var padding: Int = 16, // Overall padding (if individual paddings not set)
        var paddingStart: Int = 16,
        var paddingTop: Int = 12,
        var paddingEnd: Int = 16,
        var paddingBottom: Int = 12,
        var paddingHorizontal: Int = 16,
        var paddingVertical: Int = 12,

        // ✅ Margin Options
        var margin: Int = 8,
        var marginStart: Int = 8,
        var marginTop: Int = 8,
        var marginEnd: Int = 8,
        var marginBottom: Int = 8,
        var marginHorizontal: Int = 8,
        var marginVertical: Int = 8,

        // ✅ Enhanced Text Styling
        var textStyle: TextStyle = TextStyle.NORMAL,
        var fontFamily: String? = null, // Custom font family
        var typeface: Int = Typeface.NORMAL, // Typeface constant
        var letterSpacing: Float = 0f, // Letter spacing
        var lineSpacingMultiplier: Float = 1.0f, // Line spacing
        var lineSpacingExtra: Float = 0f, // Extra line spacing
        var textAllCaps: Boolean = false, // Transform text to uppercase
        var maxLines: Int = 1, // Maximum lines
        var ellipsize: TextEllipsize = TextEllipsize.END, // Text overflow behavior

        // ✅ Border & Shadow Options
        var borderWidth: Float = 0f,
        var borderColor: Int = Color.TRANSPARENT,
        var shadowColor: Int = Color.BLACK,
        var shadowRadius: Float = 0f,
        var shadowDx: Float = 0f,
        var shadowDy: Float = 0f,

        // ✅ Background Options
        var backgroundDrawable: Int? = null, // Custom drawable resource
        var gradientStartColor: Int = Color.TRANSPARENT,
        var gradientEndColor: Int = Color.TRANSPARENT,
        var gradientOrientation: GradientOrientation = GradientOrientation.LEFT_TO_RIGHT,

        // ✅ Animation & State Options
        var rippleColor: Int = Color.WHITE,
        var animationDuration: Long = 200L,
        var pressedAlpha: Float = 0.7f,

        // ✅ Size & Layout Options
        var width: Int = RelativeLayout.LayoutParams.WRAP_CONTENT,
        var height: Int = RelativeLayout.LayoutParams.WRAP_CONTENT,
        var minWidth: Int = 0,
        var minHeight: Int = 0,
        var maxWidth: Int = Int.MAX_VALUE,
        var maxHeight: Int = Int.MAX_VALUE,

        // ✅ Visibility & Behavior
        var showOnMain: Boolean = true,
        var showOnNext: Boolean = true,
        var visibilityMode: ButtonVisibility = ButtonVisibility.ALWAYS,
        var cornerRadius: Float = 8f,
        var elevation: Float = 4f,

        // ✅ Icon Options
        var iconResource: Int? = null,
        var iconPosition: IconPosition = IconPosition.START,
        var iconSize: Int = 24,
        var iconPadding: Int = 8,
        var iconTint: Int = Color.WHITE,

        // ✅ Advanced Layout
        var gravity: Int = Gravity.CENTER,
        var weight: Float = 0f
    )

    data class GreetingConfig(
        var text: String = "Welcome!",
        var textSize: Float = 18f,
        var textColor: Int = Color.WHITE,
        var textStyle: TextStyle = TextStyle.NORMAL,

        // ✅ Enhanced Text Styling
        var fontFamily: String? = null,
        var typeface: Int = Typeface.NORMAL,
        var letterSpacing: Float = 0f,
        var lineSpacingMultiplier: Float = 1.0f,
        var lineSpacingExtra: Float = 0f,
        var textAllCaps: Boolean = false,
        var maxLines: Int = 2,
        var ellipsize: TextEllipsize = TextEllipsize.END,
        var textAlignment: TextAlignment = TextAlignment.CENTER,
        var textGravity: Int = Gravity.CENTER,

        // ✅ Enhanced Padding Options
        var padding: Int = 16,
        var paddingStart: Int = 16,
        var paddingTop: Int = 12,
        var paddingEnd: Int = 16,
        var paddingBottom: Int = 12,
        var paddingHorizontal: Int = 16,
        var paddingVertical: Int = 12,

        // ✅ Margin Options
        var margin: Int = 8,
        var marginStart: Int = 8,
        var marginTop: Int = 8,
        var marginEnd: Int = 8,
        var marginBottom: Int = 8,
        var marginHorizontal: Int = 8,
        var marginVertical: Int = 8,

        // ✅ Background Options
        var backgroundColor: Int = Color.TRANSPARENT,
        var backgroundDrawable: Int? = null,
        var cornerRadius: Float = 8f,
        var borderWidth: Float = 0f,
        var borderColor: Int = Color.TRANSPARENT,

        // ✅ Shadow Options
        var elevation: Float = 0f,
        var shadowColor: Int = Color.BLACK,
        var shadowRadius: Float = 0f,
        var shadowDx: Float = 0f,
        var shadowDy: Float = 0f,

        // ✅ Gradient Background
        var gradientStartColor: Int = Color.TRANSPARENT,
        var gradientEndColor: Int = Color.TRANSPARENT,
        var gradientOrientation: GradientOrientation = GradientOrientation.TOP_TO_BOTTOM,

        // ✅ Position & Visibility
        var position: GreetingPosition = GreetingPosition.TOP_START,
        var showOnMain: Boolean = true,
        var showOnNext: Boolean = true,

        // ✅ Animation
        var animationType: TextAnimation = TextAnimation.NONE,
        var animationDuration: Long = 500L,

        // ✅ Size & Layout
        var width: Int = RelativeLayout.LayoutParams.WRAP_CONTENT,
        var height: Int = RelativeLayout.LayoutParams.WRAP_CONTENT,
        var minWidth: Int = 0,
        var minHeight: Int = 0,
        var maxWidth: Int = Int.MAX_VALUE,

        // ✅ Advanced Options
        var autoSizeTextType: AutoSizeTextType = AutoSizeTextType.NONE,
        var autoSizeMinTextSize: Float = 12f,
        var autoSizeMaxTextSize: Float = 18f,
        var autoSizeStepGranularity: Float = 1f
    )

    enum class TextEllipsize {
        START, MIDDLE, END, MARQUEE, NONE
    }

    enum class TextAlignment {
        START, CENTER, END, JUSTIFY
    }

    enum class GradientOrientation {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT, TOP_TO_BOTTOM, BOTTOM_TO_TOP,
        TR_TO_BL, TL_TO_BR, BR_TO_TL, BL_TO_TR
    }

    enum class IconPosition {
        START, END, TOP, BOTTOM
    }

    enum class TextAnimation {
        NONE, FADE, SLIDE, BOUNCE, TYPEWRITER, SCALE
    }

    enum class AutoSizeTextType {
        NONE, UNIFORM, GRANULAR
    }

    enum class ButtonPosition {
        TOP_START, TOP_END, BOTTOM_START, BOTTOM_END, CENTER, NONE
    }

    enum class ButtonVisibility {
        ALWAYS, ON_FIRST_IMAGE, ON_LAST_IMAGE, ON_SPECIFIC_INDICES, NEVER
    }

    enum class GreetingPosition {
        TOP_START, TOP_CENTER, TOP_END, CENTER, BOTTOM_START, BOTTOM_CENTER, BOTTOM_END
    }

    enum class TextStyle {
        NORMAL, BOLD, ITALIC, BOLD_ITALIC
    }

    // In CubeAnimatingSdk class
    interface OnButtonClickListener {
        fun onButtonClick(index: Int, image: Any?)
    }

    interface OnImageLoadListener {
        fun onImageLoad(index: Int, image: Any?)
    }

    interface OnAnimationCompleteListener {
        fun onAnimationComplete(currentIndex: Int)
    }



    init {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        binding = LayoutCubeAnimationBinding.inflate(LayoutInflater.from(context), this, true)
        setupGestureDetector()
        setupTouchListeners()

        // Initialize with first image visibility
        binding.mainContentContainer.visibility = VISIBLE
        binding.nextContentContainer.visibility = INVISIBLE

        // Set scale type for better image display
        binding.mainImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        binding.nextImageView.scaleType = ImageView.ScaleType.CENTER_CROP

        setupGestureDetector()
        setupTouchListeners()

        handleAttributes(attrs)

        applyCornerRadius()

        applyButtonConfigurations()
        applyGreetingConfigurations()
        updateButtonAndTextVisibility()
    }
    private fun handleAttributes(attrs: AttributeSet?) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.CubeAnimatingSdk,
                0,
                0
            )

            try {
                // 🌟 ADD BUTTON ATTRIBUTES HANDLING
                buttonConfig.enabled = typedArray.getBoolean(
                    R.styleable.CubeAnimatingSdk_enableButton, false
                )
                buttonConfig.position = when (typedArray.getInt(
                    R.styleable.CubeAnimatingSdk_buttonPosition, 3
                )) {
                    0 -> ButtonPosition.TOP_START
                    1 -> ButtonPosition.TOP_END
                    2 -> ButtonPosition.BOTTOM_START
                    3 -> ButtonPosition.BOTTOM_END
                    4 -> ButtonPosition.CENTER
                    else -> ButtonPosition.NONE
                }
                buttonConfig.text = typedArray.getString(
                    R.styleable.CubeAnimatingSdk_buttonText
                ) ?: "Action"
                buttonConfig.backgroundColor = typedArray.getColor(
                    R.styleable.CubeAnimatingSdk_buttonColor,
                    context.getColor(android.R.color.holo_blue_dark)
                )
                buttonConfig.textColor = typedArray.getColor(
                    R.styleable.CubeAnimatingSdk_buttonTextColor,
                    context.getColor(android.R.color.white)
                )
                buttonConfig.showOnMain = typedArray.getBoolean(
                    R.styleable.CubeAnimatingSdk_showButtonOnMain, true
                )
                buttonConfig.showOnNext = typedArray.getBoolean(
                    R.styleable.CubeAnimatingSdk_showButtonOnNext, true
                )

                // 🌟 ADD GREETING ATTRIBUTES HANDLING
                greetingConfig.text = typedArray.getString(
                    R.styleable.CubeAnimatingSdk_greetingText
                ) ?: "Welcome!"
                greetingConfig.textSize = typedArray.getDimension(
                    R.styleable.CubeAnimatingSdk_greetingTextSize, 18f
                )
                greetingConfig.textColor = typedArray.getColor(
                    R.styleable.CubeAnimatingSdk_greetingTextColor,
                    context.getColor(android.R.color.white)
                )
                greetingConfig.showOnMain = typedArray.getBoolean(
                    R.styleable.CubeAnimatingSdk_showGreetingOnMain, true
                )
                greetingConfig.showOnNext = typedArray.getBoolean(
                    R.styleable.CubeAnimatingSdk_showGreetingOnNext, true
                )

                // Apply configurations
                applyButtonConfigurations()
                applyGreetingConfigurations()
                // Handle corner radius
                cornerRadius = typedArray.getDimension(
                    R.styleable.CubeAnimatingSdk_cubeCornerRadius,
                    0f
                )

                // Handle images
                val mainImageRes = typedArray.getResourceId(
                    R.styleable.CubeAnimatingSdk_mainImage,
                    -1
                )
                val nextImageRes = typedArray.getResourceId(
                    R.styleable.CubeAnimatingSdk_nextImage,
                    -1
                )

                // Handle animation controls
                val autoRotateInterval = typedArray.getInt(
                    R.styleable.CubeAnimatingSdk_autoRotateInterval,
                    3000
                )
                val animationDuration = typedArray.getInt(
                    R.styleable.CubeAnimatingSdk_animationDuration,
                    500
                )
                val swipeEnabled = typedArray.getBoolean(
                    R.styleable.CubeAnimatingSdk_swipeEnabled,
                    true
                )
                val autoRotate = typedArray.getBoolean(
                    R.styleable.CubeAnimatingSdk_autoRotate,
                    true
                )
                val loopImages = typedArray.getBoolean(
                    R.styleable.CubeAnimatingSdk_loopImages,
                    true
                )

                // Handle scale type
                val scaleTypeIndex = typedArray.getInt(
                    R.styleable.CubeAnimatingSdk_scaleType,
                    1 // Default to centerCrop
                )
                val scaleType = when (scaleTypeIndex) {
                    0 -> ImageView.ScaleType.CENTER
                    1 -> ImageView.ScaleType.CENTER_CROP
                    2 -> ImageView.ScaleType.CENTER_INSIDE
                    3 -> ImageView.ScaleType.FIT_CENTER
                    4 -> ImageView.ScaleType.FIT_START
                    5 -> ImageView.ScaleType.FIT_END
                    6 -> ImageView.ScaleType.FIT_XY
                    7 -> ImageView.ScaleType.MATRIX
                    else -> ImageView.ScaleType.CENTER_CROP
                }

                // Apply XML configurations
                if (mainImageRes != -1 && nextImageRes != -1) {
                    loadImagesFromResources(listOf(mainImageRes, nextImageRes))
                }

                setRotationInterval(autoRotateInterval.toLong())
                setAnimationDuration(animationDuration.toLong())
                setSwipeEnabled(swipeEnabled)
                config.autoRotate = autoRotate
                config.loopImages = loopImages
                setImageScaleType(scaleType)

                Log.d("CubeSDK", "📝 XML attributes applied - CornerRadius: ${cornerRadius}px")



            } finally {
                typedArray.recycle()
            }
        }
    }

    // CORNER RADIUS PUBLIC API METHODS

    /**
     * Set corner radius for both containers in pixels
     */
    fun setCornerRadius(radius: Float) {
        this.cornerRadius = radius
        applyCornerRadius()
        Log.d("CubeSDK", "🎨 Corner radius set to: ${radius}px")
    }

    // 🌟 BUTTON AND TEXT PUBLIC API METHODS

    /**
     * Set button configuration
     */
    fun setButtonConfig(config: ButtonConfig) {
        this.buttonConfig = config
        applyButtonConfigurations()
        updateButtonAndTextVisibility()
    }

    /**
     * Set greeting configuration
     */
    fun setGreetingConfig(config: GreetingConfig) {
        this.greetingConfig = config
        applyGreetingConfigurations()
        updateButtonAndTextVisibility()
    }

    // 🌟 PRIVATE HELPER METHODS

// 🌟 PRIVATE HELPER METHODS

    private fun applyButtonConfigurations() {
        configureButton(binding.mainActionButton, buttonConfig)
        configureButton(binding.nextActionButton, buttonConfig)
    }

    private fun configureButton(button: Button, config: ButtonConfig) {
        button.text = config.text
        button.setBackgroundColor(config.backgroundColor)
        button.setTextColor(config.textColor)
        button.textSize = config.textSize
        button.setPadding(config.padding)

        val layoutParams = button.layoutParams as? RelativeLayout.LayoutParams
            ?: RelativeLayout.LayoutParams(config.width, config.height)

        layoutParams.width = config.width
        layoutParams.height = config.height

        // Clear existing rules
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_START)
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP)
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END)
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT)

        when (config.position) {
            ButtonPosition.TOP_START -> {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            }
            ButtonPosition.TOP_END -> {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            }
            ButtonPosition.BOTTOM_START -> {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
            ButtonPosition.BOTTOM_END -> {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
            ButtonPosition.CENTER -> {
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            }
            ButtonPosition.NONE -> {
                // No positioning rules
            }
        }

        button.layoutParams = layoutParams
    }

    private fun applyGreetingConfigurations() {
        configureGreeting(binding.mainGreetingText, greetingConfig)
        configureGreeting(binding.nextGreetingText, greetingConfig)
    }

    private fun configureGreeting(textView: TextView, config: GreetingConfig) {
        textView.text = config.text
        textView.textSize = config.textSize
        textView.setTextColor(config.textColor)

        when (config.textStyle) {
            TextStyle.NORMAL -> textView.setTypeface(null, Typeface.NORMAL)
            TextStyle.BOLD -> textView.setTypeface(null, Typeface.BOLD)
            TextStyle.ITALIC -> textView.setTypeface(null, Typeface.ITALIC)
            TextStyle.BOLD_ITALIC -> textView.setTypeface(null, Typeface.BOLD_ITALIC)
        }

        val layoutParams = textView.layoutParams as? RelativeLayout.LayoutParams
            ?: RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )

        // Clear existing rules
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_START)
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP)
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END)
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT)
        layoutParams.removeRule(RelativeLayout.CENTER_HORIZONTAL)
        layoutParams.removeRule(RelativeLayout.CENTER_VERTICAL)

        when (config.position) {
            GreetingPosition.TOP_START -> {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            }
            GreetingPosition.TOP_CENTER -> {
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            }
            GreetingPosition.TOP_END -> {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            }
            GreetingPosition.CENTER -> {
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            }
            GreetingPosition.BOTTOM_START -> {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
            GreetingPosition.BOTTOM_CENTER -> {
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
            GreetingPosition.BOTTOM_END -> {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
        }

        textView.layoutParams = layoutParams
    }

    private fun setupButtonClickListeners() {
        binding.mainActionButton.setOnClickListener {
            onButtonClickListener?.onButtonClick(currentImageIndex, getCurrentImage())
        }

        binding.nextActionButton.setOnClickListener {
            val nextIndex = if (currentImageIndex < getImageCount() - 1) currentImageIndex + 1 else 0
            onButtonClickListener?.onButtonClick(nextIndex, getImageAt(nextIndex))
        }
    }

    private fun updateButtonAndTextVisibility() {
        updateMainButtonAndTextVisibility()
        updateNextButtonAndTextVisibility()
    }

    private fun updateMainButtonAndTextVisibility() {
        // Button visibility
        val shouldShowButton = when (buttonConfig.visibilityMode) {
            ButtonVisibility.ALWAYS -> buttonConfig.enabled && buttonConfig.showOnMain
            ButtonVisibility.ON_FIRST_IMAGE -> buttonConfig.enabled && buttonConfig.showOnMain && currentImageIndex == 0
            ButtonVisibility.ON_LAST_IMAGE -> buttonConfig.enabled && buttonConfig.showOnMain && currentImageIndex == getImageCount() - 1
            ButtonVisibility.ON_SPECIFIC_INDICES -> buttonConfig.enabled && buttonConfig.showOnMain && buttonOnIndices.contains(currentImageIndex)
            ButtonVisibility.NEVER -> false
        }

        binding.mainActionButton.visibility = if (shouldShowButton) VISIBLE else INVISIBLE

        // Greeting visibility
        val shouldShowGreeting = greetingConfig.showOnMain &&
                (greetingOnIndices.isEmpty() || greetingOnIndices.contains(currentImageIndex))

        binding.mainGreetingText.visibility = if (shouldShowGreeting) VISIBLE else INVISIBLE
    }

    private fun updateNextButtonAndTextVisibility() {
        val nextIndex = if (currentImageIndex < getImageCount() - 1) currentImageIndex + 1 else 0

        // Button visibility for next container
        val shouldShowButton = when (buttonConfig.visibilityMode) {
            ButtonVisibility.ALWAYS -> buttonConfig.enabled && buttonConfig.showOnNext
            ButtonVisibility.ON_FIRST_IMAGE -> buttonConfig.enabled && buttonConfig.showOnNext && nextIndex == 0
            ButtonVisibility.ON_LAST_IMAGE -> buttonConfig.enabled && buttonConfig.showOnNext && nextIndex == getImageCount() - 1
            ButtonVisibility.ON_SPECIFIC_INDICES -> buttonConfig.enabled && buttonConfig.showOnNext && buttonOnIndices.contains(nextIndex)
            ButtonVisibility.NEVER -> false
        }

        binding.nextActionButton.visibility = if (shouldShowButton) VISIBLE else INVISIBLE

        // Greeting visibility for next container
        val shouldShowGreeting = greetingConfig.showOnNext &&
                (greetingOnIndices.isEmpty() || greetingOnIndices.contains(nextIndex))

        binding.nextGreetingText.visibility = if (shouldShowGreeting) VISIBLE else INVISIBLE
    }

    /**
     * Show button only on specific image indices
     */
    fun setButtonOnIndices(vararg indices: Int) {
        buttonOnIndices.clear()
        buttonOnIndices.addAll(indices.toList())
        buttonConfig.visibilityMode = ButtonVisibility.ON_SPECIFIC_INDICES
        updateButtonAndTextVisibility()
    }

    /**
     * Show greeting only on specific image indices
     */
    fun setGreetingOnIndices(vararg indices: Int) {
        greetingOnIndices.clear()
        greetingOnIndices.addAll(indices.toList())
        updateButtonAndTextVisibility()
    }

    /**
     * Set button click listener
     */
    fun setOnButtonClickListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    private fun applyCornerRadius() {
        binding.mainContentContainer.radius = cornerRadius
        binding.nextContentContainer.radius = cornerRadius
        Log.d("CubeSDK", "🎨 Applied corner radius: ${cornerRadius}px to both containers")
    }

    // Public API Methods

    /**
     * Load images from URLs with dynamic loading
     */


    /**
     * Load images from drawable resources
     */
    fun loadImagesFromResources(@DrawableRes resources: List<Int>) {
        imageResources.clear()
        imageResources.addAll(resources)
        imageUrls.clear()
        currentImageIndex = 0
        loadCurrentImage()
        startAutoRotationIfEnabled()
    }

    /**
     * Add single image URL dynamically
     */
    fun addImageUrl(url: String) {
        imageUrls.add(url)
        if (imageUrls.size == 1 && imageResources.isEmpty()) {
            currentImageIndex = 0
            loadCurrentImage()
        }
        startAutoRotationIfEnabled()
    }

    /**
     * Add single drawable resource dynamically
     */
    fun addImageResource(@DrawableRes resource: Int) {
        imageResources.add(resource)
        if (imageResources.size == 1 && imageUrls.isEmpty()) {
            currentImageIndex = 0
            loadCurrentImage()
        }
        startAutoRotationIfEnabled()
    }

    /**
     * Clear all images
     */
    fun clearImages() {
        imageUrls.clear()
        imageResources.clear()
        binding.mainImageView.setImageDrawable(null)
        binding.nextImageView.setImageDrawable(null)
    }

    /**
     * Start auto rotation - INFINITE
     */
    fun startAutoRotation() {
        config.autoRotate = true
        startAutoRotationIfEnabled()
    }

    /**
     * Stop auto rotation
     */


    /**
     * Set rotation interval in milliseconds
     */
    fun setRotationInterval(interval: Long) {
        config.rotationInterval = interval
        restartAutoRotation()
    }

    /**
     * Set animation duration in milliseconds
     */
    fun setAnimationDuration(duration: Long) {
        config.animationDuration = duration
    }

    /**
     * Enable/disable swipe gestures
     */
    fun setSwipeEnabled(enabled: Boolean) {
        config.swipeEnabled = enabled
    }

    /**
     * Set animation type
     */
    fun setAnimationType(type: AnimationType) {
        Log.d("CubeSDK", "Animation type set to: $type")
    }

    /**
     * Set placeholder image for URL loading
     */
    fun setPlaceholderImage(@DrawableRes placeholderRes: Int) {
        this.placeholderRes = placeholderRes
    }

    /**
     * Set error image for URL loading failures
     */
    fun setErrorImage(@DrawableRes errorRes: Int) {
        this.errorRes = errorRes
    }

    /**
     * Enable/disable image caching
     */
    fun setImageCaching(enabled: Boolean) {
        config.imageCaching = enabled
    }

    /**
     * Manually navigate to next image
     */
    fun next() {
        if (!isAnimating && hasMultipleImages()) {
            animateToNext()
        }
    }


    fun getImageCount(): Int = if (imageUrls.isNotEmpty()) imageUrls.size else imageResources.size

    // Event Listeners
    fun setOnImageLoadListener(listener: OnImageLoadListener) {
        onImageLoadListener = listener
    }

    fun setOnImageErrorListener(listener: OnImageErrorListener) {
        onImageErrorListener = listener
    }

    fun setOnAnimationCompleteListener(listener: OnAnimationCompleteListener) {
        onAnimationCompleteListener = listener
    }

    fun setOnSwipeListener(listener: OnSwipeListener) {
        onSwipeListener = listener
    }

    fun setOnImageClickListener(listener: OnImageClickListener) {
        onImageClickListener = listener
    }

    // Private implementation methods
    private fun loadCurrentImage() {
        if (getImageCount() == 0) return

        try {
            if (imageUrls.isNotEmpty()) {
                // Load image from URL using Glide into mainImageView
                loadImageWithGlide(binding.mainImageView, imageUrls[currentImageIndex])
                onImageLoadListener?.onImageLoad(currentImageIndex, imageUrls[currentImageIndex])
            } else {
                binding.mainImageView.setImageResource(imageResources[currentImageIndex])
                onImageLoadListener?.onImageLoad(currentImageIndex, imageResources[currentImageIndex])
            }
        } catch (e: Exception) {
            Log.e("CubeSDK", "Error loading current image: ${e.message}")
            onImageErrorListener?.onImageError(currentImageIndex, getCurrentImage(), e)
        }
    }

    private fun loadImageWithGlide(imageView: ImageView, imageUrl: String) {
        try {
            Glide.with(context)
                .load(imageUrl)
                .placeholder(placeholderRes)
                .error(errorRes)
                .centerCrop()
                .into(imageView)
        } catch (e: Exception) {
            Log.e("CubeSDK", "Glide error loading image: ${e.message}")
            // Fallback to placeholder
            imageView.setImageResource(errorRes)
            onImageErrorListener?.onImageError(currentImageIndex, imageUrl, e)
        }
    }

    private fun getCurrentImage(): Any? {
        return if (imageUrls.isNotEmpty()) imageUrls.getOrNull(currentImageIndex)
        else imageResources.getOrNull(currentImageIndex)
    }

    private fun getAllImages(): List<Any> {
        return if (imageUrls.isNotEmpty()) imageUrls
        else imageResources.map { it as Any }
    }


// Inside your CubeAnimatingSdk class

    /**
     * 🌟 1. Set the ScaleType for both internal ImageViews immediately.
     */
    fun setImageScaleType(scaleType: ImageView.ScaleType) {
        currentScaleType = scaleType
        binding.mainImageView.scaleType = scaleType
        binding.nextImageView.scaleType = scaleType
        Log.d("CubeSDK", "ScaleType applied: ${scaleType.name}")
    }

    /**
     * 2. Set a descriptive name for the current set of images.
     */
    fun setImageSetName(name: String) {
        imageSetName = name
        Log.d("CubeSDK", "Image Set Name: $imageSetName")
    }

    /**
     * 3. Set the listener for image click events.
     */

    private fun setupGestureDetector() {
        val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onDown(e: MotionEvent): Boolean = true

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null || !config.swipeEnabled || isAnimating) return false

                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y

                if (Math.abs(diffX) > Math.abs(diffY) &&
                    Math.abs(diffX) > SWIPE_THRESHOLD &&
                    Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD
                ) {
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                    return true
                }
                return false
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                onImageClickListener?.onImageClick(currentImageIndex, getCurrentImage(), getAllImages())
                return true
            }
        }

        gestureDetector = GestureDetector(context, gestureListener)
    }

    private fun setupTouchListeners() {
        // Enhanced touch listener to prevent conflicts with parent scroll views
        var initialX = 0f
        var initialY = 0f
        var isSwiping = false

        setOnTouchListener { v, event ->
            // First, let gesture detector handle the event
            gestureDetector.onTouchEvent(event)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = event.x
                    initialY = event.y
                    isSwiping = false
                    // Request parent to not intercept touch events
                    parent?.requestDisallowInterceptTouchEvent(true)
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.x - initialX
                    val deltaY = event.y - initialY

                    // Check if this is a horizontal swipe (not a scroll)
                    if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > 20) {
                        isSwiping = true
                        // Keep parent from intercepting for swipes
                        parent?.requestDisallowInterceptTouchEvent(true)
                    } else if (Math.abs(deltaY) > Math.abs(deltaX) && Math.abs(deltaY) > 20) {
                        // Vertical movement - allow parent to handle it
                        parent?.requestDisallowInterceptTouchEvent(false)
                        return@setOnTouchListener false
                    }
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    parent?.requestDisallowInterceptTouchEvent(false)

                    if (isSwiping) {
                        val finalX = event.x
                        val deltaX = finalX - initialX

                        // Determine swipe direction
                        if (Math.abs(deltaX) > 100) { // Minimum swipe distance
                            if (deltaX > 0) {
                                onSwipeRight()
                            } else {
                                onSwipeLeft()
                            }
                        }
                        isSwiping = false
                        return@setOnTouchListener true
                    }
                    return@setOnTouchListener false
                }
            }
            false
        }

        isClickable = true
    }
    private fun onSwipeLeft() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastSwipeTime < SWIPE_DEBOUNCE_DELAY || isAnimating) {
            Log.d("CubeSDK", "Swipe ignored - too fast or animating")
            return
        }
        lastSwipeTime = currentTime

        // 1. Always stop the auto-rotation timer immediately
        autoRotationHandler.removeCallbacks(autoRotationRunnable)

        val nextIndex: Int
        val direction = CubeAnimation.DIRECTION_LEFT // Left swipe is forward motion

        if (currentImageIndex < getImageCount() - 1) {
            // Normal move: Set sequence forward and start animation
            nextIndex = currentImageIndex + 1
            isForwardSequence = true
        } else if (config.loopImages) {
            // Looping wrap: Set sequence forward and start animation
            nextIndex = 0
            isForwardSequence = true // Ensure sequence is forward after loop
        } else {
            // Not looping, at last index: Prevent animation and reverse sequence for next time
            isForwardSequence = false
            Log.d("CubeSDK", "Swipe ignored - at last index, sequence switched to backward.")
            // Manually restart the timer since startAnimation() was skipped
            autoRotationHandler.postDelayed(autoRotationRunnable, config.rotationInterval)
            return
        }

        // 2. Start animation (auto-rotation restart is handled by onAnimationEnd)
        startAnimation(nextIndex, direction)
    }

    private fun onSwipeRight() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastSwipeTime < SWIPE_DEBOUNCE_DELAY || isAnimating) {
            Log.d("CubeSDK", "Swipe ignored - too fast or animating")
            return
        }
        lastSwipeTime = currentTime

        // 1. Always stop the auto-rotation timer immediately
        autoRotationHandler.removeCallbacks(autoRotationRunnable)

        val prevIndex: Int
        val direction = CubeAnimation.DIRECTION_RIGHT // Right swipe is backward motion

        if (currentImageIndex > 0) {
            // Normal move: Set sequence backward and start animation
            prevIndex = currentImageIndex - 1
            isForwardSequence = false
        } else if (config.loopImages) {
            // Looping wrap: Set sequence backward and start animation
            prevIndex = getImageCount() - 1
            isForwardSequence = false
        } else {
            // Not looping, at first index: Prevent animation and reverse sequence for next time
            isForwardSequence = true
            Log.d("CubeSDK", "Swipe ignored - at first index, sequence switched to forward.")
            // Manually restart the timer since startAnimation() was skipped
            autoRotationHandler.postDelayed(autoRotationRunnable, config.rotationInterval)
            return
        }

        // 2. Start animation (auto-rotation restart is handled by onAnimationEnd)
        startAnimation(prevIndex, direction)
    }

    private fun animateToNext() {
        val nextIndex = if (currentImageIndex < getImageCount() - 1) currentImageIndex + 1 else 0
        if (isValidIndex(nextIndex)) {
            // Next animation should be left to right
            startAnimation(nextIndex, CubeAnimation.DIRECTION_LEFT)
        }
    }

    private fun animateToPrevious() {
        val prevIndex = if (currentImageIndex > 0) currentImageIndex - 1 else getImageCount() - 1
        if (isValidIndex(prevIndex)) {
            // Previous animation should be right to left
            startAnimation(prevIndex, CubeAnimation.DIRECTION_RIGHT)
        }
    }

    /**
     * Auto slide logic with forward and backward sequencing - INFINITE LOOP
     */
    private fun onAutoSlide() {
        if (isForwardSequence) {
            if (currentImageIndex < getImageCount() - 1) {
                // Forward direction: left to right animation
                startAnimation(currentImageIndex + 1, CubeAnimation.DIRECTION_LEFT)
            } else {
                isForwardSequence = false
                // Switch to backward direction: right to left animation
                startAnimation(currentImageIndex - 1, CubeAnimation.DIRECTION_RIGHT)
            }
        } else {
            if (currentImageIndex > 0) {
                // Backward direction: right to left animation
                startAnimation(currentImageIndex - 1, CubeAnimation.DIRECTION_RIGHT)
            } else {
                isForwardSequence = true
                // Switch to forward direction: left to right animation
                startAnimation(currentImageIndex + 1, CubeAnimation.DIRECTION_LEFT)
            }
        }
    }

    private fun startAnimation(newIndex: Int, direction: Int) {
        if (isAnimating || !isValidIndex(newIndex)) return

        isAnimating = true
        Log.d("CubeSDK", "🎬 Starting animation from $currentImageIndex to $newIndex, direction: $direction")

        // Clear any previous animations
        binding.mainContentContainer.clearAnimation()
        binding.nextContentContainer.clearAnimation()

        // Set up the next image in nextImageView - but don't reload in mainImageView later
        try {
            if (imageUrls.isNotEmpty()) {
                loadImageWithGlide(binding.nextImageView, imageUrls[newIndex])
            } else {
                binding.nextImageView.setImageResource(imageResources[newIndex])
            }
        } catch (e: Exception) {
            Log.e("CubeSDK", "❌ Error loading next image: ${e.message}")
            onImageErrorListener?.onImageError(newIndex, getImageAt(newIndex), e)
            isAnimating = false
            restartAutoRotation() // Restart auto-rotation even on error
            return
        }

        // Make next container visible
        binding.nextContentContainer.visibility = VISIBLE
        binding.nextImageView.visibility = VISIBLE

        // Create and initialize animations
        val outAnimation = CubeAnimation.create(direction, false, config.animationDuration)
        val inAnimation = CubeAnimation.create(direction, true, config.animationDuration)

        outAnimation.initialize(
            binding.mainContentContainer.width,
            binding.mainContentContainer.height,
            width,
            height
        )
        inAnimation.initialize(
            binding.nextContentContainer.width,
            binding.nextContentContainer.height,
            width,
            height
        )

        outAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                Log.d("CubeSDK", "▶️ Animation started")
            }

            override fun onAnimationEnd(animation: Animation) {
                Log.d("CubeSDK", "⏹️ Animation ended, swapping containers")

                // FIX: Instead of reloading the image, just swap the image drawables
                // This prevents the blinking/reloading effect
                if (imageUrls.isNotEmpty()) {
                    // For URLs, we need to handle differently to avoid reloading
                    // Just swap the image drawables between main and next ImageViews
                    val mainDrawable = binding.mainImageView.drawable
                    val nextDrawable = binding.nextImageView.drawable

                    binding.mainImageView.setImageDrawable(nextDrawable)
                    binding.nextImageView.setImageDrawable(mainDrawable)
                } else {
                    // For resources, just update the main image view
                    binding.mainImageView.setImageResource(imageResources[newIndex])
                }

                // Alternative approach: Simply make next container the main container
                // This is more efficient and prevents any blinking
                binding.mainContentContainer.visibility = VISIBLE
                binding.nextContentContainer.visibility = INVISIBLE

                // Update current index
                currentImageIndex = newIndex
                isAnimating = false

                updateButtonAndTextVisibility()

                Log.d("CubeSDK", "✅ Animation complete. Current index: $currentImageIndex, Forward sequence: $isForwardSequence")

                // Notify listener
                onAnimationCompleteListener?.onAnimationComplete(currentImageIndex)

                // CRITICAL: Restart auto-rotation after animation completes
                restartAutoRotation()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        // Start animations
        binding.mainContentContainer.startAnimation(outAnimation)
        binding.nextContentContainer.startAnimation(inAnimation)
    }

    private fun getImageAt(index: Int): Any? {
        return if (imageUrls.isNotEmpty()) imageUrls.getOrNull(index)
        else imageResources.getOrNull(index)
    }

    private fun isValidIndex(index: Int): Boolean {
        return index in 0 until getImageCount()
    }

    private fun hasMultipleImages(): Boolean = getImageCount() > 1
    private fun hasNextImage(): Boolean = currentImageIndex < getImageCount() - 1
    private fun hasPreviousImage(): Boolean = currentImageIndex > 0

    private fun startAutoRotationIfEnabled() {
        if (config.autoRotate && hasMultipleImages()) {
            autoRotationHandler.removeCallbacks(autoRotationRunnable)
            autoRotationHandler.postDelayed(autoRotationRunnable, config.rotationInterval)
            Log.d("CubeSDK", "🔄 Auto rotation started with interval: ${config.rotationInterval}ms")
        }
    }



    private fun restartAutoRotation() {

        startAutoRotationIfEnabled()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        // Clear Glide requests
        binding.mainImageView.setImageDrawable(null)
        binding.nextImageView.setImageDrawable(null)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Ensure animations are properly sized when container size changes
        Log.d("CubeSDK", "📐 Container size changed: $w x $h")
    }
}
