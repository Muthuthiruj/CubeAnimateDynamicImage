package com.Muthuthiruj.cubeanimatingsdk

import android.R.attr.scaleType
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.*
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

open class CubeAnimation : FrameLayout {

    companion object {
        const val DIRECTION_UP = 1
        const val DIRECTION_DOWN = 2
        const val DIRECTION_LEFT = 3
        const val DIRECTION_RIGHT = 4

        const val ANIMATION_CUBE = 0
        const val ANIMATION_SLIDE = 1
        const val ANIMATION_FADE = 2
        const val ANIMATION_ZOOM = 3

        const val BUTTON_POSITION_TOP_START = 0
        const val BUTTON_POSITION_TOP_END = 1
        const val BUTTON_POSITION_BOTTOM_START = 2
        const val BUTTON_POSITION_BOTTOM_END = 3
        const val BUTTON_POSITION_CENTER = 4

        fun create(context: Context): CubeAnimation {
            return CubeAnimation(context)
        }
    }

    // Dynamic Image Loading Properties
    private var imageUrls: MutableList<String> = mutableListOf()
    private var imageResources: MutableList<Int> = mutableListOf()
    private var currentImageIndex = 0
    private var placeholderResId: Int = 0
    private var errorResId: Int = 0
    private var enableCache = true
    private var imageScaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP

    // Flexible Button Configuration
    private var isButtonEnabled = true
    private var buttonPosition = BUTTON_POSITION_TOP_START
    private var buttonText = "Login"
    private var buttonTextColor: Int = android.graphics.Color.WHITE
    private var buttonBackgroundColor: Int = android.graphics.Color.parseColor("#4CAF50")
    private var buttonCornerRadius = 16f
    private var buttonElevation = 8f

    // Auto 3D Device Effect Properties
    private var isAutoRotationEnabled = true
    private var autoRotationInterval = 4000L
    private var animationDuration = 1000L
    private var animationType = ANIMATION_CUBE
    private var cubeDirection = DIRECTION_LEFT
    private var enable3DEffect = true
    private var depthValue = 25f

    // View Components
    private lateinit var mainImageView: ImageView
    private lateinit var nextImageView: ImageView
    private lateinit var loginButton: android.widget.Button
    private lateinit var greetingTextView: android.widget.TextView

    // Animation Handlers
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val autoRotateRunnable = object : Runnable {
        override fun run() {
            if (isAutoRotationEnabled && hasMultipleImages()) {
                performNextAnimation()
            }
            handler.postDelayed(this, autoRotationInterval)
        }
    }

    // Listeners
    private var onImageLoadListener: ((Int, String?) -> Unit)? = null
    private var onImageErrorListener: ((Int, String?, Exception) -> Unit)? = null
    private var onButtonClickListener: (() -> Unit)? = null
    private var onCubeAnimationListener: ((Int) -> Unit)? = null
    private var onLoginStateChangeListener: ((Boolean) -> Unit)? = null

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr) {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        // Inflate layout
        View.inflate(context, R.layout.layout_cube_animation, this)

        // Initialize views
        mainImageView = findViewById(R.id.mainImageView)
        nextImageView = findViewById(R.id.nextImageView)
        loginButton = findViewById(R.id.loginOverlayButton)
        greetingTextView = findViewById(R.id.greetingTextView)

        setupAttributes(attrs)
        setup3DEffect()
        setupClickListeners()
        updateButtonAppearance()
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CubeAnimation)

            try {
                // Button Configuration
                isButtonEnabled = typedArray.getBoolean(
                    R.styleable.CubeAnimation_enableButton, true
                )
                buttonPosition = typedArray.getInt(
                    R.styleable.CubeAnimation_buttonPosition, BUTTON_POSITION_TOP_START
                )
                buttonText = typedArray.getString(
                    R.styleable.CubeAnimation_buttonText
                ) ?: "Login"
                buttonBackgroundColor = typedArray.getColor(
                    R.styleable.CubeAnimation_buttonColor,
                    ContextCompat.getColor(context, android.R.color.holo_green_dark)
                )
                buttonTextColor = typedArray.getColor(
                    R.styleable.CubeAnimation_buttonTextColor, android.graphics.Color.WHITE
                )

                // Animation Configuration
                isAutoRotationEnabled = typedArray.getBoolean(
                    R.styleable.CubeAnimation_autoRotation, true
                )
                autoRotationInterval = typedArray.getInt(
                    R.styleable.CubeAnimation_rotationInterval, 4000
                ).toLong()
                animationDuration = typedArray.getInt(
                    R.styleable.CubeAnimation_animationDuration, 1000
                ).toLong()
                animationType = typedArray.getInt(
                    R.styleable.CubeAnimation_animationType, ANIMATION_CUBE
                )
                cubeDirection = typedArray.getInt(
                    R.styleable.CubeAnimation_cubeDirection, DIRECTION_LEFT
                )
                enable3DEffect = typedArray.getBoolean(
                    R.styleable.CubeAnimation_enable3DEffect, true
                )

                // Image Loading Configuration
                placeholderResId = typedArray.getResourceId(
                    R.styleable.CubeAnimation_placeholderImage, 0
                )
                errorResId = typedArray.getResourceId(
                    R.styleable.CubeAnimation_errorImage, 0
                )
                enableCache = typedArray.getBoolean(
                    R.styleable.CubeAnimation_enableCache, true
                )

            } catch (e: Exception) {
                Log.e("CubeAnimation", "Error setting up attributes: ${e.message}")
            } finally {
                typedArray.recycle()
            }
        }
    }

    private fun setup3DEffect() {
        if (enable3DEffect) {
            mainImageView.cameraDistance = depthValue * resources.displayMetrics.density
            nextImageView.cameraDistance = depthValue * resources.displayMetrics.density
        }
    }

    private fun setupClickListeners() {
        setOnClickListener {
            performNextAnimation()
        }

        loginButton.setOnClickListener {
            onButtonClickListener?.invoke()
        }
    }

    /** =============================================
     * DYNAMIC IMAGE LOADING METHODS - Developer Access
     * ============================================= */

    /**
     * Load images from URLs with automatic rotation
     */
    fun loadImagesFromUrls(urls: List<String>) {
        imageUrls.clear()
        imageUrls.addAll(urls)
        imageResources.clear()

        if (urls.isNotEmpty()) {
            loadImageIntoView(mainImageView, urls[0])
            startAutoRotation()
        }
    }

    /**
     * Load images from resource IDs
     */
    fun loadImagesFromResources(resources: List<Int>) {
        imageResources.clear()
        imageResources.addAll(resources)
        imageUrls.clear()

        if (resources.isNotEmpty()) {
            mainImageView.setImageResource(resources[0])
            startAutoRotation()
        }
    }

    /**
     * Add single image URL dynamically
     */
    fun addImageUrl(url: String) {
        imageUrls.add(url)
        if (imageUrls.size == 1) {
            loadImageIntoView(mainImageView, url)
        }
        startAutoRotation()
    }

    /**
     * Add single image resource dynamically
     */
    fun addImageResource(resourceId: Int) {
        imageResources.add(resourceId)
        if (imageResources.size == 1) {
            mainImageView.setImageResource(resourceId)
        }
        startAutoRotation()
    }

    /**
     * Clear all images
     */
    fun clearImages() {
        imageUrls.clear()
        imageResources.clear()
        stopAutoRotation()
        mainImageView.setImageDrawable(null)
    }

    /**
     * Get current image count
     */
    fun getImageCount(): Int {
        return maxOf(imageUrls.size, imageResources.size)
    }

    /** =============================================
     * FLEXIBLE BUTTON CONFIGURATION - Developer Access
     * ============================================= */

    /**
     * Enable or disable the login button
     */
    fun setButtonEnabled(enabled: Boolean) {
        isButtonEnabled = enabled
        loginButton.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    /**
     * Set button position
     */
    fun setButtonPosition(position: Int) {
        buttonPosition = position
        updateButtonPosition()
    }

    /**
     * Set button text
     */
    fun setButtonText(text: String) {
        buttonText = text
        loginButton.text = text
    }

    /**
     * Set button colors
     */
    fun setButtonColors(backgroundColor: Int, textColor: Int) {
        buttonBackgroundColor = backgroundColor
        buttonTextColor = textColor
        updateButtonAppearance()
    }

    /**
     * Set button corner radius
     */
    fun setButtonCornerRadius(radius: Float) {
        buttonCornerRadius = radius
        updateButtonAppearance()
    }

    /**
     * Set button elevation
     */
    fun setButtonElevation(elevation: Float) {
        buttonElevation = elevation
        updateButtonAppearance()
    }

    /** =============================================
     * AUTO 3D DEVICE EFFECT METHODS - Developer Access
     * ============================================= */

    /**
     * Start automatic rotation
     */
    fun startAutoRotation() {
        if (hasMultipleImages()) {
            isAutoRotationEnabled = true
            handler.removeCallbacks(autoRotateRunnable)
            handler.postDelayed(autoRotateRunnable, autoRotationInterval)
        }
    }

    /**
     * Stop automatic rotation
     */
    fun stopAutoRotation() {
        isAutoRotationEnabled = false
        handler.removeCallbacks(autoRotateRunnable)
    }

    /**
     * Set rotation interval
     */
    fun setRotationInterval(intervalMs: Long) {
        autoRotationInterval = intervalMs
        if (isAutoRotationEnabled) {
            startAutoRotation()
        }
    }

    /**
     * Set animation duration
     */
    fun setAnimationDuration(durationMs: Long) {
        animationDuration = durationMs
    }

    /**
     * Set animation type
     */
    fun setAnimationType(type: Int) {
        animationType = type
    }

    /**
     * Set cube direction
     */
    fun setCubeDirection(direction: Int) {
        cubeDirection = direction
    }

    /**
     * Enable/disable 3D effect
     */
    fun set3DEffectEnabled(enabled: Boolean) {
        enable3DEffect = enabled
        setup3DEffect()
    }

    /**
     * Set 3D depth value
     */
    fun setDepthValue(depth: Float) {
        depthValue = depth
        setup3DEffect()
    }

    /** =============================================
     * LOGIN/LOGOUT STATE MANAGEMENT - Developer Access
     * ============================================= */

    /**
     * Set login state - show greeting text when logged in
     */
    fun setLoggedIn(isLoggedIn: Boolean, userName: String? = null) {
        if (isLoggedIn) {
            // User logged in - show greeting
            stopAutoRotation()
            loginButton.visibility = View.GONE
            greetingTextView.visibility = View.VISIBLE
            greetingTextView.text = userName?.let { "Welcome, $it!" } ?: "Welcome!"
        } else {
            // User logged out - show login button
            greetingTextView.visibility = View.GONE
            if (isButtonEnabled) {
                loginButton.visibility = View.VISIBLE
            }
            if (hasMultipleImages()) {
                startAutoRotation()
            }
        }
        onLoginStateChangeListener?.invoke(isLoggedIn)
    }

    /**
     * Set custom greeting text
     */
    fun setGreetingText(text: String) {
        greetingTextView.text = text
    }

    /** =============================================
     * LISTENER SETTERS - Developer Access
     * ============================================= */

    fun setOnImageLoadListener(listener: (Int, String?) -> Unit) {
        onImageLoadListener = listener
    }

    fun setOnImageErrorListener(listener: (Int, String?, Exception) -> Unit) {
        onImageErrorListener = listener
    }

    fun setOnButtonClickListener(listener: () -> Unit) {
        onButtonClickListener = listener
    }

    fun setOnCubeAnimationListener(listener: (Int) -> Unit) {
        onCubeAnimationListener = listener
    }

    fun setOnLoginStateChangeListener(listener: (Boolean) -> Unit) {
        onLoginStateChangeListener = listener
    }

    /** =============================================
     * PRIVATE HELPER METHODS
     * ============================================= */

    private fun performNextAnimation() {
        if (!hasMultipleImages()) return

        val nextIndex = (currentImageIndex + 1) % getImageCount()

        // Load next image
        if (imageUrls.isNotEmpty()) {
            loadImageIntoView(nextImageView, imageUrls[nextIndex])
        } else {
            nextImageView.setImageResource(imageResources[nextIndex])
        }

        nextImageView.visibility = View.VISIBLE

        when (animationType) {
            ANIMATION_CUBE -> performCubeAnimation(nextIndex)
            ANIMATION_SLIDE -> performSlideAnimation(nextIndex)
            ANIMATION_FADE -> performFadeAnimation(nextIndex)
            ANIMATION_ZOOM -> performZoomAnimation(nextIndex)
        }
    }

    private fun performCubeAnimation(nextIndex: Int) {
        val animatorSet = AnimatorSet()

        when (cubeDirection) {
            DIRECTION_LEFT -> {
                val currentOut = ObjectAnimator.ofFloat(mainImageView, "rotationY", 0f, -90f)
                val nextIn = ObjectAnimator.ofFloat(nextImageView, "rotationY", 90f, 0f)
                animatorSet.playSequentially(currentOut, nextIn)
            }
            DIRECTION_RIGHT -> {
                val currentOut = ObjectAnimator.ofFloat(mainImageView, "rotationY", 0f, 90f)
                val nextIn = ObjectAnimator.ofFloat(nextImageView, "rotationY", -90f, 0f)
                animatorSet.playSequentially(currentOut, nextIn)
            }
            DIRECTION_UP -> {
                val currentOut = ObjectAnimator.ofFloat(mainImageView, "rotationX", 0f, -90f)
                val nextIn = ObjectAnimator.ofFloat(nextImageView, "rotationX", 90f, 0f)
                animatorSet.playSequentially(currentOut, nextIn)
            }
            DIRECTION_DOWN -> {
                val currentOut = ObjectAnimator.ofFloat(mainImageView, "rotationX", 0f, 90f)
                val nextIn = ObjectAnimator.ofFloat(nextImageView, "rotationX", -90f, 0f)
                animatorSet.playSequentially(currentOut, nextIn)
            }
        }

        animatorSet.duration = animationDuration
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.start()

        completeAnimation(nextIndex)
    }

    private fun performSlideAnimation(nextIndex: Int) {
        val slideOut = ObjectAnimator.ofFloat(mainImageView, "translationX", 0f, -width.toFloat())
        val slideIn = ObjectAnimator.ofFloat(nextImageView, "translationX", width.toFloat(), 0f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(slideOut, slideIn)
        animatorSet.duration = animationDuration
        animatorSet.start()

        completeAnimation(nextIndex)
    }

    private fun performFadeAnimation(nextIndex: Int) {
        val fadeOut = ObjectAnimator.ofFloat(mainImageView, "alpha", 1f, 0f)
        val fadeIn = ObjectAnimator.ofFloat(nextImageView, "alpha", 0f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(fadeOut, fadeIn)
        animatorSet.duration = animationDuration
        animatorSet.start()

        completeAnimation(nextIndex)
    }

    private fun performZoomAnimation(nextIndex: Int) {
        val zoomOut = ObjectAnimator.ofFloat(mainImageView, "scaleX", 1f, 0.8f)
        val zoomOutY = ObjectAnimator.ofFloat(mainImageView, "scaleY", 1f, 0.8f)
        val zoomIn = ObjectAnimator.ofFloat(nextImageView, "scaleX", 0.8f, 1f)
        val zoomInY = ObjectAnimator.ofFloat(nextImageView, "scaleY", 0.8f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(zoomOut, zoomOutY, zoomIn, zoomInY)
        animatorSet.duration = animationDuration
        animatorSet.start()

        completeAnimation(nextIndex)
    }

    private fun completeAnimation(nextIndex: Int) {
        handler.postDelayed({
            // Update main image view
            if (imageUrls.isNotEmpty()) {
                loadImageIntoView(mainImageView, imageUrls[nextIndex])
            } else {
                mainImageView.setImageResource(imageResources[nextIndex])
            }

            // Reset views
            resetViewProperties()
            nextImageView.visibility = View.INVISIBLE

            currentImageIndex = nextIndex
            onCubeAnimationListener?.invoke(nextIndex)
        }, animationDuration)
    }

    private fun resetViewProperties() {
        mainImageView.rotationX = 0f
        mainImageView.rotationY = 0f
        mainImageView.scaleX = 1f
        mainImageView.scaleY = 1f
        mainImageView.translationX = 0f
        mainImageView.translationY = 0f
        mainImageView.alpha = 1f

        nextImageView.rotationX = 0f
        nextImageView.rotationY = 0f
        nextImageView.scaleX = 1f
        nextImageView.scaleY = 1f
        nextImageView.translationX = 0f
        nextImageView.translationY = 0f
        nextImageView.alpha = 1f
    }

    private fun loadImageIntoView(imageView: ImageView, url: String) {
        try {
            val requestOptions = RequestOptions().apply {
                if (placeholderResId != 0) placeholder(placeholderResId)
                if (errorResId != 0) error(errorResId)
                if (!enableCache) diskCacheStrategy(DiskCacheStrategy.NONE)
                scaleType(imageScaleType)
            }

            Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        val exception = e ?: Exception("Unknown error")
                        onImageErrorListener?.invoke(currentImageIndex, url, exception)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        onImageLoadListener?.invoke(currentImageIndex, url)
                        return false
                    }
                })
                .into(imageView)
        } catch (e: Exception) {
            Log.e("CubeAnimation", "Error loading image: ${e.message}")
            onImageErrorListener?.invoke(currentImageIndex, url, e)
        }
    }

    private fun hasMultipleImages(): Boolean {
        return getImageCount() > 1
    }

    private fun updateButtonPosition() {
        val params = loginButton.layoutParams as LayoutParams

        when (buttonPosition) {
            BUTTON_POSITION_TOP_START -> {
                params.gravity = android.view.Gravity.START or android.view.Gravity.TOP
            }
            BUTTON_POSITION_TOP_END -> {
                params.gravity = android.view.Gravity.END or android.view.Gravity.TOP
            }
            BUTTON_POSITION_BOTTOM_START -> {
                params.gravity = android.view.Gravity.START or android.view.Gravity.BOTTOM
            }
            BUTTON_POSITION_BOTTOM_END -> {
                params.gravity = android.view.Gravity.END or android.view.Gravity.BOTTOM
            }
            BUTTON_POSITION_CENTER -> {
                params.gravity = android.view.Gravity.CENTER
            }
        }

        loginButton.layoutParams = params
    }

    private fun updateButtonAppearance() {
        loginButton.text = buttonText
        loginButton.setBackgroundColor(buttonBackgroundColor)
        loginButton.setTextColor(buttonTextColor)

        // Apply corner radius (requires MaterialButton or custom background)
        loginButton.elevation = buttonElevation
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAutoRotation()
    }
}