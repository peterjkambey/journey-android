package com.anyflow.journey.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
 *
 * Catatan 12-09-2026: alamat email demo panjang (samuel.wong@student.anyflow.site)
 * sehingga di field satu baris ujungnya terpotong dan terlihat seperti email
 * salah. Karena itu ada kartu "Demo account" yang menampilkan alamat lengkap
 * (bisa dibaca utuh / di-tap untuk mengisi ulang), dan teks di field diperkecil
 * supaya lebih banyak karakter terlihat. Email TIDAK dipendekkan di data.
 */
@Composable
fun LoginScreen(vm: JourneyViewModel) {
    val demoEmail = "samuel.wong@student.anyflow.site"
    val demoPassword = "journey##keren"

    var email by remember { mutableStateOf(demoEmail) }
    var password by remember { mutableStateOf(demoPassword) }

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

        Spacer(Modifier.height(24.dp))

        // Kartu akun demo: alamat panjang ditampilkan utuh (wrap), jadi tidak ada
        // lagi kesan "email terpotong".
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Brand.WineSoft,
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                Text(
                    text = "DEMO ACCOUNT",
                    style = MaterialTheme.typography.labelSmall,
                    color = Brand.WineDeep,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = demoEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Brand.Ink,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Password: $demoPassword",
                    style = MaterialTheme.typography.bodySmall,
                    color = Brand.Body,
                )
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick = {
                        email = demoEmail
                        password = demoPassword
                    },
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                ) {
                    Text(
                        text = "Use demo account",
                        style = MaterialTheme.typography.labelLarge,
                        color = Brand.Wine,
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 13.sp),
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
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 13.sp),
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
            text = "API",
            style = MaterialTheme.typography.labelSmall,
            color = Brand.Muted,
        )
        Spacer(Modifier.height(2.dp))
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
