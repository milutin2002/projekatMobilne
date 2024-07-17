package com.example.projekatmobilne.utils


import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage

object FirebaseUtil {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private var photoUri:Uri?=null
    fun register(username: String, password: String, fullName: String, phoneNumber: String, onComplete: () -> Unit) {
        auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val profileUpdates = userProfileChangeRequest {
                    displayName = fullName
                }
                user?.updateProfile(profileUpdates)
                photoUri?.let { uri ->
                    val storageRef = storage.reference.child("profile_images/${user?.uid}")
                    storageRef.putFile(uri).addOnCompleteListener { uploadTask ->
                        if (uploadTask.isSuccessful) {
                            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                user?.updateProfile(userProfileChangeRequest {
                                    photoUri = downloadUri
                                })
                                onComplete()
                            }
                        } else {
                            println("Error")

                        }
                    }
                } ?: onComplete()
            } else {

            }
        }
    }

    fun signIn(username: String, password: String, onComplete: () -> Unit) {
        auth.signInWithEmailAndPassword(username, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete()
            } else {
                // Handle sign-in failure
            }
        }
    }
}