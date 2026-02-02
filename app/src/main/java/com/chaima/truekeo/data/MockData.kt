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
        id = "AeRc6FqDgwfhxlOsYbThCPdVgok1",
        username = "chaima",
        firstAndLastName = "Soy Chaimaa",
        avatarUrl = "https://xcawesphifjagaixywdh.supabase.co/storage/v1/object/public/profile_photos/pf_AeRc6FqDgwfhxlOsYbThCPdVgok1.jpg",
    )

    val mariaUser = User(
        id = "u2",
        username = "María",
        firstAndLastName = "No name",
        avatarUrl = "https://img.freepik.com/foto-gratis/mujer-joven-hermosa-sueter-rosa-calido-aspecto-natural-sonriente-retrato-aislado-cabello-largo_285396-896.jpg?w=740",
    )

    val carlosUser = User(
        id = "u3",
        username = "Carlos",
        firstAndLastName = "No name",
        avatarUrl = "https://img.freepik.com/foto-gratis/retrato-hombre-feliz-sonriente_23-2149022620.jpg?w=740",
    )

    val luciaUser = User(
        id = "u4",
        username = "Lucía",
        firstAndLastName = "No name",
        avatarUrl = "https://img.freepik.com/foto-gratis/chica-joven-rizada-alegre_176420-9473.jpg?w=740",
    )

    // Lista de todos los usuarios
    val users = listOf(chaimaUser, mariaUser, carlosUser, luciaUser)

    // Truekes de prueba
    val sampleTruekes = listOf(
        Trueke(
            id = "t1",
            title = "Cambio PS4 por bici",
            description = "Quedamos por Sol",
            hostUser = chaimaUser,
            hostItem = Item(
                id = "i1",
                name = "PS4 Slim",
                details = "Consola en perfecto estado, incluye un mando y cables. Apenas usada, como nueva.",
                imageUrls = listOf("https://cdn.wallapop.com/images/10420/fe/y1/__/c10420p932095462/i3461238421.jpg?pictureSize=W640",
                    "https://cdn.wallapop.com/images/10420/k6/o7/__/c10420p1220530228/i6227088896.jpg?pictureSize=W320"),
                brand = "Sony",
                condition = ItemCondition.NEW
            ),
            location = GeoPoint(-3.7038, 40.4168), // Sol, Madrid
            createdAt = Instant.now().minus(13, ChronoUnit.MINUTES)
        ),
        Trueke(
            id = "t2",
            title = "Cambio monitor por teclado mecánico",
            description = "Monitor gaming 144Hz, busco teclado mecánico de calidad similar",
            hostUser = mariaUser,
            hostItem = Item(
                id = "i2",
                name = "Monitor 24'' Gaming",
                details = "Monitor gaming 144Hz, 1ms de respuesta, perfecto para juegos competitivos",
                imageUrls = listOf("https://cdn.wallapop.com/images/10420/k7/bv/__/c10420p1221635498/i6233515930.jpg?pictureSize=W320"),
                brand = "ASUS",
                condition = ItemCondition.NEW
            ),
            location = GeoPoint(-3.7123, 40.4250), // Gran Vía, Madrid
            createdAt = Instant.now().minus(2, ChronoUnit.HOURS)
        ),
        Trueke(
            id = "t3",
            title = "Cambio libros DAM",
            description = "Pack completo de libros del ciclo DAM, busco libros de DAW",
            hostUser = chaimaUser,
            hostItem = Item(
                id = "i3",
                name = "Pack libros DAM",
                details = "Libros completos del ciclo de Desarrollo de Aplicaciones Multiplataforma. Buen estado.",
                imageUrls = listOf("https://cdn.wallapop.com/images/10420/k5/ti/__/c10420p1219098489/i6218005841.jpg?pictureSize=W320"),
                brand = "Anaya",
                condition = ItemCondition.NEW
            ),
            location = GeoPoint(-3.6890, 40.4095), // Atocha, Madrid
            createdAt = Instant.now().minus(45, ChronoUnit.DAYS)
        ),
        Trueke(
            id = "t4",
            title = "Cambio patinete eléctrico",
            description = "Patinete eléctrico Xiaomi, busco bicicleta plegable",
            hostUser = carlosUser,
            hostItem = Item(
                id = "i4",
                name = "Patinete Xiaomi M365",
                details = "Patinete eléctrico con 500km, batería en buen estado, incluye candado",
                imageUrls = listOf("https://cdn.wallapop.com/images/10420/3h/n7/__/c10420p1095857944/i5586890123.jpg?pictureSize=W320"),
                brand = "Xiaomi",
                condition = ItemCondition.NEW
            ),
            location = GeoPoint(-3.7050, 40.4200), // Retiro, Madrid
            status = TruekeStatus.COMPLETED,
            createdAt = Instant.now().minus(1, ChronoUnit.DAYS)
        ),
        Trueke(
            id = "t5",
            title = "Cámara réflex por portátil",
            description = "Cámara Canon EOS 700D con objetivo 18-55mm, busco portátil gaming",
            hostUser = luciaUser,
            hostItem = Item(
                id = "i5",
                name = "Canon EOS 700D",
                details = "Cámara réflex con objetivo kit, tarjeta SD 32GB, bolsa y trípode incluidos",
                imageUrls = listOf("https://cdn.wallapop.com/images/10420/qr/lm/__/c10420p945623178/i4234567890.jpg?pictureSize=W320"),
                brand = "Canon",
                condition = ItemCondition.GOOD
            ),
            location = GeoPoint(-3.6950, 40.4300), // Chamberí, Madrid
            status = TruekeStatus.COMPLETED,
            createdAt = Instant.now().minus(3, ChronoUnit.DAYS)
        ),
        Trueke(
            id = "t6",
            title = "Guitarra eléctrica por bajo",
            description = "Guitarra Fender Stratocaster, busco bajo eléctrico",
            hostUser = mariaUser,
            hostItem = Item(
                id = "i6",
                name = "Fender Stratocaster",
                details = "Guitarra eléctrica con amplificador pequeño, cable y funda acolchada",
                imageUrls = listOf("https://cdn.wallapop.com/images/10420/k0/6s/__/c10420p1209641006/i6159174364.jpg?pictureSize=W640"),
                brand = "Fender",
                condition = ItemCondition.LIKE_NEW
            ),
            location = GeoPoint(-3.7100, 40.4100), // Lavapiés, Madrid
            status = TruekeStatus.RESERVED,
            createdAt = Instant.now().minus(5, ChronoUnit.HOURS)
        ),
        Trueke(
            id = "t7",
            title = "iPhone 12 por Android tope de gama",
            description = "iPhone 12 128GB, busco Samsung S21 o similar",
            hostUser = carlosUser,
            hostItem = Item(
                id = "i7",
                name = "iPhone 12 128GB",
                details = "iPhone en perfecto estado, batería al 89%, sin arañazos, con caja original",
                imageUrls = listOf("https://cdn.wallapop.com/images/10420/hr/i9/__/c10420p1074127078/i5266670003.jpg?pictureSize=W640"),
                brand = "Apple",
                condition = ItemCondition.POOR
            ),
            location = GeoPoint(-3.7000, 40.4280), // Malasaña, Madrid
            status = TruekeStatus.COMPLETED,
            createdAt = Instant.now().minus(8, ChronoUnit.HOURS)
        ),
        Trueke(
            id = "t8",
            title = "Intercambio guitarra por teclado",
            description = "Busco teclado mecánico gaming",
            hostUser = mariaUser,
            hostItem = Item(
                id = "i8",
                name = "Guitarra Acústica Yamaha",
                details = "Guitarra en perfecto estado, sonido limpio",
                imageUrls = listOf("https://cdn.wallapop.com/images/10420/k7/e5/__/c10420p1221741690/i6234038252.jpg?pictureSize=W320"),
                brand = "Yamaha",
                condition = ItemCondition.LIKE_NEW
            ),
            takerUser = carlosUser,
            takerItem = Item(
                id = "i9",
                name = "Teclado Mecánico Logitech",
                details = "Switches GX Blue, RGB completo",
                imageUrls = listOf("https://cdn.wallapop.com/images/10420/aa/bb/__/c10420p2222222222/i9999999999.jpg"),
                brand = "Logitech",
                condition = ItemCondition.GOOD
            ),
            location = GeoPoint(-3.7050, 40.4200),
            status = TruekeStatus.RESERVED,
            createdAt = Instant.now().minus(3, ChronoUnit.HOURS)
        )
    )

    val items = listOf(
        Item(
            id = "item_1",
            name = "iPhone 12",
            details = "64GB · Color negro · En buen estado",
            imageUrls = listOf(
                "https://picsum.photos/400/400?random=1"
            ),
            brand = "Apple",
            condition = ItemCondition.GOOD
        ),
        Item(
            id = "item_2",
            name = "Bicicleta de montaña",
            details = "Ruedas 27.5 · Poco uso",
            imageUrls = listOf(
                "https://picsum.photos/400/400?random=2"
            ),
            brand = "Rockrider",
            condition = ItemCondition.LIKE_NEW
        ),
        Item(
            id = "item_3",
            name = "PlayStation 4",
            details = "Incluye mando y cables",
            imageUrls = listOf(
                "https://picsum.photos/400/400?random=3"
            ),
            brand = "Sony",
            condition = ItemCondition.GOOD
        ),
        Item(
            id = "item_4",
            name = "Auriculares Bluetooth",
            details = "Cancelación de ruido",
            imageUrls = listOf(
                "https://picsum.photos/400/400?random=4"
            ),
            brand = "Sony",
            condition = ItemCondition.LIKE_NEW
        ),
        Item(
            id = "item_5",
            name = "Cámara réflex Canon",
            details = "18–55mm · Muy cuidada",
            imageUrls = listOf(
                "https://picsum.photos/400/400?random=5"
            ),
            brand = "Canon",
            condition = ItemCondition.GOOD
        )
    )
}