package com.ssafy.smartstore.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.databinding.FragmentMapBinding
import com.ssafy.smartstore.viewModels.OrderViewModel
import java.io.IOException
import com.ssafy.smartstore.R
import java.util.*

// Order 탭 - 지도 화면
private const val TAG = "MapFragment_싸피"
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
    private lateinit var mainActivity: MainActivity
    private lateinit var mMap:GoogleMap
    private var currentMarker: Marker? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var currentPosition: LatLng
    private lateinit var binding: FragmentMapBinding
    private lateinit var mView: MapView
    private val UPDATE_INTERVAL = 1000 // 1초
    private val FASTEST_UPDATE_INTERVAL = 500 // 0.5초
    private val lat:Double = 36.1009585
    private val long:Double = 128.4229464
    private val tel:String = "tel:01012345678"
    private val viewModel: OrderViewModel by activityViewModels()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = UPDATE_INTERVAL.toLong()
            smallestDisplacement = 10.0f
            fastestInterval = FASTEST_UPDATE_INTERVAL.toLong()
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mView = binding.map
        mView.onCreate(savedInstanceState)
        mView.getMapAsync(this)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        mView.onDestroy()
        mainActivity.hideBottomNav(false)
    }

    override fun onResume() {
        mView.onResume()
        mainActivity.hideBottomNav(true)
        super.onResume()
    }

    private fun showDialogStore() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setView(R.layout.dialog_map_store)
            setTitle("매장 상세")
            setCancelable(true)
            setPositiveButton("전화걸기") { dialog, _ ->
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(tel)))
                dialog.cancel()
            }
            setNegativeButton("길찾기") { dialog, _ ->
                val gmmIntentUri = Uri.parse("google.navigation:q=$lat, $long")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
                dialog.cancel()
            }
        }
        builder.create().show()
    }

    private fun setDefaultLocation() {
        val DEFAULT_LOCATION = LatLng(lat, long)
        val markerTitle = "싸피벅스"
        val markerSnippet = "lat: $lat longitude: $long"

        currentMarker?.remove()

        val markerOptions = MarkerOptions()
        markerOptions.position(DEFAULT_LOCATION)
        markerOptions.title(markerTitle)
        markerOptions.snippet(markerSnippet)
        markerOptions.draggable(true)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

        currentMarker = mMap!!.addMarker(markerOptions)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15f)
        mMap!!.moveCamera(cameraUpdate)
    }

    /** 권한 관련 **/
    private fun checkPermission(): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationUpdates() {
        // 위치서비스 활성화 여부 check
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting()
        } else {
            if (checkPermission()) {
                mFusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null
                )
                if (mMap != null) mMap!!.isMyLocationEnabled = true
            }
        }
    }

    //위치정보 요청시 호출
    var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                val location = locationList[locationList.size - 1]
                currentPosition = LatLng(location.latitude, location.longitude)
                val markerTitle: String = getCurrentAddress(currentPosition)
                val markerSnippet = "위도: ${location.latitude.toString()}, 경도: ${location.longitude }"

                Log.d(TAG, "onLocationResult: 위도: ${location.latitude.toString()}, 경도: ${location.longitude}")

                setCurrentLocation(location, markerTitle, markerSnippet)
            }
        }
    }

    fun getCurrentAddress(latlng: LatLng): String {
        //지오코더: GPS를 주소로 변환
        val geocoder = Geocoder(this.context, Locale.getDefault())
        val addresses: List<Address>?
        try {
            addresses = geocoder.getFromLocation(
                latlng.latitude,
                latlng.longitude,
                1
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            Toast.makeText(this.context, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return "지오코더 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(this.context, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }

        return if (addresses == null || addresses.isEmpty()) {
            Toast.makeText(this.context, "주소 발견 불가", Toast.LENGTH_LONG).show()
            "주소 발견 불가"
        } else {
            val address = addresses[0]
            address.getAddressLine(0).toString()
        }
    }

    fun setCurrentLocation(location: Location, markerTitle: String?, markerSnippet: String?) {
        currentMarker?.remove()
        val currentLatLng = LatLng(location.latitude, location.longitude)
        val storeLocation = Location("싸피벅스")
        storeLocation.latitude = lat
        storeLocation.longitude = long
        viewModel.updateDistance(location.distanceTo(storeLocation).toInt())

        val cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng)
        mMap!!.moveCamera(cameraUpdate)
    }


    private fun checkLocationServicesStatus(): Boolean {
        val locationManager = requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    /******** 위치서비스 활성화 여부 check *********/
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private var needRequest = false

    private fun showDialogForLocationServiceSetting() {
        val builder: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            "앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { _, _ ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        }
        builder.setNegativeButton("취소"
        ) { dialog, _ -> dialog.cancel() }
        builder.create().show()
    }

    override fun onMapReady(p0: GoogleMap) {
        Log.d(TAG, "onMapReady: ")
        mMap = p0
        mMap!!.addMarker(MarkerOptions().apply {
            position(LatLng(36.1082418, 128.4201328))
            title("싸피벅스")
            snippet("lat: 36.1082418 lng : 128.4201328")
            draggable(true)
        })

        mMap.setOnMarkerClickListener(this)

        setDefaultLocation()

        if (checkPermission()) {
            startLocationUpdates()
        } else {
            val permissionListener = object : PermissionListener {
                override fun onPermissionGranted() {
                    startLocationUpdates()
                }
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(requireActivity(),
                        "위치 권한이 거부되었습니다.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            TedPermission.create()
                .setPermissionListener(permissionListener)
                .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_CALENDAR )
                .check()
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        showDialogStore()
        return true
    }
}
