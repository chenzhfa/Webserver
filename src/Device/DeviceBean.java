package Device;

public class DeviceBean {
	final String id;
	final String MAC;
	final String location;
	
	DeviceBean(String id, String MAC, String location) {
		this.id = id;
		this.MAC = MAC;
		this.location = location;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return id+";"+MAC+";"+location+";";
	}
}
