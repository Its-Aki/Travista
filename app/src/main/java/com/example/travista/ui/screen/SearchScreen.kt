package com.example.travista.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travista.ui.viewmodel.AutocompleteViewModel
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    viewModel: AutocompleteViewModel = hiltViewModel(),
    // navController: NavController
) {
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    val suggestions = viewModel.suggestions.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Focus on TextField and show keyboard on screen load
    LaunchedEffect(Unit) {
        delay(200)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    // Manual debounce logic
    LaunchedEffect(textState.text) {
        if (textState.text.length >= 2) {
            delay(400)
            viewModel.fetchSuggestions(textState.text)
        } else {
            viewModel.fetchSuggestions("")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Search TextField
        OutlinedTextField(
            value = textState,
            onValueChange = { textState = it },
            label = { Text("Search for places") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            singleLine = true,
            shape = MaterialTheme.shapes.extraLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // LazyColumn with Search Icon
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(suggestions.value) { prediction ->
                val mainText = prediction.structured_formatting.main_text
                val secondaryText = prediction.structured_formatting.secondary_text

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // val name = mainText
                            // val address = secondaryText
                            // navController.navigate("details_screen/${name}/${address}")
                        },
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Result Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 12.dp)
                        )

                        Column {
                            Text(
                                text = mainText,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = secondaryText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
