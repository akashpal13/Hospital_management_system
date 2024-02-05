package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagemant {
    private static final String url="jdbc:mysql://localhost:3306/hospital";
    private static final String username="root";
    private static final String password="root";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        Scanner scanner=new Scanner(System.in);
        try{
            Connection connection= DriverManager.getConnection(url,username,password);
            Patient patient=new Patient(connection,scanner);
            Doctor doctor=new Doctor(connection);

            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient: ");
                System.out.println("2. View Patient: ");
                System.out.println("3. View Doctors: ");
                System.out.println("4. Book Appointment: ");
                System.out.println("5. Exit");
                System.out.println("+---------------------------------------------------------------------");

                System.out.println("Enter Your Choice");
                int choice=scanner.nextInt();
                switch (choice){
                    case 1:
                        //add patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        // view Patient
                        patient.viewPatient();
                        System.out.println();
                        break;
                    case 3:
                        //view Doctors
                        doctor.viewDoctor();
                        System.out.println();
                        break;
                    case 4:
                        //book appointment
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        System.out.println(" Thank for You Hospital Management System !!! ");
                        return;
                    default:
                        System.out.println("Enter valid choice");

                }


            }
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }
    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner){
        System.out.print("Enter Patient Id: ");
        int patientId=scanner.nextInt();
        System.out.print("Enter Doctor Id: ");
        int doctorId=scanner.nextInt();

        System.out.print("Enter appointment date(YYYY-MM-DD): ");
        String appointmentDate=scanner.next();
        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDoctorAvailibility(doctorId,appointmentDate,connection)) {
                String appointmentQuery="insert into appointments(patient_id,doctor_id,appointment_date) values(?,?,?)";
                try{
                    PreparedStatement preparedStatement=connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1,patientId);
                    preparedStatement.setInt(2,doctorId);
                    preparedStatement.setString(3,appointmentDate);

                    int rowAffected=preparedStatement.executeUpdate();

                    if(rowAffected>0){
                        System.out.println("Appointment Booked!!");
                    } else{
                        System.out.println("failed to Book Appointment");
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            } else {
                System.out.println("Doctor not available on this date");
            }
        } else{
            System.out.println("Either doctor or patient not present");
        }
    }

    public static boolean checkDoctorAvailibility(int doctorId,String appointmentdate,Connection connection) {
        String query = "select count(*) from appointments where doctor_id=? and appointment_date=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentdate);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
