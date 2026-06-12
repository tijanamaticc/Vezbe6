# Vezba2KLK

Android aplikacija urađena kao nastavak vežbi sa fakulteta. Projekat pokriva osnovne Android teme:

- lokalnu bazu podataka sa tabelom `korisnici`
- CRUD operacije nad korisnicima
- SharedPreferences za čuvanje prijavljenog korisnika i njegove uloge
- SharedPreferences za podešavanje sinhronizacije
- učitavanje podataka iz `ContentProvider`-a

## Opis projekta

Aplikacija omogućava prijavu korisnika sa izborom uloge:

- `vozač`
- `putnik`
- `administrator`

Na osnovu uloge prikazuju se različiti delovi aplikacije. Administrator ima pristup ekranu za upravljanje korisnicima i može da dodaje, menja, pregleda i briše korisnike iz lokalne SQLite baze.

## Funkcionalnosti

### 1. SQLite baza podataka

U aplikaciji postoji lokalna baza podataka sa tabelom `korisnici`. Tabela čuva sledeća polja:

- `id`
- `ime`
- `email`
- `uloga`

Implementirane CRUD metode:

- **Create** – dodavanje korisnika
- **Read** – prikaz svih korisnika i pojedinačnog korisnika
- **Update** – izmena postojećeg korisnika
- **Delete** – brisanje korisnika

### 2. SharedPreferences za prijavljenog korisnika

U `SharedPreferences` se čuvaju:

- ime prijavljenog korisnika
- njegova uloga u sistemu
- status prijave

Na osnovu sačuvane uloge aplikacija otvara odgovarajući ekran.

### 3. SharedPreferences za sinhronizaciju

Korisnik može da izabere interval sinhronizacije:

- `nikad`
- `na svakih 1 min`
- `na svakih 15 min`
- `na svakih 30 min`

Izabrana vrednost se čuva u `SharedPreferences`, a servis koristi ta podešavanja.

### 4. ContentProvider

Kao primer rada sa `ContentProvider`-om, aplikacija učitava i prikazuje kontakte sa uređaja preko `ContactsContract`-a.

Za ovaj deo je potrebna dozvola:

- `READ_CONTACTS`

## Glavni ekrani

- `MainActivity` – početni ekran za prijavu
- `DashboardActivity` – glavni meni nakon prijave
- `UsersActivity` – CRUD ekran za korisnike
- `SyncSettingsActivity` – izbor intervala sinhronizacije
- `ContactsActivity` – prikaz kontakata iz `ContentProvider`-a

## Glavne klase

- `User` – model korisnika
- `UsersDbHelper` – SQLiteOpenHelper i CRUD logika
- `SessionManager` – SharedPreferences za prijavljenog korisnika
- `SyncPrefs` – SharedPreferences za sinhronizaciju
- `SyncService` – servis koji čita podešavanje sinhronizacije

## Kako se koristi

1. Pokreni aplikaciju.
2. Na početnom ekranu unesi ime i izaberi ulogu.
3. Ako si prijavljen kao `administrator`, dobijaš pristup CRUD ekranu za korisnike.
4. Sa dashboard-a možeš da otvoriš:
   - upravljanje korisnicima
   - podešavanje sinhronizacije
   - učitavanje kontakata iz `ContentProvider`-a
5. Podaci o prijavi i sinhronizaciji ostaju sačuvani u `SharedPreferences`.

## Pokretanje projekta

### U Android Studio-u

1. Otvori projekat.
2. Sačekaj da se završi Gradle sync.
3. Pokreni aplikaciju na emulatoru ili fizičkom uređaju.

### Preko terminala

```powershell
cd C:\Users\tijan\AndroidStudioProjects\Vezba2KLK
.\gradlew.bat assembleDebug
```

## Testiranje

Preporučeno testiranje:

- prijava različitim ulogama (`vozač`, `putnik`, `administrator`)
- dodavanje novog korisnika
- izmena postojećeg korisnika
- brisanje korisnika
- izbor intervala sinhronizacije
- učitavanje kontakata nakon odobravanja dozvole

Takođe mogu da se pokrenu i postojeći testovi:

```powershell
cd C:\Users\tijan\AndroidStudioProjects\Vezba2KLK
.\gradlew.bat test
.\gradlew.bat connectedAndroidTest
```

## Napomena

Za prikaz kontakata potrebna je dozvola `READ_CONTACTS`.

## Napomena za ocenjivanje

Projekat je urađen kao praktična demonstracija gradiva sa vežbi: SQLite baza, CRUD, SharedPreferences i rad sa ContentProvider-om.

