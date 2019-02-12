package Location;

import SQL.SQLConnection;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Arrays;

@WebServlet(name = "LocationServlet")
public class LocationServlet extends HttpServlet {

    final String [] SUPPORTED_OPERATIONS = new String[]{"locations", "readings"};
    final boolean DEBUG = true;

    private final static String dbName = "test_alternanza";
    private final static String dbUser = "root";
    private final static String dbUserPwd = "123456";
    private final static String CONN_STRING = "jdbc:mysql://localhost:3306/"+dbName+"?user="+dbUser+"&password="+dbUserPwd;
    /*
    TODO:
        ENDPOINT /locations : ritorna tutte le locations
        ENDPOINT /locations/{id}/readings : ritorna tutte le lettura di una stanza
    */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String [] strings = request.getRequestURI().split("/");
        String endpoint = strings[strings.length-1];

        if (!isSupported(endpoint)) {
            response.setStatus(404);
            out.append("Operazione non supportata");
        }

        if (endpoint.equals("locations")) {
            if (DEBUG) {
                System.out.println("GET : ritorno tutte le locations");
            }

            response.setContentType("application/json");
            out.println(getLocations());
        }

        if (endpoint.equals("readings")) {
            String location = strings[strings.length-2];

            if (DEBUG) {
                System.out.println("GET : ritorno tutte le reading di una location");
            }

            int idLocation = parseAvailableLocationId(location);

            if (idLocation == -1) {
                response.sendError(404);
            }

            response.setContentType("application/json");
            try {
                response.setStatus(200);
                out.println(getReadingsOf(idLocation));
            } catch (SQLException e) {
                out.println("{}");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }



    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();


    }

    /*


        METHODS


     */



    private boolean isSupported(String endpoint) {
        String res = Arrays.stream(SUPPORTED_OPERATIONS).filter(it -> endpoint.matches(it))
                .findAny().orElse("");

        if (res.equals("")) {
            return false;
        }

        return true;
    }

    private String getLocations(){
        String locations = "{\r\n  " +
                "\"id\": 3,\r\n  " +
                "\"name\": \"Laboratorio di Sistemi Informatici\"\r\n}";

        return locations;
    }

    private String getReadingsOf(int idLocation) throws SQLException, ClassNotFoundException {
        Gson gson = new Gson();
        SQLConnection sqlConnection = SQLConnection.getInstance(CONN_STRING);
        String res = gson.toJson(sqlConnection.getLocationReadings(idLocation));
        return res;
    }

    private int parseAvailableLocationId(String string) {
        int id = -1;

        try {
            id = Integer.parseInt(string);
        }
        catch(NumberFormatException e) {
            id = -1;
        }

        return id;
    }

}
