package usualTool.netStructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.geotools.filter.expression.ThisPropertyAccessorFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class AtNetArray {

	private List<Object> values;
	private String nameId = "temptArray";
	private Object arrayType;

	public AtNetArray(String id, Object type) {
		this.nameId = id;
		this.arrayType = type;
		this.values = new ArrayList<>();
	}

	public void addValue(Object value) {
		if (arrayType.getClass() == value.getClass()) {
			this.values.add(value);
		} else {
			new Exception("*WARN* not same instance class, " + value.getClass());
		}
	}

	public void removeValue(int index) {
		this.values.remove(index);
	}

	public void setValue(int index, Object value) {
		this.removeValue(index);
		this.values.add(index, value);
	}

	public Object getValue(int index) {
		return this.values.get(index);
	}

	public String getValueAsString(int index) {
		return (String) this.getValue(index);
	}

	public Double getValueAsDouble(int index) {
		return (Double) this.getValue(index);
	}

	public Integer getValueAsInteger(int index) {
		return (Integer) this.getValue(index);
	}

	public Boolean getValueAsBoolean(int index) {
		return (Boolean) this.getValue(index);
	}

	public AtNetArray getValueAsArray(int index) {
		return (AtNetArray) this.getValue(index);
	}

	public AtNetObject getValueAsObject(int index) {
		return (AtNetObject) this.getValue(index);
	}

	public List<Object> getValues() {
		return this.values;
	}

	public List<String> getValuesAsString() {
		return this.values.parallelStream().map(e -> (String) e).collect(Collectors.toList());
	}

	public List<Double> getValueAsDouble() {
		return this.values.parallelStream().map(e -> (Double) e).collect(Collectors.toList());
	}

	public List<Integer> getValueAsInteger() {
		return this.values.parallelStream().map(e -> (Integer) e).collect(Collectors.toList());
	}

	public List<Boolean> getValueAsBoolean() {
		return this.values.parallelStream().map(e -> (Boolean) e).collect(Collectors.toList());
	}

	public List<AtNetArray> getValueAsArray() {
		return this.values.parallelStream().map(e -> (AtNetArray) e).collect(Collectors.toList());
	}

	public List<AtNetObject> getValueAsObject() {
		return this.values.parallelStream().map(e -> (AtNetObject) e).collect(Collectors.toList());
	}

	public int getSize() {
		return this.values.size();
	}

	public String toJson() {
		JsonArray array = new JsonArray();
		// properties
		this.values.forEach(value -> {
			if (value instanceof Double) {
				array.add((Double) value);
			} else if (value instanceof Integer) {
				array.add((Integer) value);
			} else if (value instanceof String) {
				array.add((String) value);
			} else if (value instanceof Boolean) {
				array.add((Boolean) value);
			} else if (value instanceof AtNetObject) {
				array.add(((AtNetObject) value).toJson());
			} else if (value instanceof AtNetArray) {
				array.add(((AtNetArray) value).toJson());
			} else {
				new Exception("*WARN* not available instance, while parsing：" + value.getClass());
			}
		});

		return array.toString();
	}

	public List<String> toXml() {
		List<String> outList = new ArrayList<>();

		this.values.forEach(value -> {
			Element root = DocumentHelper.createElement(this.nameId);
			if (value instanceof Double) {
				root.addText(String.valueOf(value)).addAttribute("type", "double");
			} else if (value instanceof Integer) {
				root.addText(String.valueOf(value)).addAttribute("type", "int");
			} else if (value instanceof String) {
				root.addText(String.valueOf(value)).addAttribute("type", "String");
			} else if (value instanceof Boolean) {
				root.addText(String.valueOf(value)).addAttribute("type", "boolean");
			} else if (value instanceof AtNetObject) {
				try {
					root.add(DocumentHelper.parseText(((AtNetObject) value).toXml()));
				} catch (DocumentException e) {
					e.printStackTrace();
				}
			} else if (value instanceof AtNetArray) {
				((AtNetArray) value).toXml().forEach(xmlText -> {
					try {
						root.add(DocumentHelper.parseText(xmlText));
					} catch (DocumentException e) {
						e.printStackTrace();
					}
				});
			} else {
				new Exception("*WARN* not available instance, while parsing：" + value.getClass());
			}
			outList.add(root.asXML());
		});
		return outList;
	}

}
