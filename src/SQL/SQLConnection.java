package SQL;

import Device.DeviceBean;
import Device.ReadingBean;

import javax.xml.transform.Result;
import java.sql.*;


/*
    TODO: Capire come inserire i parametri nelle Query
 */

public class SQLConnection {
    private final static boolean DEBUG = true;

    public final static int SELECT = 1;
    public final static int INSERT = 2;


    private String USERNAME = null;
    private String PASSWORD = null;
    private String CONN_STRING = null;

    private Connection conn = null;

    private static SQLConnection sqlConnection = null;



    private SQLConnection(final String username, final String password, final String conn_string) {
        USERNAME = username;
        PASSWORD = password;
        CONN_STRING = conn_string;
    }

    private SQLConnection(final String conn_string){
        new SQLConnection("","", conn_string);
    }

    public static SQLConnection getInstance(final String username, final String password, final String conn_string) throws SQLException, ClassNotFoundException {
        if (sqlConnection == null) {
            sqlConnection = new SQLConnection(username, password, conn_string);
        }

        if (!username.equals(sqlConnection.USERNAME)) {
            sqlConnection.USERNAME = username;
        }

        if (!password.equals(sqlConnection.PASSWORD)) {
            sqlConnection.PASSWORD = password;
        }


        if (!conn_string.equals(sqlConnection.CONN_STRING)) {
            sqlConnection.CONN_STRING = conn_string;
        }

        return sqlConnection;
    }

    public static SQLConnection getInstance(final String conn_string) throws SQLException, ClassNotFoundException {
        if (sqlConnection == null) {
            sqlConnection = new SQLConnection(conn_string);
        }

        if (!conn_string.equals(sqlConnection.CONN_STRING)) {
            sqlConnection.CONN_STRING = conn_string;
        }

        return sqlConnection;
    }

    /*
       _____          __  .__               .___
      /     \   _____/  |_|  |__   ____   __| _/______
     /  \ /  \_/ __ \   __\  |  \ /  _ \ / __ |/  ___/
    /    Y    \  ___/|  | |   Y  (  <_> ) /_/ |\___ \
    \____|__  /\___  >__| |___|  /\____/\____ /____  >
            \/     \/          \/            \/    \/

     */


    private ResultSet doQuery(PreparedStatement pstmt, int mode) throws SQLException{

        switch (mode) {
            case SELECT:
                pstmt.executeQuery();
                return pstmt.getResultSet();
        }

        pstmt.execute();
//        if (!pstmt.execute()) {
//            if (DEBUG) {
//                System.out.println("Errore nell'esecuzione della query "+pstmt);
//            }
//            throw new SQLException("Errore esecuzione query");
//        }

        ResultSet res = pstmt.getGeneratedKeys();

        if (res.next()) {
            if (DEBUG)
                System.out.println("Generated id : "+res.getLong(1));
        }

//        else {
//            throw new SQLException("doQuery() failed.");
//        }

        return res;


//        try (ResultSet generatedKeys = preparedStmt.getGeneratedKeys()) {
//            if (generatedKeys.next()) {
//                System.out.println("*****"+generatedKeys.getLong(1));
//            }
//            else {
//                throw new SQLException("Creating user failed, no ID obtained.");
//            }
//        }
//

    }

    public void getAllReadings() throws SQLException {
        if (conn == null) {
            conn = DriverManager.getConnection(CONN_STRING);
        }

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Readings");
        while(rs.next())
            System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));
    }

    /*
    TODO:
        check MAC
        check kLocatoin
        check how to insert properly a parameter
        NB: ESCAPE QUOTES!
     */

    public void addDevice(final DeviceBean device) throws ClassNotFoundException, SQLException {
        if (DEBUG) {
            System.out.println("Provo a connettermi...");
        }

        Class.forName("com.mysql.jdbc.Driver"); //deprecato
        conn = DriverManager.getConnection(CONN_STRING);

        if (DEBUG) {
            System.out.println("Connesso");
        }

        final String stmt = "INSERT INTO Devices(macAddress, kLocation) " +
                "VALUES(?,?);";

        /*
            Executing Query
         */
        PreparedStatement preparedStmt = conn.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
        preparedStmt.setString(1,device.getMac());
        preparedStmt.setInt(2,device.getLocation());

        try {

            ResultSet res = doQuery(preparedStmt, INSERT);
        }
        catch (SQLException e) {
            throw new SQLException("Errore nella query addDevice(): "+stmt);

        }

        if (conn != null) {
            conn.close();
            conn = null;
        }
    }

    /*
    TODO:
        check date format
        check kDevice
        check kLocation
     */

    public void addReading(final ReadingBean reading) throws SQLException, ClassNotFoundException {
        if (DEBUG) {
            System.out.println("Provo a connettermi...");
        }

        Class.forName("com.mysql.jdbc.Driver"); //deprecato
        conn = DriverManager.getConnection(CONN_STRING);

        if (DEBUG) {
            System.out.println("Connesso");
        }




        int kLocation = -1;

        if (kLocation == -1) {
            throw new SQLException("Location of the Device not found!");
        }

        final String stmt = "INSERT INTO INSERT INTO Readings(data, kDevice,kLocation) " +
                "VALUES(?, ?, ?);";

        /*
            Executing Query
         */
        PreparedStatement preparedStmt = conn.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
        preparedStmt.setString(1,reading.getDatetime());
        preparedStmt.setInt(2,reading.getId());
        preparedStmt.setInt(3,kLocation);

        try {

            ResultSet res = doQuery(preparedStmt, INSERT);
        }
        catch (SQLException e) {
            throw new SQLException("Errore nella query addReading(): "+stmt);

        }

        if (conn != null) {
            conn.close();
            conn = null;
        }
    }

    public int getDeviceLocation(int id) throws ClassNotFoundException, SQLException {
        if (DEBUG) {
            System.out.println("Provo a connettermi...");
        }

        Class.forName("com.mysql.jdbc.Driver"); //deprecato
        conn = DriverManager.getConnection(CONN_STRING);

        if (DEBUG) {
            System.out.println("Connesso");
        }

        final String stmt = "SELECT kLocation\n" +
                "        FROM Devices\n" +
                "        INNER JOIN Locations ON kLocation = idLocation\n" +
                "        WHERE idDevice = ?\n";

        PreparedStatement preparedStmt = conn.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
        preparedStmt.setInt(1,id);
        ResultSet rs = preparedStmt.executeQuery();

        int kLocation= -1;

        rs.next();
        kLocation = rs.getInt("kLocation");

        if (conn != null) {
            conn.close();
            conn = null;
        }

        return kLocation;
    }

    public static void main(String [] args) throws SQLException, ClassNotFoundException {
        SQLConnection connection = SQLConnection.getInstance("jdbc:mysql://localhost:3306/test_alternanza?user=root&password=123456");
        System.out.println(connection.getDeviceLocation(3));
    }
//    private void mysql(){
//        Connection conn = null;
//        try {
//            Class.forName("com.mysql.jdbc.Driver"); //deprecato
//            //				Class.forName("com.mysql.cj.jdbc.Driver");
//            System.out.println("Provo a connettermi...");
//            conn = (Connection) DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
//            System.out.println("Connesso");
//
//
//            PreparedStatement preparedStmt = conn.prepareStatement
//                    ("INSERT INTO rilevazioni (id, k_mcontrollore, temp, nsensore, date, k_stanza ) "
//                            + "VALUES (0,6,9,1,?,2)", Statement.RETURN_GENERATED_KEYS);
//            java.util.Date uDate = new java.util.Date();
//            Timestamp startDate = new java.sql.Timestamp(uDate.getTime());//calendar.getTime().getTime());
//            System.out.println(startDate);
//            preparedStmt.setTimestamp(1, startDate);
//            preparedStmt.execute();
//
//
//            try (ResultSet generatedKeys = preparedStmt.getGeneratedKeys()) {
//                if (generatedKeys.next()) {
//                    System.out.println("*****"+generatedKeys.getLong(1));
//                }
//                else {
//                    throw new SQLException("Creating user failed, no ID obtained.");
//                }
//            }
//
//
//            Statement stmt=conn.createStatement();
//            ResultSet rs=stmt.executeQuery("select * from rilevazioni");
//            while(rs.next())
//                System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));
//
//        } catch (SQLException e) {
//
//            System.err.println(e);
//        } finally {
//            System.out.println("fine");
//            if (conn != null) {
//                conn.close();
//            }
//        }
//    }//

}
