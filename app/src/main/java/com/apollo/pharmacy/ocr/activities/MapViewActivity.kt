package com.apollo.pharmacy.ocr.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apollo.pharmacy.ocr.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*

class MapViewActivity : AppCompatActivity(), OnMapReadyCallback, OnMarkerDragListener {

    var map: GoogleMap? = null
    var geocoder: Geocoder? = null
    var locations: String? = null
    var textViewlat: TextView? = null
    var textViewLang:TextView? = null
    var saveBt:TextView? = null
    var cancelBt:TextView? = null
    var crossMark: ImageView? = null
    var address: String? = null
    var city: String? = null
    var state: String? = null
    var country: String? = null
    var postalCode: String? = null
    var knonName: String? = null
    var time = 0
    var testingmapViewLats = false
    var mapUserLats: String? = null
    var handlers: Handler? = null
    var secondHandler:Handler? = null
    var runnables: Runnable? = null
    var secondRunnable:Runnable? = null
    var mapUserLangs: String? = null
    var count: CountDownTimer? = null
    private var mapHandling = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_view)
//        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        this.setFinishOnTouchOutside(false);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT)
       window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        textViewlat = findViewById(R.id.lattitude) as TextView
        textViewLang = findViewById(R.id.longitude) as TextView
        saveBt = findViewById(R.id.save) as TextView
        cancelBt = findViewById(R.id.cancel) as TextView
        crossMark = findViewById(R.id.map_delete_icon) as ImageView

        if (intent != null) {
            testingmapViewLats = intent.getBooleanExtra("testinglatlng", false)
            mapUserLats = intent.getStringExtra("mapLats")
            mapUserLangs = intent.getStringExtra("mapLangs")
        }
        setUp()
    }

    protected fun setUp() {
        handlingListers()
    }

    private fun handlingListers() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        cancelBt!!.setOnClickListener { mapRepresentData() }
        crossMark!!.setOnClickListener {
            mapRepresentData()
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right)
        }
        saveBt!!.setOnClickListener {
            val lating = textViewlat!!.text.toString().toDouble()
            val langing = textViewLang!!.text.toString().toDouble()
            getLocationDetails(lating, langing)
            val intent = Intent()
            intent.putExtra("mapnewaddress", address)
            intent.putExtra("mapnewcity", city)
            intent.putExtra("mapnewzipcode", postalCode)
            intent.putExtra("getlatlnglocations", true)
            intent.putExtra("latitudes", textViewlat!!.text.toString())
            intent.putExtra("longitudes", textViewLang!!.text.toString())
            setResult(RESULT_OK, intent)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right)
        }
    }

    @SuppressLint("SetTextI18n")
    fun getLocationDetails(lating: Double, langing: Double) {
        val addresses: List<Address>
        geocoder = Geocoder(this, Locale.getDefault())
        try {
            addresses = geocoder!!.getFromLocation(lating, langing, 1)
            address = addresses[0].getAddressLine(0)
            city = addresses[0].locality
            state = addresses[0].adminArea
            country = addresses[0].countryName
            postalCode = addresses[0].postalCode
            knonName = addresses[0].featureName
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val latLng = LatLng(lating, langing)
        map!!.addMarker(MarkerOptions().position(latLng).draggable(true).title("Marker in : $address"))
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        if (mapHandling) {
            textViewlat!!.text = mapUserLats
            textViewLang!!.text = mapUserLangs
            mapHandling = false
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.setOnMarkerDragListener(this)
        if (!testingmapViewLats) {
            mapRepresentData()
        } else {
            mapHandling = true
            getLocationDetails(mapUserLats!!.toDouble(), mapUserLangs!!.toDouble())
        }
    }

    fun mapRepresentData() {
        if (intent != null) {
            try {
                locations = intent.getStringExtra("locatedPlace")
                var addressList: List<Address>? = null
                if (locations != null || locations != "") {
                    geocoder = Geocoder(this)
                    try {
                        addressList = geocoder!!.getFromLocationName(locations, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val address = addressList!![0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    map!!.clear()
                    map!!.addMarker(MarkerOptions().position(latLng).title(locations).draggable(true)
                    )
                    map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    textViewlat!!.text = "" + address.latitude
                    textViewLang!!.text = "" + address.longitude
                } else {
                    Toast.makeText(this, "Please Enter Valid Address", Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                    finish()

//                    Toast toast = Toast.makeText(MapViewActvity.this, "Please Enter Valid Address", Toast.LENGTH_SHORT);
//                    toast.getView().setBackground(getResources().getDrawable(R.drawable.toast_bg));
//                    TextView text = (TextView) toast.getView().findViewById(android.R.id.message);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        Typeface typeface = Typeface.createFromAsset(this.getAssets(),"font/montserrat_bold.ttf");
//                        text.setTypeface(typeface);
//                        text.setTextColor(Color.WHITE);
//                        text.setTextSize(14);
//                    }
//                    toast.show();
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Please Enter Valid Address", Toast.LENGTH_SHORT).show()
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()

//                Toast toast = Toast.makeText(MapViewActvity.this, "Please Enter Valid Address", Toast.LENGTH_SHORT);
//                toast.getView().setBackground(getResources().getDrawable(R.drawable.toast_bg));
//                TextView text = (TextView) toast.getView().findViewById(android.R.id.message);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    Typeface typeface = Typeface.createFromAsset(this.getAssets(),"font/montserrat_bold.ttf");
//                    text.setTypeface(typeface);
//                    text.setTextColor(Color.WHITE);
//                    text.setTextSize(14);
//                }
//                toast.show();
            }
        }
    }

//    fun onMarkerDragStart(marker: Marker?) {
//
//    }
//
//    fun onMarkerDrag(marker: Marker?) {
//
//    }
//
//    fun onMarkerDragEnd(marker: Marker) {
//        val position = marker.position
//        textViewlat!!.text = "" + position.latitude
//        textViewLang!!.text = "" + position.longitude
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right)
    }

    override fun onMarkerDragStart(p0: Marker?) {
    }

    override fun onMarkerDrag(p0: Marker?) {
    }

    override fun onMarkerDragEnd(marker: Marker) {
        val position = marker.position
        textViewlat!!.text = "" + position.latitude
        textViewLang!!.text = "" + position.longitude
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}