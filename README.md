# OTHivity

Our idea is to build a platform for our university, OTH Regensburg, where students can post activities,
connect with others and find new people to engage with.

## Activity API Documentation

**Base URL:** `/api/activities`

This API uses **JWT (JSON Web Token)** for authentication:

1.  **Get your Token:** You can generate your personal JWT in the application GUI under **Settings**.
2.  **Usage:** The token must be included in the HTTP `Authorization` header with the `Bearer` schema.

**Header Example:**
```http
Authorization: Bearer <YOUR_JWT_TOKEN>
```

### Data Model: `ActivityApiDto`

| Field         | Type              | Description                                                        | Create   | Update                              |
|---------------|-------------------|--------------------------------------------------------------------|----------|-------------------------------------|
| `id` | `String (UUID)` | activity identifier (response only)                                | -        | -                                   |
| `title` | `String` | activity title                                                     | required | required                            |
| `description` | `String` | activity description                                               | required | required                            |
| `date` | `String (ISO-8601)` | activity date example: `2025-01-15T18:30:00`.                      | required | required                            |
| `language` | `String (Enum)` | activity language one of: `ENGLISH`, `GERMAN`, `FRENCH`, `SPANISH` | required | required                            |
| `groupSize` | `int` | activity groupsizee must be ≥ 2                                    | required | required                            |
| `organizerId` | `String (UUID)` | club as orgaizer must refer to an existing club                    | optional | optional                            |
| `imageUrl` | `String` | activity image                                                     | required | required                            |
| `tags` | `String[]` | activity tagse one of: (e.g. `OUTDOOR`, `SPORTS`)                  | optional | optional                                 |
| `startedBy` | `String (UUID)` | activity starter = profile uuid                                    | -        | required                            |
| `takePart` | `String[] (UUID)` | activity participants = prrofil uuids                              | optional | required + must include `startedBy` |
| `addition` | `String` | address addition                                                   | optional | optional                            |
| `street` | `String` | address street                                                     | required      | required                                 |
| `houseNumber` | `String` | address housenumber                                                | required      | required                                 |
| `postalCode` | `String` | address postal code                                                | required      | required                                 |
| `city` | `String` | address city                                                       | required      | required                                 |
| `country` | `String` | address country                                                    | optional | optional                            |
| `latitude` | `String` | adress latitude (response only).                                   | -        | -                                   |
| `longitude` | `String` | adress longitude (response only).                                  | -        | -                                   |

#### ENUMS
- `language`:
    - `ENGLISH`
    - `GERMAN`
    - `FRENCH`
    - `SPANISH`
- `tags`:
    - `OUTDOOR` 🌲
    - `INDOOR` 🏠
    - `SPORTS` 🏃
    - `LEARNING` 📚
    - `PARTY` 🎉
    - `MUSIC` 🎶
    - `FOOD` 🍽️
    - `TRAVEL` ✈️
    - `HIKING` 🥾
    - `ART` 🎨
    - `RELAX` 🛋️
    - `VOLUNTEERING` 🤝
    - `BOARDGAME` 🎲
    - `GAMING` 🎮
    - `MOVIE` 🎬
    - `SOCIAL` 👥


### Endpoints

#### GET `/api/activities`

Returns all activities.

**Response:**  
`200 OK` → `List<ActivityApiDto>`

---

#### GET `/api/activities/{id}`

Returns one activity by its id.

**Response:**

`200 OK` → `ActivityApiDto`

`404 NOT FOUND` → Activity not found

---

#### POST `/api/activities`

Creates a new activity.

**Responses:**

`201 CREATED` -> `ActivityApiDto`

`400 BAD REQUEST` → Validation errors

`401 UNAUTHORIZED` → Authentication required

**Example Request:**
```json
{
  "title": "Hiking Trip",
  "description": "A fun group hike.",
  "date": "2025-05-21T10:00:00",
  "language": "ENGLISH",
  "groupSize": 10,
  "organizerId": "b32ef0c3-2b44-4f18-9cd5-6a93d9d67188",
  "imageUrl": "https://example.com/image.jpg",
  "tags": ["OUTDOOR", "HIKING"],
  "takePart": ["77d8ca72-0cd6-4a4a-9f70-4d923f6c1432"],
  "street": "Main Street",
  "houseNumber": "5A",
  "postalCode": "93047",
  "city": "Regensburg",
  "country": "Germany"
}
```
---

#### PUT `/api/activities/{id}`

Updates an existing activity.
Only the creator (`startedBy`) or users with the role `MODERATOR` are allowed to update.

**Responses:**

`200 SUCCESS` -> `ActivityApiDto`

`404 NOT FOUND` → Activity not found

`400 BAD REQUEST` → Validation errors

`401 UNAUTHORIZED` → Authentication required

**Example Request:**
```json
{
  "title": "Hiking Trip",
  "description": "A fun group hike.",
  "date": "2025-05-21T10:00:00",
  "language": "ENGLISH",
  "groupSize": 12,
  "organizerId": "b32ef0c3-2b44-4f18-9cd5-6a93d9d67188",
  "imageUrl": "https://example.com/image.jpg",
  "tags": ["OUTDOOR", "HIKING"],
  "startedBy": "77d8ca72-0cd6-4a4a-9f70-4d923f6c1432",
  "takePart": ["77d8ca72-0cd6-4a4a-9f70-4d923f6c1432"],
  "street": "Main Street",
  "houseNumber": "5A",
  "postalCode": "93047",
  "city": "Regensburg",
  "country": "Germany"
}
```
---

#### DELETE `/api/activities/{id}`
Deletes an existing activity.
Only the creator (`startedBy`) or users with the role `MODERATOR` are allowed to delete.

**Responses:**

`204 NO CONTENT` -> Activity deleted successfully

`404 NOT FOUND` → Activity not found

`401 UNAUTHORIZED` → Authentication required

---

## Architecture Concepts

Before starting to code, we entered the concept phase to carefully plan every aspect according to our needs.
The following examples, diagrams, and notes reflect our thought process during this stage.

### Database

The following diagram provides an overview of the database structure used in OTHivity.
It visualizes all core entities and the relationships between them.
![er diagram](./documentation/png/erDiagram.png)

### GET Request Example on `/dashboard`

The following diagram illustrates an HTTP GET request to `/dashboard`.  
The focus here is on the structure of the different services and how they interact with each other.

![GET request architecture](./documentation/svg/getRequestExample.svg)