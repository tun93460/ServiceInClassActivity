package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var timerTextView: TextView

    lateinit var timerBinder: TimerService.TimerBinder
    var isConnected = false

    val timerHandler = Handler(Looper.getMainLooper()) {
        timerTextView.text = it.what.toString()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerTextView = findViewById(R.id.textView)

        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                timerBinder = service as TimerService.TimerBinder
                timerBinder.setHandler(timerHandler)
                isConnected = true
            }

            override fun onServiceDisconnected(name: ComponentName) {
                isConnected = false
            }
        }

        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )

        findViewById<Button>(R.id.startButton).setOnClickListener {
            if (isConnected) {
                if (timerBinder.paused || !timerBinder.isRunning) {
                    timerBinder.start(20)
                } else {
                    timerBinder.pause()
                }
            }
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            if (isConnected) {
                if (timerBinder.isRunning) {
                    timerBinder.stop()
                }
            }
        }
    }
}