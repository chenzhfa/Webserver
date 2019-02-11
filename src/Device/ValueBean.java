package Device;

import java.util.HashMap;
import java.util.Map;


public class ValueBean {

    private Integer sensorProgressive;
    private Double reading;
    transient private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public ValueBean(int sensorProgressive, double reading) {
        this.sensorProgressive = sensorProgressive;
        this.reading = reading;
    }


    public Integer getSensorProgressive() {
        return sensorProgressive;
    }

    public void setSensorProgressive(Integer sensorProgressive) {
        this.sensorProgressive = sensorProgressive;
    }

    public Double getReading() {
        return reading;
    }

    public void setReading(Double reading) {
        this.reading = reading;
    }


    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return new StringBuilder("sensorProgressive" + sensorProgressive).append("reading" + reading).toString();
    }

}