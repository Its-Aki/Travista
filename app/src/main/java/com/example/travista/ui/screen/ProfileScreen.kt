package com.example.travista.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.travista.R
import com.example.travista.theme.TravistaTheme

@Composable
// main composable for profile screen

fun ProfileScreen(navController: NavController) {

    //////////

    val scrollState = rememberScrollState()
    Column(modifier = Modifier.padding(0.dp)
        .verticalScroll(scrollState)
        .fillMaxWidth(),
       // verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {

        // Composable for Profile name

        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center, // Aligns text at the center of Column
            horizontalAlignment = Alignment.CenterHorizontally // Aligns items horizontally
        ) {
            // Composable for profile image
            ProfileImage(size = 100.dp)
            Text(text="Mr and Mrs Family", Modifier.padding(12.dp),
                style =MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            )
           Row{
               Icon(Icons.Outlined.ManageAccounts, contentDescription = "edit profile")
               Text(text="Edit Your Profile", Modifier.padding(2.dp),
                   style =MaterialTheme.typography.bodyMedium,
                   fontWeight = FontWeight.Bold,
                   fontSize = 12.sp,
               )
           }

        }

        Spacer(Modifier.padding(5.dp).height(60.dp))

        SettingsOptions()
        /*
          val sampleApiKey = API_KEY
         val samplePlaceId ="ChIJo75VIBeuEmsRi1UBARIHKL0"
        HotelScreen(sampleApiKey,samplePlaceId)

         */
    }



}







// Composable to display profile image from firebase
@Composable
fun ProfileImage(size: Dp) {
    val context = LocalContext.current // Get current context
    var imageUrl by remember { mutableStateOf<String?>(null) } // Holds the profile image URL

    // Firebase code (Commented Out)
    /*
    val userId = FirebaseAuth.getInstance().currentUser?.uid // Get logged-in user's ID

    // Listen for real-time profile image updates
    LaunchedEffect(userId) {
        if (userId != null) {
            val userDocRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)

            userDocRef.addSnapshotListener { document, _ ->
                if (document != null && document.exists()) {
                    imageUrl = document.getString("profileImageUrl")
                }
            }
        } else {
            imageUrl = null // Reset image on logout
        }
    }
    */

    // Display profile image or default image
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl) // Load the image URL (or null)
            .crossfade(true)
            .error(R.drawable.default_background_image) // Fallback to default image if error
            .build(),
        contentDescription = "Profile Picture",
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape),
        contentScale = ContentScale.Crop
    )
}




// composable for showing menu of setting options
@Composable
fun SettingsOptions() {
    Column(modifier = Modifier.padding(0.dp)) {
        SettingsRow(
            icon = Icons.Outlined.FavoriteBorder,
            text = "Favourites",
            contentDescription = "Your Favourites",
            onClick = { /* Handle Favourites Click */ }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp))

        SettingsRow(
            icon = Icons.Outlined.Tune,
            text = "Travel Preferences",
            contentDescription = "Select your travel preferences",
            onClick = { /* Handle Travel Preferences Click */ }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp))

        SettingsRow(
            icon = Icons.Outlined.LocationOn,
            text = "Location",
            contentDescription = "Your location",
            onClick = { /* Handle Location Click */ }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp))

        SettingsRow(
            icon = Icons.Outlined.Language,
            text = "Language",
            contentDescription = "Select your language",
            onClick = { /* Handle Language Click */ }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp))

        SettingsRow(
            icon = Icons.Outlined.Settings,
            text = "Settings",
            contentDescription = "Select your settings",
            onClick = { /* Handle Settings Click */ }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp))

        SettingsRow(
            icon = Icons.Outlined.SupportAgent,
            text = "Customer Care",
            contentDescription = "Talk to customer care",
            onClick = { /* Handle Customer Care Click */ }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp))

        // Logout button with resource image

            SettingsRowWithImage(
                iconResId = R.drawable.logout_button,
                text = "Logout",
                contentDescription = "Logout",
                onClick = { /* Handle Logout Click */ }
            )

    }
}

// row of settings menu with icon
@Composable
fun SettingsRow(icon: ImageVector, text: String, contentDescription: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 8.dp)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(36.dp))
        Text(
            text = text,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = text,
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}

// row of settings menu with image
// because logout icon is deprecated
@Composable
fun SettingsRowWithImage(iconResId: Int, text: String, contentDescription: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 8.dp)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = contentDescription,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = text,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = text,
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}



@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    TravistaTheme {
        ProfileScreen(navController = NavController(LocalContext.current))
    }
}
