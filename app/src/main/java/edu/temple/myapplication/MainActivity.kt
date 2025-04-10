package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView

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

        findViewById<Button>(R.id.startButton).setOnClickListener {start()}
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {stop()}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_start -> start()
            R.id.action_stop -> stop()
            else -> return false
        }

        return super.onOptionsItemSelected(item)
    }

    private fun start() {
        if (isConnected) {
            if (timerBinder.paused || !timerBinder.isRunning) {
                timerBinder.start(1000)
            } else {
                timerBinder.pause()
            }
        }
    }

    private fun stop() {
        if (isConnected) {
            if (timerBinder.isRunning) {
                timerBinder.stop()
                timerTextView.text = "0"
            }
        }
    }
}