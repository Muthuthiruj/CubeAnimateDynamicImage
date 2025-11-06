package com.Muthuthiruj.cubeanimatingsdk

import android.graphics.Camera
import android.os.Build
import android.view.animation.Animation
import android.view.animation.Transformation

open class ViewPropertyAnimation : Animation() {

    private val camera = Camera()
    protected var width = 0
    protected var height = 0
    protected var alpha = 1.0f
    protected var pivotX = 0.0f
    protected var pivotY = 0.0f
    protected var scaleX = 1.0f
    protected var scaleY = 1.0f
    protected var rotationX = 0.0f
    protected var rotationY = 0.0f
    protected var rotationZ = 0.0f
    protected var translationX = 0.0f
    protected var translationY = 0.0f
    protected var translationZ = 0.0f
    protected var cameraX = 0.0f
    protected var cameraY = 0.0f
    protected var cameraZ = -8.0f

    private var fromAlpha = -1.0f
    private var toAlpha = -1.0f

    fun fading(fromAlpha: Float, toAlpha: Float): ViewPropertyAnimation {
        this.fromAlpha = fromAlpha
        this.toAlpha = toAlpha
        return this
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        this.width = width
        this.height = height
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        super.applyTransformation(interpolatedTime, t)
        if (fromAlpha >= 0 && toAlpha >= 0) {
            alpha = fromAlpha + (toAlpha - fromAlpha) * interpolatedTime
        }
    }

    protected fun applyTransformation(t: Transformation) {
        val matrix = t.matrix
        val w = width.toFloat()
        val h = height.toFloat()
        val pX = pivotX
        val pY = pivotY

        if (rotationX != 0f || rotationY != 0f || rotationZ != 0f) {
            camera.save()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                camera.setLocation(cameraX, cameraY, cameraZ)
            }
            if (translationZ != 0f) {
                camera.translate(0f, 0f, translationZ)
            }
            camera.rotateX(rotationX)
            camera.rotateY(rotationY)
            camera.rotateZ(-rotationZ)
            camera.getMatrix(matrix)
            camera.restore()
            matrix.preTranslate(-pX, -pY)
            matrix.postTranslate(pX, pY)
        }

        if (scaleX != 1.0f || scaleY != 1.0f) {
            matrix.postScale(scaleX, scaleY)
            val sPX = -(pX / w) * ((scaleX * w) - w)
            val sPY = -(pY / h) * ((scaleY * h) - h)
            matrix.postTranslate(sPX, sPY)
        }

        matrix.postTranslate(translationX, translationY)
        t.alpha = alpha
    }
}