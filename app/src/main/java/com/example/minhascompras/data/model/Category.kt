package com.example.minhascompras.data.model

enum class Category(val displayName: String, val icon: String) {
    FRUTAS("Frutas", "🍎"),
    VERDURAS("Verduras", "🥬"),
    LEGUMES("Legumes", "🥕"),
    CARNES("Carnes", "🥩"),
    LATICINIOS("Laticínios", "🧀"),
    PADARIA("Padaria", "🍞"),
    BEBIDAS("Bebidas", "🥤"),
    LIMPEZA("Limpeza", "🧹"),
    HIGIENE("Higiene", "🧴"),
    CONGELADOS("Congelados", "🧊"),
    GRAOS("Grãos", "🌾"),
    TEMPEROS("Temperos", "🧂"),
    DOCES("Doces", "🍫"),
    OUTROS("Outros", "📦");

    companion object {
        fun fromDisplayName(name: String): Category {
            return values().find { it.displayName == name } ?: OUTROS
        }
    }
}