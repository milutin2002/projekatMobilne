package com.example.proj

import com.example.projekatmobilne.viewModels.LocationViewModel



import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.projekatmobilne.model.ShoppingItem
import com.example.projekatmobilne.model.User
import com.example.projekatmobilne.viewModels.UserProfileViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage

object FirebaseUtil {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private var photoUri:Uri?=null
    fun fetchShoppingItems(userId: String,onSuccess: (List<ShoppingItem>) -> Unit, onFailure: (Exception) -> Unit) {
        val db = Firebase.firestore
        db.collection("shopping_items").whereEqualTo("userId",userId)
            .get()
            .addOnSuccessListener { result ->
                val items = result.map { document ->
                    document.toObject(ShoppingItem::class.java)
                }
                onSuccess(items)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    fun updateUserPoints(userId: String, points: Int) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val newPoints = snapshot.getLong("points")?.plus(points) ?: points.toLong()
            transaction.update(userRef, "points", newPoints)
        }.addOnSuccessListener {
            Log.d("Points", "User points updated successfully")
        }.addOnFailureListener { e ->
            Log.e("Points", "Failed to update user points", e)
        }
    }
    fun fetchUsers(onSuccess: (List<User>) -> Unit, onFailure: (Exception) -> Unit){
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .orderBy("points", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                onSuccess(result.toObjects(User::class.java))
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
    fun register(
        username: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        userProfileViewModel: UserProfileViewModel,
        uri: Uri,
        onFailure: (s:String) -> Unit,
        onComplete: () -> Unit
    ) {
        auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val storageRef = storage.reference.child("profile_images/${user?.uid}")
                storageRef.putFile(uri).addOnCompleteListener { uploadTask ->
                    if (uploadTask.isSuccessful) {
                        storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            userProfileViewModel.addUserProfile(user!!.uid, User(downloadUri.toString(), fullName, username, phoneNumber),onComplete)
                        }
                    } else {
                        task.exception?.message?.let { onFailure(it) }
                    }
                }
            } else {
                task.exception?.message?.let { onFailure(it) }
            }
        }
    }
    fun addItem(newItem: ShoppingItem,viewModel: LocationViewModel,onSuccess: (ShoppingItem) -> Unit, onFailure: (Exception) -> Unit){
        val db = FirebaseFirestore.getInstance()
        db.collection("shopping_items").add(newItem)
            .addOnSuccessListener { documentReference ->
                newItem.id = documentReference.id
                newItem.userId=viewModel.id.value
                db.collection("shopping_items").document(newItem.id)
                    .set(newItem)
                    .addOnSuccessListener {
                        onSuccess(newItem)
                        updateUserPoints(newItem.userId, 10)

                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun signIn(username: String, password: String, viewModel: LocationViewModel,onFailure: () -> kotlin.Unit,onComplete: (String,LocationViewModel) -> Unit) {
        auth.signInWithEmailAndPassword(username, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if(auth.currentUser!=null){
                    onComplete(auth.currentUser!!.uid,viewModel)
                }
            } else {
                onFailure();
            }
        }
    }
}