package GUIClasses.ProctorViews;

import BasicClasses.Enums.SizeOfMajorClasses;
import BasicClasses.Others.JavaConnection;
import BasicClasses.Persons.Proctor;
import GUIClasses.Interfaces.TableViews;
import GUIClasses.Interfaces.Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class StockView extends JFrame implements Views, TableViews {
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel stockDetailPanel;
    private JLabel buildingNumberL;
    private JPanel historyPanel;
    private JTable stockHistoryTable;
    private JPanel buttonPanel;
    private ProctorPage parentComponent;
    private Proctor proctor;
    private boolean readStatus;
    private JButton backButton;
    private JLabel RoomNumL;
    private JLabel MatressL;
    private JLabel MatressBaseL;
    private JLabel pillowL;
    private Vector<Vector<Object>> tableData;
    private static final int WIDTH = SizeOfMajorClasses.WIDTH.getSize();
    private static final int HEIGHT = SizeOfMajorClasses.HEIGHT.getSize();

    public StockView(ProctorPage parentComponent,Proctor proctor){
        this.proctor = proctor;
        this.parentComponent = parentComponent;
        setUpGUi();
    }
    public Proctor getProctor(){
        return proctor;
    }
    public void setStockLables(String BuilNUm,String RooNum,Integer matress,Integer MatressBase,Integer pillows){
        buildingNumberL.setText(BuilNUm);
        RoomNumL.setText(RooNum);
        MatressL.setText(String.valueOf(matress));
        MatressBaseL.setText(String.valueOf(MatressBase));
        pillowL.setText(String.valueOf(pillows));


    }


    public void loadStock(){
        JavaConnection javaConnection = new JavaConnection(JavaConnection.url,JavaConnection.user,JavaConnection.password);
        String query = " SELECT * FROM  Stock S ,ProctorControlsStock P" +
                "where EID='"+getProctor().getpId()+"' and S.BuildingNumber=P.buildingNumber ";
        String BuildingNum=null;
        String RoomNum=null;
        Integer matresses=0;
        Integer matressBases=0;
        Integer pillows=0;
        ResultSet resultSet;
         if(javaConnection.isConnected()){
             try {
                 resultSet= javaConnection.selectQuery(query);
                 BuildingNum=resultSet.getString("BuildingNumber");
                 RoomNum=resultSet.getString("RoomNumber");
                 matresses=Integer.parseInt(resultSet.getString("TotalMatress"));
                 matressBases=Integer.parseInt(resultSet.getString("TotalMatressBase"));
                 pillows=Integer.parseInt(resultSet.getString("Totalpillow"));
             }
             catch (SQLException ex){
                 ex.printStackTrace();
             }

             setStockLables(BuildingNum,RoomNum,matresses,matressBases,pillows);

         }
    }


    public Vector<Vector<Object>> loadHistory(){
        return null;
    }
    @Override
    public void setUpTable() {
        Vector<String> titles = new Vector<>();
        tableData = new Vector<>();

        titles.add("Proctor Id");
        titles.add("Proctor Name");
        titles.add("Action Type");
        titles.add("Date");

        Vector<Vector<Object>> history = loadHistory();
        readStatus = !(history.size() == 0);
        displayReadStatus(readStatus);

        stockHistoryTable.setModel(new DefaultTableModel(tableData,titles));
        stockHistoryTable.setDefaultEditor(Object.class,null);//To make each cell none editable.
    }

    public void displayReadStatus(boolean readStatus){
        if(!readStatus) JOptionPane.showMessageDialog(parentComponent,"No history found");
    }

    @Override
    public void addDataToTable(Object object) {

    }

    @Override
    public void refreshTable() {

    }

    @Override
    public void setUpGUi() {
        this.setTitle("StockView Information");
        this.setContentPane(mainPanel);
        this.setSize(new Dimension(WIDTH,HEIGHT));
        this.setLocationRelativeTo(parentComponent);
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                e.getWindow().dispose();
                parentComponent.setVisible(true);
            }
        }); //A custom action listener for the exit button.
        setUpTable();
        loadStock();
        setVisible(true);
    }

}
