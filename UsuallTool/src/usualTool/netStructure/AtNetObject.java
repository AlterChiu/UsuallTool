package usualTool.netStructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class AtNetObject {

	private Map<String, Object> attributes = new HashMap<>();
	private String text = null;
	private String nameId = "@__id";

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void removeAttributes(String key) {
		try {
			this.attributes.remove(key);
		} catch (Exception e) {
			new Exception("*WARN* no key exsis, " + key);
		}
	}

	protected Object get(String key) {
		return this.attributes.get(key);
	}

	public String getAsString(String key) {
		return this.get(key).toString();
	}

	public int getAsInt(String key) {
		return (int) this.get(key);
	}

	public double getAsDouble(String key) {
		return (double) this.get(key);
	}

	public boolean getAsBoolean(String key) {
		return (boolean) this.get(key);
	}

	public AtNetObject getAsObject(String key) {
		return (AtNetObject) this.get(key);
	}

	public AtNetArray getAsArray(String key) {
		return (AtNetArray) this.get(key);
	}

	public void addAttribute(String key, String string) {
		this.attributes.put(key, string);
	}

	public void addAttribute(String key, Boolean boo) {
		this.attributes.put(key, boo);
	}

	public void addAttribute(String key, boolean boo) {
		this.attributes.put(key, (Boolean) boo);
	}

	public void addAttribute(String key, double value) {
		this.attributes.put(key, (Double) value);
	}

	public void addAttribute(String key, Double value) {
		this.attributes.put(key, value);
	}

	public void addAttribute(String key, int value) {
		this.attributes.put(key, (Integer) value);
	}

	public void addAttribute(String key, Integer value) {
		this.attributes.put(key, value);
	}

	public void addAttribute(String key, AtNetObject value) {
		this.attributes.put(key, value);
	}

	public void addAttribute(String key, AtNetArray value) {
		this.attributes.put(key, value);
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String toJson() {
		JsonObject outObject = new JsonObject();

		// name
		outObject.addProperty(this.nameId, this.text);

		// properties
		this.attributes.keySet().forEach(key -> {
			Object value = this.attributes.get(key);
			if (value instanceof Double) {
				outObject.addProperty(key, this.getAsDouble(key));
			} else if (value instanceof Integer) {
				outObject.addProperty(key, this.getAsInt(key));
			} else if (value instanceof String) {
				outObject.addProperty(key, this.getAsString(key));
			} else if (value instanceof Boolean) {
				outObject.addProperty(key, this.getAsBoolean(key));
			} else if (value instanceof AtNetObject) {
				outObject.add(key, new JsonParser().parse(this.getAsObject(key).toJson()).getAsJsonObject());
			} else if (value instanceof AtNetArray) {
				outObject.add(key, new JsonParser().parse(this.getAsArray(key).toJson()).getAsJsonObject());
			} else {
				new Exception("*WARN* not available instance, while parsing：" + key);
			}
		});
		return outObject.toString();
	}

	public String toXml() {
		Element root = DocumentHelper
				.createElement(Optional.ofNullable(this.attributes.get(this.nameId).toString()).orElse(this.nameId));
		// properties
		this.attributes.keySet().forEach(key -> {
			Object value = this.attributes.get(key);
			if (value instanceof Double) {
				root.addElement(key).addText(String.valueOf(value)).addAttribute("type", "double");
			} else if (value instanceof Integer) {
				root.addElement(key).addText(String.valueOf(value)).addAttribute("type", "int");
			} else if (value instanceof String) {
				root.addElement(key).addText(String.valueOf(value)).addAttribute("type", "String");
			} else if (value instanceof Boolean) {
				root.addElement(key).addText(String.valueOf(value)).addAttribute("type", "boolean");
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
				new Exception("*WARN* not available instance, while parsing：" + key);
			}
		});

		return root.asXML();
	}
}
