package netCDF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;

public class NetcdfWriter {
	private NetcdfFileWriter writer;
	private Map<String, Object> globalAttribute = new TreeMap<>();
	private Map<String, Variable> Variables = new TreeMap<>();
	private Map<String, Integer> dimensionMap = new TreeMap<>();

	public NetcdfWriter(String saveAdd) throws IOException {
		this.writer = NetcdfFileWriter.createNew(NetcdfFileWriter.Version.netcdf3, saveAdd);
	}

	public void addGlobalAttribute(String key, Object value) {
		this.globalAttribute.put(key, value);
	}

	public void addGlobalAttribute(Map<String, Object> attribubteMap) {
		attribubteMap.keySet().forEach(key -> {
			this.globalAttribute.put(key, attribubteMap.get(key));
		});
	}

	public void addDimension(String name, int length) {
		this.dimensionMap.put(name, length);
	}

	public void addDimension(Map<String, Integer> dimensionMap) {
		dimensionMap.keySet().forEach(key -> {
			this.dimensionMap.put(key, dimensionMap.get(key));
		});
	}

	public void addVariable(String name, DataType type, String dimensionString) {
		this.Variables.put(name, this.writer.addVariable(null, name, type, dimensionString));
	}

	public void addVariable(String name, DataType type, List<String> dimensionString) {
		this.Variables.put(name, this.writer.addVariable(null, name, type, String.join(" ", dimensionString)));
	}

	public void addVariableAttribute(String varaibleName, String attributeKey, String attributeValue) {
		this.Variables.get(varaibleName).addAttribute(new Attribute(attributeKey, attributeValue));
	}

	public void addVariableAttribute(String varaibleName, String attributeKey, double attributeValue) {
		this.Variables.get(varaibleName).addAttribute(new Attribute(attributeKey, attributeValue));
	}

	public void addVariableAttribute(String varaibleName, String attributeKey, float attributeValue) {
		this.Variables.get(varaibleName).addAttribute(new Attribute(attributeKey, attributeValue));
	}

	public void addVariableAttribute(String varaibleName, String attributeKey, List attributeValue) {
		this.Variables.get(varaibleName).addAttribute(new Attribute(attributeKey, attributeValue));
	}

	public void addVariableAttribute(String varaibleName, Attribute attribute) {
		this.Variables.get(varaibleName).addAttribute(attribute);
	}

	public Map<String, Integer> getDimensionMap() {
		return this.dimensionMap;
	}

	public List<Dimension> getDimensionList() {
		return this.writer.getNetcdfFile().getDimensions();
	}

	public List<String> getDimensionKeyList() {
		return new ArrayList<String>(this.dimensionMap.keySet());
	}

	public List<Variable> getVaraibleList() {
		return this.writer.getNetcdfFile().getVariables();
	}

	public List<String> getVariableKeyList() {
		return new ArrayList<String>(this.Variables.keySet());
	}

	public void create() throws IOException {
		this.writer.create();
		this.writer.close();
	}
}
