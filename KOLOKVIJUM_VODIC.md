# 🎓 KOLOKVIJUM - Praktični vodič

## Kako se radi test na kolokvijumu?

**Scenario:** 
> Profesor ti da standardni projekat (npr. "Knjige" sa book API-jem), a ti trebaš da ga prilagodi za nove podatke (npr. "Pevači" sa singers API-jem)

---

## Korak po korak

### 1. KORAK: Analiziraj originalni kod

Ako je dat projekat sa "Knjigama":
```java
// UserDbHelper.java (originalni - sa Knjigama)
public class BookDbHelper extends SQLiteOpenHelper {
    public static final String TABLE_BOOKS = "knjige";
    public static final String COL_TITLE = "naslov";
    public static final String COL_AUTHOR = "autor";
    public static final String COL_GENRE = "zanr";
    
    public long insertBook(Book book) {
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, book.getTitle());
        values.put(COL_AUTHOR, book.getAuthor());
        values.put(COL_GENRE, book.getGenre());
        return db.insert(TABLE_BOOKS, null, values);
    }
}
```

### 2. KORAK: Pretvori za nove podatke

**Projekt sa Pevačima** - samo zameni nazive:

```java
// UserDbHelper.java (TVOJ - sa Pevačima)
public class SingerDbHelper extends SQLiteOpenHelper {
    public static final String TABLE_SINGERS = "pevaci";  // ← NOVO
    public static final String COL_NAME = "ime";          // ← NOVO
    public static final String COL_GENRE = "zanr";        // ← NOVO
    public static final String COL_COUNTRY = "drzava";    // ← NOVO
    
    public long insertSinger(Singer singer) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, singer.getName());       // ← NOVO
        values.put(COL_GENRE, singer.getGenre());
        values.put(COL_COUNTRY, singer.getCountry()); // ← NOVO
        return db.insert(TABLE_SINGERS, null, values);
    }
}
```

**Promene:**
- `BookDbHelper` → `SingerDbHelper`
- `TABLE_BOOKS` → `TABLE_SINGERS`
- `COL_TITLE`, `COL_AUTHOR` → `COL_NAME`, `COL_GENRE`, `COL_COUNTRY`
- Model klase `Book` → `Singer`

---

### 2. KORAK: REST API - URL zamena

**Originalni kod (sa Knjigama):**
```java
public interface BookApiService {
    @GET("books")
    Call<List<Book>> getBooks();
    
    @GET("books/{id}")
    Call<Book> getBookById(@Path("id") long id);
    
    @POST("books")
    Call<Book> createBook(@Body Book book);
}
```

**Tvoj kod (sa Pevačima) - samo zameni URL i nazive:**
```java
public interface SingerApiService {
    @GET("singers")              // ← SAMO PROMENI "books" → "singers"
    Call<List<Singer>> getSingers();
    
    @GET("singers/{id}")         // ← SAMO PROMENI "books" → "singers"
    Call<Singer> getSingerById(@Path("id") long id);
    
    @POST("singers")             // ← SAMO PROMENI "books" → "singers"
    Call<Singer> createSinger(@Body Singer singer);
}
```

**Šema zamene URL-a:**
```
GET  /books        →  GET  /singers
GET  /books/{id}   →  GET  /singers/{id}
POST /books        →  POST /singers
PUT  /books/{id}   →  PUT  /singers/{id}
DELETE /books/{id} →  DELETE /singers/{id}
```

---

### 3. KORAK: SharedPreferences - Promeni ključeve

**Originalni kod (ako je dat):**
```java
public class SettingsPrefs {
    private static final String KEY_CURRENT_BOOK = "current_book_id";
    
    public void setCurrentBook(long bookId) {
        prefs.edit().putLong(KEY_CURRENT_BOOK, bookId).apply();
    }
}
```

**Tvoj kod - samo zameni ključ:**
```java
public class SettingsPrefs {
    private static final String KEY_CURRENT_SINGER = "current_singer_id";
    
    public void setCurrentSinger(long singerId) {
        prefs.edit().putLong(KEY_CURRENT_SINGER, singerId).apply();
    }
}
```

---

### 4. KORAK: ContentProvider - Primeni na druge podatke

**Originalni kod (čitanje Kontakata):**
```java
private void loadContacts() {
    Cursor cursor = getContentResolver().query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,  // ← URL
        new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        },
        null,
        null,
        null
    );
    // ... obrada
}
```

**Ako trebaš SMS poruke - samo zameni URL:**
```java
private void loadSMS() {
    Cursor cursor = getContentResolver().query(
        Uri.parse("content://sms/inbox"),  // ← SAMO PROMENI URL
        new String[]{"_id", "address", "body", "date"},
        null,
        null,
        "date DESC"
    );
    // ... ista obrada
}
```

**Ili Kalendar:**
```java
private void loadCalendar() {
    Cursor cursor = getContentResolver().query(
        CalendarContract.Events.CONTENT_URI,  // ← SAMO PROMENI URL
        new String[]{"_id", "title", "dtstart"},
        null,
        null,
        null
    );
    // ... ista obrada
}
```

---

## Primer iz stvarnog testa

### Zadatak sa testa
> Napraviti aplikaciju za upravljanje **Filmovima**. Trebati:
> 1. Baza sa tabelom `filmovi` (naziv, režiser, godina, zanr)
> 2. CRUD operacije
> 3. Preuzimanje filmova sa API-ja
> 4. Prikazivanje elemenata iz Calendara (ContentProvider)

### Rešenje (korišćenjem šablona)

**Korak 1: Baza**
```java
// FilmDbHelper.java (copy-paste od UserDbHelper, zameni nazive)
public class FilmDbHelper extends SQLiteOpenHelper {
    public static final String TABLE_FILMS = "filmovi";
    public static final String COL_NAME = "naziv";
    public static final String COL_DIRECTOR = "reziser";
    public static final String COL_YEAR = "godina";
    public static final String COL_GENRE = "zanr";
    
    public long insertFilm(Film film) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, film.getName());
        values.put(COL_DIRECTOR, film.getDirector());
        values.put(COL_YEAR, film.getYear());
        values.put(COL_GENRE, film.getGenre());
        return db.insert(TABLE_FILMS, null, values);
    }
    // ... ostale CRUD metode sa zamenjenim nazivima
}
```

**Korak 2: API**
```java
public interface FilmApiService {
    @GET("movies")          // ← Zamenjeni URL
    Call<List<Film>> getFilms();
    
    @POST("movies")
    Call<Film> createFilm(@Body Film film);
}
```

**Korak 3: Korišćenje**
```java
// FilmActivity.java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.example.com/")  // ← Zamenjeni URL
    .addConverterFactory(GsonConverterFactory.create())
    .build();

FilmApiService api = retrofit.create(FilmApiService.class);

api.getFilms().enqueue(new Callback<List<Film>>() {
    @Override
    public void onResponse(Call<List<Film>> call, Response<List<Film>> response) {
        if (response.isSuccessful()) {
            List<Film> films = response.body();
            // Prikaži u listi
        }
    }
    // ...
});
```

**Korak 4: ContentProvider (Kalendar)**
```java
private void loadCalendarEvents() {
    Cursor cursor = getContentResolver().query(
        CalendarContract.Events.CONTENT_URI,  // ← ContentProvider URL
        new String[]{"_id", "title", "dtstart"},
        null,
        null,
        "dtstart ASC"
    );
    if (cursor != null) {
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            long time = cursor.getLong(cursor.getColumnIndexOrThrow("dtstart"));
            // Prikaži
        }
        cursor.close();
    }
}
```

---

## Čist List: Šta trebam pamtiti za kolokvijum

### 1. SQLite CRUD šabloni
```java
// CREATE
db.insert(TABLE, null, contentValues);

// READ
db.query(TABLE, null, WHERE_CLAUSE, WHERE_ARGS, null, null, ORDER_BY);

// UPDATE  
db.update(TABLE, contentValues, WHERE_CLAUSE, WHERE_ARGS);

// DELETE
db.delete(TABLE, WHERE_CLAUSE, WHERE_ARGS);
```

### 2. SharedPreferences šabloni
```java
// ČUVAJ
prefs.edit().putString("key", "value").apply();

// ČITAJ
prefs.getString("key", "default_value");

// OBRIŠI
prefs.edit().remove("key").apply();
```

### 3. Retrofit šabloni
```java
// GET
@GET("endpoint")
Call<List<T>> getAll();

// POST
@POST("endpoint")
Call<T> create(@Body T object);

// PUT
@PUT("endpoint/{id}")
Call<T> update(@Path("id") long id, @Body T object);

// DELETE
@DELETE("endpoint/{id}")
Call<Void> delete(@Path("id") long id);

// KORIŠĆENJE
api.getAll().enqueue(new Callback<List<T>>() {
    public void onResponse(...) { /* uspeh */ }
    public void onFailure(...) { /* greška */ }
});
```

### 4. ContentProvider šabloni
```java
// URL
ContentProvider.CONTENT_URI

// QUERY
Cursor cursor = getContentResolver().query(
    URI,
    new String[]{"col1", "col2"},
    null,
    null,
    ORDER_BY
);

// ITERACIJA
while (cursor.moveToNext()) {
    String value = cursor.getString(cursor.getColumnIndexOrThrow("col"));
}
cursor.close();
```

---

## Za obdanu pred profesorom - Što da kažeš

> "Projekat je urađen tako što sam:
>
> 1. **SQLite bazu** - kreiram sa `SQLiteOpenHelper`, definiram tabelu sa poljima, i implementiram sve CRUD metode (`insert`, `query`, `update`, `delete`).
>
> 2. **SharedPreferences** - čuvam male podatke (prijava, setings) u jednostavnoj key-value strukturi. Za prijavljenog korisnika čuvam ime i ulogu, a na osnovu toga prikazujem odgovarajući ekran.
>
> 3. **Sinhronizaciju** - čuvam izbor intervala korisnika (nikad, 1, 15, 30 min) i na osnovu toga pokrevam `AlarmManager` ili `WorkManager` da periodički pokreće servis.
>
> 4. **ContentProvider** - čitam sistemske podatke (npr. kontakte) preko `getContentResolver().query()`, što je standardan način za pristup podacima drugih aplikacija.
>
> 5. **REST API** - koristim `Retrofit` framework za komunikaciju sa serverom. Definiram `@GET`, `@POST`, `@PUT`, `@DELETE` metode i zahteve šaljem asinkrono sa `enqueue()`.
>
> Sve je urađeno po uzoru na vežbe sa fakulteta, a struktura je takva da se lako može prilagoditi (zameni baze, URL-ove, model klase) zavisno od zahteva."

---

## Česti greške na kolokvijumu

❌ **Greška 1:** Zaboravljanje da se `Cursor` zatvori
```java
// LOŠE
Cursor cursor = db.query(...);
while (cursor.moveToNext()) { ... }
// ZATVORI!
```

```java
// DOBRO
try {
    Cursor cursor = db.query(...);
    while (cursor.moveToNext()) { ... }
} finally {
    cursor.close();  // ← OBAVEZNO
}
```

❌ **Greška 2:** Zaboravljanje dozvole za ContentProvider
```java
// LOŠE - aplikacija će pucati ako nema dozvole
Cursor cursor = getContentResolver().query(...);

// DOBRO
if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) 
    == PackageManager.PERMISSION_GRANTED) {
    Cursor cursor = getContentResolver().query(...);
}
```

❌ **Greška 3:** Callback na glavnoj niti (network na main thread)
```java
// LOŠE - hladni pokušaj
Cursor cursor = getContentResolver().query(...);  // LOŠE - блокира UI

// DOBRO - Retrofit je asinkron
api.getUsers().enqueue(new Callback<List<User>>() {
    @Override
    public void onResponse(...) {
        // Ovde je OK, Retrofit je na background niti
    }
});
```

❌ **Greška 4:** Zaboravljanje `.apply()` na SharedPreferences
```java
// LOŠE
prefs.edit().putString("key", "value");  // Nema čuvanja!

// DOBRO
prefs.edit().putString("key", "value").apply();  // ← OBAVEZNO
```

---

## Završni saveti

✅ **Čitaj kod koji je dat** - razumevanje je ključno
✅ **Nauči šablone** - većina zadataka je zamena URL-a, naziva, polja
✅ **Testira na emulatoru** - proverite da li sve radi pre nego što predate
✅ **Komentiraj kod** - profesoru je jasnije da razumeš šta radiš
✅ **Prekapiraj od početka ako moraš** - ne mešaj starod sa novim

**SRETNO NA KOLOKVIJUMU! 🎉**


