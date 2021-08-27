package com.example.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var userfield:EditText
    private lateinit var mainbutton:Button
    private lateinit var resultfield:TextView
    private lateinit var resultdop:TextView
    private lateinit var locationManager:LocationManager
    lateinit var flpc: FusedLocationProviderClient
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userfield = findViewById(R.id.user_field)
        mainbutton = findViewById(R.id.user_button)
        resultfield = findViewById(R.id.result)
        resultdop = findViewById(R.id.resdop)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        flpc = LocationServices.getFusedLocationProviderClient(this)
        userfield.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                resultfield.text = ""
                resultdop.text = ""
            }
        })
        /*
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            flpc.getCurrentLocation(104,null).addOnSuccessListener { location : Location -> //104 - PRIORITY_LOW_POWER
                val geocoder = Geocoder(this)
                val list = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val str = list[0].locality
                userfield.setText(str)
            }
        } else {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
            ActivityCompat.requestPermissions(this, permissions,0)
        }

         */
        mainbutton.setOnClickListener {
            var otvtmp = "Пожалуйста, подождите"
            if (userfield.text.toString().trim() == "")
                //Toast.makeText(this, "Введите город", Toast.LENGTH_LONG).show()
                    resultfield.text = "Введите название города"
            else {
                if (resultdop.text == ""){
                    val city: String = userfield.text.toString()
                    val key = "e9c95bab1d070877909fd3e55310c60c"
                    val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$key&units=metric&lang=ru"
                    val urls = "https://api.openweathermap.org/data/2.5/forecast?q=$city&appid=$key&units=metric&lang=ru"
                    //val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    //imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                    doAsync {
                        val apiresponse = URL(url).readText()//обработка одиночного прогноза
                        val weather = JSONObject(apiresponse).getJSONArray("weather")
                        val desc = weather.getJSONObject(0).getString("description")
                        val main = JSONObject(apiresponse).getJSONObject("main")
                        val temp = main.getString("temp")
                        resultfield.text = "Температура: $temp ℃, $desc"
                        val apiresponses = URL(urls).readText()// обработка многодневного прогноза
                        val list = JSONObject(apiresponses).getJSONArray("list")
                        var otvet = ""
                        for (i in 1 until list.length()){
                            val obj = list.getJSONObject(i)
                            val tempd = obj.getJSONObject("main").getInt("temp")
                            val weatherd = obj.getJSONArray("weather")
                            val descd = weatherd.getJSONObject(0).getString(("description"))
                            val time = obj.getString("dt_txt")
                            otvet += "$time \n $tempd ℃, $descd \n \n"
                        }
                        resultdop.text = otvet
                    }
                    resultdop.gravity = Gravity.CENTER
                    resultdop.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    resultdop.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
                }
                /*
                if (resultfield.text == ""){
                    resultfield.text = "Город не найден"
                    resultdop.text = ""
                }
                 */
            }
        }

    }

}