package com.anyflow.journey.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anyflow.journey.ui.Brand
import com.anyflow.journey.ui.JourneyViewModel
import com.anyflow.journey.ui.WineButton

/**
 * Lubang mockup: tidak ada layar login di prototype. Endpoint API butuh token
 * Sanctum, jadi layar ini wajib. Akun demo sudah terisi supaya demo cepat.
 */
@Composable
fun LoginScreen(vm: JourneyViewModel) {
    var email by remember { mutableStateOf("samuel.wong@student.anyflow.site") }
    var password by remember { mutableStateOf("journey##keren") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brand.Canvas)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 56.dp),
    ) {
        Text(text = "Journey", style = MaterialTheme.typography.headlineLarge, color = Brand.Wine)
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Walk with God in the everyday — prayer, reflection, and your circles.",
            style = MaterialTheme.typography.bodyLarge,
            color = Brand.Muted,
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
        )

        val error = vm.authError
        if (error != null) {
            Spacer(Modifier.height(12.dp))
            Text(text = error, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))

        WineButton(
            text = if (vm.authBusy) "Signing in…" else "Sign in",
            loading = vm.authBusy,
            modifier = Modifier.fillMaxWidth(),
            onClick = { vm.login(email, password) },
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Demo account: samuel.wong@student.anyflow.site · journey##keren",
            style = MaterialTheme.typography.bodySmall,
            color = Brand.Muted,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = com.anyflow.journey.BuildConfig.API_BASE_URL,
            style = MaterialTheme.typography.labelSmall,
            color = Brand.Muted,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            modifier = Modifier.align(Alignment.Start),
        )
    }
}
