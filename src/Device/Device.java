package Device;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Arrays;

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
	final String [] SUPPORTED_OPERATIONS = new String[]{"devices", "readings", "\\d+","current"};


	private final static String dbName = "test_alternanza";
	private final static String dbUser = "root";
	private final static String dbUserPwd = "123456";
	private final static String CONN_STRING = "jdbc:mysql://localhost:3306/"+dbName+"?user="+dbUser+"&password="+dbUserPwd;
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
			Ritorna le informazioni di un dispositivo current
			Ritorna le informazioni di un dispositivo
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		/*
			Getting endpoint option
		 */

		PrintWriter out = response.getWriter();
		String [] strings = request.getRequestURI().split("/");
		String endpoint = strings[strings.length-1];

		//CHECKING IF THE OPERATION IS SUPPORTED

		if (!isSupported(endpoint)) {
			response.sendError(404);
		}


		/*
		 * ENDPOINT /devices : ritorna json con tutti i dispositivi registrati
		 */
		
		if(endpoint.equals("devices")) {

			response.setContentType("application/json");
			try {
				out.println(getDevicesJson());
				response.setStatus(200);
			} catch (SQLException e) {
				out.println("{}");
				response.setStatus(204);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				response.sendError(500);
			}
			return;
		}

		/*
		 * ENDPOINT: ritorna le letture di un dispositivo
		 * /Devices/0/readings ritorna le letture del dispositivo 0
		 */

		if(endpoint.equals("readings")) { //ritorna tutte le letture di un dispositivo /devices/{id}/readings
			response.setContentType("application/json");
			String device = strings[strings.length-2];

			if (device.equals("current")) {
				//TODO: change status error
				String deviceMac = request.getHeader("X-MAC-Address");
				if (device == null || device.equals("")) {
					response.sendError(404);
				}
				try {
					SQLConnection sqlConnection = SQLConnection.getInstance(CONN_STRING);
					int id = sqlConnection.getDeviceId(deviceMac);

					Gson gson = new Gson();
					String json = gson.toJson(sqlConnection.getDeviceReadings(id));

					response.setStatus(200);
					out.println(json);
					return;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					response.setStatus(500);
					out.println("Errore interno!");
				} catch (SQLException e) {
					e.printStackTrace();
					response.setStatus(204); //richiesta andata a buon fine ma non ha prodotto risultati
					out.println("{}");
				}
				return;
			}

			int id = parseAvailableId(strings[strings.length-2]);

			if (id == -1) {
				response.sendError(404);
				out.append("ID non esistente o sbagliato");
				return;
			}

			response.setContentType("application/json");
			try {
				out.println(getReadings(id));
			} catch (SQLException e) {
				out.println("{}");
			} catch (ClassNotFoundException e) {
				response.sendError(404);
			}
			return;
		}

		/*
		 * ENDPOINT /devices/current : ritorna le info di un dispositivo
		 */



		if (endpoint.equals("current")) {
			String deviceMac = request.getHeader("X-MAC-Address");

			try {
				response.setStatus(200);
				out.println(getDeviceInfo(deviceMac));
			} catch (SQLException e) {
				response.setStatus(204);
				out.println("{}");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return;
		}

		/*
		 * ENDPOINT /devices/0 : ritorna le informazioni di un dispositivo
		 */

		int id = parseAvailableId(endpoint);
		response.setContentType("application/json");

		if (id == -1) {
			response.sendError(404);
			return;
		}

		try {
			response.setStatus(200);
			out.println(getDeviceInfo(id));
		} catch (SQLException e) {
			response.setStatus(204);
			out.println("{}");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		PrintWriter out = response.getWriter();
		String [] strings = request.getRequestURI().split("/");
		String endpoint = strings[strings.length-1];

		if (!isSupported(endpoint)){
			response.setStatus(404);
			out.append("Operazione non supportata");
			return;
		}

		String json = request.getReader().lines()
				.reduce("", (accumulator, actual) -> accumulator+"\n" + actual);
		if (DEBUG) {
			System.out.println(request.getHeader("X-MAC-Address"));
		}
		String deviceMac = request.getHeader("X-MAC-Address");


		if (DEBUG) {
			System.out.println("POST JSON received : \n\n"+json+"\n\nEnd JSON");
		}

		if (endpoint.equals("devices")) { //registrazione nuovo client
			if (DEBUG) {
				System.out.println("POST "+request.getRequestURI());
			}

			response.setContentType("text/html");

			try {

				addDevice(json);
				response.setStatus(201);
				out.append("Nuovo dispositivo aggiunto");
			}
			catch (SQLException sqle) { //TODO: ricontrollare gli status error
				sqle.printStackTrace();
				response.setStatus(404);
				out.append(sqle.getMessage());
			}
			catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
				response.setStatus(404);
				out.append(cnfe.getMessage());
			}

			return;
		}

		if(endpoint.equals("readings")) { //registrazione nuova reading
			String device = strings[strings.length-2];

			if (!device.equals("current")) {
				response.setStatus(404);
				out.append("Servizio non esistente");
				return;
			}


			if (DEBUG) {

				System.out.println("MAC = "+ deviceMac);
				System.out.println("Headers = "+ response.getHeader("X-MAC-Address"));

			}

			if (deviceMac == null) {
				response.setStatus(404);
				out.append("MAC non presente");
				return;
			}


			response.setContentType("text/html");

			try {
				addReading(json, deviceMac);
				response.setStatus(201);
				out.append("Lettura aggiunta con successo");
			} catch (SQLException e) { //TODO: ricontrollare gli status error
				e.printStackTrace();
				response.setStatus(404);
				out.append(e.getMessage());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				response.setStatus(404);
				out.append(e.getMessage());
			}
			return;
		}

		response.setStatus(404);
		out.append("Operazione non supportata");
	}

	private boolean isSupported(String endpoint) {
		String res = Arrays.stream(SUPPORTED_OPERATIONS).filter( it -> endpoint.matches(it))
				.findAny().orElse("");

		if (res.equals("")) {
			return false;
		}

		return true;
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
	private String getReadings(int id) throws SQLException, ClassNotFoundException {
		SQLConnection sqlConnection = SQLConnection.getInstance(CONN_STRING);
		Gson gson = new Gson();
		String json = gson.toJson(sqlConnection.getDeviceReadings(id));
		System.out.println("lettura = \n"+json);
		return json;
	}
	
	private String getDeviceInfo(String mac) throws SQLException, ClassNotFoundException {
		SQLConnection sqlConnection = SQLConnection.getInstance(CONN_STRING);
		Gson gson = new Gson();
		String json = gson.toJson(sqlConnection.getDeviceInfo(sqlConnection.getDeviceId(mac)));
		return json;
	}

	private String getDeviceInfo(int id) throws SQLException, ClassNotFoundException {
		SQLConnection sqlConnection = SQLConnection.getInstance(CONN_STRING);
		Gson gson = new Gson();
		String json = gson.toJson(sqlConnection.getDeviceInfo(id));
		return json;
	}



	private String getDevicesJson() throws SQLException, ClassNotFoundException {

		SQLConnection sqlConnection = SQLConnection.getInstance(CONN_STRING);

		Gson gson = new Gson();
		String devices = gson.toJson(sqlConnection.getAllDevices());
		return devices;
	}

	/*
.----------------.  .----------------.  .----------------.  .----------------.
| .--------------. || .--------------. || .--------------. || .--------------. |
| |   ______     | || |     ____     | || |    _______   | || |  _________   | |
| |  |_   __ \   | || |   .'    `.   | || |   /  ___  |  | || | |  _   _  |  | |
| |    | |__) |  | || |  /  .--.  \  | || |  |  (__ \_|  | || | |_/ | | \_|  | |
| |    |  ___/   | || |  | |    | |  | || |   '.___`-.   | || |     | |      | |
| |   _| |_      | || |  \  `--'  /  | || |  |`\____) |  | || |    _| |_     | |
| |  |_____|     | || |   `.____.'   | || |  |_______.'  | || |   |_____|    | |
| |              | || |              | || |              | || |              | |
| '--------------' || '--------------' || '--------------' || '--------------' |
 '----------------'  '----------------'  '----------------'  '----------------'
	 */

	private void addDevice(String json) throws SQLException, ClassNotFoundException {
		Gson gson = new Gson();
		DeviceBean device = gson.fromJson(json, DeviceBean.class);
		SQLConnection connection = SQLConnection.getInstance(CONN_STRING);
		connection.addDevice(device);
	}

	private boolean addReading(String json, String mac) throws SQLException, ClassNotFoundException {
		Gson gson = new Gson();
		ReadingBean reading = gson.fromJson(json, ReadingBean.class);
		reading.setMac(mac.toLowerCase());
		SQLConnection connection = SQLConnection.getInstance(CONN_STRING);
		connection.addReading(reading);
		return true;
	}
}
