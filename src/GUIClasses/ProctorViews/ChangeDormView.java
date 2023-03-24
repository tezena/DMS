package GUIClasses.ProctorViews;

import BasicClasses.Others.JavaConnection;
import BasicClasses.Persons.Proctor;
import BasicClasses.Persons.Student;
import GUIClasses.ActionListeners.ProctorView.ChangeBackButtonListener;
import GUIClasses.ActionListeners.ProctorView.ChangeDormView.*;
import GUIClasses.Interfaces.Views;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangeDormView extends JFrame implements Views {
    private JPanel mainPanel;
    private JComboBox conditions;
    private JFormattedTextField fromBuildingNoTF;
    private JFormattedTextField searchTF;
    private JFormattedTextField toBuildingNoTF;
    private JFormattedTextField toRoomNoTF;
    private JLabel toRoomNoL;
    private JLabel conditionsL;
    private JLabel searchStudentL;
    private JButton backButton;
    private JButton changeButton;
    private JLabel numberOfStudentsL;
    private JFormattedTextField yearTF;
    private JLabel noOfStudentsL;
    private JLabel fromBuildingNoL;
    private JLabel availableSpaceL;
    private Proctor proctor;
    private Student student; //Only for the single student change;
    private DormitoryView parentComponent;
    public ChangeDormView(Proctor proctor, DormitoryView parentComponent){
        this.proctor = proctor;
        this.parentComponent = parentComponent;
        setUpGUi();
    }
    public void showParentComponent(){
        parentComponent.setVisible(true);
    }

    public String getSelectedCondition(){
        return (String) conditions.getSelectedItem();
    }

    public String getBuildingNo(){
        return fromBuildingNoTF.getText();
    }
    public String getDestinationBuildingNo(){
        return toBuildingNoTF.getText();
    }
    public String getDestinationRoomNo(){
        return toRoomNoTF.getText();
    }
    public int getNoOfStudent(String query){
        //This function will return the number of
        // students that satisfy the condition selected in the condition's comboBox.
        JavaConnection javaConnection = new JavaConnection(JavaConnection.url,JavaConnection.user,JavaConnection.password);
        ResultSet resultSet = null;
        int numberOfStudents = 0;
        if(javaConnection.isConnected()){
            resultSet = javaConnection.selectQuery(query);
        }
        try{
            while(resultSet.next()){
                numberOfStudents = resultSet.getInt("numberOfStudents"); // There will only be one row.
            }
        } catch (SQLException ex){
            ex.printStackTrace(); // For debugging.
        }
        return numberOfStudents;
    }
    public boolean setStudentIfFound(){
        String SID = getSid();
        String query = "SELECT * FROM STUDENT WHERE SID='"+SID+"'";
        String fname = null;
        JavaConnection javaConnection = new JavaConnection(JavaConnection.url,JavaConnection.user,JavaConnection.password);
        ResultSet resultSet = null;
        boolean studentIsFound = false;
        if(javaConnection.isConnected()){
            resultSet = javaConnection.selectQuery(query);
            try{
                while(resultSet.next()){
                    fname = resultSet.getString("Fname");
                    studentIsFound = !(fname == null);
                    System.out.println("Student is found: "+studentIsFound); //Remove after debugging.
                    if(studentIsFound){
                        student = new Student(fname,resultSet.getString("Lname"),
                                SID,resultSet.getString("Gender"));
                        student.setDormNo(resultSet.getString("RoomNumber"));
                        student.setBuildingNo(resultSet.getString("BuildingNumber"));
                    }
                }
            } catch (SQLException ex){
                ex.printStackTrace();//For debugging only.
                System.out.println("Inside the getStudent catch block");
            }
        }
        return  studentIsFound; //Returns true if the student is found.

    }
    public Student getStudent(){
        return student;
    }
    public void setStudent(Student student){this.student = student;}
    public void updateViewOnCondition(boolean singleStudent){
        /*
        This method will set the invisible components to visible and
        vice versa according to the selected condition in the comboBox.
         */
        numberOfStudentsL.setVisible(!singleStudent);
        noOfStudentsL.setVisible(!singleStudent);
        yearTF.setVisible(!singleStudent);
        fromBuildingNoTF.setVisible(!singleStudent);
        fromBuildingNoL.setVisible(!singleStudent);
        searchStudentL.setVisible(singleStudent);
        searchTF.setVisible(singleStudent);
        toRoomNoTF.setVisible(singleStudent);
        toRoomNoL.setVisible(singleStudent);

        yearTF.setText("");
        fromBuildingNoTF.setText("");
        toBuildingNoTF.setText("");
        toRoomNoTF.setText("");
    }

    public void setNumberOfStudentsL(int noOfStudent){
        numberOfStudentsL.setText(String.valueOf(noOfStudent));
    }

    public int getYear(){
        try{
           return Integer.parseInt(yearTF.getText());
        } catch (NumberFormatException ex){
            return 0;
        }
    }

    public void setAvailableSpaceLText(int availableSpace) {
        availableSpaceL.setText(String.valueOf(availableSpace));
    }

    public String getSid(){
        return searchTF.getText();
    }

    public void makeParentVisible(){
        parentComponent.setVisible(true);
    }

    @Override
    public void setUpGUi() {
        this.setTitle("Change Dormitory");
        this.setSize(500,300);
        this.setContentPane(mainPanel);
        this.setLocationRelativeTo(parentComponent);
        backButton.addActionListener(new ChangeBackButtonListener(this));
        conditions.addItemListener(new ConditionItemChangedListener(this));
        searchTF.addFocusListener(new SearchTFListener(this));
        yearTF.addFocusListener(new YearTFListener(this));
        changeButton.addActionListener(new ChangeButtonListener(this));
        toBuildingNoTF.addFocusListener(new ToBuildingNoTFListener(this));
        fromBuildingNoTF.addFocusListener(new FromBuildingNoTFListener(this));
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                e.getWindow().dispose();
                parentComponent.setVisible(true);
            }
        }); //A custom action listener for the exit button.

        this.setVisible(true);

        conditions.addItem("Change Batch of students");
        conditions.addItem("Change single student");

        this.pack();
    }

    public Proctor getProctor() {
        return proctor;
    }
}
