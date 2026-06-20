# ContentProvider Guide — Kontakti, Kalendar i SMS (priprema za kolokvijum)

Ovaj fajl sadrži gotove, spremne za kopiranje primere i uputstva kako da koristiš ContentProvider za:

- Kontakti (Contacts)
- Kalendar (Calendar events)
- SMS poruke

Svaki primer sadrži: manifest izmene, runtime permission kod, Activity kod za query + Cursor handling, osnovne layout fajlove i kratke instrukcije kako da prilagodiš kod za drugi provider.

NAPOMENA: Pre nego što kopiraš u projekat, proveri da li već imaš klase sa istim imenima i prebaci putanje paketa prema tvom projektu (npr. `com.example.vezba2klk`).

---

## Sadržaj

1. Brzi pregled fajlova koje treba da dodaš
2. Manifest (dozvole)
3. Runtime dozvole direktno u Activity-ju (bez helper klase)
4. Kontakti — `ContactsActivity.java` + layout
5. Kalendar — `CalendarActivity.java` + layout
6. SMS — `SmsActivity.java` + layout
7. Testiranje na emulatoru (adb)
8. Copy & change: šta menjaš kada prebacuješ na drugi entitet
9. FAQ i česte greške

---

## 1) Brzi pregled fajlova koje treba da dodaš

Predlažem da ubaciš sledeće fajlove u paket `com.example.vezba2klk` (ili tvoj paket):

- Activities / kod:
  - `ContactsActivity.java`
  - `CalendarActivity.java`
  - `SmsActivity.java`

- Layouts (res/layout):
  - `activity_contacts.xml` (ListView)
  - `item_contact.xml` (stavka liste za kontakt)
  - `activity_calendar.xml` (ListView za evente)
  - `item_event.xml` (stavka liste za event)
  - `activity_sms.xml` (ListView za SMS)
  - `item_sms.xml` (stavka liste za sms)

- Manifest izmene (AndroidManifest.xml):
  - dodaj permissions: `READ_CONTACTS`, `READ_CALENDAR`, `READ_SMS` po potrebi

Ako već imaš `ContactsActivity` ili slično u projektu, preimenuj nove klase ili spoji kod.

---

## 2) Manifest (dodaj ove dozvole)

U `AndroidManifest.xml` dodaj (ako već nije):

```xml
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.READ_CALENDAR" />
<uses-permission android:name="android.permission.READ_SMS" />
```

> Napomena: `INTERNET` se ne zahteva za ContentProvider, osim ako radiš mrežne pozive.

---

## 3) Runtime dozvole direktno u Activity-ju (bez helper klase)

Ovo je jednostavnija varijanta i sasvim je dovoljna za vežbe. U svakoj Activity klasi direktno napišeš:

- proveru dozvole preko `ContextCompat.checkSelfPermission(...)`
- traženje dozvole preko `ActivityCompat.requestPermissions(...)`
- odgovor korisnika u `onRequestPermissionsResult(...)`

To znači da **ne moraš da dodaješ posebnu `PermissionHelper.java` klasu**.

Primer koji možeš koristiti u svakoj Activity klasi:

```java
if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.READ_CONTACTS},
            REQ_READ_CONTACTS);
} else {
    loadContacts();
}
```

U `onRequestPermissionsResult(...)`:

```java
if (requestCode == REQ_READ_CONTACTS) {
    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        loadContacts();
    } else {
        Toast.makeText(this, "Dozvola odbijena", Toast.LENGTH_SHORT).show();
    }
}
```

---

## 4) Kontakti — `ContactsActivity.java`

Manifest: dodaj `READ_CONTACTS` (već gore).

Layout (`res/layout/activity_contacts.xml`):

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="8dp">

    <TextView
        android:id="@+id/textInfo"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Kontakti"
        android:textStyle="bold"
        android:padding="4dp" />

    <ListView
        android:id="@+id/listContacts"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
```

Stavka liste (`res/layout/item_contact.xml`):

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="8dp">

    <TextView
        android:id="@+id/textName"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textStyle="bold" />

    <TextView
        android:id="@+id/textNumber"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
</LinearLayout>
```

Activity kod (kopiraj i lepi u `ContactsActivity.java`):

```java
package com.example.vezba2klk;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {
    private static final int REQ_READ_CONTACTS = 101;
    private ListView listContacts;
    private TextView textInfo;
    private ArrayAdapter<ContactItem> adapter;
    private List<ContactItem> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        listContacts = findViewById(R.id.listContacts);
        textInfo = findViewById(R.id.textInfo);

        adapter = new ArrayAdapter<ContactItem>(this, 0, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_contact, parent, false);
                }
                TextView name = convertView.findViewById(R.id.textName);
                TextView number = convertView.findViewById(R.id.textNumber);
                ContactItem it = getItem(position);
                name.setText(it.name != null ? it.name : "(bez imena)");
                number.setText(it.number != null ? it.number : "");
                return convertView;
            }
        };
        listContacts.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQ_READ_CONTACTS);
        } else {
            loadContacts();
        }
    }

    private void loadContacts() {
        items.clear();
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER},
                    null, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                int idxName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int idxNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                do {
                    String name = idxName >= 0 ? cursor.getString(idxName) : null;
                    String number = idxNumber >= 0 ? cursor.getString(idxNumber) : null;
                    items.add(new ContactItem(name, number));
                } while (cursor.moveToNext());
            }

            adapter.notifyDataSetChanged();
            textInfo.setText("Pronađeno: " + items.size());
        } catch (SecurityException se) {
            Toast.makeText(this, "Nema dozvolu za čitanje kontakata", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts();
            } else {
                Toast.makeText(this, "Dozvola odbijena", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class ContactItem {
        String name;
        String number;
        ContactItem(String n, String num) { name = n; number = num; }
    }
}
```

---

## 5) Kalendar — `CalendarActivity.java`

Manifest: dodaj `READ_CALENDAR`.

Layout (`res/layout/activity_calendar.xml`): jednostavan ListView kao za kontakte.

Stavka (`res/layout/item_event.xml`): slična `item_contact.xml`, ali sa naslovom i vremenom.

Activity kod (kopiraj u `CalendarActivity.java`):

```java
package com.example.vezba2klk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    private static final int REQ_READ_CALENDAR = 201;
    private ListView listEvents;
    private TextView textInfo;
    private ArrayAdapter<EventItem> adapter;
    private List<EventItem> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        listEvents = findViewById(R.id.listEvents);
        textInfo = findViewById(R.id.textInfo);

        adapter = new ArrayAdapter<EventItem>(this, 0, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
                }
                TextView title = convertView.findViewById(R.id.textTitle);
                TextView when = convertView.findViewById(R.id.textWhen);
                EventItem it = getItem(position);
                title.setText(it.title != null ? it.title : "(bez naslova)");
                when.setText(it.when != null ? it.when : "");
                return convertView;
            }
        };
        listEvents.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR},
                    REQ_READ_CALENDAR);
        } else {
            loadEvents();
        }
    }

    private void loadEvents() {
        items.clear();
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(
                    CalendarContract.Events.CONTENT_URI,
                    new String[]{CalendarContract.Events._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART},
                    null, null,
                    CalendarContract.Events.DTSTART + " ASC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                int idxTitle = cursor.getColumnIndex(CalendarContract.Events.TITLE);
                int idxWhen = cursor.getColumnIndex(CalendarContract.Events.DTSTART);
                DateFormat df = DateFormat.getDateTimeInstance();
                do {
                    String title = idxTitle >= 0 ? cursor.getString(idxTitle) : null;
                    long whenMs = idxWhen >= 0 ? cursor.getLong(idxWhen) : 0L;
                    String whenStr = whenMs > 0 ? df.format(new Date(whenMs)) : "";
                    items.add(new EventItem(title, whenStr));
                } while (cursor.moveToNext());
            }

            adapter.notifyDataSetChanged();
            textInfo.setText("Pronađeno događaja: " + items.size());
        } catch (SecurityException se) {
            Toast.makeText(this, "Nema dozvolu za čitanje kalendara", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_READ_CALENDAR) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadEvents();
            } else {
                Toast.makeText(this, "Dozvola odbijena", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class EventItem {
        String title;
        String when;
        EventItem(String t, String w) { title = t; when = w; }
    }
}
```

---

## 6) SMS — `SmsActivity.java`

Manifest: dodaj `READ_SMS`.

Layout i item su slični prethodnima; u item stavljaš broj i telo poruke.

Activity kod (kopiraj u `SmsActivity.java`):

```java
package com.example.vezba2klk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SmsActivity extends AppCompatActivity {
    private static final int REQ_READ_SMS = 301;
    private ListView listSms;
    private TextView textInfo;
    private ArrayAdapter<SmsItem> adapter;
    private List<SmsItem> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        listSms = findViewById(R.id.listSms);
        textInfo = findViewById(R.id.textInfo);

        adapter = new ArrayAdapter<SmsItem>(this, 0, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_sms, parent, false);
                }
                TextView from = convertView.findViewById(R.id.textFrom);
                TextView body = convertView.findViewById(R.id.textBody);
                SmsItem it = getItem(position);
                from.setText(it.from != null ? it.from : "");
                body.setText(it.body != null ? it.body : "");
                return convertView;
            }
        };
        listSms.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    REQ_READ_SMS);
        } else {
            loadSms();
        }
    }

    private void loadSms() {
        items.clear();
        Cursor cursor = null;
        try {
            Uri uri = Telephony.Sms.CONTENT_URI; // content://sms
            cursor = getContentResolver().query(uri,
                    new String[]{Telephony.Sms._ID, Telephony.Sms.ADDRESS, Telephony.Sms.BODY},
                    null, null, Telephony.Sms.DATE + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                int idxAddr = cursor.getColumnIndex(Telephony.Sms.ADDRESS);
                int idxBody = cursor.getColumnIndex(Telephony.Sms.BODY);
                do {
                    String from = idxAddr >= 0 ? cursor.getString(idxAddr) : null;
                    String body = idxBody >= 0 ? cursor.getString(idxBody) : null;
                    items.add(new SmsItem(from, body));
                } while (cursor.moveToNext());
            }
            adapter.notifyDataSetChanged();
            textInfo.setText("Poruka: " + items.size());
        } catch (SecurityException se) {
            Toast.makeText(this, "Nema dozvolu za čitanje SMS", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_READ_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSms();
            } else {
                Toast.makeText(this, "Dozvola odbijena", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class SmsItem {
        String from;
        String body;
        SmsItem(String f, String b) { from = f; body = b; }
    }
}
```

---

## 7) Testiranje na emulatoru (adb)

- Dodavanje kontakta:

```
adb shell am start -a android.intent.action.INSERT -t vnd.android.cursor.item/contact --es name "Test User" --es phone "+381641234567"
```

- Dodavanje kalendarskog događaja (teže direktno preko adb, lakše je dodati kroz Calendar app UI)

- Dodavanje SMS poruke u emulator (putem `adb` nije standardno; lakše pošaljite SMS emulatoru iz Android Studio emulator kontrola ili koristite telnet/sms commands ako emulator podržava)

---

## 8) Copy & change: šta menjaš kada prebacuješ na drugi provider

- URI: promeni `CONTENT_URI` konstantu (ContactsContract, CalendarContract, Telephony)
- Projection kolone: ako tražiš druga polja, navedi ih eksplicitno
- Selection/selectionArgs: koristi WHERE ako ti treba filter
- Permissions: promeni permission koji tražiš (`READ_CONTACTS`, `READ_CALENDAR`, `READ_SMS`)
- Layout: prilagodi `item_*.xml` da prikažeš kolone koje si dobio

---

## 9) FAQ i česte greške

- Q: "Cursor je null" → A: Proveri da li imaš dozvolu; query vraća null ako nema prava.
- Q: "Ne vidim podatke u emulatoru" → A: Emulator možda nema kontakte/SMS; dodaj ih ručno ili pomoću adb.
- Q: "Zašto koristimo getColumnIndex*?" → A: da bi pronašli indeks kolone pre čitanja; `getColumnIndexOrThrow` baca grešku ako kolona ne postoji.
- Q: "Da li moram zatvarati Cursor?" → A: Da, uvek; koristi `finally` ili try-with-resources.

---

## 10) Lista fajlova koje sam izmenio/dodala u ovom zadatku

- Izmenjeno:
  - `README.md` – dodat detaljan Vežbe 6 vodič (priručnik i cheat-sheet)

- Novi fajl koji sam dodala sada:
  - `CONTENT_PROVIDER_GUIDE.md` (ovaj fajl)

> Napomena: Ako želiš da ja ubacim stvarne Java fajlove (`ContactsActivity.java`, `CalendarActivity.java`, `SmsActivity.java`) u projekt, mogu to da uradim — potvrdi i ja ću kreirati te fajlove direktno u `app/src/main/java/com/example/vezba2klk/` i layout fajlove u `app/src/main/res/layout/`.

---

Ako želiš da automatski ubacim sve pomenute klase i layout-e u tvoj projekat sada (da bude spremno za kopiranje / pokretanje), napiši "DA — ubaci fajlove" i ja ću ih dodati u projekat (i potom pokrenuti proveru grešaka). Ako želiš samo Java fajlove bez layout-a ili obrnutno, reci tačno šta želiš.

Srećno — ako želiš, sad odmah ubacujem fajlove u projekat.
