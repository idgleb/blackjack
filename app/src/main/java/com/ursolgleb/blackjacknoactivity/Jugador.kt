package com.ursolgleb.blackjacknoactivity

import android.animation.ValueAnimator
import android.app.Activity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom

class Jugador(val nombre: String, var isCrupier: Boolean) {

    val context = BlackJackActivity.obtenerContexto()
    val MIN_PUNTOS_PARA_DOBLAR: Int = 10
    val MAX_PUNTOS_PARA_DOBLAR: Int = 15
    val MAX_CANTIDAD_CARTAS: Int = 12
    val APUESTO_MIN: Int = 100
    val APUESTO_MAX: Int = 2000
    var balanceJugador: Double = 3000.0
    var apuestoJugador: Double = 0.0
    val cartasQueTiene = mutableListOf<Carta>()

    fun addApuesto(apuestoQuierePonel: Double): String {
        if (apuestoQuierePonel <= 0) {
            return "apuestoQuierePonel no puede ser menor o igual que 0"
        }
        if (apuestoQuierePonel > balanceJugador) {
            return "No hay suficiente dinero"
        }
        if ((apuestoJugador + apuestoQuierePonel) > APUESTO_MAX) {
            return "Apuesta maxima $APUESTO_MAX"
        }
        balanceJugador -= apuestoQuierePonel
        apuestoJugador += apuestoQuierePonel
        return "true"
    }

    fun agregarCarta(carta: Carta, duracion: Long): ValueAnimator? {
        var (X: Float, Y: Float) = XYproxCarta(carta)
        carta.view.bringToFront()
        BlackJackActivity.instance.jPanelFichasEnApuesto.bringToFront()
        BlackJackActivity.instance.jPanelFichasEnCrupier.bringToFront()
        return carta.mover(X, Y, duracion)
    }

    fun XYproxCarta(carta: Carta): Pair<Float, Float> {
        val labelPuntosJugador = BlackJackActivity.instance.labelPuntosJugador
        val labelPuntosCrupier = BlackJackActivity.instance.labelPuntosCrupier
        var coordX: Float
        var coordY: Float
        if (!isCrupier){
             coordX = (labelPuntosJugador.x + (cartasQueTiene.size) * carta.view.width / 2.5).toFloat()
             coordY = labelPuntosJugador.y - carta.view.height - (cartasQueTiene.size) * carta.view.height / 4
        }else{
            if (cartasQueTiene.isEmpty()){
                coordX = (labelPuntosCrupier.x + labelPuntosCrupier.width*2).toFloat()
            }else{
                coordX = (cartasQueTiene.last().getX() + carta.view.width / 2.5).toFloat()
            }
            coordY = labelPuntosCrupier.y
        }
        return Pair(coordX, coordY)
    }

    fun sumaPuntos(): Int {
        var puntos = 0
        for (carta in cartasQueTiene) {
            if (carta.valor == 11 && (puntos + carta.valor) > 21) {
                puntos++;
            } else puntos += carta.valor;
        }
        return puntos
    }

    fun robarCarta(): Carta {
        if (cartasQueTiene.isEmpty()) {
            throw NoSuchElementException("No hay mas cartas el Jugador")
        }
        return cartasQueTiene.removeAt(0)
    }
}