import com.soywiz.klock.seconds
import com.soywiz.korge.tween.get
import com.soywiz.korge.tween.tween
import com.soywiz.korge.view.alpha
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.BmpSlice
import com.soywiz.korim.color.Colors
import com.soywiz.korim.font.DefaultTtfFont
import com.soywiz.korim.format.readBitmap
import com.soywiz.korim.text.DefaultStringTextRenderer
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korio.async.launch
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.interpolation.Easing
import kotlinx.coroutines.GlobalScope

class Earth(x: Double, y: Double, private val earthSprite: Image, private val stage2: BmpSlice, private val stage3: BmpSlice) {
    private var earthStage = 0
    private val defaultEarth = earthSprite.bitmap;

    init {
        earthSprite.alpha(1)
        earthSprite.position(225.0, (y/2) - 80)
        earthSprite.alpha(0)
    }

    fun launch()
    {
        GlobalScope.launch { earthSprite.tween(earthSprite::alpha[0, 1], time = 1.seconds, easing = Easing.EASE_IN) }
    }

    fun transition(forward: Boolean = true): Boolean {
        earthStage++
        if (!forward) earthStage = 0
        if (earthStage == 0) { earthSprite.bitmap = defaultEarth; return true }
        if (earthStage == 1) { earthSprite.bitmap = stage2; return true }
        if (earthStage == 2) { earthSprite.bitmap = stage3; Winscreen(); return true }
        if (earthStage == 3) { GlobalScope.launch {earthSprite.tween(earthSprite::y[earthSprite.y,-800], earthSprite::rotation[earthSprite.rotation, (-90).degrees], time = 6.5.seconds, easing = Easing.EASE_IN_OUT)} }
        return false;
    }
}