package me.geek.mail.scheduler

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import me.geek.mail.GeekMail
import me.geek.mail.api.data.PlayerData
import me.geek.mail.api.mail.MailSub
import me.geek.mail.modules.Mail_Item
import me.geek.mail.modules.Mail_Normal
import me.geek.mail.utils.serializeItemStacks
import org.bukkit.entity.Player
import org.xerial.snappy.Snappy
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/9/10
 **/
data class MailPlayerData(
    @Expose
    override val player: Player,

    override var mail: String = "",

    override val mailData: MutableList<MailSub> = mutableListOf()
): PlayerData {

    override val user: String = player.displayName

    override val uuid: UUID = player.uniqueId

    override var newPlayer: Boolean = true


    override fun toByteArray(): ByteArray {
        val data = this.toJsonText()
        GeekMail.debug(data)
        return Snappy.compress(data.toByteArray(charset = Charsets.UTF_8))
    }

    override fun toJsonText(): String {
        this.mailData.forEach {
            when (it) {
                is Mail_Item -> {
                    it.itemStackString = it.itemStacks.serializeItemStacks()
                }
                is Mail_Normal -> {
                    it.itemStacks.let { i ->
                        if (i.isNotEmpty()) {
                            it.itemStackString = i.serializeItemStacks()
                        }
                    }
                }
                else -> {

                }
            }
        }
        return GsonBuilder()
            .setExclusionStrategies(Exclude())
            .setPrettyPrinting()
            .create().toJson(this)
    }
}
