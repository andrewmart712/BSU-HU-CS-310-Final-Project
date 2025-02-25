import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This application will keep track of things like what classes are offered by
 * the school, and which students are registered for those classes and provide
 * basic reporting. This application interacts with a database to store and
 * retrieve data.
 */
public class SchoolManagementSystem {

    public static void getAllClassesByInstructor(String first_name, String last_name) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
            connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();

        	String sql = "SELECT"
        			+ "	first_name, last_name, title, code, classes.name AS class_name, terms.name AS term\n"
        			+ "FROM instructors\n"
        			+ "JOIN academic_titles ON academic_titles.academic_title_id = instructors.academic_title_id\n"
        			+ "JOIN class_sections ON class_sections.instructor_id = instructors.instructor_id\n"
        			+ "JOIN classes ON classes.class_id = class_sections.class_id\n"
        			+ "JOIN terms ON terms.term_id = class_sections.term_id\n"
        			+ "WHERE instructors.first_name = " + "\"" + first_name + "\"" + ";";
            ResultSet resultSet = sqlStatement.executeQuery(sql);
            
            System.out.println("First Name | Last Name | Title | Code | Name | Term");
            System.out.println("-".repeat(80));
            
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1) + " | "
                        + resultSet.getString(2) + " | " + resultSet.getString(3)
                        + " | " + resultSet.getString(4)
                        + " | " + resultSet.getString(5)
                        + " | " + resultSet.getString(6));
            }

            System.out.println("-".repeat(80));
            
        } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

    }

    public static void submitGrade(String studentId, String classSectionID, String grade) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
            connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();

            String sql = String.format("UPDATE class_registrations SET class_registrations.grade_id = " +
                    "(SELECT grades.grade_id FROM grades WHERE grades.letter_grade = '%s') " +
                    "WHERE class_registrations.student_id = '%s' AND class_registrations.class_section_id = '%s'; ",
                    grade, studentId, classSectionID);

            sqlStatement.executeUpdate(sql);

            ResultSet resultSet = sqlStatement.executeQuery("SELECT * FROM class_registrations");

            System.out.println("Grade has been submitted!");

        } catch (SQLException sqlException) {
            System.out.println("Failed to submit grade");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void registerStudent(String studentId, String classSectionID) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
            connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();

            String sql = String.format("INSERT INTO class_registrations (student_id, class_section_id) " +
                            "VALUES ('%s', '%s');",
                    studentId,
                    classSectionID);
            sqlStatement.executeUpdate(sql);

            ResultSet resultSet = sqlStatement.executeQuery("SELECT * FROM class_registrations");

            System.out.println("Class Registration ID | Student ID | Class Section ID");

            while (resultSet.next()) {
                if (resultSet.getString(2).equals(classSectionID)
                        && resultSet.getString(3).equals(studentId)) {
                    System.out.println(resultSet.getString(1) + " | "
                            + resultSet.getString(3) + " | " + resultSet.getString(2));
                }
            }

        } catch (SQLException sqlException) {
            System.out.println("Failed to register student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void deleteStudent(String studentId) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
             connection = Database.getDatabaseConnection();
             sqlStatement = connection.createStatement();

             String sql = String.format("DELETE FROM students WHERE student_id = %s;", studentId);

             sqlStatement.executeUpdate(sql);

             System.out.println("Student with id: " + studentId + " was deleted");

        } catch (SQLException sqlException) {
            System.out.println("Failed to delete student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    public static void createNewStudent(String firstName, String lastName, String birthdate) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
            connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();

            String sql = String.format("INSERT INTO students (first_name, last_name, birthdate) " +
                    "VALUES ('%s', '%s', '%s');",
                        firstName,
                        lastName,
                        birthdate);
            sqlStatement.executeUpdate(sql);

            ResultSet resultSet = sqlStatement.executeQuery("SELECT * FROM students");

            System.out.println("Student ID | First Name | Last Name | Birthdate");

            while (resultSet.next()) {
                if (resultSet.getString(2).equals(firstName)
                        && resultSet.getString(3).equals(lastName)) {
                    System.out.println(resultSet.getInt(1) + " | "
                            + resultSet.getString(2) + " | " + resultSet.getString(3)
                            + " | " + resultSet.getDate(4));
                }
            }

        } catch (SQLException sqlException) {
            System.out.println("Failed to create student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

    }

    public static void listAllClassRegistrations() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();

            String sql = "SELECT"
            		+ "	class_registrations.student_id, class_sections.class_section_id, first_name, last_name, code AS Code, classes.name AS Name, terms.name AS Term, letter_grade AS 'Letter Grade'\n"
            		+ "FROM class_registrations\n"
            		+ "JOIN class_sections ON class_sections.class_section_id = class_registrations.class_section_id\n"
            		+ "JOIN students ON students.student_id = class_registrations.student_id\n"
            		+ "JOIN terms ON terms.term_id = class_sections.term_id\n"
            		+ "LEFT JOIN grades ON grades.grade_id = class_registrations.grade_id\n"
            		+ "JOIN classes ON classes.class_id = class_sections.class_id;";
            ResultSet resultSet = sqlStatement.executeQuery(sql);

            System.out.println("Student ID | class_section_id | First Name | Last Name | Code | Name | Term | Letter Grade");
            System.out.println("-".repeat(80));

            while (resultSet.next()) {
                System.out.println(resultSet.getInt(1) + " | "
                        + resultSet.getInt(2) + " | " + resultSet.getString(3)
                        + " | " + resultSet.getString(4)
                        + " | " + resultSet.getString(5)
                        + " | " + resultSet.getString(6)
                        + " | " + resultSet.getString(7)
                        + " | " + resultSet.getString(8));
            }

        } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void listAllClassSections() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();

            String sql = "SELECT"
            		+ "	class_section_id, code, classes.name, terms.name AS term\n"
            		+ "FROM class_sections\n"
            		+ "JOIN classes ON classes.class_id = class_sections.class_id\n"
            		+ "JOIN terms ON terms.term_id = class_sections.term_id;;";
            ResultSet resultSet = sqlStatement.executeQuery(sql);

            System.out.println("Class Section ID | Code | Name | term");
            System.out.println("-".repeat(80));

            while (resultSet.next()) {
                System.out.println(resultSet.getInt(1) + " | "
                        + resultSet.getString(2) + " | " + resultSet.getString(3)
                        + " | " + resultSet.getString(4));
            }

        } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void listAllClasses() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();

            String sql = "SELECT * FROM classes;";
            ResultSet resultSet = sqlStatement.executeQuery(sql);

            System.out.println("Class ID | Code | Name | Description");
            System.out.println("-".repeat(80));

            while (resultSet.next()) {
                System.out.println(resultSet.getInt(1) + " | "
                        + resultSet.getString(2) + " | " + resultSet.getString(3)
                        + " | " + resultSet.getString(4));
            }
           
        } catch (SQLException sqlException) {
            System.out.println("Failed to get students");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    public static void listAllStudents() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
             connection = Database.getDatabaseConnection();
             sqlStatement = connection.createStatement();

             String sql = "SELECT * FROM students;";
             ResultSet resultSet = sqlStatement.executeQuery(sql);

             System.out.println("Student ID | First Name | Last Name | Birthdate");
             System.out.println("-".repeat(80));

             while (resultSet.next()) {
                 System.out.println(resultSet.getInt(1) + " | "
                         + resultSet.getString(2) + " | " + resultSet.getString(3)
                         + " | " + resultSet.getDate(4));
             }

        } catch (SQLException sqlException) {
            System.out.println("Failed to get students");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /***
     * Splits a string up by spaces. Spaces are ignored when wrapped in quotes.
     *
     * @param command - School Management System cli command
     * @return splits a string by spaces.
     */
    public static List<String> parseArguments(String command) {
        List<String> commandArguments = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command);
        while (m.find()) commandArguments.add(m.group(1).replace("\"", ""));
        return commandArguments;
    }

    public static void main(String[] args) {
        System.out.println("Welcome to the School Management System");
        System.out.println("-".repeat(80));

        Scanner scan = new Scanner(System.in);
        String command = "";

        do {
            System.out.print("Command: ");
            command = scan.nextLine();
            ;
            List<String> commandArguments = parseArguments(command);
            command = commandArguments.get(0);
            commandArguments.remove(0);

            if (command.equals("help")) {
                System.out.println("-".repeat(38) + "Help" + "-".repeat(38));
                System.out.println("test connection \n\tTests the database connection");

                System.out.println("list students \n\tlists all the students");
                System.out.println("list classes \n\tlists all the classes");
                System.out.println("list class_sections \n\tlists all the class_sections");
                System.out.println("list class_registrations \n\tlists all the class_registrations");
                System.out.println("list instructor <first_name> <last_name>\n\tlists all the classes taught by that instructor");


                System.out.println("delete student <studentId> \n\tdeletes the student");
                System.out.println("create student <first_name> <last_name> <birthdate> \n\tcreates a student");
                System.out.println("register student <student_id> <class_section_id>\n\tregisters the student to the class section");

                System.out.println("submit grade <studentId> <class_section_id> <letter_grade> \n\tcreates a student");
                System.out.println("help \n\tlists help information");
                System.out.println("quit \n\tExits the program");
            } else if (command.equals("test") && commandArguments.get(0).equals("connection")) {
                Database.testConnection();
            } else if (command.equals("list")) {
                if (commandArguments.get(0).equals("students")) listAllStudents();
                if (commandArguments.get(0).equals("classes")) listAllClasses();
                if (commandArguments.get(0).equals("class_sections")) listAllClassSections();
                if (commandArguments.get(0).equals("class_registrations")) listAllClassRegistrations();

                if (commandArguments.get(0).equals("instructor")) {
                    getAllClassesByInstructor(commandArguments.get(1), commandArguments.get(2));
                }
            } else if (command.equals("create")) {
                if (commandArguments.get(0).equals("student")) {
                    createNewStudent(commandArguments.get(1), commandArguments.get(2), commandArguments.get(3));
                }
            } else if (command.equals("register")) {
                if (commandArguments.get(0).equals("student")) {
                    registerStudent(commandArguments.get(1), commandArguments.get(2));
                }
            } else if (command.equals("submit")) {
                if (commandArguments.get(0).equals("grade")) {
                    submitGrade(commandArguments.get(1), commandArguments.get(2), commandArguments.get(3));
                }
            } else if (command.equals("delete")) {
                if (commandArguments.get(0).equals("student")) {
                    deleteStudent(commandArguments.get(1));
                }
            } else if (!(command.equals("quit") || command.equals("exit"))) {
                System.out.println(command);
                System.out.println("Command not found. Enter 'help' for list of commands");
            }
            System.out.println("-".repeat(80));
        } while (!(command.equals("quit") || command.equals("exit")));
        System.out.println("Bye!");
    }
}

