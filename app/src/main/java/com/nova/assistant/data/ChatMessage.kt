
package com.nova.assistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val role: String,

    val content: String,

    val createdAt: Long = System.currentTimeMillis()
)
