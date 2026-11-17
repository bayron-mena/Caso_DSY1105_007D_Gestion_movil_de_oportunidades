package com.example.opportunitymanagement

import android.app.Application
import com.example.opportunitymanagement.di.AppContainer
import com.example.opportunitymanagement.di.DefaultAppContainer

class OpportunityApplication : Application() {
    val container: AppContainer by lazy { DefaultAppContainer(this) }
}
