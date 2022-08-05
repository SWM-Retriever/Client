package org.retriever.dailypet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.retriever.dailypet.databinding.ActivityFamilyEntranceBinding
import org.retriever.dailypet.databinding.ActivitySelectFamilyTypeBinding

class FamilyEntranceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFamilyEntranceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyEntranceBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        /* 가족 생성 버튼 */
        binding.btnCreateFamily.setOnClickListener{
            val nextIntent = Intent(this, CreateFamilyActivity::class.java)
            startActivity(nextIntent)
        }
        /* 초대코드 페이지 버튼 */
        binding.btnToInvitePage.setOnClickListener{
            val nextIntent = Intent(this, FindFamilyActivity::class.java)
            startActivity(nextIntent)
        }
    }
}