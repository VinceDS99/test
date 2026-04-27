package com.openclassrooms.eventorias.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.eventorias.data.model.Event
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton
import android.content.Context
import android.location.Geocoder
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.Locale
import java.util.UUID
import android.location.Address
import android.os.Build

@Singleton
class EventRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()

    // Retourne un Flow qui écoute les changements Firestore en temps réel
    fun getEvents(): Flow<List<Event>> = callbackFlow {
        val listener = firestore.collection("events")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // propage l'erreur au Flow
                    return@addSnapshotListener
                }
                val events = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(events)
            }
        // Quand le Flow est annulé, on arrête d'écouter Firestore
        awaitClose { listener.remove() }
    }


    suspend fun createEvent(event: Event, imageUri: Uri, context: Context): Result<Unit> {
        return try {
            // 1. Upload image sur Firebase Storage
            val imageUrl = uploadImage(imageUri)

            // 2. Geocoder : convertit l'adresse en coordonnées (pour usage futur)
            val geocoder = Geocoder(context, Locale.getDefault())


            val addresses = mutableListOf<Address>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(event.location, 1) { addresses.addAll(it) }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocationName(event.location, 1)?.let { addresses.addAll(it) }
            }

            // 3. Sauvegarde dans Firestore
            val eventWithImage = event.copy(imageUrl = imageUrl)
            firestore.collection("events")
                .add(eventWithImage)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadImage(uri: Uri): String {
        val storage = FirebaseStorage.getInstance()
        val ref = storage.reference
            .child("events/${UUID.randomUUID()}.jpg")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
}