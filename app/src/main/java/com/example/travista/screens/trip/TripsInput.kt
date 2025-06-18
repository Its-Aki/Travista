package com.example.travista.screens.trip

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.travista.navigation.ScreenNavigation
import com.example.travista.viewmodel.AutocompleteViewModel
import kotlinx.coroutines.delay

//  navController.navigate(ScreenNavigation.TripsDisplay.passArgs(startingAddress = textState1.text, destinationAddress = textState2.text))
//                    }
@Composable
fun TwoSearchBar(navController: NavController) {
    val fromVM: AutocompleteViewModel = hiltViewModel(key = "fromVM")
    val toVM: AutocompleteViewModel = hiltViewModel(key = "toVM")

    var textState1 by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var textState2 by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

    val suggestions1 = fromVM.suggestions.collectAsState()
    val suggestions2 = toVM.suggestions.collectAsState()

    var activeField by rememberSaveable { mutableStateOf("") }
    var hasSelectedFrom by rememberSaveable { mutableStateOf(false) }
    var hasSelectedTo by rememberSaveable { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val fromFocusRequester = remember { FocusRequester() }
    val toFocusRequester = remember { FocusRequester() }

    val fromInteraction = remember { MutableInteractionSource() }
    val toInteraction = remember { MutableInteractionSource() }

    val fromIsFocused by fromInteraction.collectIsFocusedAsState()
    val toIsFocused by toInteraction.collectIsFocusedAsState()

    var isSwapped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isSwapped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "rotation"
    )

    LaunchedEffect(textState1.text) {
        if (textState1.text.length > 2 && activeField == "from") {
            delay(400)
            fromVM.fetchSuggestions(textState1.text)
        }
    }

    LaunchedEffect(textState2.text) {
        if (textState2.text.length > 2 && activeField == "to") {
            delay(400)
            toVM.fetchSuggestions(textState2.text)
        }
    }

    val currentSuggestions = when (activeField) {
        "from" -> suggestions1.value
        "to" -> suggestions2.value
        else -> emptyList()
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {

        Box(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(28.dp))
                .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(28.dp))
                .padding(vertical = 24.dp, horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Box {
                Column(
                    verticalArrangement = Arrangement.spacedBy(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // FROM
                    OutlinedTextField(
                        value = textState1,
                        onValueChange = { textState1 = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(fromFocusRequester)
                            .onFocusChanged {
                                if (!it.isFocused && !hasSelectedFrom) textState1 = TextFieldValue("")
                                if (it.isFocused) {
                                    activeField = "from"
                                    hasSelectedFrom = false
                                }
                            },
                        placeholder = { Text("From") },
                        leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                        trailingIcon = {
                            if (fromIsFocused && textState1.text.isNotEmpty()) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.clickable {
                                    textState1 = TextFieldValue("")
                                    focusManager.clearFocus()
                                    activeField = ""
                                    hasSelectedFrom = false
                                })
                            }
                        },
                        singleLine = true,
                        interactionSource = fromInteraction,
                        shape = RoundedCornerShape(50.dp)
                    )

                    // TO
                    OutlinedTextField(
                        value = textState2,
                        onValueChange = { textState2 = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(toFocusRequester)
                            .onFocusChanged {
                                if (!it.isFocused && !hasSelectedTo) textState2 = TextFieldValue("")
                                if (it.isFocused) {
                                    activeField = "to"
                                    hasSelectedTo = false
                                }
                            },
                        placeholder = { Text("To") },
                        leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = Color.Red) },
                        trailingIcon = {
                            if (toIsFocused && textState2.text.isNotEmpty()) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.clickable {
                                    textState2 = TextFieldValue("")
                                    focusManager.clearFocus()
                                    activeField = ""
                                    hasSelectedTo = false
                                })
                            }
                        },
                        singleLine = true,
                        interactionSource = toInteraction,
                        shape = RoundedCornerShape(50.dp)
                    )
                }

                // Swap icon in center
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.background, CircleShape)
                        .clickable {
                            val temp = textState1
                            textState1 = textState2
                            textState2 = temp
                            isSwapped = !isSwapped
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.SwapVert,
                        contentDescription = "Swap",
                        tint = Color(0xFF2F80ED),
                        modifier = Modifier
                            .size(28.dp)
                            .graphicsLayer { rotationZ = rotation }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(
            visible = currentSuggestions.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                currentSuggestions.forEach { prediction ->
                    val main = prediction.structured_formatting.main_text
                    val secondary = prediction.structured_formatting.secondary_text

                    ElevatedCard(
                        onClick = {
                            val fullText = "$main, $secondary"
                            if (activeField == "from") {
                                textState1 = TextFieldValue(fullText)
                                fromFocusRequester.requestFocus()
                                hasSelectedFrom = true
                            } else {
                                textState2 = TextFieldValue(fullText)
                                toFocusRequester.requestFocus()
                                hasSelectedTo = true
                            }

                            keyboardController?.hide()
                            focusManager.clearFocus()
                            activeField = ""
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.elevatedCardColors(MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.elevatedCardElevation(6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(main, style = MaterialTheme.typography.titleMedium)
                                Text(secondary, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // AI planning section
        if (textState1.text.isNotEmpty() && textState2.text.isNotEmpty() && currentSuggestions.isEmpty() && activeField == "") {
            val context = LocalContext.current
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Are you planning your next trip?",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Plan your next journey with AI. Let AI assist you in creating a memorable trip, from start to finish.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Reminder: Please make sure to verify all the details before finalizing your plan professionally.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                IconButton(
                    onClick = {
                        if (textState1.text == textState2.text) {
                            Toast.makeText(context, "Please enter different places", Toast.LENGTH_SHORT).show()
                        } else {
                            navController.navigate(
                                ScreenNavigation.TripsDisplay.passArgs(
                                    startingAddress = textState1.text,
                                    destinationAddress = textState2.text
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(50.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Place, contentDescription = "plan with ai", tint = MaterialTheme.colorScheme.background)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Plan your tour with AI",
                            style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.background)
                        )
                    }
                }
            }
        } else if (activeField == "") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Choose your destination", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}










