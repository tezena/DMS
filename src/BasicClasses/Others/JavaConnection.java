package BasicClasses.Others;

import javax.swing.*;
import java.sql.*;
import java.sql.ResultSet;
import java.sql.Statement;

public class JavaConnection {

   /* public static String URL = "jdbc:sqlserver://DMS-SERVER\\SQLEXPRESS;DatabaseName=DMS;" +
                "encrypt=true;trustServerCertificate=true;IntegratedSecurity=true;";*/
   public static String url = "jdbc:sqlserver://DMS-SERVER\\SQLEXPRESS;DatabaseName=DMS;" +
           "encrypt=true;trustServerCertificate=true;";
   public static String user="sa";
   public static String password="1923";
    Connection connection;
    Statement statement;
    ResultSet resultSet;

    public JavaConnection(String url,String user,String password){
        try{
            connection = DriverManager.getConnection(url,user,password);
            statement = connection.createStatement();
            System.out.println("connected");
        }catch (SQLException ex){
            ex.printStackTrace();//For debugging purposes
            JOptionPane.showMessageDialog(null,"Couldn't Connect to server","Connection error",JOptionPane.ERROR_MESSAGE);
            connection = null;
            statement = null;
            resultSet = null;
        }
    }
    public static String stripCotation(String text){
        while(text.contains("'")){
            int indexOfCot = text.indexOf("'");
            String tmp = text.substring(0,indexOfCot);
            text = text.substring(indexOfCot+1);
            text = tmp + text;
        }
        return text;
    }


    public JavaConnection(String url,String user,String password, String query){
        this(url,user,password);
        try{
            statement.executeQuery(query);
        }catch (SQLException ex){
            ex.printStackTrace();//For debugging purposes
            JOptionPane.showMessageDialog(null,"Couldn't Execute the query","Connection error",JOptionPane.ERROR);
            resultSet = null;
        }
    }

    public int insertQuery(String query){
        try{
            statement.execute(query);
            return 1;
        }catch (SQLException ex){
            System.out.println("Query: "+query);//For debugging only.
            ex.printStackTrace();//For debugging purposes.
            return 0;
        }
    }
    public boolean updateQuery(String query){
        try{
            statement.execute(query);
            return true;
        }catch (SQLException ex){
            System.out.println("Query: "+query);//For debugging purposes.
            ex.printStackTrace();//For debugging purposes.
            return false;
        }
    }

    public ResultSet selectQuery(String query){
        ResultSet tmpResultSet;
        try{
            tmpResultSet = statement.executeQuery(query);
        }catch (SQLException ex){
            System.out.println("Query: "+query);//For debugging purposes.
            ex.printStackTrace();//For debugging purposes.
            return null;
        }
        return tmpResultSet;
    }
    public boolean isConnected(){
        return !(connection.equals(null));//Connection is not initialised or, it is null if the connection is not successful.
    }
    public  static void main(String[] arg){
      JavaConnection  c1= new JavaConnection(url,user,password);
    }
}
