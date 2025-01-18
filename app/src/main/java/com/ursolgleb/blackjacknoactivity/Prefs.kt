package com.ursolgleb.blackjacknoactivity

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    val PREFS_DATA = "com.ursolgleb.blackjacknoactivity"
    val KEY_BALANCE = "key_balance"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_DATA, 0)

    var balanceJugador: Float
        get() = prefs.getFloat(KEY_BALANCE, DEFAUL_BALANCE.toFloat())
        set(value) = prefs.edit().putFloat(KEY_BALANCE, value).apply()
}
