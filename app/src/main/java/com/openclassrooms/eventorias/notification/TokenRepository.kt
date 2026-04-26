package com.openclassrooms.eventorias.notification

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

object TokenRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Récupère le token actuel et le sauvegarde
    fun refreshAndSaveToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            saveToken(token)
        }
    }

    fun saveToken(token: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(uid)
            .update("fcmToken", token)
            .addOnFailureListener {
                // Si le document n'existe pas encore, on le crée
                firestore.collection("users")
                    .document(uid)
                    .set(mapOf("fcmToken" to token))
            }
    }
}