package com.ursolgleb.blackjacknoactivity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout




class BarajaDeCartas(var BlackJackActivity: BlackJackActivity, x: Float, y: Float, anchuraCarta:Int) {

    var anchuraCarta = anchuraCarta

    private val nombresCartas = listOf("as", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jota", "reina", "rey")
    private val valoresCartas = listOf(11, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10)
    private val palosCartas = listOf("corazon", "diamante", "trebol", "pica")

    var cartas: MutableList<Carta> = mutableListOf()

    private var xBaraja: Float = 0.0f
    private var yBaraja: Float = 0.0f
    private var rotation = 0f
    private var desplazoX: Int = 0
    private var desplazoY: Int = 0
    val MIN_CARTAS: Int = 30

    init {
        iniciar(x, y)
    }

    private fun iniciar(x: Float, y: Float) {
        val frameLayout = BlackJackActivity.findViewById<FrameLayout>(R.id.main)
        BlackJackActivity.viewModel.cartas.forEach {
            frameLayout.removeView(it.view)
        }
        BlackJackActivity.viewModel.cartas.clear()


        palosCartas.forEach {

            nombresCartas.forEachIndexed { index, nombre ->

                val textView = TextView(BlackJackActivity)

                val carta = Carta(BlackJackActivity, nombre, valoresCartas[index], it, textView)
                BlackJackActivity.viewModel.cartas.add(carta)
                //cartas.add(Carta(BlackJackActivity, nombre, valoresCartas[index], it, textView))

                BlackJackActivity.viewModel.cartas.last().setCoordYRotacion(
                    x - (BlackJackActivity.viewModel.cartas.size * 50).toFloat(),
                    y + (BlackJackActivity.viewModel.cartas.size * 50).toFloat(),
                    -60f
                )

                textView.text = BlackJackActivity.viewModel.cartas.last().background()
                textView.textSize = 0f

                val drawableName = BlackJackActivity.viewModel.cartas.last().background()
                val resourceId =
                    BlackJackActivity.resources.getIdentifier(
                        drawableName,
                        "drawable",
                        BlackJackActivity.packageName
                    )
                textView.setBackgroundResource(resourceId)

                frameLayout.addView(textView)

                val layoutParams = textView.layoutParams
                layoutParams.width = anchuraCarta
                layoutParams.height = (1.46*layoutParams.width).toInt()
                textView.layoutParams = layoutParams

            }
        }

        cartas = BlackJackActivity.viewModel.cartas
        barajar(x, y, -60f, 50, 50)
    }

    fun setCoordYRotacionYDesplazo(x: Float, y: Float, rot: Float, desplX: Int, desplY: Int) {
        this.xBaraja = x
        this.yBaraja = y
        this.rotation = rot
        this.desplazoX = desplX
        this.desplazoY = desplY
        BlackJackActivity.viewModel.cartas.asReversed().forEachIndexed { ind, carta ->
            carta.view.bringToFront()
            val layoutTop = BlackJackActivity.findViewById<ConstraintLayout>(R.id.layoutTop)
            carta.setCoordYRotacion(
                xBaraja - (ind * desplazoX).toFloat(),
                yBaraja + (ind * desplazoY).toFloat(),
                rotation
            )
        }
    }

    fun getX() = xBaraja
    fun getY() = yBaraja
    fun getRotacion() = rotation
    fun getdesplazoX() = desplazoX
    fun getdesplazoY() = desplazoY


    fun barajar(x: Float, y: Float, rot: Float, desplazoX: Int, desplazoY: Int) {
        this.cartas.shuffle()
        setCoordYRotacionYDesplazo(x, y, rot, desplazoX, desplazoY)
    }

    fun robarCarta(): Carta? {

        if (cartas.isEmpty()) {
            BlackJackActivity.soundPool.play(BlackJackActivity.soundIdpop, 1f, 1f, 1, 0, 1f)
            BlackJackActivity.mostrarMsg("No hay mas cartas en la baraja!!!", Color.GRAY)
            return null
        }
        return cartas.removeAt(0)

    }

    fun repartir(
        listaJugdores: MutableList<Jugador>,
        cantidadCartasParaCadaJugador: Int,
        duracion: Long
    ) {

        val cantidadJugadores = listaJugdores.size
        var iteraciones = cantidadJugadores * cantidadCartasParaCadaJugador
        var turno = 0
        fun ciclo() {

            val jugador = listaJugdores[turno++]
            if (turno == cantidadJugadores) turno = 0

            robarCarta()?.let {
                jugador.agregarCarta(it, duracion)?.addListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        //
                        jugador.cartasQueTiene.add(it)
                        if (!(jugador.isCrupier && jugador.cartasQueTiene.size==2))it.abrirCerarCatra(BlackJackActivity.duracionAbrirCartas)

                        iteraciones--
                        if (iteraciones > 0) {
                            ciclo()
                        } else {
                            BlackJackActivity.viewModel.isRepartirPrincipal = BlackJackActivity.pasosSiHayBlackJack()
                        }
                    }
                })
            }

        }
        ciclo()

    }

}