package Device;


import java.util.HashMap;
import java.util.Map;

public class DeviceBean {

	private Integer id;
	private String mac;
	private Integer location;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	public DeviceBean(String mac, Integer location) {
		this.location = location;
		this.mac = mac;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public Integer getLocation() {
		return location;
	}

	public void setLocation(Integer location) {
		this.location = location;
	}

	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

//	@Override
//	public String toString() {
//		return new ToStringBuilder(this).append("id", id).append("mac", mac).append("location", location).append("additionalProperties", additionalProperties).toString();
//	}

}