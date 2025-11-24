import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Single-file implementation of the Hotel Management System (HMS)
 * Demonstrates core functional modules: Room Management, Reservation, and basic Check-out/Billing.
 * NOTE: For a real-world project, use separate files and packages as discussed previously.
 */
public class Main {

    // --- 1. MODEL CLASSES (Data Entities) ---

    /**
     * Defines a room entity.
     */
    static class Room {
        private int roomNumber;
        private String type; 
        private double price;
        private boolean isBooked;

        public Room(int roomNumber, String type, double price) {
            this.roomNumber = roomNumber;
            this.type = type;
            this.price = price;
            this.isBooked = false;
        }

        // Getters
        public int getRoomNumber() { return roomNumber; }
        public String getType() { return type; }
        public double getPrice() { return price; }
        public boolean isBooked() { return isBooked; }

        // Setters
        public void setBooked(boolean isBooked) { this.isBooked = isBooked; }

        @Override
        public String toString() {
            return String.format("Room %d (%s) - Price: $%.2f, Status: %s", 
                roomNumber, type, price, (isBooked ? "Occupied" : "Available"));
        }
    }

    /**
     * Defines the customer entity.
     */
    static class Guest {
        private int guestId;
        private String name;
        private String contact;

        public Guest(int guestId, String name, String contact) {
            this.guestId = guestId;
            this.name = name;
            this.contact = contact;
        }

        // Getters
        public int getGuestId() { return guestId; }
        public String getName() { return name; }
        // ... other getters
    }

    /**
     * Links a guest to a room for a specific period.
     */
    static class Reservation {
        private int reservationId;
        private Guest guest;
        private Room room;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private boolean isActive;

        public Reservation(int reservationId, Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
            this.reservationId = reservationId;
            this.guest = guest;
            this.room = room;
            this.checkInDate = checkInDate;
            this.checkOutDate = checkOutDate;
            this.isActive = true;
        }
        
        // Getters/Setters
        public Room getRoom() { return room; }
        public int getReservationId() { return reservationId; }
        public Guest getGuest() { return guest; }
        public boolean isActive() { return isActive; }
        public LocalDate getCheckInDate() { return checkInDate; }
        public LocalDate getCheckOutDate() { return checkOutDate; }
        
        public void setActive(boolean isActive) { this.isActive = isActive; }
        
        public long getNumberOfNights() {
            return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        }
    }
    
    // --- 2. SERVICE CLASSES (Business Logic) ---

    /**
     * Manages the room inventory.
     */
    static class RoomService {
        private List<Room> rooms = new ArrayList<>();

        public RoomService() {
            // Initialize with sample rooms
            rooms.add(new Room(101, "Single", 50.00));
            rooms.add(new Room(102, "Double", 75.00));
            rooms.add(new Room(201, "Suite", 150.00));
            rooms.add(new Room(202, "Double", 75.00));
        }

        public List<Room> getAvailableRooms() {
            List<Room> available = new ArrayList<>();
            for (Room room : rooms) {
                if (!room.isBooked()) {
                    available.add(room);
                }
            }
            return available;
        }

        public Optional<Room> findRoom(int roomNumber) {
            return rooms.stream()
                        .filter(r -> r.getRoomNumber() == roomNumber)
                        .findFirst();
        }

        public void updateRoomStatus(int roomNumber, boolean isBooked) {
            findRoom(roomNumber).ifPresent(room -> room.setBooked(isBooked));
        }
    }

    /**
     * Handles the reservation logic.
     */
    static class ReservationService {
        private List<Reservation> reservations = new ArrayList<>();
        private RoomService roomService;
        private int nextReservationId = 1001;
        private int nextGuestId = 1;

        public ReservationService(RoomService roomService) {
            this.roomService = roomService;
        }

        public Reservation bookRoom(String guestName, String contact, int roomNumber, LocalDate checkIn, LocalDate checkOut) {
            Room room = roomService.findRoom(roomNumber).orElse(null);

            if (room == null || room.isBooked()) {
                System.err.println("Error: Room " + roomNumber + " is not available or does not exist.");
                return null;
            }

            Guest newGuest = new Guest(nextGuestId++, guestName, contact);
            Reservation newReservation = new Reservation(nextReservationId++, newGuest, room, checkIn, checkOut);
            
            roomService.updateRoomStatus(roomNumber, true);
            reservations.add(newReservation);
            System.out.println("\nSUCCESS: Booking made! Reservation ID: " + newReservation.getReservationId());
            System.out.println("Guest: " + newGuest.getName() + ", Room: " + room.getRoomNumber());
            return newReservation;
        }
        
        public Optional<Reservation> findActiveReservation(int reservationId) {
            return reservations.stream()
                .filter(r -> r.getReservationId() == reservationId && r.isActive())
                .findFirst();
        }

        // Returns true if checkout was successful
        public boolean checkOut(int reservationId) {
            Optional<Reservation> reservationOpt = findActiveReservation(reservationId);
            
            if (reservationOpt.isPresent()) {
                Reservation reservation = reservationOpt.get();
                reservation.setActive(false);
                reservation.getRoom().setBooked(false); 
                
                // Trigger Billing calculation
                double billAmount = BillingService.generateBill(reservation);
                System.out.printf("\nCHECKOUT SUCCESS: Reservation ID %d. Total Bill: $%.2f%n", 
                                  reservationId, billAmount);
                return true;
            } else {
                 System.err.println("\nERROR: Active reservation not found for ID: " + reservationId);
                 return false;
            }
        }
    }
    
    /**
     * Calculates the total charges for a reservation (Simplified Billing Module).
     */
    static class BillingService {
        private static final double TAX_RATE = 0.10; // 10% tax

        public static double generateBill(Reservation reservation) {
            long nights = reservation.getNumberOfNights();
            double roomCharge = reservation.getRoom().getPrice() * nights;
            double tax = roomCharge * TAX_RATE;
            double total = roomCharge + tax;

            System.out.println("\n--- INVOICE ---");
            System.out.println("Guest: " + reservation.getGuest().getName());
            System.out.printf("Room %d (%s) for %d nights%n", 
                              reservation.getRoom().getRoomNumber(), 
                              reservation.getRoom().getType(), nights);
            System.out.printf("Room Charges: $%.2f%n", roomCharge);
            System.out.printf("Tax (%.0f%%): $%.2f%n", TAX_RATE * 100, tax);
            System.out.println("-----------------");
            System.out.printf("TOTAL DUE: $%.2f%n", total);
            System.out.println("-----------------");
            
            return total;
        }
    }

    // --- 3. MAIN APPLICATION (User Interface) ---

    private static final RoomService roomService = new RoomService();
    private static final ReservationService reservationService = new ReservationService(roomService);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("--- Welcome to the Hotel Management System Console ---");
        boolean running = true;
        while (running) {
            displayMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        viewAvailableRoomsFlow();
                        break;
                    case 2:
                        bookRoomFlow();
                        break;
                    case 3:
                        checkOutFlow();
                        break;
                    case 4:
                        running = false;
                        System.out.println("Exiting System. Goodbye!");
                        break;
                    default:
                        System.err.println("Invalid choice. Please enter 1, 2, 3, or 4.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter a number for the menu choice.");
            }
        }
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n==================================");
        System.out.println("Select an action:");
        System.out.println("1. View Available Rooms");
        System.out.println("2. Book a Room");
        System.out.println("3. Check-Out Guest and Generate Bill");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void viewAvailableRoomsFlow() {
        System.out.println("\n--- Available Rooms ---");
        List<Room> available = roomService.getAvailableRooms();
        if (available.isEmpty()) {
            System.out.println("No rooms currently available.");
        } else {
            available.forEach(System.out::println);
        }
    }

    private static void bookRoomFlow() {
        try {
            System.out.print("\nEnter Guest Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Contact Number (e.g., 555-1234): ");
            String contact = scanner.nextLine();
            System.out.print("Enter Room Number to book: ");
            int roomNum = Integer.parseInt(scanner.nextLine());
            
            // Simplified date input. In a real project, use complex date parsing/validation.
            LocalDate checkIn = LocalDate.now();
            LocalDate checkOut = LocalDate.now().plusDays(3);
            System.out.printf("Booking dates assumed: %s to %s%n", checkIn, checkOut);

            reservationService.bookRoom(name, contact, roomNum, checkIn, checkOut);
            
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Invalid input for Room Number. Please enter an integer.");
        }
    }
    
    private static void checkOutFlow() {
        try {
            System.out.print("\nEnter Reservation ID for check-out: ");
            int resId = Integer.parseInt(scanner.nextLine());
            reservationService.checkOut(resId);
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Invalid input for Reservation ID. Please enter an integer.");
        }
    }
}