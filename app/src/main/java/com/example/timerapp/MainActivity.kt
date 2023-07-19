// The package declaration specifies the location of the file within the project's directory structure.
package com.example.timerapp

// Import the required classes and functions from different packages.

// The `android.os.Bundle` class is used to pass data between different activities in Android.
import android.os.Bundle

// The `androidx.activity.ComponentActivity` class is a base class for activities that use the AndroidX library.
import androidx.activity.ComponentActivity

// The `setContent` function from `androidx.activity.compose` is used to set the content view of the activity with Jetpack Compose UI.
import androidx.activity.compose.setContent

// The `fillMaxSize` function from `androidx.compose.foundation.layout` is a modifier that sets the size of a composable to match its parent's size.
import androidx.compose.foundation.layout.fillMaxSize

// The `MaterialTheme` class from `androidx.compose.material3` provides the default Material Design styling for the Compose UI elements.
import androidx.compose.material3.MaterialTheme

// The `Surface` class from `androidx.compose.material3` is a Material Design component that acts as a container for other composables.
import androidx.compose.material3.Surface

// The `Text` class from `androidx.compose.material3` is a composable that displays a text string on the screen.
import androidx.compose.material3.Text

// The `Composable` annotation from `androidx.compose.runtime` is used to define a composable function,
// which is a function that returns a composable UI element.
import androidx.compose.runtime.Composable

// The `Modifier` class from `androidx.compose.ui` is used to apply various modifications or styling to composables.
import androidx.compose.ui.Modifier

// The `Preview` annotation from `androidx.compose.ui.tooling` allows you to preview how a composable looks during development.
import androidx.compose.ui.tooling.preview.Preview

// The `TImerAppTheme` composable function from the package `com.example.timerapp.ui.theme`
// is used to apply the app's theme to the UI.
import com.example.timerapp.ui.theme.TImerAppTheme

// Define the main activity class that extends `ComponentActivity`,
// which is a base class for activities using the AndroidX library.
class MainActivity : ComponentActivity() {
    // The `onCreate` method is called when the activity is created,
    // and this is where you initialize the activity and set up its UI.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view of the activity using Jetpack Compose.
        setContent {
            // Apply the `TImerAppTheme` to the content of the activity to ensure consistent styling.
            TImerAppTheme {
                // Define a `Surface` composable that acts as a container for other composables.
                // It uses the `fillMaxSize()` modifier to make it fill the entire screen.
                // The background color is set to the `background` color from the `MaterialTheme`.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Call the `Greeting` composable function with the argument "Android"
                    // to display a greeting message on the screen.
                    Greeting("Android")
                }
            }
        }
    }
}

// The `Greeting` composable function takes a `name` argument (a String) and an optional `modifier` argument (of type `Modifier`).
// It displays a greeting message on the screen using the `Text` composable.
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        // The `text` parameter of the `Text` composable displays the greeting message.
        // It is set to "Hello" followed by the value of the `name` parameter.
        text = "Hello $name!",
        // The `modifier` parameter allows applying optional styling to the `Text` composable.
        modifier = modifier
    )
}

// The `GreetingPreview` composable function is marked as a preview function using the `@Preview` annotation.
// This allows you to see how the UI looks during development.
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    // Apply the `TImerAppTheme` to the content of the preview.
    TImerAppTheme {
        // Call the `Greeting` composable function with the argument "Android"
        // to preview how the greeting text looks with the app's theme.
        Greeting("Android")
    }
}
