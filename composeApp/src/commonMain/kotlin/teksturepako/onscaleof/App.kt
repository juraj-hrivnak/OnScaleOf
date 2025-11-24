package teksturepako.onscaleof

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.*
import com.composeunstyled.platformtheme.EmojiVariant
import com.composeunstyled.platformtheme.WebFontOptions
import com.composeunstyled.platformtheme.buildPlatformTheme
import com.mohamedrejeb.calf.permissions.ExperimentalPermissionsApi
import com.mohamedrejeb.calf.permissions.Permission
import com.mohamedrejeb.calf.permissions.isGranted
import com.mohamedrejeb.calf.permissions.rememberPermissionState
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import io.github.vinceglb.filekit.saveImageToGallery
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch

// Modern color scheme
object AppColors {
    val Primary = Color(0xFF6366F1) // Indigo
    val PrimaryDark = Color(0xFF4F46E5)
    val Secondary = Color(0xFF8B5CF6) // Purple
    val Background = Color(0xFFF8FAFC)
    val Surface = Color.White
    val TextPrimary = Color(0xFF1E293B)
    val TextSecondary = Color(0xFF64748B)
    val Border = Color(0xFFE2E8F0)
    val Success = Color(0xFF10B981)
    val Error = Color(0xFFEF4444)
    val ImagePlaceholder = Color(0xFFF1F5F9)
}

val AppTheme = buildPlatformTheme(
    webFontOptions = WebFontOptions(
        emojiVariant = EmojiVariant.Colored
    )
)

@Composable
@Preview
fun App() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
        ) {
            MemeGeneratorScreen()
        }
    }
}

data class ImageData(
    val file: PlatformFile,
    val bitmap: ImageBitmap
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MemeGeneratorScreen() {
    var selectedImages by remember { mutableStateOf(List<ImageData?>(9) { null }) }
    val textFieldState = remember { TextFieldState("") }
    val scope = rememberCoroutineScope()
    var currentSelectionIndex by remember { mutableStateOf<Int?>(null) }
    var saveStatus by remember { mutableStateOf<SaveStatus?>(null) }

    val imagePickerLauncher = rememberFilePickerLauncher(
        type = FileKitType.Image,
        mode = FileKitMode.Single,
        title = "Pick an image"
    ) { file ->
        file?.let { platformFile ->
            scope.launch {
                currentSelectionIndex?.let { index ->
                    try {
                        val bitmap = platformFile.toImageBitmap()
                        selectedImages = selectedImages.toMutableList().apply {
                            set(index, ImageData(platformFile, bitmap))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    val permissionState = rememberPermissionState(Permission.Gallery)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Input Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "On a scale of random",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextPrimary
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppColors.Background)
                            .border(2.dp, AppColors.Border, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        TextField(
                            state = textFieldState,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextInput()
                        }
                    }

                    Text(
                        "how do you feel today?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextPrimary
                    )
                }
            }

            // Image Grid Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Select your images (3x3 grid)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextPrimary
                    )

                    ModernImageGrid(
                        images = selectedImages,
                        onImageClick = { index ->
                            currentSelectionIndex = index
                            imagePickerLauncher.launch()
                        }
                    )

                    Text(
                        "${selectedImages.count { it != null }}/9 images selected",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Status Message
            AnimatedVisibility(
                visible = saveStatus != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                saveStatus?.let { status ->
                    StatusBanner(status)
                }
            }

            // Save Button
            ModernButton(
                onClick = {
                    if (permissionState.status.isGranted) {
                        scope.launch {
                            try {
                                saveStatus = SaveStatus.Loading
                                val memeText = textFieldState.text.toString()
                                val memeBitmap = createMemeImage(
                                    images = selectedImages.map { it?.bitmap },
                                    text = memeText
                                )
                                val bytes = memeBitmap.toByteArray()

                                FileKit.saveImageToGallery(
                                    bytes = bytes,
                                    filename = "meme.png"
                                )

                                delay(3000)
                                saveStatus = null
                            } catch (e: Exception) {
                                e.printStackTrace()
                                saveStatus = SaveStatus.Error
                                delay(3000)
                                saveStatus = null
                            }
                        }
                    } else {
                        permissionState.launchPermissionRequest()
                    }
                },
                enabled = selectedImages.any { it != null } && textFieldState.text.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        when (saveStatus) {
                            SaveStatus.Loading -> "Saving..."
                            else -> "💾 Save to Gallery"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun Card(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = AppColors.Primary.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Surface)
    ) {
        content()
    }
}

@Composable
fun ModernImageGrid(
    images: List<ImageData?>,
    onImageClick: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (row in 0..2) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (col in 0..2) {
                    val index = row * 3 + col
                    ModernImageCell(
                        imageData = images[index],
                        onClick = { onImageClick(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ModernImageCell(
    imageData: ImageData?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isHovered) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (imageData != null) Color.Transparent
                else AppColors.ImagePlaceholder
            )
            .border(
                width = 2.dp,
                color = if (imageData != null) AppColors.Primary.copy(alpha = 0.3f)
                else AppColors.Border,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (imageData != null) {
            Image(
                bitmap = imageData.bitmap,
                contentDescription = "Selected image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    "+",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light,
                    color = AppColors.TextSecondary
                )
                Text(
                    "Add",
                    fontSize = 12.sp,
                    color = AppColors.TextSecondary.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun ModernButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val backgroundColor = if (enabled) {
        Brush.horizontalGradient(
            colors = listOf(AppColors.Primary, AppColors.Secondary)
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(AppColors.Border, AppColors.Border)
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun StatusBanner(status: SaveStatus) {
    val (emoji, text, color) = when (status) {
        SaveStatus.Success -> Triple("✅", "Saved to gallery!", AppColors.Success)
        SaveStatus.Error -> Triple("❌", "Failed to save", AppColors.Error)
        SaveStatus.Loading -> Triple("⏳", "Saving...", AppColors.Primary)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$emoji $text",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

enum class SaveStatus {
    Success, Error, Loading
}