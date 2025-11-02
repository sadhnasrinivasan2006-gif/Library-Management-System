import java.sql.*;
import java.util.Scanner;

public class LibraryManagementSystem {
    static final String DB_URL = "jdbc:mysql://localhost:3306/librarydb";
    static final String USER = "root"; // your MySQL username
    static final String PASS = "password"; // your MySQL password

    static Connection conn;

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to the database successfully.");

            Scanner scanner = new Scanner(System.in);
            int option;

            do {
                System.out.println("\nLibrary Management System");
                System.out.println("1. Add Book");
                System.out.println("2. Issue Book");
                System.out.println("3. Return Book");
                System.out.println("4. List All Books");
                System.out.println("5. Exit");
                System.out.print("Choose an option: ");
                option = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (option) {
                    case 1 -> addBook(scanner);
                    case 2 -> issueBook(scanner);
                    case 3 -> returnBook(scanner);
                    case 4 -> listBooks();
                    case 5 -> System.out.println("Exiting...");
                    default -> System.out.println("Invalid option! Please try again.");
                }
            } while (option != 5);

            scanner.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void addBook(Scanner scanner) throws SQLException {
        System.out.print("Enter Book ID: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Book Title: ");
        String title = scanner.nextLine();

        System.out.print("Enter Author: ");
        String author = scanner.nextLine();

        String query = "INSERT INTO books (book_id, title, author, status) VALUES (?, ?, ?, 'available')";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, bookId);
        pstmt.setString(2, title);
        pstmt.setString(3, author);

        int rows = pstmt.executeUpdate();
        if (rows > 0) {
            System.out.println("Book added successfully.");
        } else {
            System.out.println("Failed to add the book.");
        }
    }

    static void issueBook(Scanner scanner) throws SQLException {
        System.out.print("Enter Book ID to issue: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();

        String checkStatus = "SELECT status FROM books WHERE book_id = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkStatus);
        checkStmt.setInt(1, bookId);

        ResultSet rs = checkStmt.executeQuery();
        if (rs.next()) {
            String status = rs.getString("status");
            if (status.equalsIgnoreCase("available")) {
                String updateStatus = "UPDATE books SET status = 'issued' WHERE book_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateStatus);
                updateStmt.setInt(1, bookId);
                int rows = updateStmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Book issued successfully.");
                } else {
                    System.out.println("Error issuing the book.");
                }
            } else {
                System.out.println("Book is already issued.");
            }
        } else {
            System.out.println("Book not found.");
        }
    }

    static void returnBook(Scanner scanner) throws SQLException {
        System.out.print("Enter Book ID to return: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();

        String checkStatus = "SELECT status FROM books WHERE book_id = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkStatus);
        checkStmt.setInt(1, bookId);

        ResultSet rs = checkStmt.executeQuery();
        if (rs.next()) {
            String status = rs.getString("status");
            if (status.equalsIgnoreCase("issued")) {
                String updateStatus = "UPDATE books SET status = 'available' WHERE book_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateStatus);
                updateStmt.setInt(1, bookId);
                int rows = updateStmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Book returned successfully.");
                } else {
                    System.out.println("Error returning the book.");
                }
            } else {
                System.out.println("Book is not currently issued.");
            }
        } else {
            System.out.println("Book not found.");
        }
    }

    static void listBooks() throws SQLException {
        String query = "SELECT book_id, title, author, status FROM books";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        System.out.println("\nBooks in Library:");
        System.out.printf("%-10s %-30s %-20s %-10s\n", "Book ID", "Title", "Author", "Status");
        while (rs.next()) {
            System.out.printf("%-10d %-30s %-20s %-10s\n", rs.getInt("book_id"), rs.getString("title"), rs.getString("author"), rs.getString("status"));
        }
    }
}
