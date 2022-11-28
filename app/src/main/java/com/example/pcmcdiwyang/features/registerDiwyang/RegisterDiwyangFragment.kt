package com.example.pcmcdiwyang.features.registerDiwyang

import android.Manifest
import android.Manifest.permission.CAMERA
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pcmcdiwyang.R
import com.example.pcmcdiwyang.databinding.FragmentRegisterDiwyangBinding
import com.example.pcmcdiwyang.scanners.ActivityScanFingerprint
import com.example.pcmcdiwyang.scanners.ActivityScanIris
import com.google.android.gms.location.FusedLocationProviderClient
import java.util.*
import android.annotation.SuppressLint
import android.content.Context

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices

class RegisterDiwyangFragment : Fragment() {

    private var _binding: FragmentRegisterDiwyangBinding? = null
    private val binding get() = _binding!!
    private val PROFILE_PIC_ID = 1
    private val AADHAR_FRONT_ID = 2
    private val AADHAR_BACK_ID = 3
    private val UDIDID = 4
    private val FINGERPRINT_SCAN = 5
    private val LEFT_EYE_SCAN = 6
    private val RIGHT_EYE_SCAN = 7
    var isProfileCameraCapture = false
    var isProofCameraCapture = false
    var isFPCapture = false
    var isEyeCapture = false
    var listZoneA : List<String>  = listOf("10","14","15","19")
    var listZoneB  : List<String>  = listOf("16","17","18","22")
    var listZoneC : List<String>  = listOf("2","6","8","9")
    var listZoneD  : List<String>  = listOf("25","26","28","29")
    var listZoneE : List<String>  = listOf("3","4","5","7")
    var listZoneF : List<String>  = listOf("1","11","12","13")
    var listZoneG  : List<String>  = listOf("21","23","24","27")
    var listZoneH  : List<String>  = listOf("20","30","31","32")
    val zoneMap : Map<String, List<String>> = mapOf("A Zone" to listZoneA,"B Zone" to listZoneB, "C Zone" to listZoneC,
        "D Zone" to listZoneD, "E Zone" to listZoneE, "F Zone" to listZoneF, "G Zone" to listZoneG, "H Zone" to listZoneH)
   lateinit var zoneList: Array<String>
   lateinit var registerDiwyangViewModel :RegisterDiwyangViewModel
   var latitude : Double = 0.0
   var longitude : Double = 0.0


    @RequiresApi(Build.VERSION_CODES.M)
    private val cameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            with(binding.root) {
                when {
                    granted -> Log.e("Log", "Permission granted!")
                    shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                        //this option is available starting in API 23
                        //snackBar("Permission denied, show more info!")
                        Toast.makeText(activity, "Permission denied!", Toast.LENGTH_LONG).show()
                    }
                    else -> Toast.makeText(activity, "Permission denied!", Toast.LENGTH_LONG).show()
                }
            }
        }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(_binding==null) {
            registerDiwyangViewModel =
                ViewModelProvider(this).get(RegisterDiwyangViewModel::class.java)
            _binding = FragmentRegisterDiwyangBinding.inflate(inflater, container, false)
            addViewObservers()
            addViewModelObservers()
        }

        zoneList = zoneMap.keys.toTypedArray()
        val zoneAdapter = context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, zoneList) }
        zoneAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerZone.adapter = zoneAdapter
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun addViewObservers(){
        binding.buttonCheck.setOnClickListener {
            /*binding.llRegistrationStep1.visibility = View.VISIBLE
            binding.buttonNext.visibility = View.VISIBLE
            binding.tvRegistrationStep.text = resources.getString(R.string.registration_step_1)*/
            registerDiwyangViewModel.checkAadharCard(binding.etAadharNumber.text.toString())
        }
        binding.tvAddParentDetails.setOnClickListener {
            binding.llParentDetailsContainer.visibility = View.VISIBLE
            getLocation()
        }
        binding.buttonNext.setOnClickListener {
            if (binding.llRegistrationStep1.visibility == View.VISIBLE){
                registerDiwyangViewModel.registerUser(binding.etFirstName.text.toString(),
                    binding.etMiddleName.text.toString(),
                    binding.etLastName.text.toString(),
                    binding.etAddress.text.toString(),
                    binding.etDOB.text.toString(),
                    binding.spinnerTypeOfDivyang.selectedItem.toString(),
                    binding.etMobileNumber.text.toString(),
                    latitude.toString(),
                    longitude.toString(),
                )
            }
            controlVisibility()
        }

        binding.etAadharNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(sa: Editable) {
        }
        })

        binding.buttonCapture.setOnClickListener {
            isProfileCameraCapture = true
            openCamera(PROFILE_PIC_ID)
        }

        binding.buttonUploadAadharFront.setOnClickListener {
            isProofCameraCapture = true
            openCamera(AADHAR_FRONT_ID)
        }
        binding.buttonUploadAadharBack.setOnClickListener {
            isProofCameraCapture = true
            openCamera(AADHAR_BACK_ID)
        }
        binding.buttonUploadUUIDcard.setOnClickListener {
            isProofCameraCapture = true
            openCamera(UDIDID)
        }

        binding.buttonScanFinger.setOnClickListener {
            isFPCapture = true
            startActivityForResult(Intent(activity, ActivityScanFingerprint::class.java), FINGERPRINT_SCAN)
        }
        binding.buttonScanLeftIris.setOnClickListener {
            isEyeCapture = true
            startActivityForResult(Intent(activity, ActivityScanIris::class.java), LEFT_EYE_SCAN)
        }
        binding.buttonScanRightIris.setOnClickListener {
            isEyeCapture = true
            startActivityForResult(Intent(activity, ActivityScanIris::class.java), RIGHT_EYE_SCAN)
        }

        binding.spinnerZone.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val wardlist = zoneMap[zoneList[position]]!!
                val aa = context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, wardlist.toTypedArray()) }
                aa?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerWard.adapter = aa
            }

        }
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        binding.etDOB.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if(MotionEvent.ACTION_UP == event?.action) {
                    val datePickerDialog = context?.let { it1 -> DatePickerDialog(it1,{ view, year, monthOfYear, dayOfMonth ->
                        binding.etDOB.setText("$dayOfMonth/${monthOfYear + 1}/ $year")
                    }, year, month, day) }
                    datePickerDialog?.show()
                }
                return false
            }
        })

        binding.cbParentAddress.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                binding.etParentAddress.text = binding.etAddress.text
            }else{
                binding.etParentAddress.setText("")
            }
        }

        binding.cbParentMobileNumber.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                binding.etParentMobileNumber.text = binding.etMobileNumber.text
            }else{
                binding.etParentMobileNumber.setText("")
            }
        }
    }

    private fun addViewModelObservers(){
        registerDiwyangViewModel.isAadharCardExists.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it){
                Toast.makeText(requireContext(), "Aadhar Card registration already present", Toast.LENGTH_LONG).show()
            }else{
                binding.llRegistrationStep1.visibility = View.VISIBLE
                binding.buttonNext.visibility = View.VISIBLE
                binding.tvRegistrationStep.text = resources.getString(R.string.registration_step_1)
            }
        })

        registerDiwyangViewModel.isApplicantDataSubmitted.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it){
                controlVisibility()
            }else{
                Toast.makeText(requireContext(), "Serve Error Please Try Later", Toast.LENGTH_LONG).show()

            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun controlVisibility(){
        if (binding.llRegistrationStep1.visibility == View.VISIBLE || isProfileCameraCapture){
            binding.llAadharContainer.visibility = View.GONE
            binding.llRegistrationStep1.visibility = View.GONE
            binding.llRegistrationStep2.visibility = View.VISIBLE
            binding.tvRegistrationStep.text = resources.getString(R.string.registration_step_2)
            isProfileCameraCapture = false
        } else if (binding.llRegistrationStep2.visibility == View.VISIBLE || isFPCapture){
            binding.llAadharContainer.visibility = View.GONE
            binding.llRegistrationStep1.visibility = View.GONE
            binding.llRegistrationStep2.visibility = View.GONE
            binding.llRegistrationStep3.visibility = View.VISIBLE
            binding.tvRegistrationStep.text = resources.getString(R.string.registration_step_3)
            isFPCapture =false
        }else if(binding.llRegistrationStep3.visibility==View.VISIBLE || isEyeCapture){
            binding.llAadharContainer.visibility = View.GONE
            binding.llRegistrationStep1.visibility = View.GONE
            binding.llRegistrationStep2.visibility = View.GONE
            binding.llRegistrationStep3.visibility = View.GONE
            binding.llRegistrationStep4.visibility = View.VISIBLE
            binding.tvRegistrationStep.text = resources.getString(R.string.registration_step_4)
            isEyeCapture = false
        }
        else if(binding.llRegistrationStep4.visibility==View.VISIBLE || isProofCameraCapture){
            binding.llAadharContainer.visibility = View.GONE
            binding.llRegistrationStep1.visibility = View.GONE
            binding.llRegistrationStep2.visibility = View.GONE
            binding.llRegistrationStep3.visibility = View.GONE
            binding.llRegistrationStep4.visibility = View.GONE
            binding.llRegistrationStep5.visibility = View.VISIBLE
            binding.tvRegistrationStep.text = resources.getString(R.string.registration_step_5)
            isProofCameraCapture = false
        }else if(binding.llRegistrationStep5.visibility==View.VISIBLE){
            Toast.makeText(activity, "Completed", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission(): Boolean {
        val result = activity?.applicationContext?.let { ContextCompat.checkSelfPermission(it, CAMERA) }
        return result == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun openCamera(pic_id : Int){
        if (checkPermission()){
            openImageIntent(pic_id)
        }else {
            cameraPermission.launch(CAMERA)
        }
    }


    private fun openImageIntent(pic_id : Int) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, pic_id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Match the request 'pic id with requestCode
        var photo : Bitmap? = null

        if (resultCode != 0){
            photo = data!!.extras!!["data"] as Bitmap?
        }
        if (requestCode == PROFILE_PIC_ID && resultCode != 0 ) {
            binding.profilePic.setImageBitmap(photo)
        }else if (requestCode == AADHAR_FRONT_ID && resultCode != 0 ) {
            binding.proofAadharFront.setImageBitmap(photo)
        }else if (requestCode == AADHAR_BACK_ID && resultCode != 0 ) {
            binding.proofAadharBack.setImageBitmap(photo)
        }else if (requestCode == UDIDID && resultCode != 0 ) {
            binding.proofUUIDCard.setImageBitmap(photo)
        }else if (requestCode == FINGERPRINT_SCAN && resultCode != 0 ) {
            binding.imgFingerPrint.setImageBitmap(photo)
        }else if (requestCode == LEFT_EYE_SCAN && resultCode != 0 ) {
            binding.ivLeftEye.setImageBitmap(photo)
            binding.leftEyeTick.visibility = View.VISIBLE
        }else if (requestCode == RIGHT_EYE_SCAN && resultCode != 0 ) {
            binding.ivRightEye.setImageBitmap(photo)
            binding.rightEyeTick.visibility = View.VISIBLE
        }
        controlVisibility()
    }

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2


    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        Log.e("Gaurav", "Latitude\\n${list[0].latitude}")
                        Log.e("Gaurav", "Longitude\n${list[0].longitude}}")
                        latitude = list[0].latitude
                        longitude = list[0].longitude
                        /*mainBinding.apply {
                            tvLatitude.text = "Latitude\n${list[0].latitude}"
                            tvLongitude.text = "Longitude\n${list[0].longitude}"
                            tvCountryName.text = "Country Name\n${list[0].countryName}"
                            tvLocality.text = "Locality\n${list[0].locality}"
                            tvAddress.text = "Address\n${list[0].getAddressLine(0)}"
                        }*/
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }


}