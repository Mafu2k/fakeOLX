# fakeOLX

Mała aplikacja na Androida w stylu OLX. Po założeniu konta można przeglądać ogłoszenia innych
osób, wyszukiwać je i filtrować po kategoriach, zapisywać w ulubionych i wystawiać własne.
Swoje ogłoszenia można potem edytować albo usuwać w zakładce „Moje”.

Backendu nie pisałem od zera. Logowanie i rejestracja działają na Firebase Authentication,
a ogłoszenia i profile użytkowników leżą w Cloud Firestore (kolekcje `ogloszenia` i `users`).

## Ekrany

1. Logowanie (z wysłaniem linku do zmiany hasła)
2. Rejestracja
3. Lista ogłoszeń z wyszukiwarką i filtrem kategorii
4. Szczegóły ogłoszenia (ulubione, kontakt e-mail, edycja i usuwanie dla autora)
5. Dodawanie ogłoszenia
6. Edycja ogłoszenia
7. Moje ogłoszenia
8. Ulubione
9. Profil (statystyki, zmiana nazwy, zmiana hasła, wylogowanie)

## Technologie

Kotlin, Jetpack Compose (Material 3), MVVM (ViewModel + StateFlow), Navigation Compose,
Coil do ładowania zdjęć, Firebase Auth i Cloud Firestore.

## Uruchomienie

Projekt otwiera się w Android Studio i odpala jak każdą aplikację. Z terminala potrzebny jest
JDK 17 albo 21 (Gradle 8.13 nie działa jeszcze z JDK 25):

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

W repozytorium jest `app/google-services.json` mojego projektu Firebase. Jeśli chcesz używać
własnej bazy, podmień ten plik na swój z konsoli Firebase.

## Reguły bezpieczeństwa

Plik `firestore.rules` opisuje, kto może czytać i zmieniać dane: ogłoszenia widzi każdy
zalogowany użytkownik, ale edytować i usuwać może tylko autor. Dokument w `users` jest dostępny
tylko dla właściciela. Reguły trzeba wkleić w konsoli Firebase (Firestore → Reguły) albo wgrać
przez `firebase deploy --only firestore:rules`.

## Struktura

- `data/model` zawiera modele `Ogloszenie`, `User` i enum `Kategoria`,
- `data/repository` to dostęp do Firebase (`AuthRepository`, `OgloszeniaRepository`, `UserRepository`),
- `ui/viewmodel` trzyma stan ekranów, każdy ekran ma swój ViewModel,
- `ui/screens` to ekrany Compose, `ui/components` to wspólne elementy (karta ogłoszenia, formularz, pasek nawigacji),
- `navigation` opisuje trasy między ekranami.

## Licencja

MIT
