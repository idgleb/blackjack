package viewmodels
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.ursolgleb.blackjacknoactivity.BarajaDeCartas
import com.ursolgleb.blackjacknoactivity.Carta
import com.ursolgleb.blackjacknoactivity.Jugador


class JuegoViewModel() : ViewModel() {
    val jugador1 = Jugador("Miley", false)
    val crupier = Jugador("Ursol", true)

    var labelsFichasEnApuesto: MutableList<TextView> = mutableListOf()

    var isMas = false
    var isTurnoCrupier = false
    var isRepartirPrincipal = false
    var isDoblar = false

    val cartas: MutableList<Carta> = mutableListOf()

    var isInicaadaBaraja = false

    lateinit var baraja:BarajaDeCartas


}

