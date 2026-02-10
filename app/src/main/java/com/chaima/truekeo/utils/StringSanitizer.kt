package com.chaima.truekeo.utils

// Limpia el texto de marca para evitar símbolos raros, emojis o HTML
fun sanitizeBrand(input: String): String {
    return input
        .trim()
        // Letras, números, espacios y símbolos habituales en marcas
        .replace(Regex("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ .&'()-]"), "")
        .replace(Regex("\\s+"), " ")
        .take(30)
}