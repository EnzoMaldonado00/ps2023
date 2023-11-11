package com.maldEnz.ps.presentation.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.maldEnz.ps.databinding.ActivitySendImageMsgBinding
import com.maldEnz.ps.presentation.mvvm.viewmodel.FriendViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.android.ext.android.inject
import java.util.UUID

class SendImageMsgActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySendImageMsgBinding
    private lateinit var friendUid: String
    private lateinit var chatId: String
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserUid: String
    private var imageUri: Uri? = null
    private val friendViewModel: FriendViewModel by inject()
    private val userViewModel: UserViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendImageMsgBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        currentUserUid = auth.currentUser!!.uid
        friendUid = intent.getStringExtra("friendUid") ?: ""
        chatId = intent.getStringExtra("chatId") ?: ""
        friendViewModel.loadFriendData(friendUid)
        friendViewModel.friend.observe(this) {
            binding.friendName.text = it.userName
        }
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)

        binding.btnSend.setOnClickListener {
            val senderId = currentUserUid
            if (imageUri != null) {
                val randomUUID = UUID.randomUUID().toString()
                val imageName = "msgImage/$randomUUID.jpg"
                val imageRef = FirebaseStorage.getInstance().reference.child(imageName)
                val uploadTask: UploadTask = imageRef.putFile(imageUri!!)

                uploadTask.addOnSuccessListener {
                    imageRef.downloadUrl.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val imageURL = it.result.toString()
                            userViewModel.sendMessage(
                                chatId,
                                "",
                                senderId,
                                currentUserUid,
                                friendUid,
                                imageURL,
                            )
                        }
                    }
                }
                finish()
            }
        }
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    imageUri = data.data
                    Glide.with(this)
                        .load(imageUri)
                        .into(binding.imageMsg)
                }
            } else {
                finish()
            }
        }
}
