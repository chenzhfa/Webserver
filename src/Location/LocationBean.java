package Location;

import javax.xml.stream.Location;
import java.util.HashMap;
import java.util.Map;

public class LocationBean {

    private Integer id;
    private String name;
    transient private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public LocationBean(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return new StringBuilder("id" + id).append("name" + name).append("additionalProperties" + additionalProperties).toString();
    }

}