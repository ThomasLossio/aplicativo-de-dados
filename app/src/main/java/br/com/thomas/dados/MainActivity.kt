package br.com.thomas.dados

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
import kotlin.math.sqrt

object AppText {
    const val appName = "Dados"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DadosApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DadosApp() {
    MaterialTheme {
        var state by remember { mutableStateOf(DiceEngine.initialState()) }
        var isRolling by remember { mutableStateOf(false) }
        var showSettings by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val soundPlayer = rememberRollSoundPlayer()
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        fun rollDice() {
            if (isRolling) return
            if (state.settings.soundEnabled) soundPlayer.play()
            if (state.settings.vibrationEnabled) vibrate(context, 35)

            scope.launch {
                isRolling = true
                repeat(state.settings.rollSpeed.steps) {
                    state = DiceEngine.roll(state)
                    kotlinx.coroutines.delay(state.settings.rollSpeed.delayMillis)
                }
                state = DiceEngine.roll(state)
                if (state.settings.vibrationEnabled) vibrate(context, 55)
                isRolling = false
            }
        }

        ShakeToRoll(
            enabled = state.settings.shakeToRoll && !isRolling,
            onShake = { rollDice() },
        )

        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF39A86B)) {
            Box(modifier = Modifier.fillMaxSize()) {
                DiceBoard(
                    state = state,
                    isRolling = isRolling,
                    onRoll = { rollDice() },
                    modifier = Modifier.fillMaxSize(),
                )

                if (state.settings.showTotal) {
                    TotalBadge(
                        total = DiceEngine.total(state),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 18.dp),
                    )
                }

                BottomControls(
                    canDecrease = state.dice.size > DiceEngine.minDiceCount,
                    canIncrease = state.dice.size < DiceEngine.maxDiceCount,
                    onRoll = { rollDice() },
                    onDecrease = { state = DiceEngine.setDiceCount(state, state.dice.size - 1) },
                    onIncrease = { state = DiceEngine.setDiceCount(state, state.dice.size + 1) },
                    onSettings = { showSettings = true },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 18.dp, vertical = 24.dp),
                )
            }
        }

        if (showSettings) {
            SettingsSheet(
                state = state,
                sheetState = sheetState,
                onDismiss = { showSettings = false },
                onStateChange = { state = it },
            )
        }
    }
}

@Composable
private fun DiceBoard(
    state: DiceState,
    isRolling: Boolean,
    onRoll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tapDescription = stringResource(id = R.string.action_roll)
    Box(
        modifier = modifier
            .semantics { contentDescription = tapDescription }
            .pointerInput(state.settings.tapToRoll, isRolling) {
                detectTapGestures {
                    if (state.settings.tapToRoll && !isRolling) onRoll()
                }
            },
    ) {
        if (state.dice.size == 1) {
            FullScreenDie(
                die = state.dice.single(),
                settings = state.settings,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            DiceGrid(
                dice = state.dice,
                settings = state.settings,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun FullScreenDie(
    die: Die,
    settings: DiceSettings,
    modifier: Modifier = Modifier,
) {
    val color = dieColor(die.id, settings.colorMode)
    DieFace(
        die = die,
        settings = settings,
        background = color,
        pipSize = 54.dp,
        numberSize = 140,
        modifier = modifier.background(color),
    )
}

@Composable
private fun DiceGrid(
    dice: List<Die>,
    settings: DiceSettings,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 118.dp),
        modifier = modifier.padding(bottom = 96.dp),
        userScrollEnabled = dice.size > 12,
    ) {
        items(dice, key = { it.id }) { die ->
            DieFace(
                die = die,
                settings = settings,
                background = dieColor(die.id, settings.colorMode),
                pipSize = 14.dp,
                numberSize = 38,
                modifier = Modifier
                    .padding(2.dp)
                    .aspectRatio(1f),
            )
        }
    }
}

@Composable
private fun DieFace(
    die: Die,
    settings: DiceSettings,
    background: Color,
    pipSize: Dp,
    numberSize: Int,
    modifier: Modifier = Modifier,
) {
    val face = DiceFaceRenderer.render(die.value, settings.displayMode)
    Box(
        modifier = modifier
            .background(background)
            .padding(18.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (face.number != null) {
            Text(
                text = face.number,
                color = Color.White.copy(alpha = 0.88f),
                fontSize = numberSize.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        } else {
            PipCanvas(
                pips = face.pips,
                pipSize = pipSize,
                color = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun PipCanvas(
    pips: List<PipPosition>,
    pipSize: Dp,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val radius = pipSize.toPx() / 2f
        fun point(position: PipPosition): Offset {
            val left = size.width * 0.22f
            val centerX = size.width * 0.5f
            val right = size.width * 0.78f
            val top = size.height * 0.22f
            val centerY = size.height * 0.5f
            val bottom = size.height * 0.78f
            return when (position) {
                PipPosition.TopLeft -> Offset(left, top)
                PipPosition.TopCenter -> Offset(centerX, top)
                PipPosition.TopRight -> Offset(right, top)
                PipPosition.MiddleLeft -> Offset(left, centerY)
                PipPosition.Center -> Offset(centerX, centerY)
                PipPosition.MiddleRight -> Offset(right, centerY)
                PipPosition.BottomLeft -> Offset(left, bottom)
                PipPosition.BottomCenter -> Offset(centerX, bottom)
                PipPosition.BottomRight -> Offset(right, bottom)
            }
        }

        pips.forEach { position ->
            drawCircle(color = color, radius = radius, center = point(position))
        }
    }
}

@Composable
private fun TotalBadge(total: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = R.string.total_format, total),
        color = Color.White,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.42f))
            .padding(horizontal = 18.dp, vertical = 9.dp),
    )
}

@Composable
private fun BottomControls(
    canDecrease: Boolean,
    canIncrease: Boolean,
    onRoll: () -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoundControlButton(text = "🎲", description = stringResource(id = R.string.action_roll), onClick = onRoll)
        RoundControlButton(text = "−", description = stringResource(id = R.string.action_decrease_dice), enabled = canDecrease, onClick = onDecrease)
        RoundControlButton(text = "+", description = stringResource(id = R.string.action_increase_dice), enabled = canIncrease, onClick = onIncrease)
        RoundControlButton(text = "⚙", description = stringResource(id = R.string.action_settings), onClick = onSettings)
    }
}

@Composable
private fun RoundControlButton(
    text: String,
    description: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = { if (enabled) onClick() },
        modifier = Modifier
            .size(54.dp)
            .semantics { contentDescription = description },
        shape = CircleShape,
        containerColor = Color(0xB02B6070),
        contentColor = Color.White,
    ) {
        Text(text = text, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = if (enabled) Color.White else Color.White.copy(alpha = 0.35f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsSheet(
    state: DiceState,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onStateChange: (DiceState) -> Unit,
) {
    var expanded by remember { mutableStateOf(SettingsSection.DiceCount) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFF7F7FB),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 26.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(id = R.string.settings_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            SettingsRow(
                label = stringResource(id = R.string.settings_dice_count),
                accent = Color(0xFFEF4444),
                expanded = expanded == SettingsSection.DiceCount,
                onClick = { expanded = SettingsSection.DiceCount },
            ) {
                Stepper(
                    value = state.dice.size,
                    min = DiceEngine.minDiceCount,
                    max = DiceEngine.maxDiceCount,
                    onChange = { onStateChange(DiceEngine.setDiceCount(state, it)) },
                )
            }

            SettingsRow(
                label = stringResource(id = R.string.settings_dice_values),
                accent = Color(0xFFF59E0B),
                expanded = expanded == SettingsSection.Values,
                onClick = { expanded = SettingsSection.Values },
            ) {
                RangeEditor(
                    title = stringResource(id = R.string.settings_default_range),
                    range = state.settings.defaultRange,
                    onChange = { onStateChange(DiceEngine.setDefaultRange(state, it)) },
                )
                Spacer(modifier = Modifier.height(10.dp))
                state.dice.forEach { die ->
                    RangeEditor(
                        title = "Dado ${die.id}",
                        range = die.range,
                        showUseDefault = die.hasCustomRange,
                        onUseDefault = { onStateChange(DiceEngine.clearDieRange(state, die.id)) },
                        onChange = { onStateChange(DiceEngine.setDieRange(state, die.id, it)) },
                    )
                }
            }

            SettingsRow(
                label = stringResource(id = R.string.settings_appearance),
                accent = Color(0xFFD6CA2E),
                expanded = expanded == SettingsSection.Appearance,
                onClick = { expanded = SettingsSection.Appearance },
            ) {
                ChoiceRow(
                    label = stringResource(id = R.string.settings_display_mode),
                    options = listOf(
                        DisplayMode.Automatic to stringResource(id = R.string.display_auto),
                        DisplayMode.NumbersOnly to stringResource(id = R.string.display_numbers),
                    ),
                    selected = state.settings.displayMode,
                    onSelect = { mode -> onStateChange(DiceEngine.updateSettings(state) { it.copy(displayMode = mode) }) },
                )
                ChoiceRow(
                    label = stringResource(id = R.string.settings_color_mode),
                    options = listOf(
                        ColorMode.Multicolor to stringResource(id = R.string.colors_multi),
                        ColorMode.Single to stringResource(id = R.string.colors_single),
                    ),
                    selected = state.settings.colorMode,
                    onSelect = { mode -> onStateChange(DiceEngine.updateSettings(state) { it.copy(colorMode = mode) }) },
                )
            }

            SettingsRow(
                label = stringResource(id = R.string.settings_animation),
                accent = Color(0xFF77C84B),
                expanded = expanded == SettingsSection.Rolling,
                onClick = { expanded = SettingsSection.Rolling },
            ) {
                ChoiceRow(
                    label = stringResource(id = R.string.settings_roll_speed),
                    options = listOf(
                        RollSpeed.Slow to stringResource(id = R.string.speed_slow),
                        RollSpeed.Normal to stringResource(id = R.string.speed_normal),
                        RollSpeed.Fast to stringResource(id = R.string.speed_fast),
                    ),
                    selected = state.settings.rollSpeed,
                    onSelect = { speed -> onStateChange(DiceEngine.updateSettings(state) { it.copy(rollSpeed = speed) }) },
                )
                ToggleRow(
                    label = stringResource(id = R.string.settings_tap_to_roll),
                    checked = state.settings.tapToRoll,
                    onCheckedChange = { checked -> onStateChange(DiceEngine.updateSettings(state) { it.copy(tapToRoll = checked) }) },
                )
            }

            SettingsRow(
                label = stringResource(id = R.string.settings_feedback),
                accent = Color(0xFF56B8CF),
                expanded = expanded == SettingsSection.Feedback,
                onClick = { expanded = SettingsSection.Feedback },
            ) {
                ToggleRow(
                    label = stringResource(id = R.string.settings_shake_to_roll),
                    checked = state.settings.shakeToRoll,
                    onCheckedChange = { checked -> onStateChange(DiceEngine.updateSettings(state) { it.copy(shakeToRoll = checked) }) },
                )
                ToggleRow(
                    label = stringResource(id = R.string.settings_sound),
                    checked = state.settings.soundEnabled,
                    onCheckedChange = { checked -> onStateChange(DiceEngine.updateSettings(state) { it.copy(soundEnabled = checked) }) },
                )
                ToggleRow(
                    label = stringResource(id = R.string.settings_vibration),
                    checked = state.settings.vibrationEnabled,
                    onCheckedChange = { checked -> onStateChange(DiceEngine.updateSettings(state) { it.copy(vibrationEnabled = checked) }) },
                )
            }

            SettingsRow(
                label = stringResource(id = R.string.settings_total),
                accent = Color(0xFFD53BC4),
                expanded = expanded == SettingsSection.Total,
                onClick = { expanded = SettingsSection.Total },
            ) {
                ToggleRow(
                    label = stringResource(id = R.string.settings_total),
                    checked = state.settings.showTotal,
                    onCheckedChange = { checked -> onStateChange(DiceEngine.updateSettings(state) { it.copy(showTotal = checked) }) },
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(
    label: String,
    accent: Color,
    expanded: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(accent),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = label.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = label, modifier = Modifier.weight(1f), fontSize = 16.sp)
                Text(text = if (expanded) "⌄" else "›", fontSize = 22.sp, color = Color.Gray)
            }
            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun Stepper(
    value: Int,
    min: Int,
    max: Int,
    onChange: (Int) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { onChange(value - 1) }, enabled = value > min) {
            Text("−")
        }
        Text(
            text = value.toString(),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        Button(onClick = { onChange(value + 1) }, enabled = value < max) {
            Text("+")
        }
    }
}

@Composable
private fun RangeEditor(
    title: String,
    range: DiceRange,
    showUseDefault: Boolean = false,
    onUseDefault: () -> Unit = {},
    onChange: (DiceRange) -> Unit,
) {
    Column {
        Text(text = title, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RangeValueButton(
                label = stringResource(id = R.string.settings_min_value),
                value = range.min,
                modifier = Modifier.weight(1f),
                onDecrease = {
                    val nextMin = (range.min - 1).coerceAtLeast(1)
                    if (DiceRange.isValid(nextMin, range.max)) onChange(DiceRange(nextMin, range.max))
                },
                onIncrease = {
                    val nextMin = (range.min + 1).coerceAtMost(range.max)
                    if (DiceRange.isValid(nextMin, range.max)) onChange(DiceRange(nextMin, range.max))
                },
            )
            RangeValueButton(
                label = stringResource(id = R.string.settings_max_value),
                value = range.max,
                modifier = Modifier.weight(1f),
                onDecrease = {
                    val nextMax = (range.max - 1).coerceAtLeast(range.min)
                    if (DiceRange.isValid(range.min, nextMax)) onChange(DiceRange(range.min, nextMax))
                },
                onIncrease = {
                    val nextMax = (range.max + 1).coerceAtMost(100)
                    if (DiceRange.isValid(range.min, nextMax)) onChange(DiceRange(range.min, nextMax))
                },
            )
        }
        if (showUseDefault) {
            Button(onClick = onUseDefault) {
                Text(text = stringResource(id = R.string.settings_follow_default))
            }
        }
    }
}

@Composable
private fun RangeValueButton(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 12.sp, color = Color.DarkGray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onDecrease, contentPadding = ButtonDefaults.ContentPadding) {
                Text("−")
            }
            Text(
                text = value.toString(),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            )
            Button(onClick = onIncrease, contentPadding = ButtonDefaults.ContentPadding) {
                Text("+")
            }
        }
    }
}

@Composable
private fun <T> ChoiceRow(
    label: String,
    options: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit,
) {
    Column {
        Text(text = label, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { (value, text) ->
                Button(
                    onClick = { onSelect(value) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (value == selected) Color(0xFF2B6070) else Color(0xFFE5E7EB),
                        contentColor = if (value == selected) Color.White else Color.Black,
                    ),
                ) {
                    Text(text)
                }
            }
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun ShakeToRoll(
    enabled: Boolean,
    onShake: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnShake by rememberUpdatedState(onShake)

    DisposableEffect(enabled, context, lifecycleOwner) {
        if (!enabled) return@DisposableEffect onDispose { }

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        var lastShakeAt = 0L
        var registered = false

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val acceleration = sqrt(
                    event.values[0] * event.values[0] +
                        event.values[1] * event.values[1] +
                        event.values[2] * event.values[2],
                )
                val now = System.currentTimeMillis()
                if (acceleration > 17f && now - lastShakeAt > 900L) {
                    lastShakeAt = now
                    currentOnShake()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        fun register() {
            if (accelerometer == null || registered) return
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
            registered = true
        }

        fun unregister() {
            if (!registered) return
            sensorManager.unregisterListener(listener)
            registered = false
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> register()
                Lifecycle.Event.ON_PAUSE -> unregister()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            register()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            unregister()
        }
    }
}

@Composable
private fun rememberRollSoundPlayer(): RollSoundPlayer {
    val player = remember { RollSoundPlayer() }
    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }
    return player
}

private class RollSoundPlayer {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 60)

    fun play() {
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 90)
    }

    fun release() {
        toneGenerator.release()
    }
}

private fun vibrate(context: Context, durationMillis: Long) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(VibratorManager::class.java)
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(durationMillis, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(durationMillis)
    }
}

private enum class SettingsSection {
    DiceCount,
    Values,
    Appearance,
    Rolling,
    Feedback,
    Total,
}

private fun dieColor(id: Int, mode: ColorMode): Color {
    if (mode == ColorMode.Single) return Color(0xFF78C943)

    val palette = listOf(
        Color(0xFF78C943),
        Color(0xFF8E63C7),
        Color(0xFFE39A31),
        Color(0xFF57B9CE),
        Color(0xFFB03034),
        Color(0xFF8B8D36),
        Color(0xFF427A34),
        Color(0xFF2B6070),
    )
    return palette[(id - 1).floorMod(palette.size)]
}

private fun Int.floorMod(other: Int): Int = ((this % other) + other) % other

@Preview(showBackground = true)
@Composable
private fun DadosAppPreview() {
    DadosApp()
}
