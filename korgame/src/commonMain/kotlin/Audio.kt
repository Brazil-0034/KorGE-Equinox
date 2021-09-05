import com.soywiz.klock.infiniteTimes
import com.soywiz.korau.sound.readMusic
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korge.*

class Audio {
    suspend fun play() {
        val music = resourcesVfs["bgm.mp3"].readMusic()
        val channel = music.playForever()
    }
}