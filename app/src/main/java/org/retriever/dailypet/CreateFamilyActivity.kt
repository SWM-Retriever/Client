package org.retriever.dailypet

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import org.retriever.dailypet.databinding.ActivityCreateFamilyBinding
import org.retriever.dailypet.interfaces.RetrofitService
import org.retriever.dailypet.models.General
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class CreateFamilyActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateFamilyBinding
    private lateinit var retrofit : Retrofit
    private lateinit var retrofitService : RetrofitService
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var BASE_URL: String
    private lateinit var KEY : String
    private lateinit var HOST : String
    private var SUBMIT = false
    private var bitmap : Bitmap? = null
    private var isValidGroupname : Boolean = false
    private val TAG = "CREATE GROUP ACTIVITY"
    private val CODE_NICKNAME = 200
    private val CODE_GROUP = 200
    private val CODE_FAIL = 400
    // Permissions
    val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val PERMISSIONS_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*binding = ActivityCreateFamilyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        BASE_URL = BuildConfig.BASE_URL
        KEY = getString(R.string.KEY)
        HOST = getString(R.string.HOST)

        *//* API Init *//*
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitService = retrofit.create(RetrofitService::class.java)

        *//* Camera Register *//*
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                Log.d(TAG, "Get Image from Gallery")
                bitmap = it.data?.extras?.get("data") as Bitmap
                bitmap = Bitmap.createScaledBitmap(bitmap!!, 300, 300, true)
                binding.imgPhoto.setImageBitmap(bitmap)
            }
        }
        *//* Gallery Register *//*
        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                Log.d(TAG, "RESULT_OK")
                val imageData: Uri? = it.data?.data
                try {
                    Log.d(TAG, "Get Image from Camera")
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageData)
                    bitmap = Bitmap.createScaledBitmap(bitmap!!, 300, 300, true)
                    binding.imgPhoto.setImageBitmap(bitmap)
                } catch (e: Exception) { e.printStackTrace() }
            }
        }

        *//* Text Listener *//*
        binding.editTextGroupNickname.addTextChangedListener(object : TextWatcher {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun afterTextChanged(p0: Editable?) {
                submitCheck()
                if(binding.editTextGroupNickname.text.isNotBlank()){
                    binding.editTextGroupNickname.background = applicationContext.getDrawable(R.drawable.whiteblue_click_button)
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        init()*/
    }

    /*@SuppressLint("UseCompatLoadingForDrawables")
    private fun init() = with(binding){
        *//* Upload Profile Image *//*
        btnCamera.setOnClickListener{
            Log.d(TAG, "Button Photo Upload")
            val popupMenu = PopupMenu(applicationContext, it)
            menuInflater.inflate(R.menu.camera_menu, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.camera->{ // 카메라
                        Log.d(TAG, "Select Camera")
                        takePicture()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.gallery->{ // 갤러리
                        Log.d(TAG, "Select Gallery")
                        openGallery()
                        return@setOnMenuItemClickListener true
                    }
                    else->{
                        return@setOnMenuItemClickListener false
                    }
                }
            }
        }

        *//* Check Group Name Validation *//*
        btnValidCheck.setOnClickListener{
            val groupName = editTextGroupName.text.toString()
            if(groupName.isBlank()){
                textValidate.text = "올바른 그룹 이름을 입력해주세요"
                textValidate.setTextColor(applicationContext.getColor(R.color.fail_red))
                editTextGroupName.background = applicationContext.getDrawable(R.drawable.fail_edittext)
                isValidGroupname = false
            }
            else checkValidGroupname(groupName)
            submitCheck()
        }

        *//* Submit Profile *//*
        btnCreate.setOnClickListener{
            Log.d(TAG, "Button Register")
            val nickname = editTextGroupNickname.text.toString()
            if(nickname.isBlank()) {
                Toast.makeText(applicationContext, "그룹 내 닉네임을 작성해주세요", Toast.LENGTH_SHORT).show()
            }
            else if(!isValidGroupname){
                Toast.makeText(applicationContext, "그룹 이름 중복검사를 해주세요", Toast.LENGTH_SHORT).show()
            }
            else{
                val groupName = editTextGroupName.text.toString()
                postFamilyInfo(groupName, nickname)
            }

        }

        *//* 이전버튼 *//*
        imgbtnBack.setOnClickListener{
            onBackPressed()
        }
    }

    *//* 사진 찍기 *//*
    private fun takePicture(){
        if(checkPermissions(PERMISSIONS)) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        }
    }

    *//* 갤러리 조회 *//*
    private fun openGallery(){
        if(checkPermissions(PERMISSIONS)) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            galleryLauncher.launch(intent)
        }
    }

    private fun checkValidGroupname(grouptName : String){
        val call = retrofitService.postCheckFamilyName(KEY, HOST, grouptName)
        call.enqueue(object : Callback<General> {
            @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
            override fun onResponse(call: Call<General>, response: Response<General>) {
                val result: String = response.body().toString()
                Log.d(TAG, "CODE = ${response.code()}")
                Log.d(TAG, result)
                if(response.isSuccessful) {
                    if(response.code() == CODE_NICKNAME){ // 유효한 이름
                        binding.textValidate.text = "사용가능한 그룹 이름입니다"
                        binding.textValidate.setTextColor(applicationContext.getColor(R.color.success_blue))
                        binding.editTextGroupName.background = applicationContext.getDrawable(R.drawable.success_edittext)
                        isValidGroupname = true
                    }
                }
                else{
                    if(response.code() == CODE_FAIL){ // 유효하지 않은 이름
                        binding.textValidate.text = "이미 사용중인 그룹이름입니다\n다른 이름을 사용해주세요"
                        binding.textValidate.setTextColor(applicationContext.getColor(R.color.fail_red))
                        binding.editTextGroupName.background = applicationContext.getDrawable(R.drawable.fail_edittext)
                        isValidGroupname = false
                    }
                }
            }
            override fun onFailure(call: Call<General>, t: Throwable) {
                Log.e(TAG, "연결 실패")
            }
        })
        submitCheck()
    }

    *//* 프로필 등록 *//*
    private fun postFamilyInfo(familyName : String, nickname : String){
        val call : Call<General>
        if(bitmap != null){
            val bitmapRequestBody = bitmap!!.let { BitmapRequestBody(it) }
            val bitmapMultipartBody: MultipartBody.Part =
                MultipartBody.Part.createFormData("image", "familyImage", bitmapRequestBody)
            call = retrofitService.postFamily(KEY, HOST, familyName, nickname, bitmapMultipartBody)
        } else{
            call = retrofitService.postFamily(KEY, HOST, familyName, nickname, null)
        }

        call.enqueue(object : Callback<General> {
            override fun onResponse(call: Call<General>, response: Response<General>) {
                val result: String = response.body().toString()
                Log.d(TAG, "CODE = ${response.code()}")
                Log.d(TAG, result)
                if(response.isSuccessful) {
                    if(response.code() == CODE_GROUP){ // 프로필 등록 성공
                        Toast.makeText(applicationContext, "그룹이 성공적으로 생성되었습니다", Toast.LENGTH_SHORT).show()
                        val nextIntent = Intent(applicationContext, CreatePetActivity::class.java)
                        startActivity(nextIntent) // 가족유형 선택 페이지로 이동
                    }
                }
                else{
                    if(response.code() == CODE_FAIL){ // 프로필 등록 실패
                        Toast.makeText(applicationContext, "그룹 생성에 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<General>, t: Throwable) {
                Log.e(TAG, "연결 실패")
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun submitCheck(){
        val name = isValidGroupname
        val nickname = binding.editTextGroupNickname.text.isNotBlank()
        SUBMIT = name && nickname
        if(SUBMIT){
            binding.btnCreate.background = applicationContext.getDrawable(R.drawable.blue_button)
            binding.btnCreate.setTextColor(applicationContext.getColor(R.color.white))
        } else{
            binding.btnCreate.background = applicationContext.getDrawable(R.drawable.grey_button)
            binding.btnCreate.setTextColor(applicationContext.getColor(R.color.grey))
        }
    }

    inner class BitmapRequestBody(private val bitmap: Bitmap) : RequestBody() {
        override fun contentType(): MediaType = "image/jpeg".toMediaType()
        override fun writeTo(sink: BufferedSink) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 99, sink.outputStream())
        }
    }

    *//* 권한 허용 확인 및 요청 *//*
    private fun checkPermissions(permissions: Array<String>): Boolean {
        val permissionList : MutableList<String> = mutableListOf()
        for(permission in permissions){
            val result = ContextCompat.checkSelfPermission(this, permission)
            if(result != PackageManager.PERMISSION_GRANTED){
                permissionList.add(permission)
            }
        }
        if(permissionList.isNotEmpty()){
            ActivityCompat.requestPermissions(this, permissionList.toTypedArray(), PERMISSIONS_REQUEST) // 권한요청
            return false
        }
        return true
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for(result in grantResults){
            if(result != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "카메라 권한이 없습니다\n카메라 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }*/
}