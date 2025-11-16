package com.notesbible.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.notesbible.app.data.BibleRepository
import com.notesbible.app.ui.navigation.NotesBibleNavHost
import com.notesbible.app.ui.theme.NotesBibleTheme

class MainActivity : ComponentActivity() {

    private val repository: BibleRepository by lazy {
        (application as NotesBibleApp).container.repository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesBibleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    NotesBibleNavHost(repository = repository, modifier = Modifier)
                }
            }
        }
    }
}
