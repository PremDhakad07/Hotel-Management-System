#  Hotel Management System (HMS) in Java 

## Project Title
**Hotel Management System (HMS)**

## Overview of the Project
The Hotel Management System is a console-based application built using **Java** that automates core hotel operations. It allows staff to efficiently manage room inventory, handle guest reservations (booking, check-in, check-out), and automatically generate billing invoices. This version demonstrates a compact implementation where all Model, Service, and Application logic are contained within a **Java file**, primarily for project submission purposes.

## Features
* **Room Management:** View status and details of all available and occupied rooms.
* **Reservation System:** Book rooms by assigning a room to a new guest and recording stay dates.
* **Check-out & Billing:** Process guest check-out, automatically calculate the total bill (including room charges and a standard 10% tax), and generate a detailed invoice.
* **Guest Management:** Basic storage of guest contact information linked to a reservation.

## Technologies/Tools Used
* **Language:** Java
* **Core Concepts:** Object-Oriented Programming (OOP) using nested static classes, Data Abstraction, Collections (`ArrayList`), Date/Time API (`java.time`).
* **Tooling:** Git for Version Control, GitHub for Repository Hosting.

## Steps to Install & Run the Project

1.  **Save the Code:** Ensure the complete Java code is saved as a single file named `Main.java`.
2.  **Clone the Repository (or download the file):**
    ```bash
    git clone https://github.com/PremDhakad07/Hotel-Management-System.git
    cd hotel-management-system
    ```
3.  **Compile the Java Code:**
    Compile the single file:
    ```bash
    javac Main.java
    ```
4.  **Run the Application:**
    Execute the main class:
    ```bash
    java Main
    ```
5.  The system will launch the main console menu for interaction.

## Instructions for Testing
To test the core functionality, follow these steps in sequence:

1.  **View Available Rooms (Menu Option 1):** Verify the initial list of available rooms (e.g., 101, 102, 201, 202).
2.  **Book a Room (Menu Option 2):**
    * Enter guest details and book an available room (e.g., Room 101).
    * Verify the system confirms the booking with a Reservation ID.
    * *Negative Test:* Attempt to book the same room (101) again to confirm the system correctly denies the request.
3.  **Check-Out and Billing (Menu Option 3):**
    * Enter the Reservation ID obtained from Step 2.
    * Verify the system generates a detailed **invoice**, including charges and tax (demonstrating the **Billing System** feature).
    * Check that the room (101) is now listed as **Available** again via Menu Option 1.

