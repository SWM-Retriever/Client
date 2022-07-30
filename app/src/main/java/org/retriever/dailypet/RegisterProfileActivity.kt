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
import org.retriever.dailypet.databinding.ActivityMainBinding
import org.retriever.dailypet.databinding.ActivityRegisterProfileBinding

class RegisterProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterProfileBinding
    var isValid:Boolean = false
    var TAG = "REGISTER PROFILE"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterProfileBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

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


}