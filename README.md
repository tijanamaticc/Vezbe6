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

---

## 📘 VEŽBE 6 — DETALJNI PRIRUČNIK ZA SNALAŽENJE (za početnike)

Ovaj deo je pisan baš da možeš da se snađeš kada dobiješ zadatak, a nisi sigurna šta se tačno traži. Ideja je da ne pamtiš sve napamet, nego da znaš:

- **šta je baza**, a šta **SharedPreferences**,
- kada se koristi **ContentProvider**,
- šta znači **adapter** u `ListView`-u,
- i šta tačno treba da kopiraš i promeniš ako dobiješ isti tip zadatka sa drugim podacima.

### Kako da razumeš 4 zadatka iz vežbe

#### 1) SQLite baza + CRUD nad korisnicima
Ovaj zadatak znači:

- napraviš **lokalnu bazu podataka**,
- u njoj napraviš tabelu `korisnici`,
- i onda nad tom tabelom radiš CRUD:
  - **C**reate → dodaj korisnika,
  - **R**ead → prikaži korisnike,
  - **U**pdate → izmeni korisnika,
  - **D**elete → obriši korisnika.

Ovde **ne treba SharedPreferences** za same korisnike, jer to nije za male vrednosti nego za celu tabelu sa više redova.

U projektu se to radi preko:

- `User` — model jednog korisnika,
- `UsersDbHelper` — klasa koja pravi tabelu i ima CRUD metode,
- `UsersActivity` — ekran gde vidiš listu korisnika i radiš dodavanje/izmenu/brisanje.

##### Šta treba da znaš kada kopiraš ovaj deo
Ako dobiješ drugi entitet, npr. `vozači`, `pevači`, `knjige`:

- promeni naziv modela (`User` → `Driver` ili `Singer`),
- promeni ime tabele (`korisnici` → `vozaci` ili slično),
- promeni nazive kolona,
- promeni tekstove na ekranu,
- ali logika CRUD ostaje ista.

##### Kako izgleda CRUD logika ukratko
- **insert** → dodaje novi red u bazu,
- **query** → vraća redove iz baze,
- **update** → menja postojeći red,
- **delete** → briše postojeći red.

---

#### 2) SharedPreferences za ulogovanog korisnika i njegovu ulogu
Ovo znači da ne čuvaš celu bazu, nego samo male stvari kao:

- ime trenutno prijavljenog korisnika,
- njegova uloga,
- da li je prijavljen ili ne.

To se čuva u **SharedPreferences** jer su to male konfiguracione vrednosti, ne tabela.

U projektu se to obično radi preko klase tipa `SessionManager`.

##### Zašto se to koristi
Zato što aplikacija treba da pamti:

- ko je ulogovan,
- da li je vozač / putnik / administrator,
- i koji ekran da prikaže posle prijave.

##### Šta znače uloge
- **vozač** → može da vidi samo određene ekrane,
- **putnik** → može da vidi drugačiji skup ekrana,
- **administrator** → obično ima puni pristup, npr. CRUD korisnika.

##### Šta treba da menjaš kad kopiraš
- naziv ključa u SharedPreferences,
- nazive uloga,
- tekstove na ekranima,
- uslov u kodu koji proverava ko je prijavljen.

##### Glavna ideja
SharedPreferences je kao mala memorija aplikacije za:

- prijavu,
- podešavanja,
- i stvari koje moraju da ostanu sačuvane i posle gašenja aplikacije.

---

#### 3) SharedPreferences za podešavanje sinhronizacije
Ovde se ne čuvaju podaci o korisniku, nego **podešavanje koliko često da se sinhronizuje servis**.

Opcije koje obično traže su:

- **nikad**,
- **na svakih 1 min**,
- **na svakih 15 min**,
- **na svakih 30 min**.

##### Šta to praktično znači
Aplikacija zapamti koji je interval izabran, a onda servis koristi taj interval da zna kada da radi sinhronizaciju.

##### Šta se ovde čuva
Opet samo mala vrednost, npr. string ili broj:

- `never`
- `1`
- `15`
- `30`

ili vreme u milisekundama.

##### Važno
SharedPreferences samo **pamti izbor**. Samo čuvanje nije isto što i sinhronizacija.

Da bi sinhronizacija stvarno radila, potreban je:

- servis,
- `WorkManager`,
- ili neki drugi mehanizam za pozadinski rad.

##### Kako da razmišljaš kada kopiraš ovaj deo
Ako zadatak kaže:

- „zapamti interval“ → to je SharedPreferences,
- „pokreni periodični posao“ → to je servis / work manager / alarm.

---

#### 4) ContentProvider — učitavanje podataka iz druge aplikacije ili sistema
Ovo je deo koji najviše zbunjuje, pa jednostavno:

**ContentProvider** koristiš kada ne čitaš svoju bazu, nego podatke koje Android sistem ili druga aplikacija već izlaže preko posebnog interfejsa.

Najčešći primer je:

- kontakti iz imenika,
- kalendar,
- SMS poruke,
- mediji,
- pozivi.

##### Zašto ne čitaš direktno bazu?
Zato što druge aplikacije ne daju direktan pristup svojoj internoj bazi. Umesto toga, one nude podatke preko ContentProvider-a.

To znači:

- ne znaš i ne treba da znaš kako je njihova interna baza napravljena,
- već koristiš standardni Android način da dođeš do tih podataka.

##### Zašto je to korisno
- standardizovan pristup,
- sigurniji pristup,
- nema potrebe da znaš unutrašnju strukturu tuđe baze,
- možeš da čitaš sistemske podatke kroz isti princip.

##### Primer iz vežbi
Kad učitavaš kontakte, koristiš:

- `ContactsContract`,
- `getContentResolver().query(...)`,
- runtime dozvolu `READ_CONTACTS`.

##### Šta treba da uradiš u kodu
1. proveriš dozvolu,
2. napraviš `query` na odgovarajući `URI`,
3. pročitaš rezultate preko `Cursor`-a,
4. prikažeš ih u listi.

---

### Objašnjenje najzbunjujućih delova iz koda

#### Šta je `Adapter`
Adapter je veza između:

- tvoje liste podataka,
- i ekrana gde ih prikazuješ.

Znači, ako imaš listu korisnika u memoriji, adapter kaže:

> „Za svaki korisnik napravi jedan red na ekranu i popuni ga podacima.”

Bez adaptera, `ListView` ne bi znao kako da prikaže tvoje objekte.

##### U `UsersActivity` je to obično:
```java
ArrayAdapter<User> adapter = ...
```

To znači:

- koristi se `ArrayAdapter`,
- tip podataka je `User`,
- i svaki korisnik se pretvara u jedan red na listi.

---

#### Šta je `ArrayAdapter`
`ArrayAdapter` je gotov Android adapter koji se koristi kada imaš listu običnih objekata ili stringova.

On prima:

- kontekst (`this`),
- layout za jedan red,
- i listu podataka.

Primer:
```java
ArrayAdapter<User> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_2, users) {
    ...
};
```

To znači:

- uzmi listu `users`,
- za svaki element napravi jedan prikaz,
- i stavi ga u `ListView`.

---

#### Šta je `android.R.layout.simple_list_item_2`
To je **ugrađeni Android layout** koji već ima **dva tekstualna reda**.

Koristi se kada želiš da jedna stavka na listi prikazuje dve informacije, npr.:

- prvi red: ime i uloga,
- drugi red: email.

Dakle, to nije tvoj fajl nego **gotov layout iz Android sistema**.

##### Zašto je koristan
Zato što ne moraš sam da praviš XML za dva reda ako ti to već odgovara.

---

#### Šta su `android.R.id.text1` i `android.R.id.text2`
U `simple_list_item_2` Android je već napravio dva `TextView`-a:

- `text1` → prvi red,
- `text2` → drugi red.

Kada pišeš adapter, kažeš:

- u `text1` stavi ime,
- u `text2` stavi email ili ulogu.

To izgleda ovako:
```java
TextView text1 = view.findViewById(android.R.id.text1);
TextView text2 = view.findViewById(android.R.id.text2);
```

---

#### Zašto se koristi `notifyDataSetChanged()`
Kada promeniš listu podataka u kodu, ekran to ne zna sam od sebe.

Zato pozivaš:

```java
adapter.notifyDataSetChanged();
```

To govori:

> „E, lista se promenila — osveži prikaz.”

Bez toga, može se desiti da u bazi ima novi podatak, ali da ga na ekranu još ne vidiš.

---

### Kako da čitaš `UsersActivity` ako ti je zbunjujuća

U toj aktivnosti obično imaš ove korake:

1. učitaš listu korisnika iz baze,
2. napraviš adapter,
3. povežeš adapter sa `ListView`-om,
4. kada korisnik klikne dugme, dodaš / izmeniš / obrišeš korisnika,
5. onda ponovo osvežiš listu.

##### Jednostavno rečeno
`UsersActivity` je ekran, `UsersDbHelper` je baza, a adapter je „prevodilac” između baze i ekrana.

---

### Šta tačno radi ContentProvider i zašto se koristi
ContentProvider se koristi zato što Android ima mnogo sistemskih podataka koji nisu tvoji, ali ih možeš čitati ako imaš dozvolu.

Na primer:

- kontakti,
- kalendar,
- SMS.

##### Zašto je taj način bolji
Zato što:

- ne zavisiš od unutrašnje baze druge aplikacije,
- dobijaš standardni pristup kroz Android API,
- sigurnije je i urednije,
- radi isto za različite izvore podataka.

##### Kako to zamišljati
Ne ulaziš direktno u tuđu sobu i pretražuješ fioke.
Umesto toga, koristiš „zvono na vratima” koje vlasnik aplikacije daje kroz ContentProvider.

---

### Kako da znaš šta da kopiraš ako dobiješ novi zadatak

Ako zadatak kaže **lokalna baza + CRUD**:

- kopiraš model,
- kopiraš `SQLiteOpenHelper`,
- kopiraš activity za prikaz,
- promeniš naziv tabele, kolona i stringove.

Ako zadatak kaže **sačuvaj prijavu / ulogu / settings**:

- kopiraš `SharedPreferences` logiku,
- promeniš ključeve,
- promeniš vrednosti koje čuvaš.

Ako zadatak kaže **uzmi kontakte ili podatke iz druge aplikacije**:

- koristiš ContentProvider,
- dodaš permission,
- proveriš `Cursor`,
- prikažeš rezultate.

---

### Mini-podsetnik: šta je gde

- **SQLite** → za tvoje podatke u tabeli
- **SharedPreferences** → za male sačuvane vrednosti
- **ContentProvider** → za podatke iz sistema ili druge aplikacije
- **Adapter** → da prikaže listu na ekranu

---

### Šta da gledaš u kodu kad nisi sigurna

Kad vidiš:

- `insert`, `update`, `delete`, `query` → to je baza
- `SharedPreferences` → to je pamćenje podešavanja
- `getContentResolver().query(...)` → to je ContentProvider
- `ArrayAdapter` → to je prikaz liste
- `android.R.layout.simple_list_item_2` → gotov layout sa dva reda

---

### Kratak primer kako da razmišljaš tokom kolokvijuma

Ako piše:

> „Sačuvati korisnike i omogućiti CRUD”

ti odmah znaš:

- SQLite baza,
- jedna tabela,
- model klase,
- `SQLiteOpenHelper`,
- activity za listu i dijalog za unos.

Ako piše:

> „Sačuvati ulogovanog korisnika i ulogu”

ti odmah znaš:

- SharedPreferences,
- ključ za ime,
- ključ za ulogu,
- čitanje posle pokretanja aplikacije.

Ako piše:

> „Učitati kontakte iz imenika”

ti odmah znaš:

- ContentProvider,
- `READ_CONTACTS`,
- `Cursor`,
- `ContactsContract`.

---

### Najkraće moguće objašnjenje za usmeno

Ako te profesor pita „šta je ovo?” možeš da kažeš:

- **SQLite** je lokalna baza za trajno čuvanje podataka u aplikaciji.
- **SharedPreferences** služi za male sačuvane vrednosti kao što su login i settings.
- **ContentProvider** služi da aplikacija čita podatke koje izlaže sistem ili druga aplikacija, npr. kontakte.
- **Adapter** povezuje listu podataka sa prikazom na ekranu.

---

### Ako želiš da kopiraš i menjaš kod

Najlakši redosled je:

1. kopiraj model,
2. kopiraj bazu / preferences / provider logiku,
3. promeni imena klasa,
4. promeni nazive tabela i kolona,
5. promeni tekstove na ekranu,
6. proveri dozvole,
7. testiraj da li lista radi i da li se podaci osvežavaju.

---

### Zaključak

Ovaj projekat ti je podeljen na 4 logička dela:

1. **SQLite** → korisnici i CRUD,
2. **SharedPreferences** → prijava i uloga,
3. **SharedPreferences + servis** → interval sinhronizacije,
4. **ContentProvider** → učitavanje kontakata ili drugih sistemskih podataka.

Ako razumeš ovu podelu, mnogo lakše ćeš se snaći i kada dobiješ novi zadatak samo ćeš menjati:

- naziv entiteta,
- naziv tabele,
- naziv kolona,
- URL / URI,
- i tekstove u UI-u.

