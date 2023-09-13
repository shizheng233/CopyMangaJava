package com.shicheeng.copymanga.ui.screen.login.loginlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.viewmodel.LoginPersonalListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPersonalListScreen(
    viewModel: LoginPersonalListViewModel = hiltViewModel(),
    onAddClicked: () -> Unit,
    navigationClick: () -> Unit,
) {
    val list by viewModel.personalList.collectAsState()
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.login_personal))
                },
                scrollBehavior = topAppBarScrollBehavior,
                navigationIcon = {
                    PlainButton(
                        id = R.string.back_to_up,
                        drawableRes = R.drawable.ic_arrow_back,
                        onButtonClick = navigationClick
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = stringResource(R.string.add)
                )
            }
        }
    ) { paddingValues ->
        if (list.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.undraw_no_data_re_kwbl),
                        contentDescription = null,
                    )
                    Text(text = stringResource(id = R.string.no_login))
                }
            }
        } else {
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            ) {
                items(list) {
                    LoginPersonalSelection(
                        isSelected = it.selected,
                        localLoginDataModel = it,
                        onDelete = {
                            viewModel.delete(it)
                        }
                    ) {
                        viewModel.selectUUId(it.userID)
                    }
                }
            }
        }
    }
}