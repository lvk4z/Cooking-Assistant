# Asystent Gotowania

Mobilna aplikacja na Androida, która prowadzi przez przepis krok po kroku
i pozwala ją obsługiwać bez dotykania telefonu

## Problem

Podczas gotowania ciągłe dotykanie ekranu jest niewygodne i niehigieniczne.
Aplikacja czyta instrukcje na głos, reaguje na komendy głosowe i pilnuje
czasu.

## Funkcje

### MUST
- [ ] **Bezdotykowa nawigacja** – komendy głosowe: „dalej", „wstecz", „powtórz"
- [x] **Synteza mowy (TTS)** – aplikacja czyta na głos kolejne kroki
- [ ] **Minutniki w tle** – automatyczne uruchamianie timera dla kroku,
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

| Tydzień | Termin | Planowane elementy |
|---|---|---|
| 1 | 26.10–01.11.2026 | Szkielet aplikacji, model danych i lista przepisów |
| 2 | 02.11–08.11.2026 | Ekran gotowania, nawigacja między krokami i TTS |
| 3 | 09.11–15.11.2026 | Transkrypcja mowy, zgoda na mikrofon i podstawowe minutniki kroków |
| 4 | 16.11–22.11.2026 | Komendy głosowe: dalej, wstecz i powtórz |
| 5 | 23.11–29.11.2026 | Koordynacja nasłuchu z TTS i obsługa błędów rozpoznawania |
| 6 | 30.11–06.12.2026 | Minutniki działające w tle i powiadomienia dźwiękowe |
| 7 | 07.12–13.12.2026 | Testowanie minutników oraz przywracanie postępu gotowania |
| 8 | 14.12–20.12.2026 | Baza Room i zapisywanie przepisów |
| 9 | 21.12–27.12.2026 | Dodawanie i edycja własnych przepisów |
| 10 | 28.12.2026–03.01.2027 | Opcjonalnie: obsługa gestów czujnikiem zbliżeniowym |
| 11 | 04.01–10.01.2027 | Testy na telefonie, poprawki i przygotowanie aplikacji do oddania |


## Autor

Łukasz Zegar 
