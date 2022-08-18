package com.example.silencer

import android.app.*
import android.app.Notification
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import org.apache.http.conn.ssl.AllowAllHostnameVerifier
import java.util.*

class MainActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    var hrs = 0
    var mins = 0
    var d = 0
    var m = 0
    var y = 0
    var startTimeHour = 0
    var startTimeMinute = 0
    var startTimeDay = 0
    var startTimeMonth = 0
    var startTimeYear = 0
    var curr = ""
    var endTimeHour = 0
    var endTimeMinute = 0
    var endTimeDay = 0
    var endTimeMonth = 0
    var endTimeYear = 0
    var startTxt = ""
    var endTxt = ""


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startTimeSet = findViewById<CheckBox>(R.id.startTimeSet)
        val endTimeSet = findViewById<CheckBox>(R.id.endTimeSet)
        val startTimeTxt = findViewById<TextView>(R.id.startTimeTxt)
        val endTimeTxt = findViewById<TextView>(R.id.endTimeTxt)
        val btn = findViewById<Button>(R.id.btn)

        startTimeSet.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                val cal: Calendar = Calendar.getInstance()
                d = cal.get(Calendar.DATE)
                m = cal.get(Calendar.MONTH)
                y = cal.get(Calendar.YEAR)
                curr = "Start"
                DatePickerDialog(this, this, y, m, d).show()
            }
            else {
                startTimeTxt.text = ""
            }
        }
        endTimeSet.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                val cal: Calendar = Calendar.getInstance()
                d = cal.get(Calendar.DATE)
                m = cal.get(Calendar.MONTH)
                y = cal.get(Calendar.YEAR)
                curr = "End"
                DatePickerDialog(this, this, y, m, d).show()
            }
            else {
                endTimeTxt.text = ""
            }
        }

        createNotificationChannel()
        btn.setOnClickListener {

            if (startTimeTxt.text != "" && endTimeTxt.text != "")
                setAlarm()
            else {
                Toast.makeText(this, "Pick start and end time!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarm() {
        val calendar1 = Calendar.getInstance()
        calendar1.set(startTimeYear,startTimeMonth,startTimeDay,startTimeHour,startTimeMinute)
        val calendar2 = Calendar.getInstance()
        calendar2.set(endTimeYear,endTimeMonth,endTimeDay,endTimeHour,endTimeMinute)
        if (calendar1.timeInMillis < calendar2.timeInMillis) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val intent = Intent(this,AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP, calendar1.timeInMillis,
                pendingIntent
            )
            val alarmManager2 = getSystemService(ALARM_SERVICE) as AlarmManager
            val intent2 = Intent(this, AlarmReceiver2::class.java)
            val pendingIntent2 =
                PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_IMMUTABLE)
            alarmManager2.setExact(
                AlarmManager.RTC_WAKEUP, calendar2.timeInMillis,
                pendingIntent2
            )
        }
        else {
            Toast.makeText(this, "Start time should be before end time!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendNotification() {
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, "channel0")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Device Silencer")
            .setContentText("Your device is set to silent mode.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(100, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notif channel"
            val desc = "Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("channel0", name, importance)
            channel.description = desc
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        val dateFormat = android.text.format.DateFormat.getDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)
        val startTimeSet = findViewById<CheckBox>(R.id.startTimeSet)
        val endTimeSet = findViewById<CheckBox>(R.id.endTimeSet)
        val startTimeTxt = findViewById<TextView>(R.id.startTimeTxt)
        val endTimeTxt = findViewById<TextView>(R.id.endTimeTxt)
        if (curr == "Start"){
            startTimeHour = hourOfDay
            startTimeMinute = minute
            calendar.set(startTimeYear,startTimeMonth,startTimeDay,startTimeHour,startTimeMinute)
            val date = Date(calendar.timeInMillis)
            curr = ""
            startTimeTxt.text = dateFormat.format(date)+" "+timeFormat.format(date)
        }
        else if (curr == "End"){
            endTimeHour = hourOfDay
            endTimeMinute = minute
            calendar.set(endTimeYear,endTimeMonth,endTimeDay,endTimeHour,endTimeMinute)
            val date = Date(calendar.timeInMillis)
            endTimeTxt.text = dateFormat.format(date)+" "+timeFormat.format(date)
            curr = ""
        }
        if (startTimeTxt.text == ""){
            startTimeSet.isChecked = false
        }
        if (endTimeTxt.text == ""){
            endTimeSet.isChecked = false
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val cal: Calendar = Calendar.getInstance()
        hrs = cal.get(Calendar.HOUR)
        mins = cal.get(Calendar.MINUTE)
        val startTimeSet = findViewById<CheckBox>(R.id.startTimeSet)
        val endTimeSet = findViewById<CheckBox>(R.id.endTimeSet)
        val startTimeTxt = findViewById<TextView>(R.id.startTimeTxt)
        val endTimeTxt = findViewById<TextView>(R.id.endTimeTxt)
        if (curr == "Start"){
            startTimeDay = dayOfMonth
            startTimeMonth = month
            startTimeYear = year
        }
        else if (curr == "End"){
            endTimeDay = dayOfMonth
            endTimeMonth = month
            endTimeYear = year
        }
        TimePickerDialog(this, this, hrs, mins, true).show()
    }
}