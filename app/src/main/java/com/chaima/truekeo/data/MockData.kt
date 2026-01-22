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
            title = "Cambio PS4 por bici",
            description = "Quedamos por Sol",
            hostUser = chaimaUser,
            hostItem = Item(
                id = "i1",
                name = "PS4 Slim",
                details = "Consola en perfecto estado, incluye un mando y cables. Apenas usada, como nueva.",
                imageUrl = "https://cdn.wallapop.com/images/10420/fe/y1/__/c10420p932095462/i3461238421.jpg?pictureSize=W640",
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
                imageUrl = "https://cdn.wallapop.com/images/10420/k7/bv/__/c10420p1221635498/i6233515930.jpg?pictureSize=W320",
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
                imageUrl = "https://cdn.wallapop.com/images/10420/k5/ti/__/c10420p1219098489/i6218005841.jpg?pictureSize=W320",
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
            title = "Cámara réflex por portátil",
            description = "Cámara Canon EOS 700D con objetivo 18-55mm, busco portátil gaming",
            hostUser = luciaUser,
            hostItem = Item(
                id = "i5",
                name = "Canon EOS 700D",
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
            title = "Guitarra eléctrica por bajo",
            description = "Guitarra Fender Stratocaster, busco bajo eléctrico",
            hostUser = mariaUser,
            hostItem = Item(
                id = "i6",
                name = "Fender Stratocaster",
                details = "Guitarra eléctrica con amplificador pequeño, cable y funda acolchada",
                imageUrl = "https://cdn.wallapop.com/images/10420/k0/6s/__/c10420p1209641006/i6159174364.jpg?pictureSize=W640",
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
                imageUrl = "https://cdn.wallapop.com/images/10420/hr/i9/__/c10420p1074127078/i5266670003.jpg?pictureSize=W640",
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
                imageUrl = "https://cdn.wallapop.com/images/10420/k7/e5/__/c10420p1221741690/i6234038252.jpg?pictureSize=W320",
                brand = "Yamaha",
                condition = ItemCondition.LIKE_NEW
            ),
            takerUser = carlosUser,
            takerItem = Item(
                id = "i9",
                name = "Teclado Mecánico Logitech",
                details = "Switches GX Blue, RGB completo",
                imageUrl = "https://cdn.wallapop.com/images/10420/aa/bb/__/c10420p2222222222/i9999999999.jpg",
                brand = "Logitech",
                condition = ItemCondition.GOOD
            ),
            location = GeoPoint(-3.7050, 40.4200),
            status = TruekeStatus.RESERVED,
            createdAt = Instant.now().minus(3, ChronoUnit.HOURS)
        )
    )

    val sampleTruekesWithTaker = listOf(
        Trueke(
            id = "tx1",
            title = "PS4 por bici plegable",
            description = "Intercambio acordado en Sol",
            hostUser = chaimaUser,
            hostItem = Item(
                id = "ix1_host",
                name = "PS4 Slim 1TB",
                details = "Incluye 1 mando y cables. Funciona perfecta.",
                imageUrl = "https://cdn.wallapop.com/images/10420/k5/qk/__/c10420p1218961628/i6217265588.jpg?pictureSize=W640",
                brand = "Sony",
                condition = ItemCondition.GOOD
            ),
            takerUser = carlosUser,
            takerItem = Item(
                id = "ix1_taker",
                name = "Bici plegable Decathlon",
                details = "Plegable, ligera, ideal para ciudad.",
                imageUrl = "https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&w=1200&q=80",
                brand = "Decathlon",
                condition = ItemCondition.LIKE_NEW
            ),
            location = GeoPoint(-3.7038, 40.4168),
            status = TruekeStatus.RESERVED,
            createdAt = Instant.now().minus(2, ChronoUnit.HOURS)
        ),
        Trueke(
            id = "tx2",
            title = "Cámara por portátil",
            description = "Intercambio completado en Chamberí",
            hostUser = luciaUser,
            hostItem = Item(
                id = "ix2_host",
                name = "Canon EOS 700D",
                details = "Con objetivo 18-55mm, bolsa incluida.",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSx5m5HltyjHppJWmJMjV2Aeb1GlZ_NN6JMsA&s",
                brand = "Canon",
                condition = ItemCondition.LIKE_NEW
            ),
            takerUser = mariaUser,
            takerItem = Item(
                id = "ix2_taker",
                name = "Portátil Lenovo i5",
                details = "16GB RAM, SSD 512GB. Muy rápido.",
                imageUrl = "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=1200&q=80",
                brand = "Lenovo",
                condition = ItemCondition.GOOD
            ),
            location = GeoPoint(-3.6950, 40.4300),
            status = TruekeStatus.COMPLETED,
            createdAt = Instant.now().minus(1, ChronoUnit.DAYS)
        ),
        Trueke(
            id = "tx3",
            title = "PS4 por bici plegable",
            description = "Intercambio acordado en Sol",
            hostUser = chaimaUser,
            hostItem = Item(
                id = "ix1_host",
                name = "Casco moto",
                details = "Incluye 1 mando y cables. Funciona perfecta.",
                imageUrl = "https://cdn.wallapop.com/images/10420/j5/3o/__/c10420p1157427398/i5828379240.jpg?pictureSize=W800",
                brand = "Sony",
                condition = ItemCondition.GOOD
            ),
            takerUser = carlosUser,
            takerItem = Item(
                id = "ix1_taker",
                name = "Moto de cross",
                details = "Plegable, ligera, ideal para ciudad.",
                imageUrl = "https://media.triumphmotorcycles.co.uk/image/upload/f_auto/q_auto:eco/sitecoremedialibrary/media-library/images/spain/offers-spain/hprs%20spain/esoffers-hpr_320x320/20220614_213942_img_8315_1x1.jpg",
                brand = "Decathlon",
                condition = ItemCondition.LIKE_NEW
            ),
            location = GeoPoint(-3.7038, 40.4168),
            status = TruekeStatus.OPEN,
            createdAt = Instant.now().minus(2, ChronoUnit.HOURS)
        ),
        Trueke(
            id = "tx4",
            title = "Cámara por portátil",
            description = "Intercambio completado en Chamberí",
            hostUser = luciaUser,
            hostItem = Item(
                id = "ix2_host",
                name = "Canon EOS 700D",
                details = "Con objetivo 18-55mm, bolsa incluida.",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSx5m5HltyjHppJWmJMjV2Aeb1GlZ_NN6JMsA&s",
                brand = "Canon",
                condition = ItemCondition.LIKE_NEW
            ),
            takerUser = mariaUser,
            takerItem = Item(
                id = "ix2_taker",
                name = "Portátil Lenovo i5",
                details = "16GB RAM, SSD 512GB. Muy rápido.",
                imageUrl = "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=1200&q=80",
                brand = "Lenovo",
                condition = ItemCondition.GOOD
            ),
            location = GeoPoint(-3.6950, 40.4300),
            status = TruekeStatus.CANCELLED,
            createdAt = Instant.now().minus(1, ChronoUnit.DAYS)
        )
    )
}