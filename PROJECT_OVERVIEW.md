# PROJECT OVERVIEW — Vezbe 6 (kompletna mapa: 4 zadatka + adapteri + šta je gde)

Ovo je potpuni, jasan i praktičan vodič koji povezuje ceo projekat sa zadacima koje imaš na vežbama. Cilj: da odmah znaš

- koji fajlovi implementiraju svaki od 4 zadatka,
- koje klase i metode da pregledaš i menjaš ako prekopiraš kod za kolokvijum,
- gde su SharedPreferences ključevi i kako da ih menjaš,
- gde je sync/podešavanje sinhronizacije,
- tačno gde su primeri za ContentProvider (kontakti, kalendar, SMS),
- objašnjenje adaptera (ugrađeni i custom), i kako da zameniš `simple_list_item_2` sa sopstvenim layout-om.

Koristi ovaj fajl kao glavnu semu projekta.

---

## KRATKI SADRŽAJ

1. Pregled zadataka i fajlova (brzo)
2. Detaljan opis po zadatku (štapić za kopiranje i promenu)
3. Adapteri — objašnjenje + primeri (ArrayAdapter, custom adapter, simple_list_item_2)
4. SharedPreferences — gde su ključevi i kako ih menjaš
5. Sinhronizacija (SyncPrefs / servis / WorkManager preporuka)
6. ContentProvider — gde su fajlovi, kako rade, primeri
7. Lista novih/dodatih fajlova u projektu i šta sadrže
8. Šta dalje — automatsko ubacivanje fajlova ili prilagođavanje

---

## 1) Pregled zadataka (BRZO)

Zadatak 1 — SQLite (korisnici):
- Glavne klase: `User` (model), `UsersDbHelper` (SQLiteOpenHelper), `UsersActivity` (UI CRUD)
- Layout: `activity_users.xml`, `dialog_user_form.xml` (ili `dialog_user_form.xml` već postoji)

Zadatak 2 — SharedPreferences (prijava i uloga):
- Klase: `SessionManager` (čuva `KEY_NAME`, `KEY_ROLE`, `KEY_LOGGED_IN`), `MainActivity` za login, `DashboardActivity` za prikaz

Zadatak 3 — Sync prefs + servis:
- Klase: `SyncPrefs` (čuva `sync_interval`), `SyncSettingsActivity` (UI za izbor intervala), `SyncService` (ili preporučeno: `WorkManager`-based worker)

Zadatak 4 — ContentProvider (kontakti etc.):
- Klase: `ContactsActivity`, `CalendarActivity`, `SmsActivity`, `PermissionHelper`
- Layouts: `activity_contacts.xml`, `item_contact.xml`, `activity_calendar.xml`, `item_event.xml`, `activity_sms.xml`, `item_sms.xml`

---

## 2) Detalji po zadatku (šta menjaš kada kopiraš)

A) LOKALNA BAZA — KORISNICI (SQLite + CRUD)

Fajlovi koje treba da pregledaš / kopiraš:
- `app/src/main/java/.../User.java` — model (polja: id, name, email, role)
- `app/src/main/java/.../UsersDbHelper.java` — sadrži:
  - konstante: DATABASE_NAME, DATABASE_VERSION, TABLE_USERS, COL_ID, COL_NAME, COL_EMAIL, COL_ROLE
  - `onCreate()` — CREATE TABLE SQL
  - CRUD metode: `insertUser(User u)`, `getAllUsers()`, `getUserById(long id)`, `updateUser(User u)`, `deleteUser(long id)`
- `app/src/main/java/.../UsersActivity.java` — UI:
  - učitava listu: `dbHelper.getAllUsers()`
  - adapter: `ArrayAdapter<User>` ili custom
  - metode za dodavanje/izmenu/brisanje (dialog ili nova aktivnost)
- layout: `res/layout/activity_users.xml`, `res/layout/dialog_user_form.xml` (već si imala dialog_user_form.xml pridodan)

Promene kada radiš `find/replace` za drugi entitet:
- klasu `User` promeni u `Driver` ili bilo šta drugo
- u `UsersDbHelper` promeni ime tabele vrednosti i konstanti
- promeni sve nazive metoda (ide u paru: insertUser -> insertDriver)
- u Activity promeni setContentView na novi layout ime

Provera i tipične greške:
- Cursor zatvaranje u `getAllUsers()` i `getUserById()` (try/finally)
- proveri da `db.insert()` vraća >-1 i `update()`/`delete()` vraćaju broj promena

Primer kako proveriti insert:
```java
long id = db.insert(TABLE_USERS, null, values);
if (id == -1) { Log.e(TAG, "Insert failed"); }
```


B) SHARED PREFERENCES — PRIJAVA I ULOGA

Fajlovi:
- `SessionManager.java` — enkapsulira SharedPreferences
  - PREF_NAME e.g. "session_prefs"
  - KEY_LOGGED_IN = "logged_in" (boolean)
  - KEY_NAME = "name" (string)
  - KEY_ROLE = "role" (string)
- `MainActivity.java` — login UI (polja: inputName, spinnerRole, buttonLogin)
  - kada klikne "Prijavi se" -> `SessionManager.saveLogin(name, role)` i start `DashboardActivity`
- `DashboardActivity.java` čita `SessionManager.getRole()` i prikazuje ili sakriva opcije

Šta menjati pri copy:
- promeni nazive ključeva ako želiš, ali zamenom u celoj bazi koda (FIND/REPLACE)
- proveri da SessionManager koristi `apply()` ili `commit()` za edit

Primer čuvanja:
```java
prefs.edit().putString(KEY_NAME, name).putString(KEY_ROLE, role).putBoolean(KEY_LOGGED_IN, true).apply();
```


C) SINHRONIZACIJA — SyncPrefs + servis / WorkManager

Fajlovi:
- `SyncPrefs.java` — ključevi i vrednosti (e.g. "sync_interval")
- `SyncSettingsActivity.java` — UI za izbor intervala (ListPreference ili RadioButtons)
- `SyncService.java` ili `SyncWorker.java` (WorkManager)

Kako radi:
- korisnik menja interval u SyncSettingsActivity → vrednost se sprema u SyncPrefs
- pri promeni, pozivaš `applySyncSetting(context)` koja otkazuje stari posao i zakazuje novi:
  - preporučujem WorkManager (Periodički posao), minimalno 15 minuta je pouzdano
  - ako profesor traži 1 min, objasniš da je to eksperimentalno i da Doze može odložiti

Primer zakazivanja PeriodicWorkRequest:
```java
PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(SyncWorker.class, 15, TimeUnit.MINUTES).build();
WorkManager.getInstance(context).enqueueUniquePeriodicWork("sync_work", ExistingPeriodicWorkPolicy.REPLACE, request);
```


D) CONTENT PROVIDER — Kontakti, Kalendar, SMS

Fajlovi (koje smo dodali u CONTENT_PROVIDER_GUIDE.md / Sema.md):
- `PermissionHelper.java` — helper za runtime permisije
- `ContactsActivity.java` + `activity_contacts.xml` + `item_contact.xml`
- `CalendarActivity.java` + `activity_calendar.xml` + `item_event.xml`
- `SmsActivity.java` + `activity_sms.xml` + `item_sms.xml`

Ključni delovi koda:
- `getContentResolver().query(URI, projection, selection, selectionArgs, sortOrder)`
- `Cursor` handling (null check, moveToFirst, getColumnIndex, read, finally cursor.close())
- runtime permission flow: check -> request -> onRequestPermissionsResult -> call load method

URI i kolone:
- Kontakti: ContactsContract.CommonDataKinds.Phone.CONTENT_URI
  - projection: DISPLAY_NAME, NUMBER
- Kalendar: CalendarContract.Events.CONTENT_URI
  - projection: _ID, TITLE, DTSTART
- SMS: Telephony.Sms.CONTENT_URI (ili content://sms)
  - projection: _ID, ADDRESS, BODY, DATE

Test procedure:
- Dodaj test podatke na emulator (adb example komande u CONTENT_PROVIDER_GUIDE.md)
- Pokreni Activity, daj dozvolu, proveri da li lista pokazuje elemente

---

## 3) ADAPTERI — objašnjenje i primeri

Šta je adapter?
- Adapter je "prevodilac" između tvoje kolekcije objekata (npr. List<User>) i ListView/RecyclerView UI komponenti.

Tipovi adaptera u projektu:
- `ArrayAdapter<T>` — koristi se kada imaš jednostavnu listu objekata i želiš brz način da ih prikažeš.
- Custom `ArrayAdapter` (override getView) — kada želiš kontrolu nad kako se prikazuje jedan red.
- `SimpleCursorAdapter` — ako direktno prikazuješ Cursor rezultate (ređe u ovoj vežbi)

Korišćenje `android.R.layout.simple_list_item_2`:
- Ovo je ugrađeni layout koji sadrži dva TextView-a sa id-evima `android.R.id.text1` i `android.R.id.text2`.
- Koristi se kada želiš da jedna stavka prikaže dve linije (npr. ime + email).

Primer custom ArrayAdapter-a (skraćeno):
```java
ArrayAdapter<User> adapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_2, users) {
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }
        TextView t1 = v.findViewById(android.R.id.text1);
        TextView t2 = v.findViewById(android.R.id.text2);
        User u = getItem(position);
        t1.setText(u.getName());
        t2.setText(u.getEmail());
        return v;
    }
};
listView.setAdapter(adapter);
```

Ako želiš sopstveni izgled reda (preporučeno), kreiraj `res/layout/item_user.xml` i ubaci svoj adapter koji radi `inflate(R.layout.item_user, ...)`.

---

## 4) SHARED PREFERENCES — gde su ključevi

Tipične klase i konstante (pretraga u projektu):
- `SessionManager` ili `Session`
  - PREF_NAME = "session_prefs"
  - KEY_LOGGED_IN = "logged_in"
  - KEY_NAME = "name"
  - KEY_ROLE = "role"

- `SyncPrefs` ili `Settings`
  - PREF_NAME = "sync_prefs"
  - KEY_SYNC_INTERVAL = "sync_interval" (vrednosti: "never", "1", "15", "30")

Kako menjati ključeve:
- uradi Find/Replace preko projekta (npr. KEY_ROLE -> KEY_USER_ROLE) i sačuvaj konzistentnost

---

## 5) SINHRONIZACIJA — gde da pogledaš kod

- Traži fajlove `SyncPrefs`, `SyncService`, `SyncSettingsActivity`.
- Ako koristi WorkManager, traži `SyncWorker`.
- Ključna funkcija: `applySyncSetting(Context)` koja čita SharedPreferences i zakazuje/otkazuje periodični posao.

Napomena:
- WorkManager periodic minimalno 15 min pouzdano.
- Ako profesor traži 1-min opciju, objasni da je to eksperimentalno (AlarmManager/foreground service) i da sistemske optimizacije mogu da odlože izvršavanje.

---

## 6) CONTENT PROVIDER — gde su fajlovi i šta rade

Listu fajlova koje smo dodali (ako si potvrdila ubacivanje):
- `CONTENT_PROVIDER_GUIDE.md` — primeri i adb komande
- `Sema.md` — mapa fajlova i shema
- `ContactsActivity.java` — Activity za prikaz kontakata
- `CalendarActivity.java` — Activity za prikaz događaja
- `SmsActivity.java` — Activity za prikaz SMS-a
- `PermissionHelper.java` — runtime permisije helper
- layout fajlovi (`activity_contacts.xml`, itd.)

Pogledaj CONTENT_PROVIDER_GUIDE.md za kompletne kodove.

---

## 7) Lista fajlova koje sam kreirala / dopunila u projektu

- `README.md` — dopunjen sa vodičem Vežbe 6
- `PRIMER_SINGERS_KOMPLETAN.md` — primer CRUD + Retrofit (već u projektu)
- `CONTENT_PROVIDER_GUIDE.md` — kompletan vodič i kodovi za kontakte, kalendar, SMS
- `Sema.md` — mapa fajlova / sema podataka

(Ostali Java/XML fajlovi za ContentProvider aktivnost su spremni da se ubace ako kažeš DA)

---

## 8) Šta dalje (možemo učiniti odmah)

Opcije:
- `DA — ubaci fajlove` → ubacujem kompletne Java i XML fajlove za Contacts/Calendar/SMS + PermissionHelper + manifest izmene, pa pokrećem proveru grešaka
- `DA — ubaci samo kontakti` → ubacujem samo ContactsActivity i layout-e
- `Samo README` → želiš dodatne objašnjenja u `README.md` ili kratki cheat u gornjem delu

---

Ako želiš, odmah ubacujem kod u projekat. Napiši šta želiš (npr. `DA — ubaci fajlove`).

Kraj fajla.
