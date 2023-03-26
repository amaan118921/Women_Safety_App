package com.example.womensafety.presentation.services

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.*
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.example.womensafety.R
import com.example.womensafety.presentation.activities.MainActivity
import com.example.womensafety.domain.repositories.AppRepository
import com.example.womensafety.eventbus.MessageEvent
import com.example.womensafety.helpers.Constants
import com.example.womensafety.helpers.PermissionHelper
import com.example.womensafety.domain.models.ContactModel
import com.example.womensafety.eventbus.ContactEvent
import com.example.womensafety.utils.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject
import kotlin.math.sqrt


@AndroidEntryPoint
class LocationService: Service(), SensorEventListener, PermissionHelper.IPermission {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var notificationManager: NotificationManager
    private var auth = FirebaseAuth.getInstance()
    private var CHANNEL_ID = "HEREIAM"
    private var sensorManager: SensorManager? = null
    private var mAccel: Float = 0f
    private var mAccelCurr: Float = 0f
    private var mAccelLast: Float = 0f
    private var shakeDetected = false
    private val job = SupervisorJob()
    private var contacts = listOf<ContactModel?>()

    private val scope = CoroutineScope(Dispatchers.Default+job)

    private val permissionHelper = PermissionHelper(this, null)

    @Inject
    lateinit var appRepository: AppRepository

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        if(!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
        sensorManager = getSystemService(SensorManager::class.java)
        notificationManager  = getSystemService(NotificationManager::class.java)
        setUpNotificationChannel()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        buildNotification()
        initListener()
        registerListener()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
        when(event?.msgString) {
            Constants.GET_ALERT_CONTACTS -> {
                val ev = event as ContactEvent
                Log.d("contacts", ev.getContactsList().toString())
                this.contacts = ev.getContactsList()
            }
        }
    }

    private fun initListener() {
        Objects.requireNonNull(sensorManager)?.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)
        mAccel = 10f
        mAccelCurr = SensorManager.GRAVITY_EARTH
        mAccelLast = SensorManager.GRAVITY_EARTH
    }


    private fun registerListener() {
        sensorManager?.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun setUpNotificationChannel() {
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, getString(R.string.here_i_am_app), NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildNotification() {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE)
            }

        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(getText(R.string.app_has))
            .setContentText(getText(R.string.notification_message))
            .setPriority(Notification.PRIORITY_LOW)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(Constants.NOTIFICATION_ID, notification)
    }

    //We are simply checking instead of requesting
    private fun getLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if(permissionHelper.isPermissionGranted(Manifest.permission.SEND_SMS, this)) sendAlert(location)
            }
        }
    }

    private fun sendAlert(location: Location?) {
        val link = Utils.createLink(location?.latitude.toString(), location?.longitude.toString())
        Log.d("link", Utils.createMessage(link, ""))
        Utils.sendMessages(link, contacts, Utils.createMessage(link,""))
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val x = event?.values?.get(0)
        val y = event?.values?.get(1)
        val z = event?.values?.get(2)
        mAccelLast = mAccelCurr
        mAccelCurr = sqrt((((x!! * x) + (y!! * y) + (z!! * z))).toDouble()).toFloat()
        val delta = mAccelCurr-mAccelLast
        mAccel = mAccel*0.9f+delta
        if(mAccel>100 && !shakeDetected) {
            getLocationUpdates()
            shakeDetected=true
            initHandler()
        }
    }

    private fun initHandler() {
        Handler(Looper.myLooper()!!).postDelayed({
            shakeDetected=false
        }, 15000)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }


    override fun onDestroy() {
        super.onDestroy()
        scope.cancel(null)
        EventBus.getDefault().unregister(this)
        sensorManager?.unregisterListener(this)
    }

    override fun onPermissionGranted(requestCode: Int, permission: String) {

    }

    override fun onPermissionDenied(requestCode: Int, permission: String) {

    }

    override fun onPermissionDeniedPermanently(requestCode: Int, permission: String) {

    }

}