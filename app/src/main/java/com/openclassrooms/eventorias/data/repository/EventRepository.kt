package com.openclassrooms.eventorias.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.eventorias.data.model.Event
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

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
}