package Device;

import java.util.ArrayList;
import java.util.HashMap;

/*
 *	TODO:
 *	Quando invio le temperature ne invio una serie o una alla volta?
 *  https://app.swaggerhub.com/apis-docs/sorby/SensoriASL/1.0.0-oas3#/Rilevazioni%20di%20un%20microcontrollore/addDeviceReading
*/
//
//public class ReadingBean {
//
//	public final String id;
//	public final int sensorProgressive;
//	public final double reading;
//
//	ReadingBean(String id, int sensorProgressive, double reading) {
//		this.id = id;
//		this.sensorProgressive = sensorProgressive;
//		this.reading = reading;
//	}
//
//	@Override
//		public String toString() {
//			// TODO Auto-generated method stub
//			return id+";"+sensorProgressive+";"+reading+";";
//		}
//}


import java.util.List;
import java.util.Map;

public class ReadingBean{

	private String mac;
	private String datetime;
	private List<ValueBean> value = null;
	transient private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	public ReadingBean(String mac, String datetime, List<ValueBean> value) {
		this.mac = mac;
		this.datetime = datetime;
		this.value = value;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public List<ValueBean> getValue() {
		return value;
	}

	public void setValue(List<ValueBean> value) {
		this.value = value;
	}

	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public String toString() {
		return new StringBuilder("mac" + mac).append("datetime" + datetime).append("value" + value).toString();
	}

}



