# 📋 DETALJNА ANALIZA PROJEKTA - Vezba2KLK

## Sadržaj
1. [Šta je urađeno i kako](#šta-je-urađeno-i-kako)
2. [SQLite baza i CRUD operacije](#sqlite-baza-i-crud-operacije)
3. [SharedPreferences](#sharedpreferences)
4. [ContentProvider](#contentprovider)
5. [Sinhronizacija (Sync Service)](#sinhronizacija-sync-service)
6. [Tok izvršavanja aplikacije](#tok-izvršavanja-aplikacije)
7. [Šablonski primeri za REST API i Retrofit](#šablonski-primeri-za-rest-api-i-retrofit)
8. [Kako se pronalaze URL-i za ContentProvider](#kako-se-pronalaze-url-i-za-contentprovider)

---

## Šta je urađeno i kako

### Pregledna tabela

| Zahtev | Šta je urađeno | Gde se nalazi |
|--------|-------------------|----------------------|
| **Baza podataka korisnika** | SQLite tabela `korisnici` sa 4 kolone | `UsersDbHelper.java` |
| **CRUD operacije** | insert, select, update, delete metode | `UsersDbHelper.java` |
| **SharedPreferences - prijava** | Čuvanje ime, ulogu, status | `SessionManager.java` |
| **Dinamički ekrani po ulozi** | Admin → CRUD, ostali → dashboard | `MainActivity.java`, `DashboardActivity.java` |
| **SharedPreferences - sinhronizacija** | Čuvanje intervala (nikad, 1, 15, 30 min) | `SyncPrefs.java` |
| **ContentProvider - kontakti** | Učitavanje kontakata iz `ContactsContract` | `ContactsActivity.java` |

---

## SQLite baza i CRUD operacije

### Šta je SQLite?
- **Jednostavna baza podataka koja se čuva na uređaju** (ne u cloudu)
- Svaka aplikacija ima svoju bazu
- Idealna za lokalne podatke

### Gde je SQLite kod u projektu?

**Datoteka:** `UsersDbHelper.java`

```
Klasa UsersDbHelper extends SQLiteOpenHelper
├── DATABASE_NAME = "vezba2klk_users.db"  ← ime baze na disku
├── TABLE_USERS = "korisnici"             ← ime tabele
└── Kolone:
    ├── _id          (INT, PRIMARY KEY, AUTO INCREMENT)
    ├── ime          (TEXT, NOT NULL)
    ├── email        (TEXT, NOT NULL, UNIQUE)
    └── uloga        (TEXT, NOT NULL)
```

### 1️⃣ CREATE - Dodavanje korisnika

**Kod (linije 57-64):**
```java
public long insertUser(User user) {
    SQLiteDatabase db = getWritableDatabase();           // Otvori bazu za pisanje
    ContentValues values = new ContentValues();          // Kreiraj mapu sa podacima
    values.put(COL_NAME, user.getName());               // Stavi ime
    values.put(COL_EMAIL, user.getEmail());             // Stavi email
    values.put(COL_ROLE, user.getRole());               // Stavi ulogu
    return db.insert(TABLE_USERS, null, values);        // Umetni u bazu
}
```

**Gde se koristi:**
- `UsersActivity.java` (linije 118-120) - kada korisnik klikne "Dodaj korisnika"

**Primer poziva:**
```java
User newUser = new User("Milica Vozac", "milica@example.com", "vozač");
long result = dbHelper.insertUser(newUser);  // vraća ID korisnika
if (result > 0) {
    Toast.makeText(this, "Korisnik dodat", Toast.LENGTH_SHORT).show();
}
```

---

### 2️⃣ READ - Čitanje korisnika

**Kod (linije 80-105):**

**A) Čitaj jednog korisnika:**
```java
public User getUserById(long userId) {
    SQLiteDatabase db = getReadableDatabase();          // Otvori bazu za čitanje
    Cursor cursor = db.query(                           // Pravi query (SQL SELECT)
        TABLE_USERS,                                    // iz tabele korisnici
        null,                                           // sve kolone (null = *)
        COL_ID + "=?",                                  // WHERE _id = ?
        new String[]{String.valueOf(userId)},          // vrednost za ?
        null,null,null                                  // bez GROUP BY, HAVING, ORDER BY
    );
    try {
        if (cursor.moveToFirst()) {                     // Ako postoji red
            return cursorToUser(cursor);                // Pretvori u User objekat
        }
        return null;
    } finally {
        cursor.close();                                 // Zatvori cursor
    }
}
```

**B) Čitaj sve korisnike:**
```java
public List<User> getAllUsers() {
    List<User> users = new ArrayList<>();
    SQLiteDatabase db = getReadableDatabase();
    Cursor cursor = db.query(
        TABLE_USERS,
        null,
        null,
        null,
        null,
        null,
        COL_NAME + " ASC"  // Sortiraj po imenu uzlazno
    );
    try {
        while (cursor.moveToNext()) {                   // Za svaki red
            users.add(cursorToUser(cursor));            // Dodaj u listu
        }
    } finally {
        cursor.close();
    }
    return users;
}
```

**Gde se koristi:**
- `UsersActivity.java` (linije 75-79) - učita sve korisnike iz baze

---

### 3️⃣ UPDATE - Izmena korisnika

**Kod (linije 66-73):**
```java
public int updateUser(User user) {
    SQLiteDatabase db = getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COL_NAME, user.getName());           // Nova vrednost imena
    values.put(COL_EMAIL, user.getEmail());         // Novi email
    values.put(COL_ROLE, user.getRole());           // Nova uloga
    return db.update(
        TABLE_USERS,
        values,
        COL_ID + "=?",                              // WHERE _id = ?
        new String[]{String.valueOf(user.getId())} // Pronađi korisnika po ID
    );
}
```

**Gde se koristi:**
- `UsersActivity.java` (linije 121-127) - kada korisnik klikne na postojećeg korisnika i izmeni podatke

**Primer:**
```java
User editedUser = users.get(0);
editedUser.setName("Novo Ime");
editedUser.setEmail("novi@example.com");
int rowsChanged = dbHelper.updateUser(editedUser);  // vraća broj promenjenoh redova
```

---

### 4️⃣ DELETE - Brisanje korisnika

**Kod (linije 75-78):**
```java
public int deleteUser(long userId) {
    SQLiteDatabase db = getWritableDatabase();
    return db.delete(
        TABLE_USERS,
        COL_ID + "=?",                          // WHERE _id = ?
        new String[]{String.valueOf(userId)}   // ID za brisanje
    );
}
```

**Gde se koristi:**
- `UsersActivity.java` (linije 135-145) - dugme "Obriši" (long click na item)

---

### Napomena: Seed Data (Početne podatke)

**Kod (linije 38-40):**
```java
insertSeedUser(db, "Ana Admin", "ana.admin@example.com", ROLE_ADMIN);
insertSeedUser(db, "Marko Vozac", "marko.vozac@example.com", ROLE_DRIVER);
insertSeedUser(db, "Jelena Putnik", "jelena.putnik@example.com", ROLE_PASSENGER);
```

Kada se aplikacija prvi put pokrene, baza se automatski popuni sa 3 korisnika. To je praktično za testiranje.

---

## SharedPreferences

### Šta je SharedPreferences?
- **Jednostavni „key-value" storage** (kao mala baza, ali mnogo jednostavnija)
- Čuva stringove, brojeve, boolean vrednosti
- **NIJE za kompleksne podatke** (samo za setings/konfiguraciju)

### Dva odvojena SharedPreferences u projektu

---

### 1️⃣ SessionManager - Čuvanje prijavljenog korisnika

**Datoteka:** `SessionManager.java`

**Šta se čuva:**
```
Ključ                 │ Vrednost
───────────────────────┼────────────────────
logged_in             │ true/false
logged_name           │ "Milica"
logged_role           │ "vozač" / "putnik" / "administrator"
```

**Kod:**

**Login (čuvanje):**
```java
public void login(String name, String role) {
    prefs.edit()
        .putBoolean(KEY_LOGGED_IN, true)
        .putString(KEY_NAME, name)
        .putString(KEY_ROLE, role)
        .apply();  // Sačuvaj
}
```

**Čitanje:**
```java
public boolean isLoggedIn() {
    return prefs.getBoolean(KEY_LOGGED_IN, false);  // Ako nema, vrati false
}

public String getUserName() {
    return prefs.getString(KEY_NAME, "");  // Ako nema, vrati ""
}

public String getUserRole() {
    return prefs.getString(KEY_ROLE, UsersDbHelper.ROLE_PASSENGER);
}
```

**Logout (brisanje):**
```java
public void logout() {
    prefs.edit().clear().apply();  // Obriši sve
}
```

**Gde se koristi:**

1. **Prijava** - `MainActivity.java` (linije 44):
   ```java
   sessionManager.login(name, role);
   ```

2. **Provera da li je već prijavljen** - `MainActivity.java` (linije 21-24):
   ```java
   if (sessionManager.isLoggedIn()) {
       openDashboard();  // Preskoči login ekran
       return;
   }
   ```

3. **Provera uloge za CRUD pristup** - `UsersActivity.java` (linije 30-34):
   ```java
   if (!sessionManager.isLoggedIn() || 
       !UsersDbHelper.ROLE_ADMIN.equals(sessionManager.getUserRole())) {
       finish();  // Zatvori, ne dozvoli pristup
   }
   ```

---

### 2️⃣ SyncPrefs - Čuvanje podešavanja sinhronizacije

**Datoteka:** `SyncPrefs.java`

**Šta se čuva:**
```
Ključ          │ Vrednost (milisekunde)
───────────────┼────────────────────
sync_interval  │ -1L (nikad)
               │ 60000L (1 minut)
               │ 900000L (15 minuta)
               │ 1800000L (30 minuta)
```

**Kod:**

**Čuvanje:**
```java
public void setInterval(long intervalMillis) {
    prefs.edit().putLong(KEY_INTERVAL, intervalMillis).apply();
}
```

**Čitanje:**
```java
public long getInterval() {
    return prefs.getLong(KEY_INTERVAL, SYNC_NEVER);  // Ako nema, nikad
}
```

**Čitljiva oznaka:**
```java
public String getReadableLabel() {
    long interval = getInterval();
    if (interval == SYNC_1_MIN) {
        return "Svaki 1 minut";
    }
    if (interval == SYNC_15_MIN) {
        return "Svaki 15 minuta";
    }
    if (interval == SYNC_30_MIN) {
        return "Svaki 30 minuta";
    }
    return "Nikad";
}
```

**Gde se koristi:**
- `SyncSettingsActivity.java` - kada korisnik izbere interval iz Spinner-a
- `SyncService.java` - čita trenutni interval i na osnovu toga pokriva sinhronizaciju

---

## ContentProvider

### Šta je ContentProvider?
- **Sistem koji Android koristi da deli podatke između aplikacija**
- Svaka sistemska aplikacija ima svoj ContentProvider (Kontakti, Kalendar, SMS, itd.)
- **Naša aplikacija čita podatke** - ne kreira svoj ContentProvider

### Kako se čitaju Kontakti preko ContentProvider-a?

**Datoteka:** `ContactsActivity.java`

**Tok:**
1. Provera dozvole `READ_CONTACTS` (runtime permission)
2. Ako korisnik dozvoli → čitaj kontakte
3. Prikaži ih u listi

**Kod (linije 62-91):**

```java
private void loadContacts() {
    contacts.clear();
    
    // 1. Koristi getContentResolver() da pristupnaš ContentProvider-u
    Cursor cursor = getContentResolver().query(
        // ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        // = URI do Kontakt aplikacije
        // = "content://com.android.contacts/data/phones"
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        
        // Koje kolone trebaš?
        new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        },
        
        null,  // WHERE - nema filtera (vrati sve)
        null,  // WHERE vrednosti - nema vrednosti
        
        // ORDER BY
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
    );
    
    // 2. Obradi rezultate
    if (cursor != null) {
        try {
            int nameIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            );
            int numberIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.NUMBER
            );
            
            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIndex);
                String number = cursor.getString(numberIndex);
                contacts.add(name + " - " + number);  // Dodaj u listu
            }
        } finally {
            cursor.close();  // Zatvori cursor
        }
    }
    
    adapter.notifyDataSetChanged();  // Osveži prikaz
}
```

### Dozvola - Runtime Permission

**Kod (linije 54-60):**

```java
private void checkPermissionAndLoadContacts() {
    // Proveri da li je dozvola već odobrena
    if (ContextCompat.checkSelfPermission(
        this, 
        Manifest.permission.READ_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED) {
        loadContacts();  // Ima dozvole - čitaj
    } else {
        // Nema dozvole - traži je
        ActivityCompat.requestPermissions(
            this,
            new String[]{Manifest.permission.READ_CONTACTS},
            REQUEST_CONTACTS
        );
    }
}
```

**Rezultat (linije 94-104):**

```java
@Override
public void onRequestPermissionsResult(int requestCode, 
    @NonNull String[] permissions, 
    @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_CONTACTS) {
        if (grantResults.length > 0 && 
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadContacts();  // Korisnik dozvoli - čitaj
        } else {
            infoText.setText("Dozvola nije odobrena.");
        }
    }
}
```

### Gde se pronalaze URL-i za ContentProvider?

**URL-ovi za glavne sistemske aplikacije:**

| Aplikacija | URI | Napomena |
|------------|-----|---------|
| **Kontakti** | `content://com.android.contacts/contacts` | Svi kontakti |
| **Kontakti - telefoni** | `content://com.android.contacts/data/phones` | Sa brojevima |
| **Kontakti - email** | `content://com.android.contacts/data/emails` | Sa email-ima |
| **Kalendar** | `content://com.android.calendar/calendars` | Kalendari |
| **Kalendar - dogadaji** | `content://com.android.calendar/events` | Događaji |
| **SMS** | `content://sms/inbox` | Primljene SMS |
| **SMS - sve** | `content://sms` | Sve SMS poruke |

**Gde pronaći:**

1. **Android dokumentacija:**
   - https://developer.android.com/reference/android/provider/ContactsContract
   - https://developer.android.com/reference/android/provider/CalendarContract
   - https://developer.android.com/reference/android/provider/Telephony

2. **U tvom kodu:**
   - `ContactsContract.CommonDataKinds.Phone.CONTENT_URI` - konstanta
   - `ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME` - naziv kolone

3. **Primer - svi kontakti bez filtera:**
   ```java
   Cursor cursor = getContentResolver().query(
       ContactsContract.Contacts.CONTENT_URI,  // Glavni URL
       new String[] {
           ContactsContract.Contacts._ID,      // Kolone
           ContactsContract.Contacts.DISPLAY_NAME
       },
       null,  // nema WHERE filtera
       null,
       ContactsContract.Contacts.DISPLAY_NAME + " ASC"
   );
   ```

---

## Sinhronizacija (Sync Service)

### Šta je Sinhronizacija?
- **Periodičko pokretanje neke operacije** (npr. slanje podataka na server)
- Intervali: nikad, 1 min, 15 min, 30 min

### Sadašnja implementacija

**Datoteka:** `SyncService.java`

```java
public class SyncService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SyncService", "Servis pokrenuta");
        // Ovde bi trebalo da se odvija sinhronizacija
        // (npr. HTTP poziv na server, upload baze, itd.)
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
```

### Kako se koristi?

1. **Pročitaj interval iz SharedPreferences:**
   ```java
   SyncPrefs syncPrefs = new SyncPrefs(context);
   long interval = syncPrefs.getInterval();  // npr. 900000L (15 minuta)
   ```

2. **Zakaži servis:**
   ```java
   // Primer sa AlarmManager (jednostavnije za obuku)
   AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
   Intent intent = new Intent(context, SyncService.class);
   PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
   
   if (interval == SyncPrefs.SYNC_NEVER) {
       alarmManager.cancel(pendingIntent);  // Otkaži
   } else {
       alarmManager.setRepeating(
           AlarmManager.RTC_WAKEUP,
           System.currentTimeMillis(),
           interval,                         // interval u milisekundama
           pendingIntent
       );
   }
   ```

---

## Tok izvršavanja aplikacije

### 1. Aplikacija se pokreće → MainActivity
```
MainActivity.onCreate()
    │
    ├─ SessionManager.isLoggedIn()?
    │   ├─ DA  → openDashboard() + finish()
    │   └─ NE  → prikaži Login ekran
    │
    └─ Korisnik unese ime i izabere ulogu
        └─ Klikne "Prijavi se"
            └─ SessionManager.login(name, role)  [SharedPref čuva]
                └─ openDashboard()
```

### 2. DashboardActivity - Glavni meni
```
DashboardActivity
    │
    ├─ Prikaži opcije:
    │   ├─ Upravljanje korisnicima
    │   ├─ Podešavanje sinhronizacije
    │   ├─ Učitavanje kontakata
    │   └─ Odjava
    │
    └─ Samo administrator može:
        └─ Pristupiti CRUD-u
```

### 3. UsersActivity - CRUD ekran
```
UsersActivity
    │
    ├─ Učitaj sve korisnike [UsersDbHelper.getAllUsers()]
    ├─ Prikaži u listi
    │
    ├─ Klikni na korisnika:
    │   ├─ Otvori dijalog za izmenu
    │   ├─ Izmeni podatke
    │   └─ UsersDbHelper.updateUser()
    │
    ├─ Dugo klikni na korisnika:
    │   ├─ Potvrdi brisanje
    │   └─ UsersDbHelper.deleteUser()
    │
    └─ Klikni "Dodaj":
        ├─ Otvori prazan dijalog
        ├─ Unesi podatke
        └─ UsersDbHelper.insertUser()
```

---

## Šablonski primeri za REST API i Retrofit

### ❗ VAŽNO: Šta je REST API i Retrofit?

- **REST API** = Internet servis koji vraća podatke (obično u JSON formatu)
- **Retrofit** = Android biblioteka koja olakšava pozivanje REST API-ja
- **GET** = preuzmi podatke sa servera
- **POST** = pošalji podatke na server
- **PUT** = izmeni podatke na serveru
- **DELETE** = obriši podatke sa servera

### Primer 1: Čitanje podataka sa servera (GET)

**URL primer:**
```
https://api.example.com/users
https://api.example.com/users/5          (korisnik sa ID 5)
https://api.example.com/books?genre=sci-fi
```

**Retrofit interfejs:**
```java
public interface ApiService {
    @GET("users")
    Call<List<User>> getUsers();
    
    @GET("users/{id}")
    Call<User> getUserById(@Path("id") long id);
    
    @GET("books")
    Call<List<Book>> getBooks(@Query("genre") String genre);
}
```

**Kako se koristi:**
```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.example.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build();

ApiService api = retrofit.create(ApiService.class);
Call<List<User>> call = api.getUsers();

call.enqueue(new Callback<List<User>>() {
    @Override
    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
        if (response.isSuccessful()) {
            List<User> users = response.body();  // Podaci sa servera
            // Prikaži podatke
        }
    }
    
    @Override
    public void onFailure(Call<List<User>> call, Throwable t) {
        Toast.makeText(context, "Greška: " + t.getMessage(), Toast.LENGTH_SHORT).show();
    }
});
```

### Primer 2: Slanje podataka (POST)

**URL:**
```
https://api.example.com/users
```

**Body (JSON):**
```json
{
    "ime": "Marko",
    "email": "marko@example.com",
    "uloga": "vozač"
}
```

**Retrofit interfejs:**
```java
public interface ApiService {
    @POST("users")
    Call<User> createUser(@Body User user);
    
    // Ili ako ne trebaš odgovor:
    @POST("users")
    Call<Void> createUserNoResponse(@Body User user);
}
```

**Kako se koristi:**
```java
User newUser = new User("Marko", "marko@example.com", "vozač");
Call<User> call = api.createUser(newUser);

call.enqueue(new Callback<User>() {
    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        if (response.isSuccessful()) {
            User created = response.body();
            Toast.makeText(context, "Korisnik dodat: " + created.getId(), Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onFailure(Call<User> call, Throwable t) {
        Toast.makeText(context, "Greška: " + t.getMessage(), Toast.LENGTH_SHORT).show();
    }
});
```

### Primer 3: Izmena podataka (PUT)

**URL:**
```
https://api.example.com/users/5
```

**Body:**
```json
{
    "id": 5,
    "ime": "Novi Marko",
    "email": "novi.marko@example.com",
    "uloga": "putnik"
}
```

**Retrofit interfejs:**
```java
public interface ApiService {
    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") long id, @Body User user);
}
```

**Kako se koristi:**
```java
User editedUser = new User();
editedUser.setId(5);
editedUser.setName("Novi Marko");
editedUser.setEmail("novi.marko@example.com");
editedUser.setRole("putnik");

Call<User> call = api.updateUser(5, editedUser);

call.enqueue(new Callback<User>() {
    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        if (response.isSuccessful()) {
            Toast.makeText(context, "Korisnik izmenjen", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onFailure(Call<User> call, Throwable t) {
        Toast.makeText(context, "Greška pri izmeni", Toast.LENGTH_SHORT).show();
    }
});
```

### Primer 4: Brisanje podataka (DELETE)

**URL:**
```
https://api.example.com/users/5
```

**Retrofit interfejs:**
```java
public interface ApiService {
    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") long id);
}
```

**Kako se koristi:**
```java
Call<Void> call = api.deleteUser(5);

call.enqueue(new Callback<Void>() {
    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
        if (response.isSuccessful()) {
            Toast.makeText(context, "Korisnik obrisan", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onFailure(Call<Void> call, Throwable t) {
        Toast.makeText(context, "Greška pri brisanju", Toast.LENGTH_SHORT).show();
    }
});
```

### Primer 5: URL sa varijablama i parametrima

```java
public interface ApiService {
    // 1. @Path - deo URL-a
    @GET("users/{userId}/books/{bookId}")
    Call<Book> getUserBook(@Path("userId") long userId, @Path("bookId") long bookId);
    // Poziv: /users/10/books/25
    
    // 2. @Query - parametar na kraju URL-a
    @GET("users")
    Call<List<User>> searchUsers(@Query("name") String name, @Query("role") String role);
    // Poziv: /users?name=Marko&role=vozač
    
    // 3. Kombinovano
    @GET("users/{id}/messages")
    Call<List<Message>> getUserMessages(
        @Path("id") long userId,
        @Query("limit") int limit
    );
    // Poziv: /users/10/messages?limit=20
}
```

---

### ✅ Šablonski kod - KOPIRATI I PRILAGODITI

```java
// 1. Doda Retrofit u build.gradle (app)
dependencies {
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
}

// 2. Kreiraj ApiService.java
public interface ApiService {
    @GET("singers")  // ← PROMENI URL
    Call<List<Singer>> getSingers();
    
    @GET("singers/{id}")
    Call<Singer> getSingerById(@Path("id") long id);
    
    @POST("singers")
    Call<Singer> createSinger(@Body Singer singer);
    
    @PUT("singers/{id}")
    Call<Singer> updateSinger(@Path("id") long id, @Body Singer singer);
    
    @DELETE("singers/{id}")
    Call<Void> deleteSinger(@Path("id") long id);
}

// 3. Korišćenje
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.example.com/")  // ← PROMENI URL
    .addConverterFactory(GsonConverterFactory.create())
    .build();

ApiService api = retrofit.create(ApiService.class);

// GET sve
api.getSingers().enqueue(new Callback<List<Singer>>() {
    @Override
    public void onResponse(Call<List<Singer>> call, Response<List<Singer>> response) {
        if (response.isSuccessful()) {
            singers = response.body();
        }
    }
    @Override
    public void onFailure(Call<List<Singer>> call, Throwable t) {}
});

// POST
Singer newSinger = new Singer("Goca", "goca@example.com", "pop");
api.createSinger(newSinger).enqueue(new Callback<Singer>() {...});

// PUT
api.updateSinger(singerId, editedSinger).enqueue(new Callback<Singer>() {...});

// DELETE
api.deleteSinger(singerId).enqueue(new Callback<Void>() {...});
```

---

## Kako se pronalaze URL-i za ContentProvider

### Metod 1: Android Dokumentacija

1. Otvori https://developer.android.com/reference/android/provider/ContactsContract
2. Traži **"public static final Uri CONTENT_URI"**
3. Primer: `ContactsContract.Contacts.CONTENT_URI` = `content://com.android.contacts/contacts`

### Metod 2: Android SDK Referencija u Android Studio-u

1. Pritisni **Ctrl+Q** (Mac: Cmd+J) na `ContactsContract`
2. Vidiš dokumentaciju sa URL-ovima

### Metod 3: Adb Shell (Advanced)

```bash
adb shell content query --uri content://com.android.contacts/contacts
```

### Sistemski URL-i (najčešće korišćeni)

```java
// Kontakti
ContactsContract.Contacts.CONTENT_URI
// = "content://com.android.contacts/contacts"

ContactsContract.CommonDataKinds.Phone.CONTENT_URI
// = "content://com.android.contacts/data/phones"

ContactsContract.CommonDataKinds.Email.CONTENT_URI
// = "content://com.android.contacts/data/emails"

// Kalendar
CalendarContract.Calendars.CONTENT_URI
// = "content://com.android.calendar/calendars"

CalendarContract.Events.CONTENT_URI
// = "content://com.android.calendar/events"

// SMS
Telephony.Sms.CONTENT_URI
// = "content://sms"

Telephony.Sms.Inbox.CONTENT_URI
// = "content://sms/inbox"
```

---

## Zaključak

Projekat pokriva sledeće teme:

✅ **SQLite baza** - čuvanje lokalnih podataka
✅ **CRUD operacije** - dodavanje, čitanje, izmena, brisanje
✅ **SharedPreferences** - čuvanje malih podataka (prijava, setings)
✅ **ContentProvider** - čitanje sistemskih podataka (kontakti)
✅ **Sinhronizacija** - periodičko pokretanje operacija

Svi ovi koncepti su fundamentalni za Android razvoj i mogu se primenjuje za:
- Mobilne aplikacije za ride-sharing
- E-commerce aplikacije
- Social media aplikacije
- Aplikacije za upravljanje bazom podataka
- i mnogo drugih...

**Za kolokvijum:** očekuje se da znaš strukturu, šta je gde, kako se koristi i da mozes minimalne izmene (promeni URL, promeni bazu imena, dodaj novo polje u bazu, itd.)


