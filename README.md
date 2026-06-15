# 📱 Vezba2KLK - Mobilna Aplikacija za Upravljanje Korisnicima

**Android aplikacija** razvijena kao praktična demonstracija glavnih Android tehnologija: **SQLite baza podataka**, **CRUD operacije**, **SharedPreferences**, **ContentProvider**, i **Background sinhronizacija**.

## ✨ Funkcionalnosti

✅ **Lokalna SQLite baza podataka** sa tabelom `korisnici`  
✅ **CRUD operacije** - dodavanje, čitanje, izmena i brisanje korisnika  
✅ **SharedPreferences** - čuvanje prijavljenog korisnika i njegove uloge  
✅ **Dinamički ekrani** - različiti sadržaj zavisno od uloge korisnika  
✅ **Podešavanje sinhronizacije** - izbor intervala (nikad, 1 min, 15 min, 30 min)  
✅ **ContentProvider** - čitanje kontakata sa uređaja  

## 🔑 Glavne karakteristike

### Uloge korisnika
- **vozač** - može pregled
- **putnik** - može pregled
- **administrator** - puni CRUD pristup

### Lokalni storage
- SQLite baza za trajne podatke
- SharedPreferences za setings i prijavu

## Opis projekta

Aplikacija omogućava prijavu korisnika sa izborom uloge:

- `vozač`
- `putnik`
- `administrator`

Na osnovu uloge prikazuju se različiti delovi aplikacije. **Administrator** ima pristup ekranu za upravljanje korisnicima i može da dodaje, menja, pregleda i briše korisnike iz lokalne SQLite baze.

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

## 📚 Dodatni Resursi

U projektu su dostupni detaljni vodiči:

- **`ANALIZA_DETALJNO.md`** - Detaljno objašnjenje svakog dela koda (kako se koristi, gde je šta)
- **`KOLOKVIJUM_VODIC.md`** - Kako se radi na kolokvijumu i što se trebaju znati 
- **`PRIMER_SINGERS_KOMPLETAN.md`** - Kompletan primer sa Retrofit API integracijom (lako se može prilagoditi)

## ⚠️ Dozvole

Za prikaz kontakata potrebna je dozvola `READ_CONTACTS`. Aplikacija će je tražiti u runtime-u.

---

## 📎 Cheat-sheet: copy & change (za kolokvijum)

Ispod su jasne upute koje fajlove KOPIRAŠ i tačno šta MENJAŠ za tri najčešća tipa zadataka: A) lokalna SQLite tabela, B) REST API (Retrofit) + sinkronizacija, C) ContentProvider (sistemski podaci). Takođe su tu i SharedPreferences koraci.

NAPOMENA: uradi FIND/REPLACE u fajlovima koje kopiraš (ide brzo na kolokvijumu).

A) LOKALNA BAZA (SQLite) — KOPIRAJ I MENJAJ
- Fajlovi za kopiranje (iz primera):
  - `Singer.java` (model)
  - `SingerDbHelper.java` (SQLiteOpenHelper + CRUD)
  - `SingersActivity.java` (UI + pozivi DB)
  - `res/layout/activity_singers.xml`, `res/layout/dialog_singer_form.xml`
- Šta tačno menjaš (find/replace):
  - `Singer` → `NewEntityName` (klasa i filename)
  - `SingerDbHelper` → `NewEntityDbHelper`
  - `TABLE_SINGERS` ("pevaci") → npr. `TABLE_DRIVERS` ("vozaci")
  - `COL_NAME`, `COL_GENRE`, `COL_COUNTRY` → zameni kolone prema novom modelu
  - `getAllSingers()` → `getAllNewEntities()`, `insertSinger()` → `insertNewEntity()` itd.
- SharedPreferences: ako ima key specifičan za entitet promeni npr. `KEY_CURRENT_SINGER` → `KEY_CURRENT_DRIVER`.
- Provera pre predaje: build, test Dodaj/Izmeni/Obriši, da li se `Cursor` zatvara.

B) REST API (Retrofit) + sinkronizacija — KOPIRAJ I MENJAJ
- Fajlovi za kopiranje:
  - `SingerApiService.java` (Retrofit interface)
  - Retrofit inicijalizacija u `SingersActivity` ili novu klasu `ApiClient`
  - (opcionalno) `SingersActivity.java` za `syncFromServer()` logiku i mapiranje u lokalnu DB
- Šta tačno menjaš:
  - `baseUrl("https://api.example.com/")` → zameni sa datim base URL-om
  - endpointi: `@GET("singers")` → `@GET("drivers")` ili drugi
  - model: `Singer` → `Driver` (ako se menja entitet)
  - funkcije: `getAllSingers()` → `getAllDrivers()`
- Kako graditi URL-e i kada koristimo @Path/@Query:
  - @Path za deo putanje: `/entities/{id}`
  - @Query za parametre posle `?` (npr. `?page=1`)
- Build.gradle: dodaj Retrofit i Gson ako ih nema
  - implementation 'com.squareup.retrofit2:retrofit:2.9.0'
  - implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
- Permissions: `<uses-permission android:name="android.permission.INTERNET" />`

C) CONTENT PROVIDER (sistemski podaci) — KOPIRAJ I MENJAJ
- Fajlovi za kopiranje:
  - `ContactsActivity.java` (primer sa ContactsContract)
  - layout za prikaz (lista, infoText, buttons)
- Šta tačno menjaš za drugi provider (npr. Calendar, SMS):
  - URI: `ContactsContract.CommonDataKinds.Phone.CONTENT_URI` → `CalendarContract.Events.CONTENT_URI` ili `Uri.parse("content://sms")`
  - Kolone: DISPLAY_NAME/NUMBER → TITLE/DTSTART/BODY/ADDRESS zavisno od providera
  - Runtime permission: `READ_CONTACTS` → `READ_CALENDAR` / `READ_SMS` gde je potrebno
  - Query parametri/selection: dodaj WHERE ako tražiš specifične redove
- Kako pronaći tačan URI i kolone:
  - Android docs: `ContactsContract`, `CalendarContract`, `Telephony`
  - U Android Studio: Ctrl+klik na konstante
  - Koristi `adb shell content query --uri <uri>` za brzo ispitivanje
- Testiranje na emulatoru: dodaj kontakte ručno ili pomoću `adb` komandi
  - `adb shell am start -a android.intent.action.INSERT -t vnd.android.cursor.item/contact --es name "Test User"`

D) SHARED PREFERENCES (kako i šta kopirati)
- Fajlovi/klase:
  - `SessionManager.java` — čuva prijavu (KEY_LOGGED_IN, KEY_NAME, KEY_ROLE)
  - `SyncPrefs.java` — čuva interval sinhronizacije (SYNC_NEVER, SYNC_1_MIN, ...)
- Šta menjaš:
  - Ako promeniš ključeve, uradi FIND/REPLACE kroz ceo projekat
  - Kada korisnik promeni interval, pozovi funkciju koja (re)planira sync (WorkManager/AlarmManager)

E) KRATKA CHECKLISTA (pre predaje)
- Dodaj potrebne dozvole u `AndroidManifest.xml` i traži runtime permissions gde treba
- Zatvori sve `Cursor` u finally bloku
- Network pozive stavi u background (Retrofit `enqueue()`)
- Proveri da li si promenio sve reference (class name, filenames)
- Pokreni build: `.\gradlew.bat assembleDebug`

Ako želiš, odmah ću: A) ubaciti ovaj cheat-sheet i u gornji deo README-a kao sažetak (uradiću to sada), i/ili B) dodati WorkManager primer. Reci da li da ubacim u README i potvrdim izmene.
