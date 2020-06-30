package com.example.original_music

import android.animation.ObjectAnimator
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android.original_music.media.library.JsonSource
import com.example.android.original_music.media.library.MusicSource
import com.example.original_music.services.ParsePlaylists
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.net.URL


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dj, R.id.navigation_songs, R.id.navigation_youtube
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val playerView: ConstraintLayout = findViewById(R.id.playerContainer)


        ObjectAnimator.ofFloat(playerView, "translationY", 130f).apply {
            duration = 5000;
            start()
        }

        // Coroutine Services
        val serviceJob = SupervisorJob()
        val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
        val remoteJsonSource: Uri =
            Uri.parse("https://api-v2.hearthis.at/mikeds?type=playlists&count=20")
        lateinit var mediaSource: MusicSource
        mediaSource = JsonSource(context = this, source = remoteJsonSource)

        serviceScope.launch {
            mediaSource.load()
        }
    }
}
