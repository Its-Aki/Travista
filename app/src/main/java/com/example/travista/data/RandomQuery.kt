package com.example.travista.data

object CityQueryGenerator {

    private val topTourismCountries = listOf(
    "France", "Spain", "United States", "China", "Italy", "Turkey", "Mexico", "Thailand", "United Kingdom",
    "Japan", "Austria", "Greece", "Malaysia", "Russia", "Portugal", "Canada", "Poland", "Netherlands", "Saudi Arabia",
    "Hungary", "Hong Kong", "South Korea", "India", "Vietnam", "Czech Republic", "Ukraine", "Switzerland", "Sweden", "Indonesia",
    "Croatia", "Belgium", "Egypt", "Australia", "Singapore", "Morocco", "Brazil", "United Arab Emirates", "Ireland", "South Africa",
    "Norway", "Denmark", "Slovakia", "Finland", "Romania", "New Zealand", "Tunisia", "Philippines", "Argentina", "Israel",
    "Serbia", "Bulgaria", "Jordan", "Peru", "Colombia", "Chile", "Slovenia", "Lithuania", "Latvia", "Estonia",
    "Albania", "Iceland", "Sri Lanka", "Dominican Republic", "Kazakhstan", "Belarus", "Panama", "Georgia", "Nepal", "Costa Rica",
    "Qatar", "Bosnia and Herzegovina", "Luxembourg", "Ecuador", "Kuwait", "Armenia", "Uzbekistan", "Malta", "Cyprus", "Oman",
    "Lebanon", "Paraguay", "Bangladesh", "Montenegro", "Macau", "Mauritius", "Kenya", "Uruguay", "Azerbaijan", "Guatemala",
    "Namibia", "Tanzania", "Bolivia", "Honduras", "Puerto Rico", "Barbados", "Maldives", "Bahrain", "Reunion", "Laos",
    "El Salvador", "Madagascar", "Cuba", "Jamaica", "Zimbabwe", "Ghana", "Mozambique", "Ethiopia", "Zambia", "Cameroon",
    "Botswana", "Fiji", "Trinidad and Tobago", "Bahamas", "Seychelles", "Pakistan", "Nicaragua", "Papua New Guinea", "Cambodia", "Rwanda",
    "Venezuela", "North Macedonia", "Mongolia", "Belize", "Andorra", "Greenland", "Liechtenstein", "Tajikistan", "Togo", "Benin",
    "Cape Verde", "Palestine", "Iran", "Iraq", "Sudan", "Democratic Republic of Congo", "Eswatini", "Gabon", "Brunei", "Turkmenistan",
    "Malawi", "Liberia", "Ivory Coast", "Senegal", "Uganda", "Sri Lanka", "Guinea", "Haiti", "Yemen", "Suriname"
    )


    private val templates = listOf(
        "top tourist cities to visit in",
        "most popular cities for travelers in",
        "famous cities worth visiting in",
        "must-visit cities for tourism in",
        "most visited cities in",
        "top destinations and cities in",
        "best cities for sightseeing in",
        "best cities to explore in",
        "top cultural cities in",
        "iconic travel cities in"
    )
    fun getRandomCityQuery(): String {
        val randomCountry = topTourismCountries.random()
        val randomTemplate = templates.random()
        return "$randomTemplate $randomCountry"
    }
}

