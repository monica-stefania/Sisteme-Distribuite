data class BidderData(
    val name: String = "",
    val phone: String = "",
    val email: String = ""
)

fun randomBidderData(): BidderData
{
    val firstNames = listOf(
        "Stefania",
        "Vlad",
        "Paul",
        "Theo",
        "Sebi",
        "Ana",
        "Ion",
        "Maria",
        "Alex",
        "Elena",
        "Mihai",
        "David",
        "Sofia"
    )
    val lastNames =
        listOf("Luca", "Timofte", "Rusu", "Rosu", "Popescu", "Ionescu", "Marin", "Dumitrescu", "Stan", "Georgescu")

    val name = "${firstNames.random()} ${lastNames.random()}"
    val phone = "07" + (10000000..99999999).random()
    val email = name.lowercase().replace(" ", "-") + "@gmail.com"

    return BidderData(name, phone, email)
}