package com.notesbible.app.data.models

data class BibleVersionDefinition(
    val id: String,
    val name: String,
    val language: String,
    val abbreviation: String,
    val downloadPath: String,
    val description: String
)

val DefaultBibleVersions = listOf(
    BibleVersionDefinition(
        id = "es_1909",
        name = "Reina Valera 1909",
        language = "Español",
        abbreviation = "RVR1909",
        downloadPath = "https://raw.githubusercontent.com/thiagobodruk/bible/master/json/es_1909.json",
        description = "Edición histórica castellana con ortografía clásica."
    ),
    BibleVersionDefinition(
        id = "es_rvc",
        name = "Reina Valera Contemporánea",
        language = "Español",
        abbreviation = "RVC",
        downloadPath = "https://raw.githubusercontent.com/thiagobodruk/bible/master/json/es_rvc.json",
        description = "Traducción moderna fácil de leer."
    ),
    BibleVersionDefinition(
        id = "en_kjv",
        name = "King James Version",
        language = "English",
        abbreviation = "KJV",
        downloadPath = "https://raw.githubusercontent.com/thiagobodruk/bible/master/json/en_kjv.json",
        description = "Publicada en 1611, una de las versiones inglesas más conocidas."
    ),
    BibleVersionDefinition(
        id = "en_bbe",
        name = "Bible in Basic English",
        language = "English",
        abbreviation = "BBE",
        downloadPath = "https://raw.githubusercontent.com/thiagobodruk/bible/master/json/en_bbe.json",
        description = "Inglés simple pensado para lectores no nativos."
    )
)
