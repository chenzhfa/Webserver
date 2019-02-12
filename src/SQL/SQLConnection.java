package SQL;

import Device.DeviceBean;
import Device.ReadingBean;
import Device.ValueBean;
import Location.LocationBean;

import javax.xml.stream.Location;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


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


    public DeviceBean getDeviceInfo(int id) throws SQLException, ClassNotFoundException {
        if (DEBUG) {
            System.out.println("Provo a connettermi...");
        }

        Class.forName("com.mysql.jdbc.Driver"); //deprecato
        conn = DriverManager.getConnection(CONN_STRING);

        if (DEBUG) {
            System.out.println("Connesso");
        }

        DeviceBean device = null;

        final String stmt = "SELECT idDevice, macAddress, kLocation\n" +
                "FROM Devices\n" +
                "WHERE idDevice = ?;";

        final PreparedStatement preparedStatement = conn.prepareStatement(stmt);
        preparedStatement.setInt(1, id);
        ResultSet rs = preparedStatement.executeQuery();
        rs.next();

        device = new DeviceBean(rs.getInt(1), rs.getString(2), rs.getInt(3));

        if (conn != null) {
            conn.close();
            conn = null;
        }

        return device;
    }

    public List<DeviceBean> getAllDevices() throws ClassNotFoundException, SQLException {

        if (DEBUG) {
            System.out.println("Provo a connettermi...");
        }

        Class.forName("com.mysql.jdbc.Driver"); //deprecato
        conn = DriverManager.getConnection(CONN_STRING);

        if (DEBUG) {
            System.out.println("Connesso");
        }

        String stmt = "SELECT idDevice, macAddress, kLocation\n" +
                "FROM Devices";
        PreparedStatement preparedStatement = conn.prepareStatement(stmt);

        ResultSet rs = preparedStatement.executeQuery();

        ArrayList<DeviceBean>  devices = new ArrayList<>();

        while (rs.next()) {
            devices.add(new DeviceBean(rs.getInt("idDevice"), rs.getString("macAddress"),rs.getInt("kLocation")));

        }


        if (conn != null) {
            conn.close();
            conn = null;
        }

        return devices;
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
        preparedStmt.execute();
        try {

            ResultSet res = preparedStmt.getResultSet();
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

        int idDevice = getDeviceId(reading.getMac());

        int kLocation = -1;
        kLocation = getDeviceLocation(idDevice);

        if (kLocation == -1) {
            throw new SQLException("Location of the Device not found!");
        }

        final String stmt = "INSERT INTO Readings(data, kDevice,kLocation) " +
                "VALUES(?, ?, ?);";

        if (DEBUG) {
            System.out.println("Provo a connettermi...");
        }

        Class.forName("com.mysql.jdbc.Driver"); //deprecato
        conn = DriverManager.getConnection(CONN_STRING);

        if (DEBUG) {
            System.out.println("Connesso" +
                    "addReading() ...");
        }

        /*
            Pushing Reading
         */
        final PreparedStatement preparedStmt = conn.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
        preparedStmt.setString(1,reading.getDatetime());

        /*
            TODO:
                convert datetime
                perché usare long invece di int?
         */
        preparedStmt.setInt(2,idDevice);
        preparedStmt.setInt(3,kLocation);
        preparedStmt.execute();

        ResultSet generatedKeys = preparedStmt.getGeneratedKeys();

        generatedKeys.next();
        int kReading = generatedKeys.getInt(1);

        if (DEBUG) {
            System.out.println("kReading = "+kReading+" in addReading()");
        }

        if (conn != null) {
            conn.close();
            conn = null;
        }

        addValues(kReading, reading.getValue());
    }

    private void addValues(int kReading, List<ValueBean> values) throws ClassNotFoundException, SQLException {

        if (DEBUG) {
            System.out.println("Provo a connettermi...");
        }

        Class.forName("com.mysql.jdbc.Driver"); //deprecato
        conn = DriverManager.getConnection(CONN_STRING);

        if (DEBUG) {
            System.out.println("Connesso" +
                    "addReading() ...");
        }

        /*
            Pushing Value
         */

        final String stmt = "INSERT INTO R_Values(sensorProgressive, reading, kReading) " +
                "VALUES (?, ?, ?);";

        final PreparedStatement pushValueStatement = conn.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);

        for (ValueBean value : values) {
            pushValueStatement.setInt(1,value.getSensorProgressive());
            pushValueStatement.setDouble(2,value.getReading());
            pushValueStatement.setInt(3, kReading);
            pushValueStatement.execute();
            ResultSet generatedKeys = pushValueStatement.getGeneratedKeys();
            generatedKeys.next();
            int idValue = generatedKeys.getInt(1);
            if (DEBUG) {
                System.out.println("generatedKeys() Pushing Value: "+idValue);
            }
        }

        if (conn != null) {
            conn.close();
            conn = null;
        }
    }

    /*
        Get
     */

    public List<ReadingBean> getDeviceReadings(int id) throws ClassNotFoundException, SQLException {
        if (DEBUG) {
            System.out.println("Provo a connettermi...");
        }

        Class.forName("com.mysql.jdbc.Driver"); //deprecato
        conn = DriverManager.getConnection(CONN_STRING);

        if (DEBUG) {
            System.out.println("Connesso " +
                    "getDeviceId() ...");
        }

        final String stmt = "" +
                "SELECT idReading, macAddress, data, name, sensorProgressive, reading\n" +
                "FROM Devices \n" +
                "INNER JOIN Readings ON idDevice = kDevice\n" +
                "INNER JOIN Locations ON Readings.kLocation = idLocation\n" +
                "INNER JOIN R_Values ON idReading = kReading  \n" +
                "WHERE idDevice = ?;";

        PreparedStatement preparedStatement = conn.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, id);
        ResultSet res = preparedStatement.executeQuery();
        res.next();

        ArrayList<ReadingBean> readings = new ArrayList<>();
        ArrayList<ValueBean> values = new ArrayList<>();

        String deviceMac = res.getString(2);


        /*
        TODO:
            Capire se la location serve o no
         */

        /*
            Concateno i values con le reading e li metto in una lista
         */

        while(true) {
            if (res.isAfterLast()) {
                break;
            }
            int currentIdReading = res.getInt("idReading");
            if (DEBUG) {
                System.out.println("currentIdReading = "+currentIdReading);
            }
            String date = res.getString("data");
            String locationName = res.getString("name");

            while (!res.isAfterLast() &&res.getInt("idReading") == currentIdReading) { //è sempre la stessa lettura
                int sensorProgressive = res.getInt("sensorProgressive");
                int reading = res.getInt("reading");

                if (DEBUG) {
                    System.out.println("sensorProgressive: "+sensorProgressive+ "\n" +
                            "reading: "+reading);
                }
                ValueBean valueBean = new ValueBean(sensorProgressive,reading); //sensorProgressive and its reading temperature
                values.add(valueBean);
                res.next();
            }
            ReadingBean readingBean = new ReadingBean(deviceMac, date, values);
            values = new ArrayList<>();
            readings.add(readingBean);

        }

        if (conn != null) {
            conn.close();
            conn = null;
        }

        return readings;
    }

    public int getDeviceId(String mac) throws ClassNotFoundException, SQLException {
        if (DEBUG) {
            System.out.println("Provo a connettermi...");
        }

        Class.forName("com.mysql.jdbc.Driver"); //deprecato
        if (DEBUG) {
            System.out.println("CONN_SRING = "+CONN_STRING);
        }
        conn = DriverManager.getConnection(CONN_STRING);

        if (DEBUG) {
            System.out.println("Connesso " +
                    "getDeviceId() ...");
        }

        final String stmt = "SELECT idDevice FROM Devices WHERE macAddress = ?";

        final PreparedStatement preparedStatement = conn.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, mac);

        if (DEBUG) {
            System.out.println("MAC = "+mac);
        }

        final ResultSet rs = preparedStatement.executeQuery();
        rs.next();

        if (DEBUG) {
            System.out.println("Query done!");
        }

        int id = rs.getInt(1);

        if (conn != null) {
            conn.close();
            conn = null;
        }

        return id;
    }

    public int getDeviceLocation(int id) throws ClassNotFoundException, SQLException {
        if (DEBUG) {
            System.out.println("Provo a connettermi...");
        }

        Class.forName("com.mysql.jdbc.Driver"); //deprecato
        conn = DriverManager.getConnection(CONN_STRING);

        if (DEBUG) {
            System.out.println("Connesso\n" +
                    "getDeviceLocation() ...");

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
        System.out.println(connection.getDeviceId("ff:gg:hh:ii:ll:mm"));
    }

    /*
        Locations
     */

    public int getLocationId(final String locationName) throws ClassNotFoundException, SQLException {
        int id = 0;

        if (DEBUG) {
            System.out.println("Provo a connettermi...");
        }

        Class.forName("com.mysql.jdbc.Driver"); //deprecato
        conn = DriverManager.getConnection(CONN_STRING);

        if (DEBUG) {
            System.out.println("Connesso\n" +
                    "getDeviceLocation() ...");

        }

        final String stmt = "SELECT idLocation \n" +
                "FROM Locations\n" +
                "WHERE name = ?;";

        PreparedStatement preparedStatement = conn.prepareStatement(stmt);

        ResultSet rs = preparedStatement.executeQuery();
        rs.next();
        id = rs.getInt(1);

        if (conn != null) {
            conn.close();
            conn = null;
        }

        return id;
    }

    public List<ReadingBean> getLocationReadings(int idLocation) throws SQLException, ClassNotFoundException {
        final String stmt = "SELECT idReading, macAddress, data, name, sensorProgressive, reading\n" +
                "FROM Devices \n" +
                "INNER JOIN Readings ON idDevice = kDevice\n" +
                "INNER JOIN Locations ON Readings.kLocation = idLocation\n" +
                "INNER JOIN R_Values ON idReading = kReading  \n" +
                "WHERE idLocation = ?\n" +
                "GROUP BY idReading, sensorProgressive, reading;";

        if (DEBUG) {
            System.out.println("Provo a connettermi...");
        }

        Class.forName("com.mysql.jdbc.Driver"); //deprecato
        conn = DriverManager.getConnection(CONN_STRING);

        if (DEBUG) {
            System.out.println("Connesso " +
                    "getDeviceId() ...");
        }

        PreparedStatement preparedStatement = conn.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, idLocation);
        ResultSet res = preparedStatement.executeQuery();
        res.next();

        ArrayList<ReadingBean> readings = new ArrayList<>();
        ArrayList<ValueBean> values = new ArrayList<>();

        String deviceMac = res.getString(2);


        /*
        TODO:
            Capire se la location serve o no
         */

        /*
            Concateno i values con le reading e li metto in una lista
         */

        while(true) {
            if (res.isAfterLast()) {
                break;
            }
            int currentIdReading = res.getInt("idReading");
            if (DEBUG) {
                System.out.println("currentIdReading = "+currentIdReading);
            }
            String date = res.getString("data");
            String locationName = res.getString("name");

            while (!res.isAfterLast() &&res.getInt("idReading") == currentIdReading) { //è sempre la stessa lettura
                int sensorProgressive = res.getInt("sensorProgressive");
                int reading = res.getInt("reading");

                if (DEBUG) {
                    System.out.println("sensorProgressive: "+sensorProgressive+ "\n" +
                            "reading: "+reading);
                }
                ValueBean valueBean = new ValueBean(sensorProgressive,reading); //sensorProgressive and its reading temperature
                values.add(valueBean);
                res.next();
            }
            ReadingBean readingBean = new ReadingBean(deviceMac, date, values);
            values = new ArrayList<>();
            readings.add(readingBean);

        }

        if (conn != null) {
            conn.close();
            conn = null;
        }

        return readings;
    }

    public List<LocationBean> getLocations() throws ClassNotFoundException, SQLException {

        if (DEBUG) {
            System.out.println("Provo a connettermi...");
        }

        Class.forName("com.mysql.jdbc.Driver"); //deprecato
        conn = DriverManager.getConnection(CONN_STRING);

        if (DEBUG) {
            System.out.println("Connesso " +
                    "getDeviceId() ...");
        }

        final String stmt = "SELECT idLocation, name\n" +
                "FROM Locations;";

        PreparedStatement preparedStatement = conn.prepareStatement(stmt);
        ResultSet rs = preparedStatement.executeQuery();
        final ArrayList<LocationBean> locations = new ArrayList<>();
        while(rs.next()) {
            locations.add(new LocationBean(rs.getInt(1), rs.getString(2)));
        };

        if (conn != null) {
            conn.close();
            conn = null;
        }

        return locations;
    }

}
