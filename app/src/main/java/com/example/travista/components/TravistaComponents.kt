package com.example.travista.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmailInput(
    modifier: Modifier=Modifier,
    emailState: MutableState<String>,
    labelId:String="Email",
    enabled:Boolean=true,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
){
    InputField(
        modifier = modifier,
        valueState = emailState,
        labelId = labelId,
        enabled = enabled,
        imeAction = imeAction,
        keyboardActions = onAction,
        keyboardType = KeyboardType.Email
    )
}

@Composable
fun InputField(
    modifier: Modifier=Modifier,
    valueState: MutableState<String>,
    labelId:String="Email",
    enabled:Boolean=true,
    isSingleLine:Boolean=true,
    imeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyboardActions: KeyboardActions = KeyboardActions.Default
){
    OutlinedTextField(value = valueState.value, onValueChange = { valueState.value = it },
        singleLine = isSingleLine, modifier = modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = keyboardActions,
        label = { Text(text = labelId) }
    )

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PasswordInput(modifier: Modifier,
                  passwordState: MutableState<String>,
                  enabled: Boolean,
                  labelId:String,
                  passwordVisibility: MutableState<Boolean>,
                  onAction: KeyboardActions= KeyboardActions.Default,
                  imeAction: ImeAction=ImeAction.Done) {
    val visualTransformation=if (passwordVisibility.value) VisualTransformation.None else
        PasswordVisualTransformation()

    OutlinedTextField(
        value = passwordState.value,
        onValueChange ={ passwordState.value=it },
        label = { Text(text = labelId)},
        singleLine = true,
        modifier= modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxSize(),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions=onAction,
        visualTransformation = visualTransformation,
        trailingIcon = {PasswordVisibility(passwordVisibility=passwordVisibility)}
    )
}

@Composable
fun PasswordVisibility(passwordVisibility: MutableState<Boolean>) {
    val visible=passwordVisibility.value
    IconButton(onClick = { passwordVisibility.value=!visible}) {
        Icons.Default.Close
    }
}

@Composable
fun SubmitButton(
    textId : String,
    loading:Boolean,
    validInputs:Boolean,
    onClick:()->Unit)
{
    Button(onClick = onClick,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth() ,
        enabled = !loading && validInputs,
        shape = CircleShape
    ) {
        if (loading)
            CircularProgressIndicator(modifier = Modifier.size(25.dp))
        else
            Text(text = textId,modifier=Modifier.padding(5.dp))

    }
}