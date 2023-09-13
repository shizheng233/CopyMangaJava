package com.shicheeng.copymanga.ui.screen.main.personal.personaldetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.logininfoshort.LoginInfoShortDataModel
import com.shicheeng.copymanga.ui.screen.compoents.ErrorScreen
import com.shicheeng.copymanga.ui.screen.compoents.LoadingScreen
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.util.UIState
import com.shicheeng.copymanga.viewmodel.PersonalDetailViewModel
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDetail(
    personalDetailViewModel: PersonalDetailViewModel = hiltViewModel(),
    onReLogin: () -> Unit,
    onBack: () -> Unit,
) {

    val data by personalDetailViewModel.data.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.personal_info)) },
                navigationIcon = {
                    PlainButton(
                        id = R.string.back_to_up,
                        drawableRes = R.drawable.ic_arrow_back,
                        onButtonClick = onBack
                    )
                }
            )
        }
    ) { padding ->
        when (data) {
            is UIState.Success -> {
                val primary = (data as UIState.Success<LoginInfoShortDataModel>).content
                Column(
                    Modifier.padding(padding)
                ) {
                    PersonalDetailTwoRowText(
                        secondaryText = primary.results.info.nickname,
                        primaryText = stringResource(R.string.nickname_text)
                    )
                    PersonalDetailTwoRowText(
                        secondaryText = primary.results.info.username,
                        primaryText = stringResource(id = R.string.user_name_text)
                    )
                    PersonalDetailTwoRowText(
                        primaryText = stringResource(R.string.gender),
                        secondaryText = primary.results.info.gender.display
                    )
                }
            }

            is UIState.Loading -> {
                LoadingScreen()
            }

            is UIState.Error<*> -> {
                val error = (data as UIState.Error<*>).errorMessage
                ErrorScreen(
                    errorMessage = error.message ?: "",
                    needSecondaryText = (error is HttpException) && (error.code() == 401),
                    secondaryText = stringResource(id = R.string.re_login),
                    onTry = {
                        personalDetailViewModel.retry()
                    },
                    onSecondaryClick = onReLogin
                )
            }
        }
    }

}