import com.soywiz.klock.infiniteTimes
import com.soywiz.klock.seconds
import com.soywiz.korau.sound.await
import com.soywiz.korau.sound.playing
import com.soywiz.korau.sound.readMusic
import com.soywiz.korau.sound.readSound
import com.soywiz.korge.*
import com.soywiz.korge.time.delay
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.font.TtfFont
import com.soywiz.korim.format.*
import com.soywiz.korim.text.DefaultStringTextRenderer
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.geom.plus
import com.soywiz.korma.interpolation.Easing
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

suspend fun main() = Korge(width = 1024, height = 575, bgcolor = Colors["#2b2b2b"]) {

    /**
     * readme.md
     *
     *  # Solstice
     *  This is my take on the "Summer" theme
     *  I thought about making a few very literal games
     *  about surfing, the beach, etc... but it all sounded
     *  maybe a bit too bland.
     *
     *  So I took a more literal, second definition theme.
     *  "Summer" - n. First Seasonal Solstice,
     *  When the earth leans towards the sun and away from the moon.
     *  Good plot? I think so.
     *
     *  This repo is the project dumped all into one class file and resources folder,
     *  for reference purpose.
     */
/*
    val interacted = false;

    while (!interacted)
    {

    }
*/
    // BGM After Interaction (~~Non ITCHIO~~) - jank ass workaround for browsers
    val music = resourcesVfs["bgm.mp3"].readMusic()
    var channel = music.play()

    // Universal Font
    val font = TtfFont(resourcesVfs["Montserrat-Medium.ttf"].readAll())

    /*
    # Seasonal Backdrops
     */

    // default (winter)
    val sceneBackdrop = image(resourcesVfs["tall-blue.png"].readBitmap()) {
        anchor(.5, .5)
        scale(1)
        position(this.width / 2, this.height / 2)
    }
    val fallBackdrop = image(resourcesVfs["alto-brown.png"].readBitmap()) { alpha(0) }
    val springBackdrop = image(resourcesVfs["alto-green.png"].readBitmap()) { alpha(0) }
    val summerBackdrop = image(resourcesVfs["alto-summer.png"].readBitmap()) { alpha(0) }

    /*
    # Seasonal Icons
     */
    val winterIcon = image(resourcesVfs["winter.png"].readBitmap()) { alpha(0) }
    val fallIcon = image(resourcesVfs["fall.png"].readBitmap()) { alpha(0) }
    val springIcon = image(resourcesVfs["spring.png"].readBitmap()) { alpha(0) }


    /*
    # Default Player Configuration
     */
    val player = image(resourcesVfs["dot.png"].readBitmap())
    player.position((this.width / 3.5) - 10, this.height / 2) // subtract 10 to prevent mountain ocd
    player.alpha = 0.0
    player.rotation(75.degrees)

    // im so sorry about this but why don't "\t" or right-align do what i think they're supposed to do?
    val earthPoem = text("                                                                         Dear Earth,\n                           My journey shall take one sun orbit\nBut I shall return, with the three seasons aligned", 22.0, Colors.WHITE, font, TextAlignment.CENTER, DefaultStringTextRenderer, Text.DEFAULT_AUTO_SCALING)
    //earthPoem.alignRightToLeftOf(this.views.root, 0)

    val firstMessage = text(" Bend low again, night of summer stars.\n\n    whose ancient vista is the long view\n\n turns its wide brightness now and here:\n\nBelow, we loll outdoors, sing & make fire.\n\n", 22.0, Colors.WHITE, font, TextAlignment.CENTER, DefaultStringTextRenderer, Text.DEFAULT_AUTO_SCALING)
    val moonPoem = text("Sweet Moon,\n The Summer Equinox is approaching.\n  Harmonize Spring, Winter, and Fall to attain it.", 22.0, Colors.WHITE, font, TextAlignment.CENTER, DefaultStringTextRenderer, Text.DEFAULT_AUTO_SCALING)


    firstMessage.position(this.width / 2, this.height / 2)
    moonPoem.position(this.width / 2, this.height / 2.35) // the higher the divisor, the more leftward it is (2 = mid, 2.5 = left, 1.5 = right))
    earthPoem.position(this.width / 1.9, this.height / 1.62)
    firstMessage.visible = false
    moonPoem.visible = false
    earthPoem.visible = false

    /*
    # Intro Sequence
     */

    val tiltEarth = Earth(this.width, this.height,
		image(resourcesVfs["earth-tilt.png"].readBitmap()),
		resourcesVfs["earth-tilt-2.png"].readBitmapSlice(),
		resourcesVfs["earth-tilt-3.png"].readBitmapSlice())

    sceneBackdrop.tween(sceneBackdrop::y[this.height, 0], time = 6.seconds, easing = Easing.SMOOTH)
    firstMessage.visible = true
    firstMessage.tween(firstMessage::y[this.height, -200], time = 15.seconds, easing = Easing.EASE_IN_QUAD)

    val pillar = image(resourcesVfs["pillar.png"].readBitmap())
    pillar.visible = false
    pillar.visible = true
    pillar.tween(pillar::y[450, 0], time = 5.seconds, easing = Easing.EASE_IN_OUT)

    // poem tween-in
    moonPoem.visible = true
    moonPoem.tween(moonPoem::alpha[0, 1], time = 5.seconds, easing = Easing.EASE_IN_QUAD)
    earthPoem.visible = true
    earthPoem.tween(earthPoem::alpha[0, 1], time = 3.seconds, easing = Easing.EASE_IN_QUAD)

    // icon reveal
    winterIcon.position(this.width / 2 - 40, this.height - 125)
    fallIcon.position(this.width / 2 - 40 - 150, this.height - 125)
    springIcon.position(this.width / 2 - 40 + 150, this.height - 125)

    winterIcon.tween(winterIcon::alpha[0, 0.25], time = 1.seconds, easing = Easing.EASE_IN)
    fallIcon.tween(fallIcon::alpha[0, 0.25], time = 1.seconds, easing = Easing.EASE_IN)
    springIcon.tween(springIcon::alpha[0, 0.25], time = 1.seconds, easing = Easing.EASE_IN)

    // poem tweenout
    moonPoem.tween(moonPoem::alpha[1, 0], time = 5.seconds, easing = Easing.EASE_IN_QUAD)
    earthPoem.tween(earthPoem::alpha[1, 0], time = 3.seconds, easing = Easing.EASE_IN_QUAD)

    music.play()

    /*
    # Launch Sequence
     */
    pillar.tween(pillar::y[0, 450], time = 5.seconds, easing = Easing.EASE_IN_OUT)

    val launchText = text("Hold [Left Click] to Stay Afloat", 22.0, Colors.WHITE, font, TextAlignment.CENTER, DefaultStringTextRenderer, Text.DEFAULT_AUTO_SCALING)
    launchText.position((this.width / 2) - 15, this.height / 2)
    launchText.alpha = 0.0

    val earth = image(resourcesVfs["earth.png"].readBitmap())
    earth.alpha(0)

    fun pop(x: Double, y: Double, long: Boolean = false) {
        launch {
            val death = image(resourcesVfs["outline.png"].readBitmap())
            death.position(x, y)
            death.center()
            if (!long) death.tween(death::scale[0, 1.5],death::alpha[1, 0], time = 0.1.seconds, easing = Easing.EASE_OUT)
            else death.tween(death::scale[0, 2.5],death::alpha[1, 0], time = 2.5.seconds, easing = Easing.EASE_OUT)
        }
    }

    player.tween(player::alpha[0, 1], time = 5.seconds, easing = Easing.EASE_IN_OUT)

    launchText.tween(launchText::alpha[0, 1], time = 2.seconds, easing = Easing.EASE_IN_OUT)

    var onGoing = true

    val input = this.input

    var transition = false
    var transitionCount = 0

    val transitionDropHeight = sceneBackdrop.y + this.height / 2

    val earthDockedIconPos = earth.pos

    var coinChance = 60
    var darkCoins = false // maybe on fall?
    val victory = resourcesVfs["victory.png"].readBitmapSlice()

    var heatWave = true

    this.addUpdater { time ->
        if (onGoing) {
            val scale = time.milliseconds
            val buttons: Int = input.mouseButtons
            if (buttons == 0) {
                player.y += 0.3 * scale
            } else {
                player.y -= 0.2 * scale
            }

            // if fall out of world, reset
            if (player.y > this.height || player.y < 0) {
                player.y = this.height / 2
                pop(player.x, player.y)
                earth.alpha(0)
            }

            val rand = (0..10000).random()

            if (rand < coinChance) {
                val coin = image(player.bitmap)

                if (darkCoins && rand < (coinChance / 2)) {
                    coin.colorMul = RGBA(255, 80, 20, 255)
                    coin.rotation(85.degrees)
                } else {
                    coin.rotation(75.degrees)
                }

                coin.scale(0.5, 0.5)
                coin.position(this.width, this.height / 2 + (-100..100).random())

                this.addUpdater {
                    coin.x -= 0.25 * scale
                }
            }

            if (transition) {
                launch { music.play() }
                transitionCount++
                onGoing = false
                transition = false
                var targetPlanet = winterIcon
                if (winterIcon.alpha == 1.0) {
                    targetPlanet = fallIcon
                    if (fallIcon.alpha == 1.0) {
                        targetPlanet = springIcon
                        if (springIcon.alpha == 1.0) {

                            /*

                            # SUMMER CONDITION

                             */

                            onGoing = false
                            launch {
                                coinChance = 400
                                darkCoins = true
                                fallIcon.tween(fallIcon::y[fallIcon.y, -400], time = 0.5.seconds, easing = Easing.EASE_IN)
                                winterIcon.tween(winterIcon::y[winterIcon.y, -400], time = 0.5.seconds, easing = Easing.EASE_IN)
                                springIcon.tween(springIcon::y[springIcon.y, -400], time = 0.5.seconds, easing = Easing.EASE_IN)

                                sceneBackdrop.tween(sceneBackdrop::alpha[1, 0], time = 2.seconds, easing = Easing.EASE_OUT)
                                sceneBackdrop.bitmap = summerBackdrop.bitmap
                                val summerText = text("Summer", 42.0, Colors.WHITE, font, TextAlignment.CENTER, DefaultStringTextRenderer, Text.DEFAULT_AUTO_SCALING)
                                summerText.alpha(0)
                                summerText.position(490, 15)
                                sceneBackdrop.tween(sceneBackdrop::alpha[0, 1], time = 2.seconds, easing = Easing.EASE_IN)

                                tiltEarth.launch()

                                summerText.tween(summerText::alpha[0, 1], time = 1.5.seconds, easing = Easing.EASE_IN)
                                summerText.tween(summerText::alpha[1, 0], time = 2.5.seconds, easing = Easing.EASE_OUT)

                                val summerOverlay = image(resourcesVfs["summeroverlay.png"].readBitmap())
                                summerOverlay.alpha(0)
                                while (heatWave) {
                                    delay((3..5).random().seconds)
                                    summerOverlay.tween(summerOverlay::alpha[0, 0.6], time = 2.seconds, easing = Easing.EASE_IN)
                                    delay((1..2).random().seconds)
                                    summerOverlay.tween(summerOverlay::alpha[0.6, 0], time = 3.seconds, easing = Easing.EASE_OUT)
                                }
                            }
                        } else {
                            launch { springIcon.tween(springIcon::alpha[0, 1], time = 0.75.seconds, easing = Easing.EASE_IN) }
                            pop(springIcon.x, springIcon.y)

                            launch {
                                /*earth.tween(earth::alpha[1, 0], time = 1.seconds, easing = Easing.EASE_OUT)
                                earth.visible = false*/
                            }
                        }
                    } else {
                        launch { fallIcon.tween(fallIcon::alpha[0, 1], time = 0.75.seconds, easing = Easing.EASE_IN) }
                        pop(fallIcon.x, fallIcon.y)
                    }
                } else {
                    launch { winterIcon.tween(winterIcon::alpha[0, 1], time = 0.75.seconds, easing = Easing.EASE_IN) }
                    pop(winterIcon.x, winterIcon.y)
                }

                if (transitionCount == 1) {
                    launch {
                        sceneBackdrop.tween(sceneBackdrop::alpha[1, 0], time = 2.seconds, easing = Easing.EASE_IN_OUT)
                        sceneBackdrop.y = transitionDropHeight
                        sceneBackdrop.bitmap = fallBackdrop.bitmap
                        val fallText = text("Fall", 42.0, Colors.WHITE, font, TextAlignment.CENTER, DefaultStringTextRenderer, Text.DEFAULT_AUTO_SCALING)
                        fallText.alpha(0)
                        fallText.position(490, 15)

                        sceneBackdrop.tween(sceneBackdrop::alpha[0, 1], time = 2.seconds, easing = Easing.EASE_IN_OUT)
                        fallText.tween(fallText::alpha[0, 1], time = 1.5.seconds, easing = Easing.EASE_IN)
                        fallText.tween(fallText::alpha[1, 0], time = 2.5.seconds, easing = Easing.EASE_OUT)
                    }
                } else if (transitionCount == 2) {
                    launch {
                        sceneBackdrop.tween(sceneBackdrop::alpha[1, 0], time = 2.seconds, easing = Easing.EASE_IN_OUT)
                        sceneBackdrop.y = transitionDropHeight
                        sceneBackdrop.bitmap = springBackdrop.bitmap
                        val springText = text("Spring", 42.0, Colors.WHITE, font, TextAlignment.CENTER, DefaultStringTextRenderer, Text.DEFAULT_AUTO_SCALING)
                        springText.alpha(0)
                        springText.position(495, 15)

                        sceneBackdrop.tween(sceneBackdrop::alpha[0, 1], time = 2.seconds, easing = Easing.EASE_IN_OUT)
                        springText.tween(springText::alpha[0, 1], time = 1.5.seconds, easing = Easing.EASE_IN)
                        springText.tween(springText::alpha[1, 0], time = 2.5.seconds, easing = Easing.EASE_OUT)
                    }
                }

                launch {
                    val tempPlanet = image(targetPlanet.bitmap)
                    tempPlanet.position(targetPlanet.pos)
                    tempPlanet.x += tempPlanet.width / 3
                    tempPlanet.visible = false

                    earth.tween(earth::pos[earth.pos, tempPlanet.pos], earth::alpha[earth.alpha, 0.1], time = 1.5.seconds, easing = Easing.EASE_IN_OUT)
                    earth.tween(earth::pos[tempPlanet.pos, earthDockedIconPos], earth::alpha[earth.alpha, 0.1], time = 2.25.seconds, easing = Easing.EASE_IN_OUT_BACK)

                    onGoing = true
                }
            }
        }
    }

    launchText.tween(launchText::alpha[1, 0], time = 2.seconds, easing = Easing.EASE_OUT_IN)

    earth.position((this.width / 2 - (earth.width / 2)), this.height - earth.height * 6)

    earth.tween(earth::alpha[0, 0.1], time = 1.seconds, easing = Easing.EASE_IN_OUT)

    // im so sorry for this, but am I clever or really f*ing stupid?
    // catch coins via rotation instead of object identification
    // because i have no idea how to do that in KorGE

    var earthTilt = 0
    val earthRate = 0.15

    player.onCollision(filter = { it.rotation == player.rotation || it.rotation == 85.degrees }) {
        if (onGoing) {
            pop(it.x, it.y)

            if (transitionCount > 3)
            {
                if (it.rotation == 85.degrees)
                {
                    earth.alpha(0)
                }
                else
                {
                    earth.alpha += (earthRate/1.5)
                    if (earth.alpha >= 0.9) {
                        if (tiltEarth.transition()) pop(300.0, 285.0, true)
                        earth.alpha(0)
                        earthTilt += 15
                    }

                    if (earthTilt >= 45)
                    {
                        player.alpha(0)
                        launch {
                            heatWave = false
                            onGoing = false
                            player.tween(player::alpha[1,0], time = 0.25.seconds, easing = Easing.EASE_OUT)

                            sceneBackdrop.tween(sceneBackdrop::alpha[1, 0], time = 0.5.seconds, easing = Easing.EASE_OUT)
                            sceneBackdrop.bitmap = victory
                            sceneBackdrop.tween(sceneBackdrop::alpha[0, 1], time = 0.5.seconds, easing = Easing.EASE_IN)
                            delay(3.seconds)
                            tiltEarth.transition()
                        }
                    }
                }
            }
            else
            {
                earth.alpha += earthRate

                if (earth.alpha >= 0.95) transition = true
            }

            it.rotation(0.degrees)
            launch {
                it.tween(it::alpha[1, 0], time = 0.25.seconds, easing = Easing.EASE_OUT)
            }
        }
    }
}

