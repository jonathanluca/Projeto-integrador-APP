package br.com.myapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.maps.android.PolyUtil
import java.text.SimpleDateFormat
import java.util.*

class Registrar_ponto : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    private val polygonPoints = listOf(
//        com.google.android.gms.maps.model.LatLng(-22.8363405896867, -47.0526888398160),
//        com.google.android.gms.maps.model.LatLng(-22.831744750589777, -47.0534708333304),
//        com.google.android.gms.maps.model.LatLng(-22.83059176664599, -47.0444059745111),
//        com.google.android.gms.maps.model.LatLng(-22.83208015670517, -47.0440574516844),
//        com.google.android.gms.maps.model.LatLng(-22.83373067292926, -47.042658188004125),
//        com.google.android.gms.maps.model.LatLng(-22.83601268933377, -47.04146036101791),
//        com.google.android.gms.maps.model.LatLng(-22.8363405896867, -47.0526888398160)
//    )
private val polygonPoints = listOf(
    com.google.android.gms.maps.model.LatLng(-22.8363405896867, -47.0526888398160),
    com.google.android.gms.maps.model.LatLng(-22.831744750589777, -47.0534708333304),
    com.google.android.gms.maps.model.LatLng(-22.83059176664599, -47.0444059745111),
    com.google.android.gms.maps.model.LatLng(-22.83208015670517, -47.0440574516844),
    com.google.android.gms.maps.model.LatLng(-22.83373067292926, -47.042658188004125),
    com.google.android.gms.maps.model.LatLng(-22.83601268933377, -47.04146036101791),
    com.google.android.gms.maps.model.LatLng(-22.8363405896867, -47.0526888398160)
)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_ponto)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        findViewById<Button>(R.id.button_registrar_ponto).setOnClickListener {
            if (checkPermissions()) {
                punchTheClock()
            } else {
                requestPermissions()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1000)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            punchTheClock()
        } else {
            Toast.makeText(this, "Permissão de localização é necessária para registrar o ponto.", Toast.LENGTH_LONG).show()
        }
    }

    private fun punchTheClock() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates = 1
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        val isInsidePolygon = PolyUtil.containsLocation(location.latitude, location.longitude, polygonPoints, true)
                        if (isInsidePolygon) {
                            registerPoint(location)
                        } else {
                            Toast.makeText(applicationContext, "Você está fora da localização permitida", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Localização não disponível", Toast.LENGTH_LONG).show()
                    }
                }
            }, null)
        }
    }

    private fun registerPoint(location: Location) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedTime: String = dateFormat.format(Date())
        val ponto = hashMapOf(
            "userId" to FirebaseAuth.getInstance().currentUser?.uid,
            "timestamp" to formattedTime,
            "latitude" to location.latitude,
            "longitude" to location.longitude
        )

        FirebaseDatabase.getInstance().reference.child("pontos").push().setValue(ponto)
            .addOnSuccessListener {
                Toast.makeText(this, "Ponto registrado em: $formattedTime", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Log.e("FirebaseError", "Falha ao registrar ponto", it)
                Toast.makeText(this, "Falha ao registrar ponto no banco de dados.", Toast.LENGTH_SHORT).show()
            }
    }
}
