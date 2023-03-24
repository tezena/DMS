package GUIClasses.StudentViews;

import BasicClasses.Others.Cloth;
import BasicClasses.Others.JavaConnection;
import BasicClasses.Persons.Student;
import BasicClasses.Requests.ClothTakeOutRequest;
import GUIClasses.ActionListeners.ClothTakeOut.AddButtonListener;
import GUIClasses.ActionListeners.ClothTakeOut.FinishButtonListener;
import GUIClasses.Interfaces.RequestViews;
import GUIClasses.Interfaces.TableViews;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ClothTakeOutForm extends JFrame implements RequestViews, TableViews {
    private JButton addButton;
    private JButton finishButton;
    private JPanel mainPanel;
    private JPanel buttonPanel;
    private JPanel titlePanel;
    private JTable clothTable;
    private JLabel descriptionLabel;
    private JScrollPane scrollPane;
    private JPanel userInputPanel;
    private JTextField clothNameTF;
    private JTextField clothAmountTF;
    private JLabel clothNameL;
    private JLabel clothAmountL;
    private ClothTakeOutRequest clothList;
    private Vector<Vector<Object>> tableData;
    private Student student;
    private StudentPage parentComponent;
    private JavaConnection javaConnection;
    private int clothCount;
    public final int WIDTH = 500;
    public final int HEIGHT = 300;

    public ClothTakeOutForm(Student student,StudentPage parentComponent){
        javaConnection = new JavaConnection(JavaConnection.url,JavaConnection.user,JavaConnection.password);
        Integer lastRequestId = this.getLastClothRequestCount();
        clothList = new ClothTakeOutRequest(student.getsId(),lastRequestId);
        tableData = new Vector<>();
        this.student = student;
        this.parentComponent = parentComponent;
        clothCount = 0;
        setUpGUi();
        setUpTable();
    }
    @Override
    public void setUpGUi() {
        this.setTitle("Student cloth Take Out Form");
        this.setContentPane(mainPanel);
        this.setSize(WIDTH,HEIGHT);
        this.setLocationRelativeTo(parentComponent);
        this.setResizable(false);
        this.setVisible(true);
        this.addButton.addActionListener(new AddButtonListener(this));
        this.finishButton.addActionListener(new FinishButtonListener(this));
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                e.getWindow().dispose();
                parentComponent.setVisible(true);
            }
        }); //A custom action listener for the exit button.
    }

    @Override
    public void setUpTable(){
        Vector<String> title = new Vector<>();

        title.add("No");
        title.add("Cloth Name");
        title.add("Amount");
        clothTable.setModel(new DefaultTableModel(tableData,title));
        clothTable.setDefaultEditor(Object.class,null);  //This part is here for disabling the editing of the table.
        clothTable.getColumn("No").setMaxWidth(50);
    }

    public ClothTakeOutRequest getClothRequest() {
        return clothList;
    }
    @Override
    public void addDataToTable(Object object){
        Cloth cloth = (Cloth) object;
        Vector<Object> tmp = new Vector<>();
        tmp.add(clothList.getClothsList().size());
        tmp.add(cloth.getClothName());
        tmp.add(cloth.getClothAmount());
        tableData.add(tmp);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public Cloth getClothInfo() throws NumberFormatException{
        String name = JavaConnection.stripCotation(clothNameTF.getText());
        int amount = Integer.parseInt(clothAmountTF.getText());
        return new Cloth(name,amount);
    }

    public JTextField getClothAmountTF() {
        return clothAmountTF;
    }

    public JTextField getClothNameTF() {
        return clothNameTF;
    }

    @Override
    public void refreshTable(){
       DefaultTableModel tableModel = (DefaultTableModel) clothTable.getModel();
       tableModel.fireTableDataChanged();
    }

    public void clear(){
        clothNameTF.setText("");
        clothAmountTF.setText("");
    }

    public Integer updateDataBase() {
        Integer updateStatus = 0;
        int tmp1=0,tmp2=0;

        for (Cloth c : clothList.getClothsList()) { //Loop to update the ClothTakeOut table.
            String query = "INSERT INTO ClothTakeOut(reportCount,reportType,ClothName,Amount)" +
                            "VALUES('" +clothList.getRequestCount()+"','" +clothList.getRequestType()+"','"+ c.getClothName()+ "','" +
                    c.getClothAmount()+"');";
            System.out.println("Query: "+query);//For debugging purposes.
            if (javaConnection.isConnected()) tmp1 = javaConnection.insertQuery(query);//If query is successful the java connection returns 1.
            query = "INSERT INTO StudentTakesClothOut(ReporterId,ClothRequestId,reportedDate)" +
                    "VALUES('" +clothList.getRequesterId()+"','"+ getCurrentClothRequestId()+"','"+ clothList.getRequestedDate()+ "');";
            System.out.println("Query: "+query);//For debugging purposes.
            if(javaConnection.isConnected()) tmp2 = javaConnection.insertQuery(query);//If query is successful the java connection returns 1.
        }
        if(tmp1==1 & tmp2==1){//If both queries are successful.
            updateStatus = 1;
        }
        else updateStatus = 0;
        return updateStatus;
    }
    public void displayUpdateStatus(Integer updateStatus){
        if (updateStatus.equals(1))
            JOptionPane.showMessageDialog(null, "Request sent successfully", "Message sent", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(null, "Sorry couldn't send your request due to connection error", "Connection error", JOptionPane.ERROR_MESSAGE);
    }
    public Integer getLastClothRequestCount(){
        int lastRequestCount = 0;
        String query = "SELECT TOP 1 * FROM ClothTakeOut ORDER BY reportCount DESC, clothName DESC;";
        ResultSet resultSet = javaConnection.selectQuery(query);
        try{
            resultSet.next();
            String tmp = resultSet.getString("reportCount");
            lastRequestCount = Integer.parseInt(tmp);
            return lastRequestCount;
        }catch(SQLException ex){
            return 0;
        }
    }
    public Integer getCurrentClothRequestId(){
        String query = "SELECT LAST_VALUE(ReportId) OVER(ORDER BY reportCount) reportId FROM ClothTakeOut where reportCount="+clothList.getRequestCount();
        System.out.println("Query: "+query);//For debugging purposes.
        ResultSet tmp = javaConnection.selectQuery(query);
        int requestId = 0;
        try{
            if(tmp.next())
                requestId = tmp.getInt("ReportId");
        } catch (SQLException ex){
            ex.printStackTrace();
        }
        System.out.println("ReportId: "+requestId);//For debugging.
        return requestId;
    }
    public boolean checkSimilarNameCloth(Cloth cloth){
        for(Cloth c: clothList.getClothsList()){
            return (c.getClothName().equalsIgnoreCase(cloth.getClothName()));
        }
        return false;
    }
    public StudentPage getParentComponent(){
        return parentComponent;
    }
    public void showParentComponent(){
        parentComponent.setVisible(true);
    }
}
