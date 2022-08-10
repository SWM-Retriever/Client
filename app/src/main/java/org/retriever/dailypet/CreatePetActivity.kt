/** @author Sehun Ahn **/
package org.retriever.dailypet

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
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
import org.retriever.dailypet.databinding.ActivityCreatePetBinding
import org.retriever.dailypet.interfaces.RetrofitService
import org.retriever.dailypet.models.General
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.*

class CreatePetActivity : AppCompatActivity() {
    private var backKeyPressedTime : Long = 0
    private lateinit var binding : ActivityCreatePetBinding
    private lateinit var retrofit : Retrofit
    private lateinit var retrofitService : RetrofitService
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private var isValidPetName = false
    private val TAG = "CREATE PET ACTIVITY"
    private lateinit var BASE_URL: String
    private lateinit var KEY : String
    private lateinit var HOST : String
    private var bitmap : Bitmap? = null
    private val CODE_NICKNAME = 200
    private val CODE_FAIL = 400
    private var DOG = false
    private var CAT = false
    private var MIX = false
    private var UNKOWN = false
    // Permissions
    val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val PERMISSIONS_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePetBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        BASE_URL = getString(R.string.URL)
        KEY = getString(R.string.KEY)
        HOST = getString(R.string.HOST)

        /* Pop-up Calender */
        binding.editTextBirth.setOnClickListener{
            it.setOnClickListener{
                val cal = Calendar.getInstance()
                val dateSetListener = DatePickerDialog.OnDateSetListener{
                        view, year, month, day ->
                    val dateString = "${year}년 ${month + 1}월 ${day}일"
                    binding.editTextBirth.setText(dateString)
                }
                DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        }

        /* Pop-up Search */
        binding.editTextBreed.setOnClickListener{
            BreedSearchDialog(applicationContext){}.show()
        }

        init()
    }

    private fun init() = with(binding){
        /* API Init */
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitService = retrofit.create(RetrofitService::class.java)

        /* Camera Register */
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                Log.d(TAG, "Get Image from Gallery")
                bitmap = it.data?.extras?.get("data") as Bitmap
                bitmap = Bitmap.createScaledBitmap(bitmap!!, 300, 300, true)
                imgCreatePetPhoto.setImageBitmap(bitmap)
            }
        }
        /* Gallery Register */
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
                    imgCreatePetPhoto.setImageBitmap(bitmap)
                } catch (e: Exception) { e.printStackTrace() }
            }
        }

        /* Upload Profile Image */
        btnCreatePetProfileLoad.setOnClickListener{
            Log.d(TAG, "Button Photo Upload")
            var popupMenu = PopupMenu(applicationContext, it)
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

        /* Check Pet Name Validation */
        btnPetNameCheck.setOnClickListener{
            Log.d(TAG, "Button Nickname Check")
            val petName = binding.editTextPetName.text.toString()
            if(petName == ""){
                textPetNameValidate.text = "반려동물 이름을 입력해주세요"
                textPetNameValidate.setTextColor(Color.RED)
                isValidPetName = false
            }
            else checkValidPetName(petName)
        }

        /* Pet Type */
        btnPetTypeDog.setOnClickListener{
            btnPetTypeDog.setBackgroundColor(getColor(R.color.main_pink))
            btnPetTypeCat.setBackgroundColor(getColor(R.color.main_blue))
            DOG = true
            CAT = false
        }
        btnPetTypeCat.setOnClickListener{
            btnPetTypeCat.setBackgroundColor(getColor(R.color.main_pink))
            btnPetTypeDog.setBackgroundColor(getColor(R.color.main_blue))
            DOG = false
            CAT = true
        }

        /* Pet Breed */
        btnPetBreedMix.setOnClickListener{
            btnPetBreedMix.setBackgroundColor(getColor(R.color.main_pink))
            btnPetBreedUnknown.setBackgroundColor(getColor(R.color.main_blue))
            MIX = true
            UNKOWN = false
        }
        btnPetBreedUnknown.setOnClickListener{
            btnPetBreedUnknown.setBackgroundColor(getColor(R.color.main_pink))
            btnPetBreedMix.setBackgroundColor(getColor(R.color.main_blue))
            MIX = false
            UNKOWN = true
        }

        /* Submit Profile */
        btnCreatePetSubmit.setOnClickListener{
            Log.d(TAG, "Button Create")
            if(isValidPetName ){
                val petName = editTextPetName.text.toString()
                val weight = editTextPetWeight.text.toString()
                postPetInfo(petName, weight)
            } else{
                Toast.makeText(applicationContext, "필수항목을 모두 작성해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /* 사진 찍기 */
    private fun takePicture(){
        if(checkPermissions(PERMISSIONS)) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        }
    }

    /* 갤러리 조회 */
    private fun openGallery(){
        if(checkPermissions(PERMISSIONS)) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            galleryLauncher.launch(intent)
        }
    }

    private fun checkValidPetName(petName : String){
        val call = retrofitService.postCheckPetName(KEY, HOST, petName)
        call.enqueue(object : Callback<General> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<General>, response: Response<General>) {
                val result: String = response.body().toString()
                Log.d(TAG, "CODE = ${response.code()}")
                Log.d(TAG, result)
                if(response.isSuccessful) {
                    if(response.code() == CODE_NICKNAME){ // 유효한 닉네임
                        binding.textPetNameValidate.text = "사용가능한 반려동물 이름입니다"
                        binding.textPetNameValidate.setTextColor(Color.BLUE)
                        isValidPetName = true
                    }
                }
                else{
                    if(response.code() == CODE_FAIL){ // 유효하지 않은 닉네임
                        binding.textPetNameValidate.text = "이미 존재하는 반려동물 이름입니다"
                        binding.textPetNameValidate.setTextColor(Color.RED)
                    }
                }
            }
            override fun onFailure(call: Call<General>, t: Throwable) {
                Log.e(TAG, "연결 실패")
            }
        })
    }

    /* 반려동물 등록 */
    private fun postPetInfo(petName : String, weight : String){
        val call : Call<General>
        if(bitmap != null){
            val bitmapRequestBody = bitmap!!.let { BitmapRequestBody(it) }
            val bitmapMultipartBody: MultipartBody.Part =
                MultipartBody.Part.createFormData("image", "petImage", bitmapRequestBody)
            call = retrofitService.postProfileWithImage(KEY, HOST, petName, weight, bitmapMultipartBody)
        } else{
            call = retrofitService.postProfile(KEY, HOST, petName, weight)
        }

        call.enqueue(object : Callback<General> {
            override fun onResponse(call: Call<General>, response: Response<General>) {
                val result: String = response.body().toString()
                Log.d(TAG, "CODE = ${response.code()}")
                Log.d(TAG, result)
                if(response.isSuccessful) {
                    if(response.code() == 200){ // 반려동물 등록 성공
                        Toast.makeText(applicationContext, "반려동물 등록에 성공하였습니다", Toast.LENGTH_SHORT).show()
                        val nextIntent = Intent(applicationContext, CreationCompleteActivity::class.java)
                        startActivity(nextIntent) // 등록 완료 페이지로 이동
                    }
                }
                else{
                    if(response.code() == CODE_FAIL){ // 반려동물 등록 실패
                        Toast.makeText(applicationContext, "반려동물 등록에 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<General>, t: Throwable) {
                Log.e(TAG, "연결 실패")
            }
        })
    }

    inner class BitmapRequestBody(private val bitmap: Bitmap) : RequestBody() {
        override fun contentType(): MediaType = "image/jpeg".toMediaType()
        override fun writeTo(sink: BufferedSink) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 99, sink.outputStream())
        }
    }

    /* 권한 허용 확인 및 요청 */
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
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() > backKeyPressedTime + 2500){
            backKeyPressedTime = System.currentTimeMillis()
            return
        } else{
            finishAffinity()
        }
    }
}