# Media Ratings Platform (MRP) - Intermediate Submission Protocol

## Architekturmuster
Schichtbasierte Architektur mit klarer Trennung der Verantwortlichkeiten: Client → Server → Handler → Router → Controller → Service → Model

## Package-Struktur
- at.technikum/
- application/
  - common/ (Application Interface, Router)
  - controller/ (HTTP Request Handler)
  - model/ (Datenmodelle)
  - service/ (Geschäftslogik)
  - MainApplication (Hauptanwendung)
- server/
  - http/ (HTTP Komponenten)
  - util/ (Hilfsklassen)
  - Main (Server-Einstiegspunkt)

## Implementierungsdetails

### HTTP Server Implementation
- Server-Klasse: Verwaltet HTTP Server auf Port 8080
- Handler-Klasse: Verarbeitet eingehende HTTP Requests
- RequestMapper: Transformiert HttpExchange in Request-Objekte
- Response-Handling: Korrekte HTTP Status Codes und Content-Types

### Authentifizierungssystem
- Token-basierte Authentifizierung gemäß Spezifikation
- SHA-256 Password Hashing für Passwort-Speicherung
- Bearer Token im Format: username-mrpToken-[UUID]
- Singleton AuthService für zentrale Authentifizierungslogik

## REST Endpoints (Intermediate)

### User Management
- POST /users/register - Benutzerregistrierung
- POST /users/login - Benutzeranmeldung mit Token-Rückgabe
- GET /users/profile - Profilabfrage (token-geschützt)
- POST /users/logout - Benutzerabmeldung

### Media Management
- POST /media - Media Entry erstellen (token-geschützt)
- GET /media - Alle Media Entries abrufen
- GET /media/{id} - Spezifischen Media Entry abrufen
- PUT /media/{id} - Media Entry aktualisieren (nur Creator)
- DELETE /media/{id} - Media Entry löschen (nur Creator)

## Datenmodelle

### User
- username, password (gehasht)

### MediaEntry
- id, title, description, mediaType, releaseYear, genres, ageRestriction, creatorUsername

### AuthRequest/AuthResponse
- Für Login/Registrierung Requests und Responses

## Aufgetretene Probleme und Lösungen

### Problem 1: Token Validation in Controllers
**Problem:** Wiederholter Validierungscode in MediaController und UserController  
**Lösung:** TokenUtility Klasse mit wiederverwendbaren Methoden

### Problem 2: ID-Generierung für MediaEntries
**Problem:** Inkonsistente ID-Generierung beim Erstellen und Updaten  
**Lösung:** Zentrale ID-Generierung im Konstruktor mit Fallback im Setter

### Problem 3: Singleton Service Instanzen
**Problem:** Mehrere Service-Instanzen führten zu Inkonsistenzen  
**Lösung:** Singleton Pattern für AuthService und MediaService

### Problem 4: Path Parameter Extraction
**Problem:** Komplexe ID-Extraktion aus URL-Pfaden  
**Lösung:** extractIdFromPath() Hilfsmethode in MediaController

## Geschätzte Zeitaufwände

| Komponente | Geschätzte Zeit | Tatsächliche Zeit |
|------------|------------------|-------------------|
| Projekt-Setup | 1h | 1h |
| HTTP Server Grundgerüst | 4h | 5h |
| Router & Controller Structure | 3h | 4h |
| Authentication System | 6h | 8h |
| Media Management | 5h | 6h |
| Model Klassen | 3h | 2h |
| Dokumentation | 3h | 4h |
| **Gesamt** | **25h** | **30h** |

## GitHub Repository
Repository URL: https://github.com/mrfunilp/MRP-Projekt-EmilCeric