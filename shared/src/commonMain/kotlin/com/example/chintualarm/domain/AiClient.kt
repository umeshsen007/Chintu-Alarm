package com.example.chintualarm.domain

import com.example.chintualarm.Secrets
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.HttpTimeout
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class AiRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class AiResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content? = null
)

@Serializable
data class AiAction(
    val action: String, // ADD, UPDATE, DELETE
    val id: String? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val label: String? = null,
    val replyToUser: String // The message to show in the chat UI
)


class AiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60000
            connectTimeoutMillis = 60000
            socketTimeoutMillis = 60000
        }
    }

    private val systemPrompt = """
        You are Chintu AI, a helpful alarm assistant.
        The user will give you commands like "Wake me up at 6 AM" or "Delete my gym alarm".
        You must return a raw JSON array of actions to take. 
        DO NOT wrap the response in markdown blocks like ```json. Return ONLY the raw JSON array.
        
        Fields for an action:
        - action: "ADD", "UPDATE", or "DELETE"
        - id: (Only for UPDATE or DELETE) the string ID of the alarm.
        - hour: (0-23)
        - minute: (0-59)
        - label: String
        - replyToUser: A friendly conversational response confirming what you did.

        Example:
        [
            {
                "action": "ADD",
                "hour": 6,
                "minute": 0,
                "label": "Wake up",
                "replyToUser": "I've set an alarm for 6:00 AM."
            }
        ]
    """.trimIndent()

    suspend fun parseIntent(userMessage: String, currentAlarmsJson: String, currentTime: String): List<AiAction>? {
        val prompt = """
            Current time is: $currentTime
            Existing alarms: $currentAlarmsJson
            
            User says: "$userMessage"
            
            Based on the user's message, what actions should be taken?
        """.trimIndent()

        val requestBody = AiRequest(
            contents = listOf(
                Content(
                    parts = listOf(Part(text = prompt))
                )
            ),
            systemInstruction = Content(
                parts = listOf(Part(text = systemPrompt))
            )
        )

        return try {
            val response: AiResponse = client.post("https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=${Secrets.GEMINI_API_KEY}") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

            val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (responseText != null) {
                // Parse the JSON array. We need to handle potential markdown formatting if the model disobeys instructions.
                val cleanJson = responseText.replace("```json", "").replace("```", "").trim()
                Json { ignoreUnknownKeys = true }.decodeFromString<List<AiAction>>(cleanJson)
            } else {
                throw Exception("Response text was null")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
