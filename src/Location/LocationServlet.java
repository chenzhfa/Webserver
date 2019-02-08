package Location;

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

            response.setContentType("application/json");

            int idLocation = parseAvailableLocationId(location);

            if (idLocation == -1) {
                response.setStatus(404);
                out.append("Operazione non supportata");
            }

            response.setContentType("application/json");
            response.setStatus(200);
            out.println(getReadingsOf(idLocation));
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

    private String getReadingsOf(int idLocation) {
        String res = "[\\r\\n" +
                "  {\\r\\n    " +
                "\\\"id\\\": 2,\\r\\n    " +
                "\\\"datetime\\\": \\\"2019-01-30 23:18:29\\\",\\r\\n    " +
                "\\\"value\\\": [\\r\\n      {\\r\\n        " +
                "\\\"sensorProgressive\\\": 0,\\r\\n        " +
                "\\\"reading\\\": 19.2\\r\\n      },\\r\\n      " +
                "{\\r\\n        \\\"sensorProgressive\\\": 1,\\r\\n        " +
                "\\\"reading\\\": 19.5\\r\\n      }\\r\\n    ]\\r\\n  }\\r\\n]";
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
