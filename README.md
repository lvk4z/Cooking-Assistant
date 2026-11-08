# Asystent Gotowania

Mobilna aplikacja na Androida, która prowadzi przez przepis krok po kroku
i pozwala ją obsługiwać bez dotykania telefonu

## Problem

Podczas gotowania ciągłe dotykanie ekranu jest niewygodne i niehigieniczne.
Aplikacja czyta instrukcje na głos, reaguje na komendy głosowe i pilnuje
czasu.

## Funkcje

### MUST
- [x] **Bezdotykowa nawigacja** – komendy głosowe: „dalej", „wstecz", „powtórz"
- [x] **Synteza mowy (TTS)** – aplikacja czyta na głos kolejne kroki
- [x] **Minutniki w tle** – automatyczne uruchamianie timera dla kroku,
      śledzenie wielu timerów naraz, powiadomienia dźwiękowe

### COULD
- [ ] **Gesty** – przesunięcie dłoni nad telefonem (czujnik zbliżeniowy)
      lub rozpoznawanie gestów kamerą (MediaPipe)


## Architektura

```
app/
├── data/        # model Recipe, Step, repozytorium, Room
├── voice/       # TtsManager, VoiceCommandListener
├── timer/       # TimerService, TimerManager, powiadomienia
├── gesture/     # ProximityGestureDetector (COULD)
└── ui/          # ekrany Compose: lista przepisów, tryb gotowania
```


## Plan rozwoju

- [x] Szkielet projektu, model danych, lista przepisów
- [ ] Ekran gotowania + TTS
- [ ] Komendy głosowe
- [ ] Timery w tle + powiadomienia
- [ ] Gesty (proximity, potem kamera)
- [ ] Dodawanie własnych przepisów

## Uruchomienie i nawigacja

Otwórz projekt w Android Studio Rabbit, skonfiguruj SDK 37 i uruchom moduł
`app` na Androidzie 8.0 (API 26) lub nowszym.

Lista przepisów korzysta z danych `RecipeRepository`. Kliknięcie przepisu
dodaje `CookingRoute(recipeId)` do stosu Navigation 3 i na razie pokazuje
tylko tekst z tytułem przepisu. Systemowy przycisk lub gest Wstecz wraca
do listy. `RecipeListScreen` otrzymuje dane i callback `onRecipeClick(id)`,
bez zależności od nawigacji.

Stos jest utrzymywany przez `remember { mutableStateListOf<Any>(...) }`
w `MainActivity`, zgodnie z wymaganym wzorcem. Na tym etapie nie jest
odtwarzany po ponownym utworzeniu aktywności ani po zakończeniu procesu.

## Autor

Łukasz Zegar 
