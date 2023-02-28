package com.bignerdranch.android.geoquiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

const val CHEATER_KEY = "CHEATER_KEY"

class CheatViewModel(private val savedStateHandle: SavedStateHandle): ViewModel() {
    var cheatStatus: Boolean
        get() = savedStateHandle[CHEATER_KEY] ?: false
        set(value) = savedStateHandle.set(CHEATER_KEY, value)
}