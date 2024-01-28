package com.shicheeng.copymanga.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.CircleLoadingButton
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.util.LoginState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavClick: () -> Unit,
    onLoadingSuccess: () -> Unit,
) {

    val (username, onUsername) = rememberSaveable { mutableStateOf("") }
    val (password, onPassword) = rememberSaveable { mutableStateOf("") }
    val loginState by viewModel.loginStatus.collectAsState()
    var isPasswordError by remember { mutableStateOf(false) }
    var isUsernameError by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = loginState) {
        if (loginState is LoginState.Success) {
            onLoadingSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.login_text))
                },
                navigationIcon = {
                    PlainButton(
                        id = R.string.back_to_up,
                        drawableRes = R.drawable.ic_arrow_back,
                        onButtonClick = onNavClick
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.undraw_login_re),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .clip(MaterialTheme.shapes.large),
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = username,
                onValueChange = onUsername,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                label = {
                    Text(text = stringResource(R.string.user_name_text))
                },
                singleLine = true,
                isError = isUsernameError
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = onPassword,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                label = {
                    Text(text = stringResource(R.string.password_text))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                ),
                singleLine = true,
                isError = loginState is LoginState.Error<*> || isPasswordError,
                visualTransformation = if (!isPasswordVisible) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                trailingIcon = {
                    PlainButton(
                        id = { R.string.password_text },
                        drawableRes = {
                            if (isPasswordVisible) {
                                R.drawable.baseline_visibility_off_24
                            } else {
                                R.drawable.baseline_visibility_24
                            }
                        }
                    ) {
                        isPasswordVisible = !isPasswordVisible
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircleLoadingButton(
                isLoading = loginState == LoginState.Loading,
                onClick = {
                    when {
                        username.isEmptyOrBlank() -> isUsernameError = true
                        password.isEmptyOrBlank() -> isPasswordError = true
                        password.isEmptyOrBlank() && username.isEmptyOrBlank() -> {
                            isPasswordError = true
                            isUsernameError = true
                        }

                        else -> {
                            viewModel.loginUP(username, password)
                        }
                    }
                }
            )
        }
    }
}


private fun String.isEmptyOrBlank() = this.isEmpty() || this.isBlank()