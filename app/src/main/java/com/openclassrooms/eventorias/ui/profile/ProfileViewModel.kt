package com.openclassrooms.eventorias.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.eventorias.notification.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _isUploadingPhoto = MutableStateFlow(false)
    val isUploadingPhoto: StateFlow<Boolean> = _isUploadingPhoto

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val user = auth.currentUser
        if (user == null) {
            _uiState.value = ProfileUiState.Error("Utilisateur non connecté")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = ProfileUiState.Loading

                val doc = firestore.collection("users")
                    .document(user.uid)
                    .get()
                    .await()

                val notificationsEnabled = doc.getBoolean("notificationsEnabled") ?: false

                // Priorité : photo dans Storage (via Auth) sinon vide
                _uiState.value = ProfileUiState.Success(
                    displayName = user.displayName ?: "Nom inconnu",
                    email = user.email ?: "Email inconnu",
                    photoUrl = user.photoUrl?.toString() ?: "",
                    notificationsEnabled = notificationsEnabled
                )
            } catch (e: Exception) {
                val user2 = auth.currentUser
                _uiState.value = ProfileUiState.Success(
                    displayName = user2?.displayName ?: "Nom inconnu",
                    email = user2?.email ?: "Email inconnu",
                    photoUrl = user2?.photoUrl?.toString() ?: "",
                    notificationsEnabled = false
                )
            }
        }
    }

    fun onPhotoSelected(uri: Uri) {
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                _isUploadingPhoto.value = true

                // Chemin fixe par utilisateur — écrase toujours la même photo
                val ref = storage.reference
                    .child("profiles/${user.uid}/avatar.jpg")

                ref.putFile(uri).await()
                val downloadUrl = ref.downloadUrl.await().toString()

                // Met à jour Firebase Auth avec la nouvelle URL
                val profileUpdates = userProfileChangeRequest {
                    photoUri = Uri.parse(downloadUrl)
                }
                user.updateProfile(profileUpdates).await()

                // Met à jour l'état local
                val current = _uiState.value as? ProfileUiState.Success ?: return@launch
                _uiState.value = current.copy(photoUrl = downloadUrl)

            } catch (e: Exception) {
                // Silencieux — photo actuelle reste affichée
            } finally {
                _isUploadingPhoto.value = false
            }
        }
    }

    fun onNotificationsToggle(enabled: Boolean) {
        val current = _uiState.value as? ProfileUiState.Success ?: return
        _uiState.value = current.copy(notificationsEnabled = enabled)

        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                if (enabled) {
                    // Réactive : récupère et sauvegarde le token
                    TokenRepository.refreshAndSaveToken()
                    firestore.collection("users")
                        .document(uid)
                        .update("notificationsEnabled", true)
                        .await()
                } else {
                    // Désactive : supprime le token pour ne plus recevoir de notifs
                    firestore.collection("users")
                        .document(uid)
                        .update(mapOf(
                            "notificationsEnabled" to false,
                            "fcmToken" to null
                        ))
                        .await()
                }
            } catch (e: Exception) {
                // Silencieux
            }
        }
    }

    fun retry() {
        loadProfile()
    }

}