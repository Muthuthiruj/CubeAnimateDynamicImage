package com.muthuthiruj.cube_animation_sdk

fun interface OnImageLoadListener {
    fun onImageLoad(index: Int, image: Any?)
}

fun interface OnImageErrorListener {
    fun onImageError(index: Int, image: Any?, exception: Exception)
}

fun interface OnAnimationCompleteListener {
    fun onAnimationComplete(currentIndex: Int)
}

fun interface OnSwipeListener {
    fun onSwipe(direction: Int)
}

fun interface OnImageClickListener {
    fun onImageClick(index: Int, image: Any?, allImages: List<Any>?)
}