package com.example.cosmorun

import android.content.Context
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.UUID
import android.content.SharedPreferences


class ScoreManager(private val context: Context) {
    private val database = FirebaseDatabase.getInstance("https://cosmorun-d5d84-default-rtdb.europe-west1.firebasedatabase.app")
    private val auth = Firebase.auth
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("CosmoRunPrefs", Context.MODE_PRIVATE)


    private val DEVICE_ID_KEY = "device_id"


    private val deviceId: String
        get() {
            var id = sharedPreferences.getString(DEVICE_ID_KEY, null)
            if (id == null) {
                id = UUID.randomUUID().toString()
                sharedPreferences.edit().putString(DEVICE_ID_KEY, id).apply()
            }
            return id
        }


    private val currentUserId: String
        get() = auth.currentUser?.uid ?: deviceId


    suspend fun ensureUserAuthenticated() {
        if (auth.currentUser == null) {
            try {

                auth.signInAnonymously().await()
                Log.d("ScoreManager", "Autoryzacja anonimowa zakończona sukcesem")
            } catch (e: Exception) {
                Log.e("ScoreManager", "Błąd autoryzacji anonimowej", e)

            }
        }
    }


    private fun getUserHighScoreRef(): DatabaseReference {
        return database.getReference("users").child(currentUserId).child("high-score")
    }


    fun saveHighScore(score: Int, onComplete: (Boolean) -> Unit) {
        val ref = getUserHighScoreRef()


        ref.get().addOnSuccessListener { dataSnapshot ->
            val currentHighScore = dataSnapshot.getValue(Int::class.java) ?: 0


            if (score > currentHighScore) {
                ref.setValue(score).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("ScoreManager", "Rekord został pomyślnie zapisany: $score")
                        onComplete(true)
                    } else {
                        Log.e("ScoreManager", "Błąd podczas zapisywania rekordu", task.exception)
                        onComplete(false)
                    }
                }
            } else {

                onComplete(true)
            }
        }.addOnFailureListener { exception ->
            Log.e("ScoreManager", "Błąd podczas pobierania bieżącego rekordu", exception)
            onComplete(false)
        }
    }


    fun loadHighScore(onResult: (Int) -> Unit) {
        getUserHighScoreRef().get().addOnSuccessListener { dataSnapshot ->
            val highScore = dataSnapshot.getValue(Int::class.java) ?: 0
            onResult(highScore)
            Log.d("ScoreManager", "Rekord przesłany: $highScore")
        }.addOnFailureListener { exception ->
            Log.e("ScoreManager", "Błąd ładowania rekordu", exception)
            onResult(0)
        }
    }


    fun addHighScoreListener(onScoreChanged: (Int) -> Unit): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newHighScore = snapshot.getValue(Int::class.java) ?: 0
                onScoreChanged(newHighScore)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ScoreManager", "Błąd śledzenia rekordu: ${error.message}")
            }
        }

        getUserHighScoreRef().addValueEventListener(listener)
        return listener
    }


    fun removeHighScoreListener(listener: ValueEventListener) {
        getUserHighScoreRef().removeEventListener(listener)
    }




}