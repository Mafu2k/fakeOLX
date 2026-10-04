# fakeOLX

Mała aplikacja na Androida w stylu OLX. Po założeniu konta można przeglądać ogłoszenia innych
osób, filtrować je po kategoriach i wystawiać własne. Swoje ogłoszenia można potem edytować
albo usuwać w zakładce „Moje ogłoszenia”.

Backendu nie pisałem od zera. Logowanie i rejestracja działają na Firebase Authentication,
a ogłoszenia i profile użytkowników leżą w Cloud Firestore (kolekcje `ogloszenia` i `users`).

## Technologie

Kotlin, Jetpack Compose, MVVM (ViewModel + StateFlow), Navigation Compose, Firebase Auth i Firestore.

## Uruchomienie

Projekt otwiera się w Android Studio i odpala jak każdą aplikację. Z terminala:

```bash
./gradlew assembleDebug
```

W repozytorium jest `app/google-services.json` mojego projektu Firebase. Jeśli chcesz używać
własnej bazy, podmień ten plik na swój z konsoli Firebase.

## Struktura

- `data/model` zawiera modele `Ogloszenie`, `User` i enum `Kategoria`,
- `data/repository` to dostęp do Firebase (`AuthRepository`, `OgloszeniaRepository`),
- `ui/viewmodel` trzyma stan ekranów,
- `ui/screens` to ekrany Compose,
- `navigation/NavGraph.kt` opisuje trasy między ekranami.

## Licencja

MIT
