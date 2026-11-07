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

- [ ] Szkielet projektu, model danych, lista przepisów
- [ ] Ekran gotowania + TTS
- [ ] Komendy głosowe
- [ ] Timery w tle + powiadomienia
- [ ] Gesty (proximity, potem kamera)
- [ ] Dodawanie własnych przepisów

## Autor

Łukasz Zegar 
