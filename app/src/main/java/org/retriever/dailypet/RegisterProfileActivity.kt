package org.retriever.dailypet

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast

class RegisterProfileActivity : AppCompatActivity() {
    var isValid = false
    var TAG = "RegisterProfile"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_profile)

        var photoUploadButton = findViewById<Button>(R.id.btn_registerProfileUpload)
        var nicknameCheckButton = findViewById<Button>(R.id.btn_registerProfileCheck)
        var validateText = findViewById<TextView>(R.id.text_registerProfileValidate)
        var registerButton = findViewById<Button>(R.id.btn_registerProfileSubmit)

        photoUploadButton.setOnClickListener{
            Log.d(TAG, "Button Photo Upload")
            var popupMenu = PopupMenu(applicationContext, it)
            menuInflater.inflate(R.menu.camera_menu, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.camera->{
                        Log.d(TAG, "Select Camera")
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
        nicknameCheckButton.setOnClickListener{
            Log.d(TAG, "Button NickName Check")
            if(isValid){
                validateText.setText("중복된 닉네임입니다. 다른 닉네임을 사용해주세요")
                validateText.setTextColor(Color.RED)
                isValid = false
            }
            else{
                validateText.setText("사용가능한 닉네임입니다")
                validateText.setTextColor(Color.BLUE)
                isValid = true;
            }
        }
        registerButton.setOnClickListener{
            Log.d(TAG, "Button Register")
            val nextIntent = Intent(this, RegisterProfileActivity::class.java)
            startActivity(nextIntent)
        }

    }
}