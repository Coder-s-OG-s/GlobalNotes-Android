package com.globalnotes.android.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globalnotes.android.ui.theme.*

@Composable
fun AuthScreen(onBack: () -> Unit = {}, onAuthSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignIn by remember { mutableStateOf(true) }

    Surface(
        color = OnboardingBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBack) { // Reverted to original onBack() as pagerState and scope are not defined in AuthScreen
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AuthTextPrimary // Reverted to original tint
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Logo Section
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(AuthButtonBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = null,
                    tint = OnboardingDotActive,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Global Notes",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                ),
                color = AuthTextPrimary
            )
            Text(
                text = "Your premium digital workspace",
                style = MaterialTheme.typography.bodyMedium,
                color = AuthTextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Auth Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = AuthCardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        TabItem("Sign In", isSelected = isSignIn) { isSignIn = true }
                        TabItem("Sign Up", isSelected = !isSignIn) { isSignIn = false }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Input Fields
                    AuthInputLabel("Email")
                    AuthTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "journal@example.com"
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Password",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = AuthTextPrimary
                        )
                        Text(
                            "Forgot Password?",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = AuthAccent,
                            modifier = Modifier.clickable { }
                        )
                    }
                    AuthTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Enter password",
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Sign In Button
                    Button(
                        onClick = onAuthSuccess,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .shadow(8.dp, RoundedCornerShape(32.dp)),
                        shape = RoundedCornerShape(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AuthButtonBackground)
                    ) {
                        Text(
                            if (isSignIn) "Sign In" else "Sign Up",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(AuthInputBorder.copy(alpha = 0.5f)))
                        Text(
                            "  OR CONTINUE WITH  ",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.5.sp),
                            color = AuthTextSecondary
                        )
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(AuthInputBorder.copy(alpha = 0.5f)))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Google Button
                    Surface(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(32.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, AuthInputBorder.copy(alpha = 0.5f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "G ", 
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF4285F4)
                                )
                            )
                            Text(
                                "Google",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = AuthTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Pro Badge
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, OnboardingDotActive.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    .clickable { },
                color = OnboardingDotActive.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(OnboardingDotActive))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Free Plan — Upgrade to Pro",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = OnboardingDotActive
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FooterLink("Help")
                FooterLink("Privacy")
                FooterLink("Terms")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TabItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) AuthTextPrimary else AuthTextSecondary
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(40.dp)
                    .height(2.dp)
                    .background(AuthTextPrimary)
            )
        }
    }
}

@Composable
fun AuthInputLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        color = AuthTextPrimary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, AuthInputBorder, RoundedCornerShape(16.dp)),
        color = AuthInputBackground,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge,
                        color = AuthTextSecondary
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = AuthTextPrimary),
                    visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
                    singleLine = true
                )
            }
            if (isPassword) {
                Icon(
                    imageVector = Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = AuthTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun FooterLink(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = AuthTextSecondary,
        modifier = Modifier.padding(horizontal = 12.dp).clickable { }
    )
}
