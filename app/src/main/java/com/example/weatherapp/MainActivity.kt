package com.example.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private  val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        showWeatherData("jaipur")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object  : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    showWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
              return true
            }

        })
    }

    private fun showWeatherData(cityName : String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName,"eaec2efeced9d6d17ac9f467026a8de8","metric")
        response.enqueue(object : Callback<WeatherApp>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?: "unkown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

//                    Log.d("TAG", "onResponse: $temperature")
                    binding.temp.text = "$temperature C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp: $maxTemp C"
                    binding.minTemp.text = "Min Temp: $minTemp C"
                    binding.humiduty.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.seaLevel.text = "$seaLevel hPa"
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text = "$cityName"

                    changeImgAccordingWeatherCondtions(condition)

                }
            }
            override fun onFailure(call: Call<WeatherApp>, response: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun changeImgAccordingWeatherCondtions(condition: String) {
        when (condition) {
            "Clear Sky","Sunny","Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds","Clouds","Overcast","Mist","Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }
//private fun changeImgAccordingWeatherConditions(condition: String) {
//    val backgroundResource: Int
//    val animationResource: Int
//
//    when (condition) {
//        "Clear Sky", "Sunny", "Clear" -> {
//            backgroundResource = R.drawable.sunny_background
//            animationResource = R.raw.sun
//        }
//        "Partly Cloudy", "Clouds", "Overcast", "Mist", "Foggy" -> {
//            backgroundResource = R.drawable.colud_background
//            animationResource = R.raw.cloud
//        }
//        "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
//            backgroundResource = R.drawable.rain_background
//            animationResource = R.raw.rain
//        }
//        "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
//            backgroundResource = R.drawable.snow_background
//            animationResource = R.raw.snow
//        }
//        else -> {
//            backgroundResource = R.drawable.sunny_background
//            animationResource = R.raw.sun
//        }
//    }
//
//    binding.root.setBackgroundResource(backgroundResource)
//    binding.lottieAnimationView.setAnimation(animationResource)
//    binding.lottieAnimationView.playAnimation()
//}


    fun dayName(timestamp: Long) : String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    fun date(): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }
}
