package com.example.semafix.ui.theme.screens.register

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.semafix.R
import com.example.semafix.data.AuthViewModel
import com.example.semafix.navigation.Routes
import com.example.semafix.ui.theme.SemaFixTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()

    val token = stringResource(R.string.default_web_client_id)
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(token)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { authResult ->
                    if (authResult.isSuccessful) {
                        Toast.makeText(context, "Signed in with Google", Toast.LENGTH_SHORT).show()
                        navController.navigate(Routes.Dashboard.route)
                    } else {
                        Toast.makeText(context, "Google sign-in failed", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: ApiException) {
            Toast.makeText(context, "Google sign-in error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val gradient = Brush.verticalGradient(colors = listOf(Color(0xFF2C2C2C), Color(0xFF1E1E1E)))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .verticalScroll(scrollState)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
                .padding(top = 40.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2B2B)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Create Account", style = MaterialTheme.typography.headlineLarge, color = Color.White)
                Text("Speak Out to be heard", style = MaterialTheme.typography.bodyLarge, color = Color.LightGray)

                OutlinedInputField("Full Name", fullName, { fullName = it }, Icons.Default.Person)
                OutlinedInputField("Phone Number", phone, { phone = it }, Icons.Default.Phone)
                OutlinedInputField("Email", email, { email = it }, Icons.Default.Email)

                PasswordInputField("Password", password, { password = it }, passwordVisible, { passwordVisible = !passwordVisible })
                PasswordInputField("Confirm Password", confirmPassword, { confirmPassword = it }, confirmPasswordVisible, { confirmPasswordVisible = !confirmPasswordVisible })

                Button(
                    onClick = {
                        if (password == confirmPassword) {
                            authViewModel.registerUser(fullName, email, phone, password)
                        } else {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) {
                    Text("Sign Up", style = MaterialTheme.typography.labelLarge)
                }

                OutlinedButton(
                    onClick = {
                        val signInIntent = googleSignInClient.signInIntent
                        launcher.launch(signInIntent)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, Color.White)
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Up with Google", color = Color.White)
                }

                TextButton(onClick = { navController.navigate(Routes.Login.route) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Already have an account? Sign In", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun OutlinedInputField(label: String, value: String, onChange: (String) -> Unit, icon: ImageVector) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, color = Color.LightGray) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = Color.White) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
fun PasswordInputField(label: String, value: String, onChange: (String) -> Unit, visible: Boolean, toggleVisibility: () -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, color = Color.LightGray) },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White) },
        trailingIcon = {
            IconButton(onClick = toggleVisibility) {
                Icon(
                    imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle password visibility",
                    tint = Color.White
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}





@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    SemaFixTheme {
        RegisterScreen(rememberNavController())
    }
}