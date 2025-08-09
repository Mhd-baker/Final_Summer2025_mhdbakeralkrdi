package com.mhdbaker;

import java.sql.*;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:oracle:thin:@199.212.26.208:1521:SQLD";
    private static final String DB_USER = "COMP214_M25_ers_43";
    private static final String DB_PASS = "password";

    // Insert applicant and return generated applicant_id (or -1 if failed)
    public static int insertApplicant(String fullname, String contact, String education, java.sql.Date appliedDate, double salary) {
        int generatedId = -1;
        String sql = "INSERT INTO ApplicantTable (fullname, contact, education, date_applied, salary) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"APPLICANT_ID"})) {

            stmt.setString(1, fullname);
            stmt.setString(2, contact);
            stmt.setString(3, education);
            stmt.setDate(4, appliedDate);
            stmt.setDouble(5, salary);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                // Get generated primary key value
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
                rs.close();
                System.out.println("Applicant inserted successfully with ID: " + generatedId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedId;
    }

    // Insert employment record linked to applicant_id
    public static void insertEmployment(int applicantId, String companyName, String jobTitle, java.sql.Date startDate, java.sql.Date endDate) {
        String sql = "INSERT INTO EmploymentTable (applicant_id, company_name, job_title, start_date, end_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, applicantId);
            stmt.setString(2, companyName);
            stmt.setString(3, jobTitle);
            stmt.setDate(4, startDate);
            stmt.setDate(5, endDate);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Employment record inserted successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
