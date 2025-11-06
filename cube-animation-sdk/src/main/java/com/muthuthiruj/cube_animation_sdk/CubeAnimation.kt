package com.muthuthiruj.cube_animation_sdk


import android.view.animation.Transformation

open class CubeAnimation : ViewPropertyAnimation {

    companion object {
        const val DIRECTION_LEFT = 1
        const val DIRECTION_RIGHT = 2
        const val DIRECTION_UP = 3
        const val DIRECTION_DOWN = 4

        fun create(direction: Int, enter: Boolean, duration: Long): CubeAnimation {
            return when (direction) {
                DIRECTION_UP, DIRECTION_DOWN -> VerticalCubeAnimation(direction, enter, duration)
                else -> HorizontalCubeAnimation(direction, enter, duration)
            }
        }
    }

    protected val direction: Int
    protected val enter: Boolean

    protected constructor(direction: Int, enter: Boolean, duration: Long) : super() {
        this.direction = direction
        this.enter = enter
        setDuration(duration)
    }

    private class VerticalCubeAnimation(
        direction: Int,
        enter: Boolean,
        duration: Long
    ) : CubeAnimation(direction, enter, duration) {

        override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
            super.initialize(width, height, parentWidth, parentHeight)
            pivotX = width * 0.5f
            pivotY = if (enter == (direction == DIRECTION_UP)) 0.0f else height.toFloat()
            cameraZ = -height * 0.015f
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val value = if (enter) interpolatedTime - 1.0f else interpolatedTime
            val adjustedValue = if (direction == DIRECTION_DOWN) value * -1.0f else value
            rotationX = adjustedValue * 90.0f
            translationY = -adjustedValue * height

            super.applyTransformation(interpolatedTime, t)
            applyTransformation(t)
        }
    }

    private class HorizontalCubeAnimation(
        direction: Int,
        enter: Boolean,
        duration: Long
    ) : CubeAnimation(direction, enter, duration) {

        override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
            super.initialize(width, height, parentWidth, parentHeight)
            pivotX = if (enter == (direction == DIRECTION_LEFT)) 0.0f else width.toFloat()
            pivotY = height * 0.5f
            cameraZ = -width * 0.015f
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val value = if (enter) interpolatedTime - 1.0f else interpolatedTime
            val adjustedValue = if (direction == DIRECTION_RIGHT) value * -1.0f else value
            rotationY = -adjustedValue * 90.0f
            translationX = -adjustedValue * width

            super.applyTransformation(interpolatedTime, t)
            applyTransformation(t)
        }
    }
}