# fakeOLX

Aplikacja mobilna na Androida wzorowana na serwisie ogłoszeniowym OLX. Umożliwia
rejestrację, logowanie oraz zarządzanie ogłoszeniami — przeglądanie, dodawanie,
edycję i usuwanie własnych wpisów. Napisana w Kotlinie z użyciem Jetpack Compose
w architekturze MVVM.

## Funkcjonalności

- Rejestracja i logowanie użytkownika
- Lista wszystkich ogłoszeń
- Szczegóły pojedynczego ogłoszenia
- Dodawanie nowego ogłoszenia (z kategorią)
- Edycja i usuwanie własnych ogłoszeń („Moje ogłoszenia")
- Podział ogłoszeń na kategorie

## Stack

- Kotlin
- Jetpack Compose (UI deklaratywne)
- Architektura MVVM (ViewModel + repozytoria)
- Jetpack Navigation (NavGraph)
- Gradle (Kotlin DSL)

## Architektura

```
com.example.fakeolx/
├── MainActivity.kt
├── navigation/        # NavGraph — trasy między ekranami
├── data/
│   ├── model/         # Ogloszenie, Kategoria, User
│   └── repository/    # AuthRepository, OgloszeniaRepository
└── ui/
    ├── screens/       # Login, Register, ListaOgloszen, Szczegoly,
    │                  #   DodajOgloszenie, EdytujOgloszenie, MojeOgloszenia
    ├── components/     # wspólne komponenty Compose
    ├── viewmodel/      # AuthViewModel, OgloszeniaViewModel
    └── theme/          # kolory, typografia, kształty
```

## Uruchomienie

Wymagania: Android Studio (aktualna wersja) oraz SDK Androida.

Otwórz projekt w Android Studio i uruchom na emulatorze lub urządzeniu przyciskiem
**Run**. Alternatywnie z konsoli:

```bash
./gradlew assembleDebug
```

## Autor

Łukasz Janicki

## Licencja

MIT — szczegóły w pliku [LICENSE](LICENSE).
