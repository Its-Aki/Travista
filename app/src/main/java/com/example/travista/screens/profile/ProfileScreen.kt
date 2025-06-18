package com.example.travista.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.travista.R
import com.example.travista.navigation.ScreenNavigation
import com.example.travista.navigation.rootnavigation.BottomNavItems
import com.example.travista.screens.login.AuthViewModel
import com.example.travista.theme.TravistaTheme
import com.google.firebase.auth.FirebaseAuth
import java.nio.file.WatchEvent

@Composable
fun ProfileScreen(navController: NavController,rootNavController: NavController,authViewModel: AuthViewModel) {
    val profileViewModel: ProfileScreenViewModel=viewModel()
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileHeader(profileViewModel = profileViewModel)

        Spacer(modifier = Modifier.height(32.dp))

        SettingsOptionList(navController,rootNavController,authViewModel)
    }
}

@Composable
fun ProfileHeader(profileViewModel: ProfileScreenViewModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ProfileImage(size = 100.dp,viewModel = profileViewModel)

        Text(
            text = profileViewModel.getUserName() ?: "",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { /* Navigate to edit profile */ }
        ) {
            Icon(Icons.Outlined.ManageAccounts, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Edit Profile", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun ProfileImage(size: Dp,viewModel: ProfileScreenViewModel) {
    val context = LocalContext.current
    var imageUrl by remember { mutableStateOf<String?>(null) }
    imageUrl=viewModel.getUserPhotoUrl()
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .error(R.drawable.profile)
            .build(),
        contentDescription = "Profile Picture",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape)
    )
}

@Composable
fun SettingsOptionList(navController: NavController,rootNavController: NavController,authViewModel: AuthViewModel) {
    val options = listOf(
        Triple(Icons.Outlined.FavoriteBorder, "Favourites") { /* Navigate to favourites */ },
        Triple(Icons.Outlined.Tune, "Travel Preferences") { /* Open preferences */
        navController.navigate(ScreenNavigation.TravelPreferences.route)
        },
        Triple(Icons.Outlined.LocationOn, "Location") { /* Navigate to location screen */ },
        Triple(Icons.Outlined.Language, "Language") { /* Open language settings */ },
        Triple(Icons.Outlined.Settings, "Settings") { /* Open app settings */ },
        Triple(Icons.Outlined.SupportAgent, "Customer Care") { /* Contact support */ }
    )

    options.forEach { (icon, label, action) ->
        SettingsRow(icon = icon, text = label, onClick = action)
    }

    SettingsRowWithImage(
        iconResId = R.drawable.logout_button,
        text = "Logout"

    ) {
        authViewModel.logoutUser()
        rootNavController.navigate(ScreenNavigation.AuthRoute.route) {
            popUpTo(BottomNavItems.Profile.route) { inclusive = true }
        // Clears backstack
        }
        // Handle logout (clear session, go to login screen)
    }
}

@Composable
fun SettingsRow(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = text, modifier = Modifier.padding(start=8.dp).size(28.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, modifier = Modifier.weight(1f), fontSize = 16.sp)
        Icon( imageVector = Icons.Outlined.ChevronRight, contentDescription = null)
    }
    HorizontalDivider()
}

@Composable
fun SettingsRowWithImage(iconResId: Int, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = text,
            modifier = Modifier.padding(start = 8.dp).size(28.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, modifier = Modifier.weight(1f), fontSize = 16.sp)
        Icon(
            modifier = Modifier.padding(start = 8.dp),
            imageVector = Icons.Outlined.ChevronRight, contentDescription = null
        )
    }
    HorizontalDivider()
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    TravistaTheme {
        ProfileScreen(navController = NavController(LocalContext.current), rootNavController =NavController(LocalContext.current), authViewModel = viewModel() )
    }
}