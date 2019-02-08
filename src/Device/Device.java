package Device;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import SQL.SQLConnection;

//https://app.swaggerhub.com/apis-docs/sorby/SensoriASL/1.0.0-oas3

/**
 * Servlet implementation class Device
 */
@WebServlet(name = "Device")
public class Device extends HttpServlet {
	final String [] SUPPORTED_OPERATIONS = new String[]{"devices", "readings", "\\d+"};

	private SQLConnection sqlConnection = SQLConnection.getInstance(
			"","","");

	private final static String CONN_STRING = "jdbc:mysql://localhost:3306/test_alternanza?user=root&password=123456";
	final static boolean DEBUG = true;
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Device() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */

	/*
		TODO:
			Prende tutti i dispositivi registrati
			Ritorna tutte le letture di un dispositivo
			Ritorna le informazioni di un dispositivo current: non fatto
			Ritorna le informazioni di un dispositivo
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		/*
			Getting endpoint option
		 */

		PrintWriter out = response.getWriter();
		String [] strings = request.getRequestURI().split("/");
		String endpoint = strings[strings.length-1];

		//CHECKING IF THE OPERATION IS SUPPORTED

		if (!isSupported(endpoint)) {
			response.setStatus(404);
			out.append("Operazione non supportata");
		}


		/*
		 * ENDPOINT /devices : ritorna json con tutti i dispositivi registrati
		 */
		
		if(endpoint.equals("devices")) {

			if (DEBUG) {
				System.out.println("GET localhost:8080/devices");
			}

			response.setContentType("application/json");
			response.setStatus(200);
			out.append(getDevicesJson());
			return;
		}


		/*
		 * ENDPOINT: ritorna le letture di un dispositivo
		 * /Devices/0/readings ritorna le letture del dispositivo 0
		 */

		if(endpoint.equals("readings")) { //ritorna tutte le letture di un dispositivo /devices/{id}/readings
			response.setContentType("application/json");
			String device = strings[strings.length-2];

			if (DEBUG) {
				System.out.println("GET localhost:8080/devices/{id}/readings");
			}

			if (device.equals("current")) {
				//TODO: do whatever
				response.setContentType("text/plain");
				return;
			}

			int id = parseAvailableId(strings[strings.length-2]);

			if (id == -1) {
				out.append("ID non esistente o sbagliato");
				return;
			}

			out.append(getReadings(id));
			return;
		}

		/*
		 * ENDPOINT /devices/current : ritorna le informazioni di un dispositivo
		 */



		if (endpoint.equals("current")) {

		}

		/*
		 * ENDPOINT /devices/0 : ritorna le informazioni di un dispositivo
		 */

		int id = parseAvailableId(endpoint);
		if (id != -1) {
			response.setContentType("application/json");
			response.setStatus(200);
			out.append(getDeviceInfo(id));
		}

		response.setStatus(404);
		out.append("Operazione non supportata");
		return;
	}
		
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */

	/*
	TODO:
		Registrazione nuovo client;
		Registrazione nuova reading

	 */

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		String [] strings = request.getRequestURI().split("/");
		String endpoint = strings[strings.length-1];

		if (isSupported(endpoint)){
			response.setStatus(404);
			out.append("Operazione non supportata");
		}

		String json = request.getReader().lines()
				.reduce("", (accumulator, actual) -> accumulator+"\n" + actual);

		if (DEBUG) {
			System.out.println("POST JSON received : \n\n"+json+"\n\n End JSON");
		}

		if (endpoint.equals("devices")) { //registrazione nuovo client
			if (DEBUG) {
				System.out.println("POST "+request.getRequestURI());
			}
		}

		if(endpoint.equals("readings")) { //registrazione nuova reading

			if (DEBUG) {
				System.out.println("POST "+request.getRequestURI());
			}

			int id = parseAvailableId(strings[strings.length-2]);

			if (id == -1) {
				response.setContentType("text/html");
				response.setStatus(404);
				out.append("ID non esistente o sbagliato");
				return;
			}


			response.setContentType("application/json");
			response.setStatus(201);
			out.append(String.valueOf(addReading(id)));
			return;
		}

		response.setStatus(404);
		out.append("Operazione non supportata");
	}
	
	/*
	 * 
	 * 
	 * METHODS
	 * 
	 * 
	 */

	/*
		Check if supported type
	 */

	private boolean isSupported(String endpoint) {
		String res = Arrays.stream(SUPPORTED_OPERATIONS).filter( it -> endpoint.matches(it))
				.findAny().orElse("");

		if (res.equals("")) {
			return false;
		}

		return true;
	}

	/*
		@return -1 error
	 */
	private int parseAvailableId(String string) {
		int id = -1;

		try {
			id = Integer.parseInt(string);
		}
		catch(NumberFormatException e) {
			id = -1;
		}

		return id;
	}

	/*
	 * Bisogna passargli l'ID del dispositivo di cui si vogliono ottenere le letture
	 */
	private String getReadings(int id) {
		String lettura = 
				"["+
				  "{"+
				    "id : 2,"+
				    "value : ["+
				      "{" +
				        "sensorProgressive : 0,"+
				        "reading : 19.2" +
				      "}," +
				      "{" +
				        "sensorProgressive : 1,"+ 
				        "reading : 19.5"+ 
				      "}" +
				    "]" +
				  "}" +
				"]";
		System.out.println("lettura = \n"+lettura);
		return lettura;
	}
	
	private String getDeviceInfo(int id) {
		return "";
	}
	
	private String getDevicesJson() {

		String devices = ""
				+ "[\r\n" + 
				"  {\r\n" + 
				"    \"id\": 1,\r\n" + 
				"    \"mac\": \"AA:BB:CC:DD:EE:FF\",\r\n" + 
				"    \"location\": 3\r\n" + 
				"  },\r\n" +
				"{\r\n" + 
				"    \"id\": 2,\r\n" + 
				"    \"mac\": \"GG:HH:II:LL:MM:NN\",\r\n" + 
				"    \"location\": 2\r\n" + 
				"  }\r\n"+ 
				"]";
		return devices;
	}

	private boolean addReading(int id) {

		return true;
	}
}
