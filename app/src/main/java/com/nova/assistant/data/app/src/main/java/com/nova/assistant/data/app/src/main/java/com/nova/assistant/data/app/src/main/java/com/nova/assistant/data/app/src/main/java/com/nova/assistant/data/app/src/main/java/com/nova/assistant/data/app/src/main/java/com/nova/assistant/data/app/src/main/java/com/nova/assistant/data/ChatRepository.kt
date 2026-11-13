
package com.nova.assistant.data

class ChatRepository(
    private val dao: ChatDao,
    private val settings: SecureSettings
) {
    val messages = dao.getAllMessages()

    suspend fun sendMessage(text: String): String {
        val cleanText = text.trim()
        require(cleanText.isNotEmpty()) { "Message cannot be empty" }

        val backendUrl = settings.backendUrl.trim()
        val accessToken = settings.accessToken.trim()

        require(backendUrl.isNotEmpty()) {
            "Backend URL is not configured"
        }
        require(accessToken.isNotEmpty()) {
            "Access token is not configured"
        }

        dao.insertMessage(
            ChatMessage(role = "user", content = cleanText)
        )

        val recentMessages = dao.getRecentMessages(20).reversed()

        val apiMessages = recentMessages.map {
            ApiMessage(
                role = it.role,
                content = it.content
            )
        }

        val api = ApiFactory.create(backendUrl)
        val response = api.sendMessage(
            authorization = "Bearer $accessToken",
            request = ChatRequest(messages = apiMessages)
        )

        if (!response.isSuccessful) {
            throw Exception("Server error: ${response.code()}")
        }

        val reply = response.body()?.reply?.trim()
            ?: throw Exception("Empty response from server")

        dao.insertMessage(
            ChatMessage(role = "assistant", content = reply)
        )

        return reply
    }

    suspend fun clearChat() {
        dao.deleteAllMessages()
    }
}
