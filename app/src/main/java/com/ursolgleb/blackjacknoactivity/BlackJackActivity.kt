package com.ursolgleb.blackjacknoactivity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isNotEmpty
import com.google.android.material.snackbar.Snackbar
import viewmodels.JuegoViewModel
import kotlin.properties.Delegates

private var iterDeRenuevo = 0

private var alturaFicha = 0
private var anchuraFicha = 0

private val colorPierdes = Color.parseColor("#710001")
private val colorGanas = Color.parseColor("#007100")
val colorComun = Color.parseColor("#431500")
private val colorEmpate = Color.parseColor("#002f40")

private const val dispX = 3
private const val dispY = 3
private const val yBaraja = -140f
private const val rotBaraja = -70f
private const val compensadoXBaraja = 180

private const val dispYfichas = 7
private const val candFichEnCol = 15

class BlackJackActivity : AppCompatActivity() {

    var anchuraCarta = 0

    companion object {
        lateinit var instance: BlackJackActivity
        fun obtenerContexto(): Context {
            return instance
        }
    }


    val viewModel: JuegoViewModel by viewModels() // Usando delegation para obtener el ViewModel

    val duracionFlotarCartas = 400L
    val duracionAbrirCartas = 140L
    val duracionFicas = 2000L

    var coeficienteDeGanancia = 1.0

    lateinit var mediaPlayer: MediaPlayer

    lateinit var soundPool: SoundPool
    var soundIdtupierdes: Int = 0
    var soundIdtuganas: Int = 0
    var soundIdsacarfichas: Int = 0
    var soundIdpop: Int = 0
    var soundIdpierdes2: Int = 0
    var soundIdnuevabarajavoz: Int = 0
    var soundIdnuevabaraja2: Int = 0
    var soundIdganasfichas2: Int = 0
    var soundIdganasfichas: Int = 0
    var soundIdflotar: Int = 0
    var soundIdempate: Int = 0
    var soundIdblackjack: Int = 0
    var soundIdapostar: Int = 0
    var soundIdabrir: Int = 0

    lateinit var main: FrameLayout
    lateinit var jPanelFichasEnApuesto: FrameLayout
    lateinit var jPanelFichasEnCrupier: FrameLayout
    lateinit var imgFichasCrupier: ImageView
    lateinit var barajaLeft: ImageView
    lateinit var linButApuesto: LinearLayout
    lateinit var linia_botones: LinearLayout
    lateinit var labelApuesto: TextView
    lateinit var labelBalance: TextView
    lateinit var labelPuntosJugador: TextView
    lateinit var labelPuntosCrupier: TextView
    lateinit var labelCartasEnBaraja: TextView
    lateinit var butRepartir: Button

    lateinit var butMas: Button
    lateinit var butParar: Button
    lateinit var butDoblar: Button
    lateinit var botones_sacar: LinearLayout
    lateinit var layoutCenter: ConstraintLayout
    lateinit var imagen_apuesto: ImageView
    lateinit var butSacarTodas: Button
    lateinit var butSacarUna: Button
    lateinit var layoutTop: ConstraintLayout

    lateinit var popupWindow: PopupWindow


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_black_jack)
        instance = this

        iterDeRenuevo = 0

        fullScreen()

        main = findViewById(R.id.main)
        jPanelFichasEnApuesto = findViewById(R.id.jPanelFichasEnApuesto)
        jPanelFichasEnCrupier = findViewById(R.id.jPanelFichasEnCrupier)
        imgFichasCrupier = findViewById(R.id.imgFichasCrupier)
        barajaLeft = findViewById(R.id.barajaLeft)
        linButApuesto = findViewById(R.id.linButApuesto)
        linia_botones = findViewById(R.id.linia_botones)
        labelApuesto = findViewById(R.id.labelApuesto)
        labelBalance = findViewById(R.id.labelBalance)
        labelPuntosJugador = findViewById(R.id.labelPuntosJugador)
        labelPuntosCrupier = findViewById(R.id.labelPuntosCrupier)
        labelCartasEnBaraja = findViewById(R.id.labelCartasEnBaraja)
        butRepartir = findViewById(R.id.butRepartir)
        butMas = findViewById(R.id.butMas)
        butParar = findViewById(R.id.butParar)
        butDoblar = findViewById(R.id.butDoblar)
        botones_sacar = findViewById(R.id.botones_sacar)
        layoutCenter = findViewById(R.id.layoutCenter)
        imagen_apuesto = findViewById(R.id.imagen_apuesto)
        butSacarTodas = findViewById(R.id.butSacarTodas)
        butSacarUna = findViewById(R.id.butSacarUna)
        layoutTop = findViewById(R.id.layoutTop)


        // Inicializar el MediaPlayer y cargar el archivo de música de fondo
        mediaPlayer = MediaPlayer.create(this, R.raw.jazzfondo)
        mediaPlayer.isLooping = true  // Configurar para que la música se repita
        mediaPlayer.setVolume(0.2f, 0.2f)
        // Iniciar la música de fondo
        mediaPlayer.start()

        // Configurar SoundPool con atributos de audio
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(25)  // Número máximo de sonidos simultáneos
            .setAudioAttributes(audioAttributes)
            .build()
        // Cargar los archivos de sonido (en la carpeta res/raw)
        soundIdtupierdes = soundPool.load(this, R.raw.tupierdes, 1)
        soundIdtuganas = soundPool.load(this, R.raw.tuganas, 1)
        soundIdsacarfichas = soundPool.load(this, R.raw.sacarfichas, 1)
        soundIdpop = soundPool.load(this, R.raw.pop, 1)
        soundIdpierdes2 = soundPool.load(this, R.raw.pierdes2, 1)
        soundIdnuevabarajavoz = soundPool.load(this, R.raw.nuevabarajavoz, 1)
        soundIdnuevabaraja2 = soundPool.load(this, R.raw.nuevabaraja2, 1)
        soundIdganasfichas2 = soundPool.load(this, R.raw.ganasfichas2, 1)
        soundIdganasfichas = soundPool.load(this, R.raw.ganasfichas, 1)
        soundIdflotar = soundPool.load(this, R.raw.flotar, 1)
        soundIdempate = soundPool.load(this, R.raw.empate, 1)
        soundIdblackjack = soundPool.load(this, R.raw.blackjack, 1)
        soundIdapostar = soundPool.load(this, R.raw.apostar, 1)
        soundIdabrir = soundPool.load(this, R.raw.abrir, 1)


        var listaJugdores: MutableList<Jugador> =
            mutableListOf(viewModel.jugador1, viewModel.crupier)

        val butApuesto10: ImageButton = findViewById(R.id.butApuesto10)
        val butApuesto25: ImageButton = findViewById(R.id.butApuesto25)
        val butApuesto50: ImageButton = findViewById(R.id.butApuesto50)
        val butApuesto100: ImageButton = findViewById(R.id.butApuesto100)
        val butApuesto500: ImageButton = findViewById(R.id.butApuesto500)

        val popupView = layoutInflater.inflate(R.layout.popup_layout, null)
        val butReiniciar: Button = popupView.findViewById(R.id.butReiniciar)

        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            false
        )

        /////////

        try {
            viewModel.baraja.cartas.forEach {
                it.BlackJackActivity = instance
                // Obtener el parent actual de la vista
                val currentParent = it.view.parent as? ViewGroup

                // Eliminar la vista del parent actual si existe
                currentParent?.removeView(it.view)

                // Agregar la vista al nuevo contenedor "main"
                main.addView(it.view)
            }
        } catch (e: Exception) {
            // Manejar la excepción en caso de que ocurra
            e.printStackTrace()
            // Aquí puedes mostrar un mensaje de error o hacer alguna acción personalizada
            //Toast.makeText(this, "Error moviendo las vistas: ${e.message}", Toast.LENGTH_SHORT).show()
        }


        //////////


        var timerSiempre: CountDownTimer? = null
        fun startTimerSiempre(timeMillis: Long) {
            timerSiempre = object : CountDownTimer(timeMillis, 5) {
                override fun onTick(p0: Long) {

                    gameover()

                    val labelInfo: TextView = findViewById(R.id.labelInfo)
                    labelApuesto.text = "$ ${viewModel.jugador1.apuestoJugador}"
                    labelBalance.text = "Balance $${viewModel.jugador1.balanceJugador}"
                    labelPuntosJugador.text = "${viewModel.jugador1.sumaPuntos()}"
                    labelPuntosCrupier.text = "${viewModel.crupier.sumaPuntos()}"
                    labelCartasEnBaraja.text =
                        "${viewModel.baraja?.cartas?.size} cartas \nen baraja"
                    labelInfo.text =
                        "Apuesta: min:${viewModel.jugador1.APUESTO_MIN}, max:${viewModel.jugador1.APUESTO_MAX}"

                    if (viewModel.jugador1.sumaPuntos() == 0) {
                        labelPuntosJugador.visibility = View.INVISIBLE
                    } else labelPuntosJugador.visibility = View.VISIBLE

                    if (viewModel.crupier.sumaPuntos() == 0 ||
                        viewModel.crupier.cartasQueTiene.size >= 2 &&
                        !viewModel.crupier.cartasQueTiene.get(1).isAbierta()
                    ) {
                        labelPuntosCrupier.visibility = View.INVISIBLE
                    } else labelPuntosCrupier.visibility = View.VISIBLE

                    if (viewModel.jugador1.cartasQueTiene.isEmpty()
                        && !viewModel.isMas
                        && !viewModel.isTurnoCrupier
                        && !viewModel.isRepartirPrincipal
                        && !viewModel.isDoblar
                    ) {
                        butApuesto10.visibility = View.VISIBLE
                        butApuesto25.visibility = View.VISIBLE
                        butApuesto50.visibility = View.VISIBLE
                        butApuesto100.visibility = View.VISIBLE
                        butApuesto500.visibility = View.VISIBLE
                        butRepartir.visibility = View.VISIBLE
                    } else {
                        butApuesto10.visibility = View.INVISIBLE
                        butApuesto25.visibility = View.INVISIBLE
                        butApuesto50.visibility = View.INVISIBLE
                        butApuesto100.visibility = View.INVISIBLE
                        butApuesto500.visibility = View.INVISIBLE
                        butRepartir.visibility = View.INVISIBLE
                    }

                    if (viewModel.jugador1.cartasQueTiene.isEmpty()
                        && viewModel.jugador1.apuestoJugador != 0.0
                        && !viewModel.isMas
                        && !viewModel.isTurnoCrupier
                        && !viewModel.isRepartirPrincipal
                        && !viewModel.isDoblar
                    ) {
                        butSacarTodas.visibility = View.VISIBLE
                        butSacarUna.visibility = View.VISIBLE
                    } else {
                        butSacarTodas.visibility = View.INVISIBLE
                        butSacarUna.visibility = View.INVISIBLE
                    }

                    if (!viewModel.jugador1.cartasQueTiene.isEmpty()
                        && viewModel.jugador1.apuestoJugador != 0.0
                        && !viewModel.isMas
                        && !viewModel.isTurnoCrupier
                        && !viewModel.isRepartirPrincipal
                        && !viewModel.isDoblar
                    ) {
                        butMas.visibility = View.VISIBLE
                        butParar.visibility = View.VISIBLE
                    } else {
                        butMas.visibility = View.INVISIBLE
                        butParar.visibility = View.INVISIBLE
                    }

                    if (viewModel.jugador1.cartasQueTiene.size == 2
                        && viewModel.jugador1.apuestoJugador != 0.0
                        && viewModel.jugador1.sumaPuntos() <= viewModel.jugador1.MAX_PUNTOS_PARA_DOBLAR
                        && viewModel.jugador1.sumaPuntos() >= viewModel.jugador1.MIN_PUNTOS_PARA_DOBLAR
                        && !viewModel.isMas
                        && !viewModel.isTurnoCrupier
                        && !viewModel.isRepartirPrincipal
                        && !viewModel.isDoblar
                    ) {
                        butDoblar.visibility = View.VISIBLE
                    } else {
                        butDoblar.visibility = View.INVISIBLE
                    }

                }

                override fun onFinish() {

                    timerSiempre?.start()
                }
            }.start()
        }

        main.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            if ((iterDeRenuevo++) < 35) {
                setBaraja()
                setBotones()
                setCoordParaPanelesDeFichas()
                setFichasEnApuesta()
                startTimerSiempre(100000)
            }
        }

        main.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

            }
        })


        butApuesto10.setOnClickListener {
            addApuestoVisual(10.0, R.drawable.f10)
        }
        butApuesto25.setOnClickListener {
            addApuestoVisual(25.0, R.drawable.f25)

        }
        butApuesto50.setOnClickListener {
            addApuestoVisual(50.0, R.drawable.f50)

        }
        butApuesto100.setOnClickListener {
            addApuestoVisual(100.0, R.drawable.f100)
        }
        butApuesto500.setOnClickListener {
            addApuestoVisual(500.0, R.drawable.f500)
        }
        butSacarUna.setOnClickListener {

            soundPool.play(soundIdsacarfichas, 1f, 1f, 1, 0, 1f)

            val size = viewModel.labelsFichasEnApuesto.size
            viewModel.jugador1.balanceJugador += viewModel.labelsFichasEnApuesto.last().text.toString()
                .toDouble()
            viewModel.jugador1.apuestoJugador -= viewModel.labelsFichasEnApuesto.last().text.toString()
                .toDouble()
            if (jPanelFichasEnApuesto.isNotEmpty()) {
                jPanelFichasEnApuesto.removeViewAt(size - 1)
            }
            viewModel.labelsFichasEnApuesto.removeLast()
        }
        butSacarTodas.setOnClickListener {

            soundPool.play(soundIdsacarfichas, 1f, 1f, 1, 0, 1f)

            viewModel.jugador1.balanceJugador += viewModel.jugador1.apuestoJugador
            viewModel.jugador1.apuestoJugador = 0.0;
            jPanelFichasEnApuesto.removeAllViews();
            viewModel.labelsFichasEnApuesto.clear();
            jPanelFichasEnCrupier.removeAllViews();
        }


        butReiniciar.setOnClickListener {
            prepararMesaParaNuevosPuestos()
            viewModel.jugador1.balanceJugador = DEFAUL_BALANCE
            nuevaBaraja()
            popupWindow.dismiss()
        }

        butRepartir.setOnClickListener {
            butRepartir.visibility = View.INVISIBLE
            if (viewModel.jugador1.apuestoJugador < viewModel.jugador1.APUESTO_MIN) {

                soundPool.play(soundIdpop, 1f, 1f, 1, 0, 1f)

                mostrarMsg("Apuesta minima ${viewModel.jugador1.APUESTO_MIN}", colorComun)
            } else {
                viewModel.isRepartirPrincipal = true
                viewModel.baraja.repartir(listaJugdores, 2, duracionFlotarCartas)
            }
        }

        butMas.setOnClickListener {
            viewModel.isMas = true
            viewModel.baraja.robarCarta()?.let { it1 ->
                viewModel.jugador1.agregarCarta(it1, duracionFlotarCartas)
                    ?.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            //
                            viewModel.jugador1.cartasQueTiene.add(it1)
                            if (!(viewModel.jugador1.isCrupier && viewModel.jugador1.cartasQueTiene.size == 2)) it1.abrirCerarCatra(
                                duracionAbrirCartas
                            )

                            if (viewModel.jugador1.sumaPuntos() == 21) {
                                viewModel.isTurnoCrupier = true
                                timerTurnoCrupier()
                            } else if (viewModel.jugador1.sumaPuntos() > 21) {
                                resultarJuego(0.0, "Pierdes -${viewModel.jugador1.apuestoJugador}")
                            } else viewModel.isMas = false
                        }
                    })
            }
        }
        butParar.setOnClickListener {
            viewModel.isTurnoCrupier = true
            timerTurnoCrupier()
        }
        butDoblar.setOnClickListener {
            viewModel.isDoblar = true
            val jugador1 = viewModel.jugador1
            val resultDeTratarPonerApuesto = jugador1.addApuesto(jugador1.apuestoJugador)
            if (resultDeTratarPonerApuesto.toBoolean()) {

                soundPool.play(soundIdapostar, 1f, 1f, 1, 3, 2f)

                clonarFichasEnApuesto(jPanelFichasEnApuesto)

                viewModel.baraja.robarCarta()?.let { it1 ->
                    jugador1.agregarCarta(it1, duracionFlotarCartas)
                        ?.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                //
                                jugador1.cartasQueTiene.add(it1)
                                if (!(jugador1.isCrupier && jugador1.cartasQueTiene.size == 2)) it1.abrirCerarCatra(
                                    duracionAbrirCartas
                                )

                                if (jugador1.sumaPuntos() > 21) {
                                    resultarJuego(0.0, "Pierdes -${jugador1.apuestoJugador}")
                                } else {
                                    viewModel.isTurnoCrupier = true
                                    timerTurnoCrupier()
                                }

                            }
                        })
                }

            } else {

                soundPool.play(soundIdpop, 1f, 1f, 1, 0, 1f)

                mostrarMsg(resultDeTratarPonerApuesto, colorComun)
                viewModel.isDoblar = false
            }
        }

    }

    private fun gameover() {
        if (!popupWindow.isShowing && viewModel.jugador1.apuestoJugador == 0.0 && viewModel.jugador1.balanceJugador < 100) {
            popupWindow.showAtLocation(
                findViewById(android.R.id.content),
                Gravity.CENTER_VERTICAL,
                0,
                0
            )
        }
    }

    override fun onPause() {
        super.onPause()
        SharedApp.prefs.balanceJugador = viewModel.jugador1.balanceJugador.toFloat()
        mediaPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberar recursos del MediaPlayer
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        // Liberar recursos del SoundPool
        soundPool.release()
    }

    private fun setBotones() {

        if (main.width <= main.height) {
            alturaFicha = main.width / 10
            anchuraFicha = main.width / 10
        } else {
            alturaFicha = main.height / 14
            anchuraFicha = main.height / 14
        }

        labelPuntosCrupier.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (main.height >= main.width) {
                    val x = botones_sacar.x
                    val y = barajaLeft.y + barajaLeft.height
                    setCoord(labelPuntosCrupier, x, y)
                } else {
                    val x = botones_sacar.x
                    val y = barajaLeft.y + barajaLeft.height
                    setCoord(labelPuntosCrupier, x, y)
                }
                labelPuntosCrupier.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })


        labelPuntosJugador.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (main.height >= main.width) {
                    val x = botones_sacar.x
                    val y = botones_sacar.y + botones_sacar.height - labelPuntosJugador.height
                    setCoord(labelPuntosJugador, x, y)
                } else {
                    val x = botones_sacar.x
                    val y = botones_sacar.y + botones_sacar.height - labelPuntosJugador.height
                    setCoord(labelPuntosJugador, x, y)
                }
                labelPuntosJugador.bringToFront()
                labelPuntosJugador.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        botones_sacar.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (main.height >= main.width) {
                    butSacarTodas.textSize = main.width * 0.01f
                    butSacarUna.textSize = main.width * 0.01f
                    val widthBotSac = (main.width * 0.4).toInt()
                    val heightBotSac = (widthBotSac * 0.35).toInt()
                    setTamano(botones_sacar, widthBotSac, heightBotSac)
                    val x = imagen_apuesto.x - botones_sacar.width
                    val y = imagen_apuesto.y
                    setCoord(botones_sacar, x, y)
                } else {
                    butSacarTodas.textSize = main.height * 0.01f
                    butSacarUna.textSize = main.height * 0.01f
                    val widthBotSac = (main.width * 0.4).toInt()
                    val heightBotSac = (widthBotSac * 0.17).toInt()
                    setTamano(botones_sacar, widthBotSac, heightBotSac)
                    val x = imagen_apuesto.x - botones_sacar.width
                    val y = imagen_apuesto.y
                    setCoord(botones_sacar, x, y)
                }
                botones_sacar.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        var widthLinButApuesto: Int
        var heightLinButApuesto: Int
        var y: Float
        var x: Float

        linButApuesto.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (main.height >= main.width) {
                    widthLinButApuesto = main.width
                    heightLinButApuesto = (widthLinButApuesto / 5.6).toInt()
                    setTamano(linButApuesto, widthLinButApuesto, heightLinButApuesto)
                    y = (main.height - heightLinButApuesto).toFloat()
                    x = ((main.width - linButApuesto.width) / 2).toFloat()
                    setCoord(linButApuesto, x, y)
                } else {
                    widthLinButApuesto = (main.width * 0.5).toInt()
                    heightLinButApuesto = (widthLinButApuesto / 6.3).toInt()
                    setTamano(linButApuesto, widthLinButApuesto, heightLinButApuesto)
                    y = (main.height - heightLinButApuesto).toFloat()
                    x = ((main.width - linButApuesto.width) / 2).toFloat()
                    setCoord(linButApuesto, 0f, y)
                }
                linButApuesto.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        linia_botones.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                if (main.height >= main.width) {
                    //butRepartir.textSize = main.width*0.008f
                    //butMas.textSize = main.width*0.008f
                    //butParar.textSize = main.width*0.008f
                    //butDoblar.textSize = main.width*0.008f
                    val widthBotLin = (main.width).toInt()
                    val heightBotLin = (widthBotLin * 0.12).toInt()
                    setTamano(linia_botones, widthBotLin, heightBotLin)

                    val x = ((main.width - linia_botones.width) / 2).toFloat()
                    val y = linButApuesto.y - linia_botones.height
                    setCoord(linia_botones, x, y)
                } else {
                    //butRepartir.textSize = main.height*0.008f
                    //butMas.textSize = main.height*0.008f
                    //butParar.textSize = main.height*0.008f
                    //butDoblar.textSize = main.height*0.008f
                    val widthBotLin = (main.width * 0.5).toInt()
                    val heightBotLin = (widthBotLin * 0.15).toInt()
                    setTamano(linia_botones, widthBotLin, heightBotLin)

                    val x = linButApuesto.width.toFloat()
                    val y = linButApuesto.y
                    setCoord(linia_botones, x, y)
                }
                linia_botones.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        labelBalance.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (main.height >= main.width) {
                    labelBalance.textSize = main.width * 0.018f
                    val x = 0f
                    val y = linia_botones.y - labelBalance.height
                    setCoord(labelBalance, x, y)
                } else {
                    labelBalance.textSize = main.height * 0.018f
                    val x = 0f
                    val y = linia_botones.y - labelBalance.height
                    setCoord(labelBalance, x, y)
                }
                labelBalance.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        labelApuesto.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                if (main.height >= main.width) {
                    labelApuesto.textSize = main.width * 0.015f
                    val x = ((main.width - labelApuesto.width) / 2).toFloat()
                    val y = labelBalance.y - labelApuesto.height
                    setCoord(labelApuesto, x, y)
                } else {
                    labelApuesto.textSize = main.height * 0.015f
                    val x = ((main.width - labelApuesto.width) / 2).toFloat()
                    val y = labelBalance.y - labelApuesto.height
                    setCoord(labelApuesto, x, y)
                }
                labelApuesto.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        imagen_apuesto.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (main.height >= main.width) {
                    setTamano(
                        imagen_apuesto,
                        (anchuraFicha * 1.3).toInt(),
                        (anchuraFicha * 1.3).toInt()
                    )
                    val x = ((main.width - imagen_apuesto.width) / 2).toFloat()
                    val y = labelApuesto.y - imagen_apuesto.height
                    setCoord(imagen_apuesto, x, y)
                } else {
                    setTamano(
                        imagen_apuesto,
                        (anchuraFicha * 1.3).toInt(),
                        (anchuraFicha * 1.3).toInt()
                    )
                    val x = ((main.width - imagen_apuesto.width) / 2).toFloat()
                    val y = labelApuesto.y - imagen_apuesto.height
                    setCoord(imagen_apuesto, x, y)
                }
                imagen_apuesto.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        layoutCenter.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                if (main.height >= main.width) {
                    val heightLayoutCenter =
                        main.height - imagen_apuesto.height - labelApuesto.height - labelBalance.height - linia_botones.height - linButApuesto.height - imgFichasCrupier.height
                    setTamano(layoutCenter, main.width, heightLayoutCenter)
                    val x = 0f
                    val y = imagen_apuesto.y - layoutCenter.height
                    setCoord(layoutCenter, x, y)
                } else {
                    val heightLayoutCenter =
                        main.height - imagen_apuesto.height - labelApuesto.height - labelBalance.height - linButApuesto.height - imgFichasCrupier.height
                    setTamano(layoutCenter, main.width, heightLayoutCenter)
                    val x = 0f
                    val y = imagen_apuesto.y - layoutCenter.height
                    setCoord(layoutCenter, x, y)
                }
                layoutCenter.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        imgFichasCrupier.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (main.height >= main.width) {
                    val widthImgFichCrup = (main.width * 0.5).toInt()
                    val heightImgFichCrup = (widthImgFichCrup * 0.4).toInt()
                    setTamano(imgFichasCrupier, widthImgFichCrup, heightImgFichCrup)
                    val x = ((main.width - imgFichasCrupier.width) / 2).toFloat()
                    val y = 0f
                    setCoord(imgFichasCrupier, x, y)
                } else {
                    val widthImgFichCrup = (main.height * 0.5).toInt()
                    val heightImgFichCrup = (widthImgFichCrup * 0.4).toInt()
                    setTamano(imgFichasCrupier, widthImgFichCrup, heightImgFichCrup)
                    val x = ((main.width - imgFichasCrupier.width) / 2).toFloat()
                    val y = 0f
                    setCoord(imgFichasCrupier, x, y)
                }
                imgFichasCrupier.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        barajaLeft.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (main.height >= main.width) {
                    val widthBarajaLeft = (main.width * 0.2).toInt()
                    val heightBarajaLeft = (widthBarajaLeft * 1).toInt()
                    setTamano(barajaLeft, widthBarajaLeft, heightBarajaLeft)
                    val x = imgFichasCrupier.x - barajaLeft.width
                    val y = imgFichasCrupier.y
                    setCoord(barajaLeft, x, y)
                } else {
                    val widthBarajaLeft = (main.height * 0.2).toInt()
                    val heightBarajaLeft = (widthBarajaLeft * 1).toInt()
                    setTamano(barajaLeft, widthBarajaLeft, heightBarajaLeft)
                    val x = imgFichasCrupier.x - barajaLeft.width
                    val y = imgFichasCrupier.y
                    setCoord(barajaLeft, x, y)
                }
                barajaLeft.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })


    }

    private fun setCoordParaPanelesDeFichas() {

        jPanelFichasEnApuesto.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (main.height >= main.width) {
                    val x = (main.width / 2 - anchuraFicha / 2).toFloat()
                    val y =
                        (imagen_apuesto.y + imagen_apuesto.height * 0.9 - jPanelFichasEnApuesto.height).toFloat()
                    setCoord(jPanelFichasEnApuesto, x, y)
                } else {
                    val x = (main.width / 2 - anchuraFicha / 2).toFloat()
                    val y =
                        (imagen_apuesto.y + imagen_apuesto.height * 0.9 - jPanelFichasEnApuesto.height).toFloat()
                    setCoord(jPanelFichasEnApuesto, x, y)
                }
                jPanelFichasEnApuesto.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        jPanelFichasEnCrupier.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val x2 = 0f
                val y2 = (-jPanelFichasEnCrupier.height / 2).toFloat()
                setCoord(jPanelFichasEnCrupier, x2, y2)
                jPanelFichasEnCrupier.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

    }


    private fun setBaraja() {
        if (main.width <= main.height) {
            anchuraCarta = main.width / 6
        } else {
            anchuraCarta = (main.height / 8).toInt()
        }
        if (!viewModel.isInicaadaBaraja) {
            viewModel.baraja = BarajaDeCartas(instance, 0f, 0f, anchuraCarta)
            viewModel.isInicaadaBaraja = true
        }

        viewModel.jugador1.cartasQueTiene.forEachIndexed { index, it ->
            it.BlackJackActivity = instance
            // Obtener el parent actual de la vista
            val currentParent = it.view.parent as? ViewGroup

            // Eliminar la vista del parent actual si existe
            currentParent?.removeView(it.view)

            // Agregar la vista al nuevo contenedor "main"
            main.addView(it.view)

            val layoutParams = it.view.layoutParams
            //layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams.width = anchuraCarta
            layoutParams.height = (1.46 * layoutParams.width).toInt()
            it.view.layoutParams = layoutParams

            var coordX = (labelPuntosJugador.x + index * it.view.width / 2.5).toFloat()
            var coordY = labelPuntosJugador.y - it.view.height - index * it.view.height / 4

            it.setCoordYRotacion(coordX, coordY, 0f)
        }

        viewModel.crupier.cartasQueTiene.forEachIndexed { index, it ->
            it.BlackJackActivity = instance
            // Obtener el parent actual de la vista
            val currentParent = it.view.parent as? ViewGroup

            // Eliminar la vista del parent actual si existe
            currentParent?.removeView(it.view)

            // Agregar la vista al nuevo contenedor "main"
            main.addView(it.view)

            val layoutParams = it.view.layoutParams
            layoutParams.width = anchuraCarta
            layoutParams.height = (1.46 * layoutParams.width).toInt()
            it.view.layoutParams = layoutParams

            var coordX: Float
            var coordY: Float

            if (index == 0) {
                coordX = (labelPuntosCrupier.x + labelPuntosCrupier.width * 2).toFloat()
            } else {
                coordX = (viewModel.crupier.cartasQueTiene.get(index - 1)
                    .getX() + it.view.width / 2.5).toFloat()
            }
            coordY = labelPuntosCrupier.y

            it.setCoordYRotacion(coordX, coordY, 0f)
        }

        viewModel.baraja.cartas.forEachIndexed { index, it ->
            val layoutParams = it.view.layoutParams
            layoutParams.width = anchuraCarta
            layoutParams.height = (1.46 * layoutParams.width).toInt()
            it.view.layoutParams = layoutParams
        }

        viewModel.baraja.BlackJackActivity = instance


        viewModel.baraja?.setCoordYRotacionYDesplazo(
            (main.width / 2 + imgFichasCrupier.width / 2 + compensadoXBaraja).toFloat(),
            yBaraja,
            rotBaraja,
            dispX,
            dispY
        )

    }

    private fun setFichasEnApuesta() {
        val size = viewModel.labelsFichasEnApuesto.size
        for (i in 0 until size) {
            val textView = viewModel.labelsFichasEnApuesto.get(i)
            val parent = textView.parent as? ViewGroup
            parent?.removeView(textView)
            val layoutParamsView = textView.layoutParams
            layoutParamsView.height = alturaFicha
            layoutParamsView.width = anchuraFicha
            textView.layoutParams = layoutParamsView
            val y: Int =
                jPanelFichasEnApuesto.height - layoutParamsView.height - (i) % candFichEnCol * dispYfichas
            val x: Int = layoutParamsView.width * ((i) / candFichEnCol)
            setCoord(textView, x.toFloat(), y.toFloat())
            jPanelFichasEnApuesto.addView(textView)
        }
    }

    private fun fullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Pantalla completa para versiones anteriores a API 30
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        }

    }

    private fun clonarFichasEnApuesto(panel: FrameLayout) {
        val size = viewModel.labelsFichasEnApuesto.size
        for (i in 0 until size) {
            val textView = TextView(this)
            textView.textSize = 0f
            viewModel.labelsFichasEnApuesto.add(textView)
            panel.addView(textView)
            textView.layoutParams = viewModel.labelsFichasEnApuesto.get(i).layoutParams
            textView.background = viewModel.labelsFichasEnApuesto.get(i).background
            textView.text = viewModel.labelsFichasEnApuesto.get(i).text
            textView.translationY =
                viewModel.labelsFichasEnApuesto.get(i).translationY - alturaFicha - dispYfichas * candFichEnCol
            textView.translationX =
                viewModel.labelsFichasEnApuesto.get(i).translationX + viewModel.labelsFichasEnApuesto.get(
                    i
                ).width / 2
        }
    }

    fun pasosSiHayBlackJack(): Boolean {
        if (viewModel.jugador1.sumaPuntos() == 21 && viewModel.crupier.sumaPuntos() == 21) {
            resultarJuego(1.0, "Empate")
            return true
        }
        if (viewModel.jugador1.sumaPuntos() == 21 && (viewModel.crupier.sumaPuntos() < 21 || viewModel.crupier.sumaPuntos() > 21)) {
            resultarJuego(2.5, "BlackJack! Ganas +${viewModel.jugador1.apuestoJugador * 2.5}")
            return true
        }
        return false
    }

    private fun resultarJuego(coefDeGanancia: Double, mensajeTitulo: String) {

        if (!viewModel.crupier.cartasQueTiene.get(1).isAbierta()) {
            viewModel.crupier.cartasQueTiene.get(1).abrirCerarCatra(duracionAbrirCartas)
        }

        coeficienteDeGanancia = coefDeGanancia
        if (coeficienteDeGanancia < 1) {

            soundPool.play(soundIdtupierdes, 1f, 1f, 1, 0, 1f)
            soundPool.play(soundIdpierdes2, 1f, 1f, 1, 0, 1f)

            timerTuPierdes(duracionFicas)
            mostrarMsg(mensajeTitulo, colorPierdes)
        }
        if (coeficienteDeGanancia > 1) {
            if (coeficienteDeGanancia == 2.5) {
                soundPool.play(soundIdblackjack, 1f, 1f, 1, 0, 1f)
                soundPool.play(soundIdganasfichas, 1f, 1f, 1, 0, 1f)
            } else {
                soundPool.play(soundIdtuganas, 1f, 1f, 1, 0, 1f)
                soundPool.play(soundIdganasfichas2, 1f, 1f, 1, 0, 1f)
            }
            clonarFichasEnApuesto(jPanelFichasEnCrupier)
            timerTuGanasOSorteo(duracionFicas)
            mostrarMsg(mensajeTitulo, colorGanas)
        }
        if (coeficienteDeGanancia == 1.0) {
            soundPool.play(soundIdempate, 1f, 1f, 1, 0, 1f)
            timerTuGanasOSorteo(duracionFicas)
            mostrarMsg(mensajeTitulo, colorEmpate)
        }

    }

    fun timerTuPierdes(duracion: Long) {
        val viewToAnimate = findViewById<FrameLayout>(R.id.jPanelFichasEnApuesto)
        viewToAnimate.bringToFront()
        val originalX = viewToAnimate.translationX
        val originalY = viewToAnimate.translationY
        val newX = originalX
        val newY = -jPanelFichasEnApuesto.height.toFloat() + imgFichasCrupier.height
        val animX = ValueAnimator.ofFloat(originalX, newX)
        val animY = ValueAnimator.ofFloat(originalY, newY)
        animX.duration = duracion
        animY.duration = duracion
        animX.addUpdateListener { valAnim ->
            viewToAnimate.translationX = valAnim.animatedValue as Float
        }
        animY.addUpdateListener { valAnim ->
            viewToAnimate.translationY = valAnim.animatedValue as Float
        }
        animX.start()
        animY.start()
        animY.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)

                prepararMesaParaNuevosPuestos()


                //
            }
        })

    }

    fun timerTuGanasOSorteo(duracion: Long) {
        jPanelFichasEnCrupier.bringToFront()
        val originalX1 = jPanelFichasEnCrupier.translationX
        val originalY1 = jPanelFichasEnCrupier.translationY
        val newX1 = (main.width / 2).toFloat()
        val newY1 = (main.height - jPanelFichasEnCrupier.height / 2).toFloat()
        val animX1 = ValueAnimator.ofFloat(originalX1, newX1)
        val animY1 = ValueAnimator.ofFloat(originalY1, newY1)
        animX1.duration = duracion
        animY1.duration = duracion
        animX1.addUpdateListener { valAnim ->
            jPanelFichasEnCrupier.translationX = valAnim.animatedValue as Float
        }
        animY1.addUpdateListener { valAnim ->
            jPanelFichasEnCrupier.translationY = valAnim.animatedValue as Float
        }
        animX1.start()
        animY1.start()
        animY1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)

                //
            }
        })


        val viewToAnimate = findViewById<FrameLayout>(R.id.jPanelFichasEnApuesto)
        viewToAnimate.bringToFront()
        val originalX = viewToAnimate.translationX
        val originalY = viewToAnimate.translationY
        val newX = originalX
        val newY = (main.height - viewToAnimate.height).toFloat()
        val animX = ValueAnimator.ofFloat(originalX, newX)
        val animY = ValueAnimator.ofFloat(originalY, newY)
        animX.duration = duracion
        animY.duration = duracion
        animX.addUpdateListener { valAnim ->
            viewToAnimate.translationX = valAnim.animatedValue as Float
        }
        animY.addUpdateListener { valAnim ->
            viewToAnimate.translationY = valAnim.animatedValue as Float
        }
        animX.start()
        animY.start()
        animY.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)

                prepararMesaParaNuevosPuestos()
                //
            }
        })

    }

    fun timerTurnoCrupier() {

        val crupier = viewModel.crupier
        val jugador1 = viewModel.jugador1

        if (!crupier.cartasQueTiene.get(1).isAbierta()) {
            crupier.cartasQueTiene.get(1).abrirCerarCatra(duracionAbrirCartas)
        }

        if (crupier.sumaPuntos() > 16) {
            if (crupier.sumaPuntos() > 21) {
                resultarJuego(2.0, "Ganas +${jugador1.apuestoJugador * 2}");
            } else if (crupier.sumaPuntos() > jugador1.sumaPuntos()) {
                resultarJuego(0.0, "Pierdes -${jugador1.apuestoJugador}");
            } else if (crupier.sumaPuntos() == jugador1.sumaPuntos()) {
                resultarJuego(1.0, "Empate");
            } else {
                resultarJuego(2.0, "Ganas +${jugador1.apuestoJugador * 2}");
            }
        } else {
            viewModel.baraja?.robarCarta()?.let {
                crupier.agregarCarta(it, duracionFlotarCartas)
                    ?.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            //
                            crupier.cartasQueTiene.add(it)
                            if (!(jugador1.isCrupier && jugador1.cartasQueTiene.size == 2)) it.abrirCerarCatra(
                                duracionAbrirCartas
                            )

                            if (crupier.sumaPuntos() < 17) {
                                timerTurnoCrupier()
                            } else {

                                if (crupier.sumaPuntos() > 21) {
                                    resultarJuego(2.0, "Ganas +${jugador1.apuestoJugador * 2}");
                                } else if (crupier.sumaPuntos() > jugador1.sumaPuntos()) {
                                    resultarJuego(0.0, "Pierdes -${jugador1.apuestoJugador}");
                                } else if (crupier.sumaPuntos() == jugador1.sumaPuntos()) {
                                    resultarJuego(1.0, "Empate");
                                } else {
                                    resultarJuego(2.0, "Ganas +${jugador1.apuestoJugador * 2}");
                                }

                            }
                        }
                    })
            }
        }


    }

    private fun prepararMesaParaNuevosPuestos() {

        viewModel.jugador1.balanceJugador += viewModel.jugador1.apuestoJugador * coeficienteDeGanancia
        viewModel.jugador1.apuestoJugador = 0.0

        viewModel.jugador1.cartasQueTiene.forEach {
            main.removeView(it.view)
        }
        viewModel.jugador1.cartasQueTiene.clear();

        viewModel.crupier.cartasQueTiene.forEach {
            main.removeView(it.view)
        }
        viewModel.crupier.cartasQueTiene.clear();

        setCoordParaPanelesDeFichas()

        jPanelFichasEnApuesto.removeAllViews();
        viewModel.labelsFichasEnApuesto.clear();
        jPanelFichasEnCrupier.removeAllViews();

        if ((viewModel.baraja?.cartas?.size ?: 0) < (viewModel.baraja?.MIN_CARTAS ?: 0)) {

            nuevaBaraja()

        }

        viewModel.isRepartirPrincipal = false
        viewModel.isMas = false
        viewModel.isTurnoCrupier = false
        viewModel.isDoblar = false
    }

    private fun nuevaBaraja() {
        mostrarMsg("Nueva baraja!", colorComun)
        soundPool.play(soundIdnuevabarajavoz, 1f, 1f, 1, 0, 1f)
        soundPool.play(soundIdnuevabaraja2, 1f, 1f, 1, 0, 1f)

        viewModel.baraja = BarajaDeCartas(instance, 0f, 0f, anchuraCarta)
        viewModel.isInicaadaBaraja = true

        setBaraja()
    }

    fun addApuestoVisual(apuestoQuierePonel: Double, identDrowable: Int) {

        soundPool.play(soundIdapostar, 1f, 1f, 1, 0, 1f)

        val jugador1 = viewModel.jugador1
        val resultDeTratarPonerApuesto = jugador1.addApuesto(apuestoQuierePonel)
        if (resultDeTratarPonerApuesto.toBoolean()) {
            // Crear un elemento View
            val textView = TextView(this)
            viewModel.labelsFichasEnApuesto.add(textView)
            textView.background = ContextCompat.getDrawable(this, identDrowable)
            textView.text = apuestoQuierePonel.toString()
            textView.textSize = 0F
            jPanelFichasEnApuesto.addView(textView)
            val layoutParamsView = textView.layoutParams
            layoutParamsView.height = alturaFicha
            layoutParamsView.width = anchuraFicha
            textView.layoutParams = layoutParamsView

            val size = viewModel.labelsFichasEnApuesto.size
            val y: Int =
                jPanelFichasEnApuesto.height - layoutParamsView.height - (size - 1) % candFichEnCol * dispYfichas
            val x: Int = layoutParamsView.width * ((size - 1) / candFichEnCol)
            setCoord(textView, x.toFloat(), y.toFloat())

        } else {

            soundPool.play(soundIdpop, 1f, 1f, 1, 0, 1f)

            mostrarMsg(resultDeTratarPonerApuesto, colorComun)
        }

    }

    fun mostrarMsg(msg: String, color: Int) {

        val snackbar = Snackbar.make(
            main,
            msg,
            Snackbar.LENGTH_SHORT // o Snackbar.LENGTH_LONG o una duración personalizada
        )
        snackbar.animationMode = Snackbar.ANIMATION_MODE_FADE

        val view = snackbar.view

        val drawable = GradientDrawable()
        drawable.setColor(color) // Color de fondo (puedes ajustar el color)
        val cornerRadius = 22f
        drawable.cornerRadius = cornerRadius // Radio de las esquinas en píxeles
        // Asignar el drawable creado como fondo del Snackbar
        view.background = drawable

        val textView: TextView = view.findViewById(com.google.android.material.R.id.snackbar_text)
        textView.textSize = 18f
        textView.setTextColor(Color.YELLOW)
        textView.gravity = Gravity.TOP
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        val params = view.layoutParams as FrameLayout.LayoutParams
        params.height = FrameLayout.LayoutParams.WRAP_CONTENT
        params.width = main.width / 2

        params.gravity = Gravity.CENTER_VERTICAL
        view.layoutParams = params
        setCoord(view, main.width.toFloat(), 0f)
        snackbar.show()
        moverView(params.width.toFloat() + cornerRadius, 0f, view, 600)

    }

    fun moverView(newX: Float, newY: Float, view: View, duracion: Long): ValueAnimator? {

        val viewToAnimate = view
        val originalX = viewToAnimate.translationX
        val originalY = viewToAnimate.translationY
        val animX = ValueAnimator.ofFloat(originalX, newX)
        val animY = ValueAnimator.ofFloat(originalY, newY)
        animX.duration = duracion
        animY.duration = duracion
        animX.addUpdateListener { valAnim ->
            viewToAnimate.translationX = valAnim.animatedValue as Float
        }
        animY.addUpdateListener { valAnim ->
            viewToAnimate.translationY = valAnim.animatedValue as Float
        }
        animX.start()
        animY.start()

        return animY

    }

    private fun setTamano(objeto: Any, width: Int, height: Int) {
        when (objeto) {
            is ViewGroup -> {
                val layoutParamsView = objeto.layoutParams
                layoutParamsView.width = width
                layoutParamsView.height = height
                objeto.layoutParams = layoutParamsView
            }

            is View -> {
                val layoutParamsView = objeto.layoutParams
                layoutParamsView.width = width
                layoutParamsView.height = height
                objeto.layoutParams = layoutParamsView
            }
        }
    }

    private fun setCoord(objeto: Any, x: Float, y: Float) {
        when (objeto) {
            is ViewGroup -> {
                val layoutParams = objeto.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(0, 0, 0, 0)
                layoutParams.marginEnd = 0
                layoutParams.marginStart = 0
                objeto.layoutParams = layoutParams
                objeto.translationX = x.toFloat()
                objeto.translationY = y.toFloat()
                //objeto.setPadding(0)
            }

            is View -> {
                val layoutParams = objeto.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(0, 0, 0, 0)
                layoutParams.marginEnd = 0
                layoutParams.marginStart = 0
                objeto.layoutParams = layoutParams
                objeto.translationX = x.toFloat()
                objeto.translationY = y.toFloat()
                //objeto.setPadding(0)
            }
        }
    }

}
