
package usualTool;

import java.awt.Color;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class AtColorControl {

	private TreeMap<Double, Integer[]> colorMap = new TreeMap<>();
	private int dataDecimale = 4;

	public AtColorControl(Map<Double, Integer[]> colorMap) {
		// initial collection
		colorMap.keySet().forEach(key -> {
			Integer[] rgba = colorMap.get(key);

			try {
				boolean checker = true;
				for (int index = 0; index < 3; index++) {
					if (rgba[index] > 255 || rgba[index] < 0) {
						checker = false;
						break;
					}
				}

				if (checker) {
					// get alpha
					int alpha = Optional.ofNullable(rgba[3]).orElse(255);
					Integer[] outRGBA = new Integer[] { rgba[0], rgba[1], rgba[2], alpha };
					this.colorMap.put(key, outRGBA);
				}
			} catch (Exception e) {
			}
		});
	}

	public Color getExtractColor(double value) {
		Double key = Optional.ofNullable(this.colorMap.lowerKey(value)).orElse(this.colorMap.firstKey());
		Integer rgba[] = this.colorMap.get(key);
		return new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
	}

	public Color getNeareastColor(double value) {
		Double lowerKey = Optional.ofNullable(this.colorMap.lowerKey(value)).orElse(this.colorMap.firstKey());
		Integer[] lowerRGBA = this.colorMap.get(lowerKey);

		Double higherKey = Optional.ofNullable(this.colorMap.higherKey(value)).orElse(this.colorMap.lastKey());
		Integer[] higherRGBA = this.colorMap.get(higherKey);


		if (Math.abs(higherKey - lowerKey) < Math.pow(10, -1 * this.dataDecimale)) {
			return new Color(lowerRGBA[0], lowerRGBA[1], lowerRGBA[2], lowerRGBA[3]);
		} else {

			double lowerRatio = (value - lowerKey) / (higherKey - lowerKey);
			double higherRatio = (higherKey - value) / (higherKey - lowerKey);
			Integer[] outRGBA = new Integer[] { (int) (lowerRatio * lowerRGBA[0] + higherRatio * higherRGBA[0]),
					(int) (lowerRatio * lowerRGBA[1] + higherRatio * higherRGBA[1]),
					(int) (lowerRatio * lowerRGBA[2] + higherRatio * higherRGBA[2]),
					(int) (lowerRatio * lowerRGBA[3] + higherRatio * higherRGBA[3]) };

			return new Color(outRGBA[0], outRGBA[1], outRGBA[2], outRGBA[3]);
		}

	}

	public Color getColor(double value, ColorModel model) {

		switch (model) {
		case Extract:
			return getExtractColor(value);
		case Nearest:
			return getNeareastColor(value);
		default:
			return getNeareastColor(value);
		}

	}

	public static enum ColorModel {
		Extract, Nearest;
	}

	public static Map<Double, Integer[]> FEWS_RainfallScale() {
		Map<Double, Integer[]> outMap = new TreeMap<>();
		outMap.put(0.0, new Integer[] { 255, 255, 255, 0 });
		outMap.put(0.001, new Integer[] { 255, 255, 255, 76 });
		outMap.put(1., new Integer[] { 219, 219, 219, 125 });
		outMap.put(2., new Integer[] { 175, 238, 238, 125 });
		outMap.put(6., new Integer[] { 0, 191, 255, 125 });
		outMap.put(10., new Integer[] { 79, 148, 205, 125 });
		outMap.put(15., new Integer[] { 0, 0, 255, 125 });
		outMap.put(20., new Integer[] { 50, 205, 50, 125 });
		outMap.put(30., new Integer[] { 124, 252, 0, 125 });
		outMap.put(40., new Integer[] { 255, 255, 0, 125 });
		outMap.put(50., new Integer[] { 255, 236, 139, 125 });
		outMap.put(70., new Integer[] { 255, 165, 79, 125 });
		outMap.put(90., new Integer[] { 255, 0, 0, 125 });
		outMap.put(110., new Integer[] { 205, 38, 38, 125 });
		outMap.put(130., new Integer[] { 205, 0, 205, 125 });
		outMap.put(150., new Integer[] { 205, 41, 144, 125 });
		outMap.put(200., new Integer[] { 255, 0, 255, 125 });
		outMap.put(300., new Integer[] { 255, 240, 245, 125 });

		return outMap;
	}

	public static Map<Double, Integer[]> FEWS_FloodDepth() {
		Map<Double, Integer[]> outMap = new TreeMap<>();
		outMap.put(0., new Integer[] { 255, 255, 255, 0 });
		outMap.put(0.001, new Integer[] { 173, 216, 230, 125 });
		outMap.put(0.5, new Integer[] { 255, 228, 181, 125 });
		outMap.put(1., new Integer[] { 255, 185, 15, 125 });
		outMap.put(1.5, new Integer[] { 255, 165, 0, 125 });
		outMap.put(2.0, new Integer[] { 255, 0, 0, 125 });
		outMap.put(2.5, new Integer[] { 205, 41, 144, 125 });
		outMap.put(3.0, new Integer[] { 255, 0, 255, 125 });

		return outMap;
	}



}
