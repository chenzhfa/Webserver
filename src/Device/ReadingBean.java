package Device;

import java.util.ArrayList;
import java.util.HashMap;

/*
 *	TODO:
 *	Quando invio le temperature ne invio una serie o una alla volta?
 *  https://app.swaggerhub.com/apis-docs/sorby/SensoriASL/1.0.0-oas3#/Rilevazioni%20di%20un%20microcontrollore/addDeviceReading
*/

public class ReadingBean {
	
	final String id;
	final int sensorProgressive;
	final double reading;
	
	ReadingBean(String id, int sensorProgressive, double reading) {
		this.id = id;
		this.sensorProgressive = sensorProgressive;
		this.reading = reading;
	}
	
	@Override
		public String toString() {
			// TODO Auto-generated method stub
			return id+";"+sensorProgressive+";"+reading+";";
		}
}
