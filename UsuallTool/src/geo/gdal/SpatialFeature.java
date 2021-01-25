package geo.gdal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.gdal.ogr.Geometry;

public class SpatialFeature {
	private Geometry geo;
	private Map<String, Object> properties;

	public SpatialFeature(Map<String, Object> properties, Geometry geometry) {
		this.properties = new HashMap<String, Object>();

		properties.keySet().forEach(key -> {
			try {
				this.properties.put(key, properties.get(key));
			} catch (Exception e) {
				this.properties.put(key, null);
			}
		});
		this.geo = geometry;
	}

	public SpatialFeature(Geometry geo) {
		this.properties = new HashMap<>();
		this.geo = geo;
	}

	public void addPropertie(String key, Object value) {
		this.properties.put(key, value);
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties.clear();
		this.properties = properties;
	}

	public List<String> getTitles() {
		return new ArrayList<String>(this.properties.keySet());
	}

	public void renameField(String oldName, String newName) {
		this.properties.put(newName, this.properties.get(oldName));
		this.properties.remove(oldName);
	}

	public Geometry getGeometry() {
		return this.geo;
	}

	public void setGeometry(Geometry geo) {
		this.geo = geo;
	}

	public Object getProperty(String key) {
		return Optional.ofNullable(this.properties.get(key)).orElse(null);
	}

	public Map<String, Object> getProperties() {
		return this.properties;
	}
}