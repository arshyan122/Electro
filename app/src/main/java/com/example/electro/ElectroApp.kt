package com.example.electro

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Hilt-aware Application entry point. Required for any `@AndroidEntryPoint`
 * activity/fragment to receive injected dependencies.
 */
@HiltAndroidApp
class ElectroApp : Application()
