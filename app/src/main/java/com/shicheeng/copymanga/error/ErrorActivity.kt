package com.shicheeng.copymanga.error

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.shicheeng.copymanga.app.AppAttachCompatActivity
import com.shicheeng.copymanga.ui.screen.error.ErrorScreen
import com.shicheeng.copymanga.ui.theme.CopyMangaTheme

class ErrorActivity : AppAttachCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val message = intent?.getStringExtra(ERROR_MESSAGE)
        setContent {
            CopyMangaTheme {
                ErrorScreen(message = message) {
                    finish()
                }
            }
        }
    }

    companion object {
        fun newIntentInstance(
            context: Context,
            errorMessage: String?,
        ): Intent {
            val intent = Intent(context, ErrorActivity::class.java)
            intent.putExtra(ERROR_MESSAGE, errorMessage)
            return intent
        }

        private const val ERROR_MESSAGE = "ERROR_MESSAGE"
    }

}