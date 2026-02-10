package com.chaima.truekeo.utils

object BrandData {

    val knownBrands: List<String> = listOf(
        // Tecnología / Electrónica general
        "Apple",
        "Samsung",
        "Xiaomi",
        "Huawei",
        "Sony",
        "LG",
        "Google",
        "Microsoft",
        "Nokia",
        "Motorola",
        "OnePlus",
        "Realme",
        "Oppo",
        "Vivo",
        "Honor",
        "Nothing",
        "HTC",
        "ZTE",
        "Alcatel",
        "BlackBerry",

        // Informática / Ordenadores
        "HP",
        "Lenovo",
        "Asus",
        "Acer",
        "Dell",
        "MSI",
        "Razer",
        "Gigabyte",
        "Intel",
        "AMD",
        "NVIDIA",
        "Corsair",
        "Logitech",
        "SteelSeries",
        "HyperX",
        "Kingston",
        "Western Digital",
        "Seagate",
        "Sandisk",

        // Gaming / Consolas
        "PlayStation",
        "Xbox",
        "Nintendo",
        "Valve",
        "Steam Deck",

        // Fotografía / Vídeo
        "Canon",
        "Nikon",
        "Sony Alpha",
        "Fujifilm",
        "Panasonic",
        "Olympus",
        "Leica",
        "GoPro",
        "DJI",

        // Audio
        "Bose",
        "JBL",
        "Beats",
        "Sennheiser",
        "Marshall",
        "AKG",
        "Bang & Olufsen",
        "Sonos",
        "Skullcandy",

        // Wearables
        "Garmin",
        "Fitbit",
        "Amazfit",
        "Withings",
        "Polar",

        // Electrodomésticos
        "Bosch",
        "Siemens",
        "Philips",
        "Whirlpool",
        "Balay",
        "Tefal",
        "Rowenta",
        "Braun",
        "Miele",
        "Electrolux",
        "Candy",
        "Beko",

        // Automoción / Accesorios
        "BMW",
        "Mercedes-Benz",
        "Audi",
        "Volkswagen",
        "Toyota",
        "Renault",
        "Peugeot",
        "Seat",
        "Ford",
        "Tesla",
        "Hyundai",
        "Kia",

        // Moda / Ropa
        "Nike",
        "Adidas",
        "Puma",
        "Reebok",
        "New Balance",
        "Under Armour",
        "Zara",
        "H&M",
        "Pull&Bear",
        "Bershka",
        "Uniqlo",
        "Levi's",
        "Tommy Hilfiger",
        "Calvin Klein",
        "Lacoste",
        "Ralph Lauren",
        "Jordan",

        // Calzado
        "Converse",
        "Vans",
        "Dr. Martens",
        "Timberland",
        "Skechers",

        // Belleza / Cuidado personal
        "L'Oréal",
        "Maybelline",
        "Nivea",
        "Dove",
        "Garnier",
        "Clinique",
        "MAC",
        "Sephora",

        // Otros / General
        "Ikea",
        "Decathlon",
        "Lego",
        "Hasbro",
        "Mattel",
        "Funko",
        "Dyson",
        "Xiaomi Home"
    )

    fun search(query: String, limit: Int = 8): List<String> {
        if (query.isBlank()) return emptyList()

        return knownBrands
            .filter { it.contains(query, ignoreCase = true) }
            .take(limit)
    }

    fun normalize(input: String): String =
        input.lowercase()
            .split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
}