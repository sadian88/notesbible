package com.notesbible.app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class DrawPoint(
    val x: Float,
    val y: Float
)

@Serializable
data class Stroke(
    val color: Long,
    val strokeWidth: Float,
    val points: List<DrawPoint>
)

@Serializable
data class HandwrittenNote(
    val versionId: String,
    val book: String,
    val chapter: Int,
    val verse: Int = 0,
    val strokes: List<Stroke>,
    val updatedAt: Long = System.currentTimeMillis()
)
