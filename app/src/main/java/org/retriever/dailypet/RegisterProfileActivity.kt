package org.retriever.dailypet

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kakao.sdk.user.UserApiClient
import org.retriever.dailypet.databinding.ActivityMainBinding
import org.retriever.dailypet.databinding.ActivityRegisterProfileBinding

class RegisterProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterProfileBinding
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var isValid:Boolean = false
    private var TAG = "REGISTER PROFILE"
    // Permissions
    val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val PERMISSIONS_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterProfileBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

//        UserApiClient.instance.me { user, error ->
//            if (error != null) {
//                Log.e(TAG, "사용자 정보 요청 실패", error)
//            }
//            else if (user != null) {
//                Log.d(TAG, "사용자 정보 요청 성공" +
//                        "\n회원번호: ${user.id}" +
//                        "\n이메일: ${user.kakaoAccount?.email}" +
//                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}")
//                binding.textRegisterProfileName.text = user.kakaoAccount?.profile?.nickname
//                binding.textRegisterProfileEmail.text = user.kakaoAccount?.email
//            }
//        }

        /* Camera Register */
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                Log.d(TAG, "RESULT_OK")
                val bitmap = it.data?.extras?.get("data") as Bitmap
                binding.imgRegisterProfile.setImageBitmap(bitmap)
            }
        }

        /* Upload Profile Image */
        binding.btnRegisterProfileUpload.setOnClickListener{
            Log.d(TAG, "Button Photo Upload")
            var popupMenu = PopupMenu(applicationContext, it)
            menuInflater.inflate(R.menu.camera_menu, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.camera->{
                        Log.d(TAG, "Select Camera")
                        takePicture()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.gallery->{
                        Log.d(TAG, "Select Gallery")
                        return@setOnMenuItemClickListener true
                    }
                    else->{
                        return@setOnMenuItemClickListener false
                    }
                }
            }
        }
        /* Check Nickname Validation */
        binding.btnRegisterProfileCheck.setOnClickListener{
            Log.d(TAG, "Button NickName Check")
            if(isValid){
                binding.textRegisterProfileValidate.text = "중복된 닉네임입니다. 다른 닉네임을 사용해주세요"
                binding.textRegisterProfileValidate.setTextColor(Color.RED)
                isValid = !isValid
            }
            else{
                binding.textRegisterProfileValidate.text = "사용가능한 닉네임입니다"
                binding.textRegisterProfileValidate.setTextColor(Color.BLUE)
                isValid = !isValid
            }
        }
        /* Submit Profile */
        binding.btnRegisterProfileSubmit.setOnClickListener{
            Log.d(TAG, "Button Register")
            val nextIntent = Intent(this, RegisterProfileActivity::class.java)
            startActivity(nextIntent)
        }
    }

    fun takePicture(){
        if(checkPermissions(PERMISSIONS)) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(intent)
        }
    }

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


}