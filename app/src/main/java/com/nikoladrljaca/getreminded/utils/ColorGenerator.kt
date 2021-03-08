package com.nikoladrljaca.getreminded.utils

import android.graphics.Color
import com.nikoladrljaca.getreminded.R
import kotlin.random.Random

fun generateRandomColor(): Int {
    val list = CardColor::class.sealedSubclasses
    val randomNum = Random.nextInt(5)
    list[randomNum].objectInstance?.let { cardColor ->
        return cardColor.colorValue
    }
    return CardColor.CardOrange.colorValue
}

val colorListInInts = intArrayOf(
    Color.parseColor("#e57373"),
    Color.parseColor("#81c784"),
    Color.parseColor("#ce93d8"),
    Color.parseColor("#ffcc80"),
    Color.parseColor("#f48fb1"),
    Color.parseColor("#80cbc4"),
)

private sealed class CardColor(val colorValue: Int) {
    object CardRed: CardColor(R.color.colorAccentRed)
    object CardOrange: CardColor(R.color.colorAccentOrange)
    object CardGreen: CardColor(R.color.colorAccentGreen)
    object CardBlue: CardColor(R.color.colorAccent)
    object CardPink: CardColor(R.color.colorAccentPink)
    object CardPurple: CardColor(R.color.colorAccentPurple)
}