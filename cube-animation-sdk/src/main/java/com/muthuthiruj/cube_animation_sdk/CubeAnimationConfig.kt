package com.muthuthiruj.cube_animation_sdk

data class CubeAnimationConfig(
    var autoRotate: Boolean = true,
    var rotationInterval: Long = 3000,
    var animationDuration: Long = 500,
    var swipeEnabled: Boolean = true,
    var cubeDepth: Float = 0.015f,
    var imageCaching: Boolean = true,
    var loopImages: Boolean = true,
    var pingPongMode: Boolean = true // Forward/backward sequencing
)

enum class AnimationType {
    CUBE_3D, SLIDE
}