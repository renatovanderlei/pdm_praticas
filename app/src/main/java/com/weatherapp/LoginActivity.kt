package com.weatherapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherapp.ui.theme.WeatherAppTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        LoginPage()
                    }
                }
            }
        }
    }
}

@Composable
fun LoginPage(modifier: Modifier = Modifier) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val activity = LocalContext.current as? Activity

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally,
    ) {
        Text(
            text = "Bem-vindo/a!", fontSize = 24.sp
        )

        Spacer(modifier = Modifier.size(24.dp))

        OutlinedTextField(value = email,
            label = { Text(text = "Digite seu e-mail") },
            modifier = modifier.fillMaxWidth(),
            onValueChange = { email = it })

        Spacer(modifier = Modifier.size(24.dp))

        OutlinedTextField(
            value = password,
            label = { Text(text = "Digite sua senha") },
            modifier = modifier.fillMaxWidth(),
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.size(24.dp))

        Row(modifier = modifier) {
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        Firebase.auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(activity!!) { task ->
                                if (task.isSuccessful) {
                                    activity.startActivity(
                                        Intent(activity, MainActivity::class.java).setFlags(
                                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        )
                                    )
                                    Toast.makeText(activity, "Login OK!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(activity, "Login FALHOU!", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        Toast.makeText(activity, "Preencha todos os campos", Toast.LENGTH_LONG)
                            .show()
                    }
                }, enabled = email.isNotEmpty() && password.isNotEmpty()
            ) {
                Text("Login")
            }
            Button(onClick = { email = ""; password = "" }) {
                Text("Limpar")
            }
        }
// Botão para registrar
        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = {
                    activity?.startActivity(Intent(activity, RegisterActivity::class.java))
                }
            ) {
                Text("Registrar-se")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    WeatherAppTheme {
        LoginPage()
    }
}
