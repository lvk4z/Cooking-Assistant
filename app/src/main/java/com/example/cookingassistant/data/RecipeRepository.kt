package com.example.cookingassistant.data

class RecipeRepository {

    fun getAll(): List<Recipe> = listOf(
        Recipe(
            id = 1,
            title = "Jajecznica",
            steps = listOf(
                Step("Rozbij jajka do miski"),
                Step("Dobrze wymieszaj jajka"),
                Step("Rozgrzej masło na patelni", timerSeconds = 60),
                Step("Wlej jajka na patelnię"),
                Step("Mieszaj przez 3-4 minuty", timerSeconds = 180),
                Step("Dopraw solą i pieprzem do smaku")
            )
        ),
        Recipe(
            id = 2,
            title = "Naleśniki",
            steps = listOf(
                Step("Przygotuj miskę i wymieszaj mąkę, mleko i jajka"),
                Step("Dodaj szczyptę soli i cukru do ciasta"),
                Step("Rozgrzej patelnię z masłem", timerSeconds = 90),
                Step("Wlej ciasto i rozprowadź na patelni"),
                Step("Smaż naleśnik przez 2 minuty", timerSeconds = 120),
                Step("Przewróć naleśnik i smaż drugą stronę", timerSeconds = 60),
                Step("Przełóż gotowy naleśnik na talerz")
            )
        ),
        Recipe(
            id = 3,
            title = "Makaron z sosem pomidorowym",
            steps = listOf(
                Step("Zagotuj wodę w garnku", timerSeconds = 300),
                Step("Dodaj sól i wrzuć makaron"),
                Step("Gotuj makaron przez 10-12 minut", timerSeconds = 660),
                Step("Na patelni przesmaż cebulę na oleju", timerSeconds = 180),
                Step("Dodaj przecier pomidorowy"),
                Step("Dodaj czosnek, oregano i sól"),
                Step("Dusić sos przez 5 minut", timerSeconds = 300),
                Step("Odsącz makaron i dodaj do sosu"),
                Step("Wymieszaj i podawaj")
            )
        )
    )

    fun getById(id: Int): Recipe? = getAll().find { it.id == id }
}
