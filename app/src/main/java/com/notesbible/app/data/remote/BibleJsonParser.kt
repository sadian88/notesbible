package com.notesbible.app.data.remote

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class BibleJsonParser(
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    fun parse(versionId: String, rawJson: String): List<ParsedVerse> {
        val element = json.parseToJsonElement(rawJson)
        val books = (element as? JsonArray) ?: return emptyList()
        val verses = mutableListOf<ParsedVerse>()

        books.forEachIndexed { bookIndex, bookElement ->
            val obj = bookElement.jsonObject
            val bookName = obj["book"]?.jsonPrimitive?.contentOrNull
                ?: obj["name"]?.jsonPrimitive?.contentOrNull
                ?: "Libro ${bookIndex + 1}"
            val chapterArray = obj["chapters"] as? JsonArray ?: return@forEachIndexed
            chapterArray.forEachIndexed { chapterIndex, chapterElement ->
                val versesArray = (chapterElement as? JsonArray)
                    ?: chapterElement.jsonArray
                versesArray.forEachIndexed { verseIndex, verseElement ->
                    val text = verseElement.jsonPrimitive.contentOrNull ?: verseElement.toString()
                    verses.add(
                        ParsedVerse(
                            versionId = versionId,
                            book = bookName,
                            bookIndex = bookIndex,
                            chapterNumber = chapterIndex + 1,
                            verseNumber = verseIndex + 1,
                            text = text.trim()
                        )
                    )
                }
            }
        }

        return verses
    }
}

data class ParsedVerse(
    val versionId: String,
    val book: String,
    val bookIndex: Int,
    val chapterNumber: Int,
    val verseNumber: Int,
    val text: String
)
