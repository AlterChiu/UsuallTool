package netCDF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ucar.ma2.Array;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class NetcdfBasicControl {
	private NetcdfFile netFile;

	private List<String> variablesKeys;
	private Map<String, Map<String, Attribute>> variablesAtrributes;
	private Map<String, Map<String, Dimension>> variablesDimention;

	public NetcdfBasicControl(String filePath) throws IOException {
		this.netFile = NetcdfFile.open(filePath);
		constructProcess();
	}

	public NetcdfBasicControl(NetcdfFile netFile) {
		this.netFile = netFile;
		constructProcess();
	}

	private void constructProcess() {
		// get variable keys
		try {
			this.variablesKeys.clear();
		} catch (Exception exception) {
			this.variablesKeys = new ArrayList<>();
		}

		// get variable attribute
		try {
			this.variablesAtrributes.clear();
		} catch (Exception except) {
			this.variablesAtrributes = new TreeMap<>();
		}

		// get variable dimension
		try {
			this.variablesDimention.clear();
		} catch (Exception except) {
			this.variablesDimention = new TreeMap<>();
		}

		this.netFile.getVariables().forEach(variable -> {

			// get variable keys
			this.variablesKeys.add(variable.getFullName());

			// get variable attribute
			Map<String, Attribute> attributeMap = new TreeMap<>();
			variable.getAttributes().forEach(attribute -> {
				attributeMap.put(attribute.getFullName(), attribute);
			});

			// get variable dimension
			Map<String, Dimension> dimensionMap = new TreeMap<>();
			variable.getDimensions().forEach(dimension -> {
				dimensionMap.put(dimension.getFullName(), dimension);
			});

		});

	}

	public NetcdfFile getNetFile() {
		return this.netFile;
	}

	// get variable keys
	public List<String> getVariableKeys() {
		return this.variablesKeys;
	}

	// get variable
	public Variable getVariable(int index) {
		return this.netFile.getVariables().get(index);
	}

	public Variable getVaiable(String key) {
		return getVariable(this.variablesKeys.indexOf(key));
	}

	// get variable values
	public List<Object> getVariableValues(int index) throws IOException {
		List<Integer> dimensionLength = this.getVariableDimentionLength(index);
		Collections.reverse(dimensionLength);

		List<Object> outList = new ArrayList<>();
		Array values = this.netFile.getVariables().get(index).read();

		for (int scan = 0; scan < values.getSize(); scan++) {
			outList.add(values.getObject(scan));
		}

		List<Object> reSortList = new ArrayList<>();
		for (int dimension = 0; dimension < dimensionLength.size() - 1; dimension++) {

			// tempt valueIndex
			int temptIndex = 0;
			List<Object> temptList = new ArrayList<>();
			for (Object value : outList) {
				temptList.add(value);
				temptIndex++;

				if (temptIndex == dimensionLength.get(dimension)) {
					temptIndex = 0;
					reSortList.add(temptList);
					temptList = new ArrayList<>();
				}
			}

			outList = reSortList;
			reSortList = new ArrayList<>();
		}
		return outList;
	}

	public List<Object> getVariableValues(String key) throws IOException {
		return this.getVariableValues(this.variablesKeys.indexOf(key));
	}

	// get dimension full name of variable
	public List<String> getVariableDimentionKey(int index) {
		List<String> dimentionNameList = new ArrayList<>();
		this.netFile.getVariables().get(index).getDimensions().forEach(e -> dimentionNameList.add(e.getFullName()));
		return dimentionNameList;
	}

	public List<String> getVariableDimentionKey(String key) {
		return getVariableDimentionKey(this.variablesKeys.indexOf(key));
	}

	// get the dimension length of variable
	public List<Integer> getVariableDimentionLength(int index) {
		List<Integer> dimentionLength = new ArrayList<>();
		this.netFile.getVariables().get(index).getDimensions().forEach(e -> {
			dimentionLength.add(e.getLength());
		});
		return dimentionLength;
	}

	public List<Integer> getVariableDimentionLength(String key) {
		return this.getVariableDimentionLength(this.variablesKeys.indexOf(key));
	}
}
