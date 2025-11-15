# OTHivity

Our idea is to build a platform for our university, OTH Regensburg, where students can post activities,
connect with others and find new people to engage with.

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