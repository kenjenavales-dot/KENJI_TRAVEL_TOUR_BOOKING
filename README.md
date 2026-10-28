# Travel Tour Booking System

A Maven-based Java CLI starter project using SQLite.

## Requirements
- JDK 17 or newer
- Maven (or IntelliJ IDEA with Maven support)

## Run in IntelliJ
1. Extract the ZIP.
2. Open the `TravelTourBookingSystem` folder in IntelliJ IDEA.
3. Allow Maven to load dependencies.
4. Run `src/main/java/com/yourdomain/Main.java`.

The SQLite database file `travel_tour_booking.db` is created in the project's working directory.

## Roles
- ADMIN
- STAFF
- CUSTOMER

New registrations are stored with `PENDING` status. For demonstration, an ADMIN account must be activated in the database before it can access the system. You can change a user's status to `ACTIVE` using DB Browser for SQLite.

## Important
This is a starter implementation for the deliverable. Review it against the exact roles and features in your approved System Proposal before submitting. Do not submit screenshots until you have run the project successfully yourself.
