package Device;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

//https://app.swaggerhub.com/apis-docs/sorby/SensoriASL/1.0.0-oas3

/**
 * Servlet implementation class Device
 */
@WebServlet(name = "Device")
//@WebServlet(name = "/devices/*")
public class Device extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Device() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		String [] strings = request.getRequestURI().split("/");
		String endpoint = strings[strings.length-1];
		
		System.out.println("URI : "+request.getRequestURI());
		System.out.println("endpoint = "+endpoint);
		
		/*
		 * ENDPOINT: ritorna le letture di un dispositivo
		 * /Devices/0/readings ritorna le letture del dispositivo 0
		 */
		
		if(endpoint.equals("readings")) { //ritorna tutte le letture di un dispositivo
			response.setContentType("application/json");
			
			Integer id = null;
			try {
				
				id = Integer.parseInt(strings[strings.length-2]);
			}
			catch(NumberFormatException e) {
				response.setContentType("text/plain");
				out.append("Dispositivo non riconosciuto!");
				return;
			}
			
			if (id == null) {
				out.append("ID "+id+" non esistente");
				return;
			}
			
			out.append(getReadings(id));
			return;
		}

		/*
		 * ENDPOINT: ritorna json con tutti i dispositivi registrati
		 * /Devices ritorna le letture del dispositivo 0
		 */
		
		if(endpoint.equals("devices")) { //ritorna un json con tutti i dispositivi
			response.setContentType("application/json");
			
			out.append(getDevicesJson());
			
			return;
		}
		
		return;
	}
		
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		String [] strings = request.getRequestURI().split("/");
		String endpoint = strings[strings.length-1];
		
		System.out.println("URI : "+request.getRequestURI());
		System.out.println("endpoint = "+endpoint);
		
		if(endpoint.equals("readings")) { //ritorna tutte le letture di un dispositivo
			response.setContentType("application/json");
			
			Integer id = null;
			try {
				
				id = Integer.parseInt(strings[strings.length-2]);
			}
			catch(NumberFormatException e) {
				response.setContentType("text/plain");
				out.append("Dispositivo non riconosciuto!");
				return;
			}
			
			if (id == null) {
				out.append("ID "+id+" non esistente");
				return;
			}
			
			out.append(getReadings(id));
			return;
		}
		
		
		String body = request.getReader().lines()
			    .reduce("", (accumulator, actual) -> accumulator+"\n" + actual);
		System.out.println("body" + body);
		Gson gson = new Gson();
		ReadingBean reading = gson.fromJson(body, ReadingBean.class);
		System.out.println("body = "+body);
		response.setContentType("text/plain");
		response.getWriter().append(reading.toString());
		response.getWriter().append("Content-Length: "+response.getHeader("content-length"));
	}
	
	/*
	 * 
	 * 
	 * METHODS
	 * 
	 * 
	 */
	
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
	
	private String [] toJson(Object [] objects) {
		Gson gson = new Gson();
		String [] strings = new String[objects.length];
		for(int i = 0; i < objects.length; i++) {
			strings[i] = gson.toJson(objects[i]);
		}
		
		return strings;	
	}
	
	private String addJsonObject(String source, Object object) {
		Gson gson = new Gson();
		
		return source + gson.toJson(object);
	}
}
