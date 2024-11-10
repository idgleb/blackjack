package com.ursolgleb.blackjacknoactivity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.widget.TextView

data class Carta(
    var BlackJackActivity: BlackJackActivity,
    val nombre: String,
    val valor: Int,
    val palo: String,
    val view: TextView,
) {
    private var abierta: Boolean = false
    private var xCarta: Float = 0.0f
    private var yCarta: Float = 0.0f
    private var rotation = 0f

    fun mover(newX:Float,newY:Float, duracion: Long): ValueAnimator? {
        //??
        BlackJackActivity.soundPool.play(BlackJackActivity.soundIdflotar, 1f, 1f, 1, 0, 1f)
        val viewToAnimate = view
        val originalX = viewToAnimate.translationX
        val originalY = viewToAnimate.translationY
        val animX = ValueAnimator.ofFloat(originalX, newX)
        val animY = ValueAnimator.ofFloat(originalY, newY)
        animX.duration = duracion
        animY.duration = duracion
        animX.addUpdateListener {valAnim -> viewToAnimate.translationX = valAnim.animatedValue as Float}
        animY.addUpdateListener {valAnim -> viewToAnimate.translationY =valAnim.animatedValue as Float}
        animX.start()
        animY.start()
        animY.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                setCoordYRotacion(newX,newY,0f)
                //
            }
        })
        setCoordYRotacion(getX(),getY(),0f)
        return animY
    }

    fun abrirCerarCatra(duracion:Long) {

        BlackJackActivity.soundPool.play(BlackJackActivity.soundIdabrir, 1f, 1f, 1, 0, 1f)

        abierta = !abierta

        val viewToAnimate = view
        val newWidth = 0
        val originalWidth = viewToAnimate.width

        val anim = ValueAnimator.ofInt(viewToAnimate.width, newWidth)
        anim.addUpdateListener { valueAnimator ->
            val lp = viewToAnimate.layoutParams
            lp.width = valueAnimator.animatedValue as Int
            viewToAnimate.layoutParams = lp
        }
        anim.duration = duracion

        // Crear la animación para el cambio de posición horizontal (translationX)
        val initialPosX = viewToAnimate.translationX
        val targetPosX =
            initialPosX + viewToAnimate.width/2// Define la posición horizontal a la que quieres animar

        val animTranslationX = ValueAnimator.ofFloat(initialPosX, targetPosX)
        animTranslationX.addUpdateListener { valueAnimator ->
            viewToAnimate.translationX = valueAnimator.animatedValue as Float
        }
        animTranslationX.duration = duracion

        val animTranslationX2 = ValueAnimator.ofFloat(targetPosX, initialPosX)
        animTranslationX2.addUpdateListener { valueAnimator ->
            viewToAnimate.translationX = valueAnimator.animatedValue as Float
        }
        animTranslationX2.duration = duracion

        val reverseAnim = ValueAnimator.ofInt(newWidth, originalWidth)
        reverseAnim.addUpdateListener { valueAnimator ->
            val lp = viewToAnimate.layoutParams
            lp.width = valueAnimator.animatedValue as Int
            viewToAnimate.layoutParams = lp
        }
        reverseAnim.duration = duracion

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(anim, animTranslationX)
        animatorSet.start()

        val animatorSet2 = AnimatorSet()

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Cambiar la imagen de la vista al final de la primera animación

                view.text = background()
                view.textSize = 0f
                val drawableName = background()
                val resourceId = BlackJackActivity.resources.getIdentifier(drawableName, "drawable", BlackJackActivity.packageName)
                view.setBackgroundResource(resourceId)

                //viewToAnimate.setBackgroundResource(R.drawable.f500)

                // Iniciar la animación de reversa
                animatorSet2.playTogether(reverseAnim, animTranslationX2)
                animatorSet2.start()

            }
        })

    }

    fun isAbierta() = abierta

    fun setCoordYRotacion(x: Float, y: Float, rot: Float) {
        xCarta = x
        yCarta = y
        rotation = rot
        view.translationX = xCarta
        view.translationY = yCarta
        view.rotation = rotation
    }
    fun getX() = xCarta
    fun getY() = yCarta
    fun getRotacion() = rotation

    fun background(): String = when (abierta) {
        true -> palo + nombre
        false -> CART_CUBIERTA_PNG
        else -> ""
    }


}
