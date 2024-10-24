import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

public class upgradedTP {
    // Method to grade students and generate statistics
    private static void grading(File roster, Scanner in) throws IOException {
        Scanner reader = new Scanner(roster);
        reader.nextLine();  // Skip header

        int studentCount = 0;
        // Count the number of students in the roster
        while (reader.hasNextLine()) {
            studentCount++;
            reader.nextLine();
        }

        String[][] studentData = new String[studentCount][3];  // 2D array to store ID, Name, and Grade
        
        reader.close();
        reader = new Scanner(roster);
        reader.nextLine();  // Skip header again

        for (int i = 0; i < studentCount; i++) {
            String student = reader.nextLine();
            String[] parts = student.split("\t");
            studentData[i][0] = parts[0];  // Store Student ID
            studentData[i][1] = parts[1];  // Store Student Name

            int grade = 0;
            boolean validInput = false;
            // Loop to get a valid grade from the user
            while (!validInput) {
                System.out.println("What grade would you like to give: " + student);
                try {
                    grade = in.nextInt();
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid integer.");
                    in.next();  // Clear invalid input
                }
            }
            studentData[i][2] = String.valueOf(grade);  // Store grade
        }

        // Calculate statistics
        double mean = calculateMean(studentData);
        int highest = findHighestGrade(studentData);
        int lowest = findLowestGrade(studentData);
        int passCount = countPassingStudents(studentData);
        double median = calculateMedian(studentData);

        System.out.println("Highest Grade: " + highest);
        System.out.println("Lowest Grade: " + lowest);
        System.out.println("Mean Grade: " + mean);
        System.out.println("Median Grade: " + median);
        System.out.println("Number of students who passed: " + passCount);

        // Sort students by grade in descending order
        Arrays.sort(studentData, (a, b) -> Integer.compare(Integer.parseInt(b[2]), Integer.parseInt(a[2])));  

        // Output grades to file
        try (PrintWriter out = new PrintWriter("./output/FinalGrades.tsv")) {
            out.println("StudentID\tName\tGrade");
            for (String[] student : studentData) {
                out.println(String.join("\t", student));  // Print each student's data
            }
        }
    }

    // Method to calculate mean grade
    private static double calculateMean(String[][] studentData) {
        int total = 0;
        for (String[] student : studentData) {
            total += Integer.parseInt(student[2]);  // Sum up grades
        }
        return (double) total / studentData.length;  // Return mean
    }

    // Method to find highest grade
    private static int findHighestGrade(String[][] studentData) {
        int highest = Integer.MIN_VALUE;
        for (String[] student : studentData) {
            highest = Math.max(highest, Integer.parseInt(student[2]));  // Determine highest grade
        }
        return highest;
    }

    // Method to find lowest grade
    private static int findLowestGrade(String[][] studentData) {
        int lowest = Integer.MAX_VALUE;
        for (String[] student : studentData) {
            lowest = Math.min(lowest, Integer.parseInt(student[2]));  // Determine lowest grade
        }
        return lowest;
    }

    // Method to count passing students
    private static int countPassingStudents(String[][] studentData) {
        int count = 0;
        for (String[] student : studentData) {
            if (Integer.parseInt(student[2]) > 70) {  // Count grades above 70
                count++;
            }
        }
        return count;
    }

    // Method to calculate median grade
    private static double calculateMedian(String[][] studentData) {
        int[] grades = Arrays.stream(studentData)
                             .mapToInt(s -> Integer.parseInt(s[2]))
                             .sorted()
                             .toArray();  // Sort grades
        if (grades.length % 2 == 0) {
            return (grades[grades.length / 2 - 1] + grades[grades.length / 2]) / 2.0;  // Median for even count
        } else {
            return grades[grades.length / 2];  // Median for odd count
        }
    }

    // Method to record attendance
    private static void attendance(File roster, Scanner in) throws IOException {
        Scanner reader = new Scanner(roster);
        reader.nextLine();  // Skip header

        ArrayList<String> attended = new ArrayList<>();
        ArrayList<String> absent = new ArrayList<>();

        while (reader.hasNextLine()) {
            String student = reader.nextLine();
            System.out.printf("Is %s in class? 'x' for yes. Hit Enter for no.\n", student);
            String response = in.nextLine().toLowerCase();

            if (response.equals("x")) {
                attended.add(student);  // Add to attended list
            } else {
                absent.add(student);  // Add to absent list
            }
        }

        // Calculate attendance percentage
        double attendancePercentage = (double) attended.size() / (attended.size() + absent.size()) * 100;

        // Output attendance report to file
        try (PrintWriter out = new PrintWriter("./output/Attendance.tsv")) {
            out.println("Attendance Report");
            out.println("Attended Students:");
            for (String student : attended) {
                out.println(student);  // List attended students
            }

            out.println("Absent Students:");
            for (String student : absent) {
                out.println(student);  // List absent students
            }

            out.printf("Attendance Percentage: %.2f%%\n", attendancePercentage);  // Print attendance percentage
        }
    }

    // Method to modify the roster
    private static void modify(File roster, Scanner in) throws IOException {
        String student; 
        String choice;
        boolean escape;

        PrintWriter out = new PrintWriter("./output/ModifiedRoster.tsv");
        Scanner reader = new Scanner(roster);

        reader.nextLine(); 
        out.println("StudentID\tName");  // Write header for modified roster

        while (reader.hasNextLine()) {
            student = reader.nextLine();

            do { 
                escape = false;

                System.out.printf("Is %s still on the roster? 'y' for yes. 'n' for no.\n", student);
                choice = in.nextLine().toLowerCase();

                if (!choice.equals("y") && !choice.equals("n")) {
                    System.out.println("Enter valid input! Try again.");
                    escape = true;
                }

            } while (escape);
            
            if (choice.equals("y")) {
                out.println(student);  // Add student to modified roster if still on it
            }
        }

        do { 
            escape = false;

            System.out.println("Would you like to append to your new roster? 'y' for yes. 'n' for no.");
            choice = in.nextLine().toLowerCase();

            if (!choice.equals("y") && !choice.equals("n")) {
                System.out.println("Enter valid input! Try again.");
                escape = true;
            }
            
        } while (escape);

        if (choice.equals("y")) {
            do { 
                int id;
                String name;
                boolean validInput = false;
                escape = false; 
                student = "";

                // Get valid StudentID input
                while (!validInput) {
                    System.out.println("Please enter a StudentID for the new student.");
                    try {
                        id = in.nextInt();
                        if (id > 1000 && id < 9999) {
                            student = Integer.toString(id);  // Store valid ID
                            validInput = true;  
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a valid integer.");
                        in.next(); 
                    }
                }  

                in.nextLine();  // Clear input buffer

                System.out.println("Enter the new student's Name.");
                name = in.nextLine();
                student += "\t" + name;  // Append name to student data

                System.out.println(student + " added...");
                out.println(student);  // Write new student to file

                do { 
                    escape = false; 

                    System.out.println("Would you like to stop appending to your new roster? 'y' for yes. 'n' for no.");
                    choice = in.nextLine().toLowerCase();

                    if (!choice.equals("y") && !choice.equals("n")) {
                        System.out.println("Enter valid input! Try again.");
                        escape = true;
                    }
                } while (escape);

                escape = !choice.equals("y");  // Determine whether to continue adding students

            } while (escape);
        }

        out.close();  // Close output file
    }

    public static void main(String[] args) throws IOException {
        boolean passed; 
        File roster; 
        
        roster = new File("./output/ModifiedRoster.tsv"); 
        Scanner in = new Scanner(System.in);

        if (!roster.exists()) {
            roster = new File("./output/Roster.tsv");  // Use default roster if modified one doesn't exist
        }

        grading(roster, in);  // Call grading method
        attendance(roster, in);  // Call attendance method
        modify(roster, in);  // Call modify method

        in.close();  // Close scanner
    }
}
