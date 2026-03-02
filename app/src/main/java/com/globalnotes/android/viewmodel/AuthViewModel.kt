package com.globalnotes.android.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()

    var authState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

    val currentUser get() = auth.currentUser

    // ── Email / Password ────────────────────────────────────────────────────

    fun signIn(email: String, password: String) {
        val error = validate(email, password)
        if (error != null) { authState = AuthState.Error(error); return }

        authState = AuthState.Loading
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email.trim(), password).await()
                authState = AuthState.Success
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                authState = AuthState.Error("Incorrect email or password.")
            } catch (e: Exception) {
                authState = AuthState.Error(e.localizedMessage ?: "Sign in failed. Please try again.")
            }
        }
    }

    fun signUp(email: String, password: String) {
        val error = validate(email, password)
        if (error != null) { authState = AuthState.Error(error); return }

        authState = AuthState.Loading
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email.trim(), password).await()
                authState = AuthState.Success
            } catch (e: FirebaseAuthWeakPasswordException) {
                authState = AuthState.Error("Password must be at least 6 characters.")
            } catch (e: FirebaseAuthUserCollisionException) {
                authState = AuthState.Error("An account with this email already exists.")
            } catch (e: Exception) {
                authState = AuthState.Error(e.localizedMessage ?: "Sign up failed. Please try again.")
            }
        }
    }

    // ── Google Sign-In ──────────────────────────────────────────────────────

    fun signInWithGoogle(activityContext: Context) {
        authState = AuthState.Loading
        viewModelScope.launch {
            try {
                val credentialManager = CredentialManager.create(activityContext)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(WEB_CLIENT_ID)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(activityContext, request)
                val credential = GoogleIdTokenCredential.createFrom(result.credential.data)

                val firebaseCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
                auth.signInWithCredential(firebaseCredential).await()
                authState = AuthState.Success

            } catch (e: GetCredentialCancellationException) {
                authState = AuthState.Idle // user cancelled — no error shown
            } catch (e: NoCredentialException) {
                authState = AuthState.Error("No Google account found on this device.")
            } catch (e: Exception) {
                authState = AuthState.Error(e.localizedMessage ?: "Google sign-in failed.")
            }
        }
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    fun resetError() {
        if (authState is AuthState.Error) authState = AuthState.Idle
    }

    fun resetState() {
        authState = AuthState.Idle
    }

    private fun validate(email: String, password: String): String? = when {
        email.isBlank()      -> "Please enter your email."
        !email.contains('@') -> "Please enter a valid email address."
        password.isBlank()   -> "Please enter your password."
        password.length < 6  -> "Password must be at least 6 characters."
        else                 -> null
    }

    companion object {
        // ⚠️ Replace with your Web Client ID:
        // Firebase Console → Authentication → Sign-in method → Google
        // → expand → Web SDK configuration → Web client ID
        private const val WEB_CLIENT_ID = "770911222427-kdv4e78lkp8j5hl2lma1kbg1v65ftsqr.apps.googleusercontent.com"
    }
}
