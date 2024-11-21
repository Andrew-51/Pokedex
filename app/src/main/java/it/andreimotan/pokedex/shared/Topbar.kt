package it.andreimotan.pokedex.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import it.andreimotan.pokedex.navigation.LocalNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Topbar(
    title: String?,
    viewModel: BaseViewModel,
    clickHandler: (() -> Unit)?,
    content: @Composable (PaddingValues) -> Unit
) {

    val navController = LocalNavController.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            if (title != null) {
                TopAppBar(
                    title = {
                        Text(
                            text = title,
                            color = Color.Black
                        )
                    },
                    navigationIcon = {
                        if (clickHandler != null) {
                            IconButton(onClick = {
                                if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                                    clickHandler()
                                }
                            }) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    tint = Color.Black,
                                    contentDescription = "Indietro"
                                )
                            }
                        }
                    },
                    modifier = Modifier.background(Color.LightGray)
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState =  snackbarHostState,
                modifier = Modifier
                    .padding(top = 8.dp)
            ) {
                Snackbar(
                    snackbarData = it,
                    containerColor = MaterialTheme.colorScheme.errorContainer, // Imposta il colore di sfondo a rosso
                    contentColor = MaterialTheme.colorScheme.error, // Imposta il colore del testo a bianco
                    actionContentColor = MaterialTheme.colorScheme.error, // Imposta il colore dell'azione a bianco
                )
            }
        },
        content = { paddingValues ->
            content(paddingValues)
        }
    )
}
