package SQL;

import java.sql.*;


public class SQLConnection {
    private final static boolean DEBUG = true;

    private String USERNAME = null;
    private String PASSWORD = null;
    private String CONN_STRING = null;

    private static SQLConnection sqlConnection = null;

    private SQLConnection(final String username, final String password, final String conn_string) {
        USERNAME = username;
        PASSWORD = password;
        CONN_STRING = conn_string;
    }

    public static SQLConnection getInstance(final String username, final String password, final String conn_string) {
        if (sqlConnection == null) {
            sqlConnection = new SQLConnection(username, password, conn_string);
        }

        if (!username.equals(sqlConnection.USERNAME)) {
            sqlConnection.USERNAME = username;
        }

        if (!username.equals(sqlConnection.PASSWORD)) {
            sqlConnection.PASSWORD = password;
        }

        if (!username.equals(sqlConnection.CONN_STRING)) {
            sqlConnection.CONN_STRING = conn_string;
        }

        return sqlConnection;
    }

    public void addReading() {}

    private void doQuery(final String statement) throws SQLException, ClassNotFoundException {
        Connection conn = null;


        Class.forName("com.mysql.jdbc.Driver"); //deprecato
        //Class.forName("com.mysql.cj.jdbc.Driver");

        if (DEBUG) {
            System.out.println("Provo a connettermi...");
        }

        //conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
        conn = DriverManager.getConnection(CONN_STRING);

        if (DEBUG) {
            System.out.println("Connesso");
        }

        PreparedStatement preparedStmt = conn.prepareStatement
                ("INSERT INTO Readings(data, kDevice,kLocation) " +
                                "VALUES(\"1990-09-05\", 1, 1);", Statement.RETURN_GENERATED_KEYS);
        java.util.Date uDate = new java.util.Date();
        Timestamp startDate = new java.sql.Timestamp(uDate.getTime());//calendar.getTime().getTime());
        System.out.println(startDate);
//        preparedStmt.setTimestamp(1, startDate);
        preparedStmt.execute();


        try (ResultSet generatedKeys = preparedStmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                System.out.println("*****"+generatedKeys.getLong(1));
            }
            else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }

        Statement stmt=conn.createStatement();
        ResultSet rs=stmt.executeQuery("select * from Readings");
        while(rs.next())
            System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));

        System.out.println("fine");
        if (conn != null) {
            conn.close();
        }

    }

    public static void main(String [] args) throws SQLException, ClassNotFoundException {
        SQLConnection sqlConnection = getInstance("","", "jdbc:mysql://localhost:3306/test_alternanza?user=root&password=123456");

        sqlConnection.doQuery("");

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
