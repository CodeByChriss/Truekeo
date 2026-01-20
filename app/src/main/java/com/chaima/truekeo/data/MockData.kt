package com.chaima.truekeo.data

import com.chaima.truekeo.models.GeoPoint
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.ItemCondition
import com.chaima.truekeo.models.Trueke
import com.chaima.truekeo.models.TruekeStatus
import com.chaima.truekeo.models.User
import java.time.Instant
import java.time.temporal.ChronoUnit

object MockData {
    val chaimaUser = User(
        id = "u1",
        username = "Chaima",
        avatarUrl = "https://img.freepik.com/foto-gratis/hombre-negro-posando_23-2148171639.jpg?semt=ais_hybrid&w=740&q=80",
    )

    val mariaUser = User(
        id = "u2",
        username = "María",
        avatarUrl = "https://img.freepik.com/foto-gratis/mujer-joven-hermosa-sueter-rosa-calido-aspecto-natural-sonriente-retrato-aislado-cabello-largo_285396-896.jpg?w=740",
    )

    val carlosUser = User(
        id = "u3",
        username = "Carlos",
        avatarUrl = "https://img.freepik.com/foto-gratis/retrato-hombre-feliz-sonriente_23-2149022620.jpg?w=740",
    )

    val luciaUser = User(
        id = "u4",
        username = "Lucía",
        avatarUrl = "https://img.freepik.com/foto-gratis/chica-joven-rizada-alegre_176420-9473.jpg?w=740",
    )

    // Lista de todos los usuarios
    val users = listOf(chaimaUser, mariaUser, carlosUser, luciaUser)

    // Truekes de prueba
    val sampleTruekes = listOf(
        Trueke(
            id = "t1",
            name = "Cambio PS4 por bici",
            description = "Quedamos por Sol",
            hostUser = chaimaUser,
            hostItem = Item(
                id = "i1",
                title = "PS4 Slim",
                details = "Consola en perfecto estado, incluye un mando y cables. Apenas usada, como nueva.",
                imageUrl = "https://cdn.wallapop.com/images/10420/k5/qk/__/c10420p1218961628/i6217265588.jpg?pictureSize=W320",
                brand = "Sony",
                condition = ItemCondition.NEW
            ),
            location = GeoPoint(-3.7038, 40.4168), // Sol, Madrid
            createdAt = Instant.now().minus(13, ChronoUnit.MINUTES)
        ),
        Trueke(
            id = "t2",
            name = "Cambio monitor por teclado mecánico",
            description = "Monitor gaming 144Hz, busco teclado mecánico de calidad similar",
            hostUser = mariaUser,
            hostItem = Item(
                id = "i2",
                title = "Monitor 24'' Gaming",
                details = "Monitor gaming 144Hz, 1ms de respuesta, perfecto para juegos competitivos",
                imageUrl = "https://cdn.wallapop.com/images/10420/ee/jl/__/c10420p870955066/i3115083731.jpg?pictureSize=W640",
                brand = "ASUS",
                condition = ItemCondition.NEW
            ),
            location = GeoPoint(-3.7123, 40.4250), // Gran Vía, Madrid
            createdAt = Instant.now().minus(2, ChronoUnit.HOURS)
        ),
        Trueke(
            id = "t3",
            name = "Cambio libros DAM",
            description = "Pack completo de libros del ciclo DAM, busco libros de DAW",
            hostUser = chaimaUser,
            hostItem = Item(
                id = "i3",
                title = "Pack libros DAM",
                details = "Libros completos del ciclo de Desarrollo de Aplicaciones Multiplataforma. Buen estado.",
                imageUrl = "https://cdn.wallapop.com/images/10420/k5/ti/__/c10420p1219098489/i6218005841.jpg?pictureSize=W320",
                brand = "Anaya",
                condition = ItemCondition.NEW
            ),
            location = GeoPoint(-3.6890, 40.4095), // Atocha, Madrid
            createdAt = Instant.now().minus(45, ChronoUnit.DAYS)
        ),
        Trueke(
            id = "t4",
            name = "Cambio patinete eléctrico",
            description = "Patinete eléctrico Xiaomi, busco bicicleta plegable",
            hostUser = carlosUser,
            hostItem = Item(
                id = "i4",
                title = "Patinete Xiaomi M365",
                details = "Patinete eléctrico con 500km, batería en buen estado, incluye candado",
                imageUrl = "https://cdn.wallapop.com/images/10420/3h/n7/__/c10420p1095857944/i5586890123.jpg?pictureSize=W320",
                brand = "Xiaomi",
                condition = ItemCondition.NEW
            ),
            location = GeoPoint(-3.7050, 40.4200), // Retiro, Madrid
            status = TruekeStatus.COMPLETED,
            createdAt = Instant.now().minus(1, ChronoUnit.DAYS)
        ),
        Trueke(
            id = "t5",
            name = "Cámara réflex por portátil",
            description = "Cámara Canon EOS 700D con objetivo 18-55mm, busco portátil gaming",
            hostUser = luciaUser,
            hostItem = Item(
                id = "i5",
                title = "Canon EOS 700D",
                details = "Cámara réflex con objetivo kit, tarjeta SD 32GB, bolsa y trípode incluidos",
                imageUrl = "https://cdn.wallapop.com/images/10420/qr/lm/__/c10420p945623178/i4234567890.jpg?pictureSize=W320",
                brand = "Canon",
                condition = ItemCondition.GOOD
            ),
            location = GeoPoint(-3.6950, 40.4300), // Chamberí, Madrid
            status = TruekeStatus.COMPLETED,
            createdAt = Instant.now().minus(3, ChronoUnit.DAYS)
        ),
        Trueke(
            id = "t6",
            name = "Guitarra eléctrica por bajo",
            description = "Guitarra Fender Stratocaster, busco bajo eléctrico",
            hostUser = mariaUser,
            hostItem = Item(
                id = "i6",
                title = "Fender Stratocaster",
                details = "Guitarra eléctrica con amplificador pequeño, cable y funda acolchada",
                imageUrl = "https://cdn.wallapop.com/images/10420/mt/pr/__/c10420p1156789234/i5123456789.jpg?pictureSize=W320",
                brand = "Fender",
                condition = ItemCondition.LIKE_NEW
            ),
            location = GeoPoint(-3.7100, 40.4100), // Lavapiés, Madrid
            status = TruekeStatus.RESERVED,
            createdAt = Instant.now().minus(5, ChronoUnit.HOURS)
        ),
        Trueke(
            id = "t7",
            name = "iPhone 12 por Android tope de gama",
            description = "iPhone 12 128GB, busco Samsung S21 o similar",
            hostUser = carlosUser,
            hostItem = Item(
                id = "i7",
                title = "iPhone 12 128GB",
                details = "iPhone en perfecto estado, batería al 89%, sin arañazos, con caja original",
                imageUrl = "https://cdn.wallapop.com/images/10420/xy/zq/__/c10420p1234567890/i6789012345.jpg?pictureSize=W320",
                brand = "Apple",
                condition = ItemCondition.POOR
            ),
            location = GeoPoint(-3.7000, 40.4280), // Malasaña, Madrid
            status = TruekeStatus.COMPLETED,
            createdAt = Instant.now().minus(8, ChronoUnit.HOURS)
        )
    )
}