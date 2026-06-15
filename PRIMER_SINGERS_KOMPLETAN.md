# 🎵 PRIMER: Aplikacija za Pevače (Singers)

Ovaj fajl sadrži **kompletan kod** koji se može direktno kopirati u projekat i minimalnim izmenama prilagoditi.

---

## Model klasa - Singer.java

```java
package com.example.vezba2klk;

public class Singer {
    private long id;
    private String name;
    private String genre;
    private String country;
    
    public Singer() {}
    
    public Singer(String name, String genre, String country) {
        this.name = name;
        this.genre = genre;
        this.country = country;
    }
    
    // Getters i Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    @Override
    public String toString() {
        return name + " (" + genre + ") - " + country;
    }
}
```

---

## Baza podataka - SingerDbHelper.java

```java
package com.example.vezba2klk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class SingerDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "singers.db";
    public static final int DATABASE_VERSION = 1;
    
    public static final String TABLE_SINGERS = "pevaci";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "ime";
    public static final String COL_GENRE = "zanr";
    public static final String COL_COUNTRY = "drzava";
    
    public SingerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SINGERS + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_NAME + " TEXT NOT NULL, " +
            COL_GENRE + " TEXT NOT NULL, " +
            COL_COUNTRY + " TEXT NOT NULL)");
        
        // Seed data
        insertSeedSinger(db, "Ceca", "folk", "Srbija");
        insertSeedSinger(db, "Marija Serifovic", "pop", "Srbija");
        insertSeedSinger(db, "Goca Tržan", "turbo-folk", "Srbija");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SINGERS);
        onCreate(db);
    }
    
    private void insertSeedSinger(SQLiteDatabase db, String name, String genre, String country) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_GENRE, genre);
        values.put(COL_COUNTRY, country);
        db.insert(TABLE_SINGERS, null, values);
    }
    
    // CREATE
    public long insertSinger(Singer singer) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, singer.getName());
        values.put(COL_GENRE, singer.getGenre());
        values.put(COL_COUNTRY, singer.getCountry());
        return db.insert(TABLE_SINGERS, null, values);
    }
    
    // READ - svi
    public List<Singer> getAllSingers() {
        List<Singer> singers = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_SINGERS, null, null, null, null, null, COL_NAME + " ASC");
        try {
            while (cursor.moveToNext()) {
                singers.add(cursorToSinger(cursor));
            }
        } finally {
            cursor.close();
        }
        return singers;
    }
    
    // READ - jedan
    public Singer getSingerById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_SINGERS, null, COL_ID + "=?", 
            new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return cursorToSinger(cursor);
            }
            return null;
        } finally {
            cursor.close();
        }
    }
    
    // UPDATE
    public int updateSinger(Singer singer) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, singer.getName());
        values.put(COL_GENRE, singer.getGenre());
        values.put(COL_COUNTRY, singer.getCountry());
        return db.update(TABLE_SINGERS, values, COL_ID + "=?", 
            new String[]{String.valueOf(singer.getId())});
    }
    
    // DELETE
    public int deleteSinger(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_SINGERS, COL_ID + "=?", 
            new String[]{String.valueOf(id)});
    }
    
    private Singer cursorToSinger(Cursor cursor) {
        Singer singer = new Singer();
        singer.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
        singer.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
        singer.setGenre(cursor.getString(cursor.getColumnIndexOrThrow(COL_GENRE)));
        singer.setCountry(cursor.getString(cursor.getColumnIndexOrThrow(COL_COUNTRY)));
        return singer;
    }
}
```

---

## API Servis - SingerApiService.java

```java
package com.example.vezba2klk;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface SingerApiService {
    // Čitaj sve pevače
    @GET("singers")
    Call<List<Singer>> getAllSingers();
    
    // Čitaj pevača po ID-u
    @GET("singers/{id}")
    Call<Singer> getSingerById(@Path("id") long id);
    
    // Kreiraj novog pevača
    @POST("singers")
    Call<Singer> createSinger(@Body Singer singer);
    
    // Izmeni pevača
    @PUT("singers/{id}")
    Call<Singer> updateSinger(@Path("id") long id, @Body Singer singer);
    
    // Obriši pevača
    @DELETE("singers/{id}")
    Call<Void> deleteSinger(@Path("id") long id);
}
```

---

## Aktivnost sa CRUD i API - SingersActivity.java

```java
package com.example.vezba2klk;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SingersActivity extends AppCompatActivity {
    private SingerDbHelper dbHelper;
    private ArrayAdapter<Singer> adapter;
    private List<Singer> singers = new ArrayList<>();
    private SingerApiService apiService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singers);  // ← Trebaš layout
        
        dbHelper = new SingerDbHelper(this);
        
        // Postavi Retrofit
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        apiService = retrofit.create(SingerApiService.class);
        
        ListView listView = findViewById(R.id.listSingers);
        Button addButton = findViewById(R.id.buttonAddSinger);
        Button syncButton = findViewById(R.id.buttonSyncSingers);
        Button backButton = findViewById(R.id.buttonBackSingers);
        
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, singers);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener((parent, view, position, id) -> editSinger(singers.get(position)));
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            confirmDelete(singers.get(position));
            return true;
        });
        
        addButton.setOnClickListener(v -> showSingerDialog(null));
        syncButton.setOnClickListener(v -> syncFromServer());
        backButton.setOnClickListener(v -> finish());
        
        loadSingers();
    }
    
    private void loadSingers() {
        singers.clear();
        singers.addAll(dbHelper.getAllSingers());
        adapter.notifyDataSetChanged();
    }
    
    private void showSingerDialog(Singer singer) {
        View formView = LayoutInflater.from(this).inflate(R.layout.dialog_singer_form, null);
        EditText nameInput = formView.findViewById(R.id.inputSingerName);
        EditText genreInput = formView.findViewById(R.id.inputSingerGenre);
        EditText countryInput = formView.findViewById(R.id.inputSingerCountry);
        
        if (singer != null) {
            nameInput.setText(singer.getName());
            genreInput.setText(singer.getGenre());
            countryInput.setText(singer.getCountry());
        }
        
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(singer == null ? "Dodaj pevača" : "Izmeni pevača")
            .setView(formView)
            .setPositiveButton("Sačuvaj", null)
            .setNegativeButton("Otkaži", null)
            .create();
        
        dialog.setOnShowListener(d -> 
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String name = nameInput.getText().toString().trim();
                String genre = genreInput.getText().toString().trim();
                String country = countryInput.getText().toString().trim();
                
                if (name.isEmpty() || genre.isEmpty() || country.isEmpty()) {
                    Toast.makeText(this, "Popuni sva polja", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (singer == null) {
                    Singer newSinger = new Singer(name, genre, country);
                    long result = dbHelper.insertSinger(newSinger);
                    Toast.makeText(this, result > 0 ? "Pevač dodat" : "Greška", Toast.LENGTH_SHORT).show();
                } else {
                    singer.setName(name);
                    singer.setGenre(genre);
                    singer.setCountry(country);
                    int result = dbHelper.updateSinger(singer);
                    Toast.makeText(this, result > 0 ? "Pevač izmenjen" : "Greška", Toast.LENGTH_SHORT).show();
                }
                loadSingers();
                dialog.dismiss();
            })
        );
        
        dialog.show();
    }
    
    private void editSinger(Singer singer) {
        showSingerDialog(singer);
    }
    
    private void confirmDelete(Singer singer) {
        new AlertDialog.Builder(this)
            .setTitle("Brisanje")
            .setMessage("Obriši " + singer.getName() + "?")
            .setPositiveButton("Obriši", (dialog, which) -> {
                int result = dbHelper.deleteSinger(singer.getId());
                Toast.makeText(this, result > 0 ? "Obrisan" : "Greška", Toast.LENGTH_SHORT).show();
                loadSingers();
            })
            .setNegativeButton("Otkaži", null)
            .show();
    }
    
    private void syncFromServer() {
        apiService.getAllSingers().enqueue(new Callback<List<Singer>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Singer>> call, Response<List<Singer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    singers.clear();
                    for (Singer singer : response.body()) {
                        long result = dbHelper.insertSinger(singer);
                        if (result < 0) {
                            dbHelper.updateSinger(singer);  // Ako postoji - izmeni
                        }
                    }
                    loadSingers();
                    Toast.makeText(SingersActivity.this, "Sinhronizovano", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SingersActivity.this, "Greška odgovora", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<List<Singer>> call, Throwable t) {
                Toast.makeText(SingersActivity.this, "Greška: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

---

## Layout - activity_singers.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">
    
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Pevači"
        android:textSize="24sp"
        android:textStyle="bold"
        android:textAlignment="center"
        android:layout_marginBottom="16dp" />
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:layout_marginBottom="16dp">
        
        <Button
            android:id="@+id/buttonAddSinger"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Dodaj" />
        
        <Button
            android:id="@+id/buttonSyncSingers"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:layout_marginStart="8dp"
            android:text="Sinhronizuj" />
        
        <Button
            android:id="@+id/buttonBackSingers"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:layout_marginStart="8dp"
            android:text="Nazad" />
    </LinearLayout>
    
    <ListView
        android:id="@+id/listSingers"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
```

---

## Layout - dialog_singer_form.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">
    
    <EditText
        android:id="@+id/inputSingerName"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Ime pevača"
        android:inputType="textPersonName"
        android:layout_marginBottom="12dp" />
    
    <EditText
        android:id="@+id/inputSingerGenre"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Žanr"
        android:inputType="text"
        android:layout_marginBottom="12dp" />
    
    <EditText
        android:id="@+id/inputSingerCountry"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Država"
        android:inputType="text" />
</LinearLayout>
```

---

## build.gradle - Dodaj zavisnosti

```gradle
dependencies {
    // Retrofit
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    
    // Gson
    implementation 'com.google.code.gson:gson:2.8.9'
}
```

---

## AndroidManifest.xml - Dodaj Internet dozvolu

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## Kako koristiti ovaj primer

1. **Kopiraj sve Java klase** u `com.example.vezba2klk` paket
2. **Kopiraj layout XML fajlove** u `res/layout/`
3. **Dodaj zavisnosti** u `build.gradle`
4. **Dodaj Internet dozvolu** u `AndroidManifest.xml`
5. **Zamisli API na `https://api.example.com/singers`** (ili zameni sa realnim)
6. **Testira na emulatoru**

---

## Za brz test - bez stvarnog API-ja

Ako nemaš pravi API, fokusiraj se samo na lokalnu bazu:
- Izbriši `syncFromServer()` metodu iz aktivnosti
- Koristi samo lokalnu bazu (insertSinger, updateSinger, deleteSinger)
- U realnom projektu kasnije dodaš API pozive

---

## Kako prilagoditi za druge podatke

**Recimo trebaju "Knjige" umesto "Pevača":**

1. Preimenuaj `Singer` → `Book`
2. Preimenuaj `SingerDbHelper` → `BookDbHelper`
3. Preimenuaj `SingerApiService` → `BookApiService`
4. Zameni kolone: `COL_GENRE`, `COL_COUNTRY` → `COL_TITLE`, `COL_AUTHOR`
5. Zameni URL: `/singers` → `/books`
6. To je to! 🎉

---

## ✅ FAQ i praktični vodič - šta i kada koristiti, kako kopirati zadatak

Ovaj dodatak objašnjava kako da brzo prepoznaš šta se traži na kolokvijumu i koje delove koda treba da menjaš (URL, model, tabele, dozvole...). Sve je napisano jednostavno i praktično tako da možeš brzo da kopiraš i prilagodiš kod.

1) Kako prepoznati da li treba ContentProvider, SharedPreferences ili direktan pristup bazi?
- Ako zadatak eksplicitno spominje "Kontakti", "Kalendar", "SMS" ili "ContentProvider" — koristiš ContentProvider (npr. `ContactsContract`, `CalendarContract`, `Telephony`). To su sistemski podaci koje čitaš putem `getContentResolver().query(uri, ...)`.
- Ako zadatak kaže "lokalna baza" ili zahteva da čuvaš entitete (npr. korisnici, knjige, pevači) i aplikacija je odgovorna za te podatke — koristiš SQLite/`SQLiteOpenHelper` (kao u `UsersDbHelper`/`SingerDbHelper`).
- Ako treba da sačuvaš samo male konfiguracione vrednosti ili status (prijavljen korisnik, interval sinhronizacije, token) — koristiš `SharedPreferences`.

2) Kako da znaš da li treba REST (GET/POST/PUT/DELETE) ili samo lokalna DB?
- Ako zadatak spominje URL, server, API, HTTP ili navodi krajnju tačku (npr. `https://api.example.com/...`) — koristiš Retrofit/HTTP pozive.
- Ako zadatak govori o lokalnom upravljanju podacima bez servera — radiš samo sa lokalnom SQLite bazom.

3) Kako se grade URL delovi i šta je @Path vs @Query (Retrofit)?
- Bazni URL: `baseUrl` u Retrofit builderu, npr. `https://api.example.com/`.
- Endpoint je deo iza baze: npr. `singers`, `books/5`.
- @Path - koristi se kada je varijabla deo same putanje (URL segment), npr. `/users/{id}` → `@GET("users/{id}") Call<User> get(@Path("id") long id)`.
- @Query - koristi se za query parametre koji dolaze posle `?`, npr. `/users?name=Marko&role=vozač` → `@GET("users") Call<List<User>> search(@Query("name") String name)`.

4) Kako razlikovati GET/POST/PUT/DELETE u zadatku?
- GET => "dohvati", "prikaži", "nabavi" (ne menja server)
- POST => "dodaj", "kreiraj" (stvaranje novog resursa)
- PUT/PATCH => "izmeni", "update" (menjanje resursa)
- DELETE => "obriši", "delete"

5) Dozvole (permissions) koje trebate znati
- `android.permission.INTERNET` — za mrežne zahteve (manifest-level, nije runtime permission)
- `android.permission.READ_CONTACTS` — za čitanje kontakata (manifest + runtime permission na Android 6+)
- `android.permission.READ_SMS`, `android.permission.READ_CALL_LOG` — za SMS/pozive (runtime)

Kako tražiti runtime dozvolu (primer za kontakte):
```java
if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS);
} else {
    loadContacts();
}

@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == REQUEST_CONTACTS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        loadContacts();
    }
}
```

6) ContentProvider URI - kako ih naći i primeri
- Sistemske URI konstante: pogledaj `ContactsContract`, `CalendarContract`, `Telephony` u Android dokumentaciji ili u Android Studio (Ctrl+klik). Primeri:
  - Kontakti sa brojevima: `ContactsContract.CommonDataKinds.Phone.CONTENT_URI` (obično `content://com.android.contacts/data/phones`)
  - Kalendar događaji: `CalendarContract.Events.CONTENT_URI`
  - SMS: `Uri.parse("content://sms")`

7) Šta da menjaš u šablonu kad ti daju novi zadatak (brzo kopiranje)
- Ako ti daju zadatak sa novim entitetom (npr. Pevači → Pevač):
  1. Model (`Singer.java`) - promeni ime i polja
  2. DB helper (`SingerDbHelper`) - promeni ime tabele i kolone
  3. Aktivnost/layout (`SingersActivity`, `activity_singers.xml`) - promeni stringove i ID-e ako ima
  4. API interfejs (`SingerApiService`) - promeni endpoint putanje (`@GET("singers")` itd.) i baseUrl
  5. SharedPreferences ključeve (ako postoje) - promeni string ključeve

8) Direktan pristup bazi vs ContentProvider
- Direktan pristup bazi: koristiš ako aplikacija koristi sopstvenu bazu podataka (tvoja tabela). Prednosti: puni pristup, lakše menjaš strukturu.
- ContentProvider: koristiš da čitaš (ili retko menjaš) podatke drugih aplikacija (kontakti, kalendar). Ne možeš čitati tuđu internu SQLite bazu, već preko ContentProvider API-ja.

9) Testiranje na emulatoru
- Emulator može imati prazne kontakte. Možeš dodati kontakte u emulator putem aplikacije Contacts ili pomoću `adb`:
  - `adb shell am start -a android.intent.action.INSERT -t vnd.android.cursor.item/contact --es name "Test User"` (ili ručno kroz UI)

10) Brze greške koje treba da izbegneš
- Ne zaboravi da zatvoriš `Cursor` u finally bloku
- Network pozive stavljaj u background (Retrofit `enqueue()` radi to automatski)
- Proveri i traži runtime dozvole pre korišćenja ContentProvider-a

11) Kratki primer: prepoznavanje zahteva u testu
- Ako u zadatku stoji: "Dobavi sve kontakte i prikaži ih" → koristiš `ContactsContract` + runtime `READ_CONTACTS` + `getContentResolver().query(...)`.
- Ako stoji: "Dobavi sve korisnike sa servera" → koristiš Retrofit `@GET` i baseUrl + endpoint.
- Ako stoji: "Sačuvaj korisnika u lokalnu bazu" → koristiš `SQLiteOpenHelper.insert...` i prikaz u listi.

12) Checklist koji možeš držati pored sebe na kolokvijumu
- Pročitaj zadatak: sadrži li `content://` ili ime sistemske tabele? -> ContentProvider
- Spominje li URL ili server? -> Retrofit/REST
- Spominje li "lokalna baza"? -> SQLite (`SQLiteOpenHelper`)
- Treba li runtime dozvola? -> Ako da, implementiraj `requestPermissions`
- Treba li periodična sinhronizacija? -> WorkManager / AlarmManager + SharedPreferences za interval

Ako želiš, mogu sada:
- A) Dodati iste ove upute direktno u `PRIMER_SINGERS_KOMPLETAN.md` (već uradio sam ovo),
- B) Napraviti još jedan kraći "cheat-sheet" u `README.md` da ti bude odmah pri ruci tokom ispita,
- C) Implementirati primer sa `WorkManager` umesto `SyncService` za pouzdanu sinhronizaciju.

Reci mi šta želiš dalje i mogu odmah da dopunim (B ili C) i napravim male izmene u kodu da bude spremno za kopiranje na kolokvijumu.


