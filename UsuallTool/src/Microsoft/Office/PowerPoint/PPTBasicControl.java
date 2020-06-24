package Microsoft.Office.PowerPoint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFFreeformShape;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTextBox;

import geo.gdal.GdalGlobal;

public class PPTBasicControl  implements Closeable{
	private XMLSlideShow pptFile = new XMLSlideShow();
	private String filePath;

	// <+++++++++++++++++++++++++++++>
	// <++++++++++ Construct ++++++++++++>
	// <+++++++++++++++++++++++++++++>

	// read existing pptFile
	public PPTBasicControl(String filePath) throws IOException {
		File file = new File(filePath);
		FileInputStream inputstream = new FileInputStream(file);
		this.pptFile = new XMLSlideShow(inputstream);
		this.filePath = filePath;
	}
	
	public final void close() {

		// save file
		try {
			this.pptFile.write(new FileOutputStream(this.filePath));
		} catch (Exception e) {
			try {
				this.pptFile.write(new FileOutputStream(this.filePath + "_tempt.pptx"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		// close file
		try {
			this.pptFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// <=================================================>

	// <+++++++++++++++++++++++++++++>
	// <++++++++++ functions ++++++++++++>
	// <+++++++++++++++++++++++++++++>
	public void addNewSlide() {
		this.pptFile.createSlide();
	}

	public void deletSlide(int index) {
		this.pptFile.removeSlide(index);
	}

	public List<XSLFSlide> getSlides() {
		return this.pptFile.getSlides();
	}

	public Map<String, PPTObject> getPPTObjects(int slideIndex) {
		Map<String, PPTObject> outMap = new HashMap<>();

		getTables(slideIndex).forEach(object -> outMap.put(object.getName(), object));

		getShapes(slideIndex).forEach(object -> outMap.put(object.getName(), object));

		getTextBox(slideIndex).forEach(object -> outMap.put(object.getName(), object));

		getPicture(slideIndex).forEach(object -> outMap.put(object.getName(), object));

		return outMap;
	}

	public List<PPTTable> getTables(int slideIndex) {
		List<PPTTable> outTables = new ArrayList<>();
		for (XSLFShape shape : this.pptFile.getSlides().get(slideIndex)) {

			// get table object
			if (shape instanceof XSLFTable) {
				outTables.add(new PPTTable((XSLFTable) shape));
			}
		}
		return outTables;
	}

	public List<PPTShape> getShapes(int slideIndex) {
		List<PPTShape> outShapes = new ArrayList<>();

		for (XSLFShape shape : this.pptFile.getSlides().get(slideIndex)) {

			// get table object
			if (shape instanceof XSLFAutoShape) {
				outShapes.add(new PPTShape((XSLFAutoShape) shape));
			} else if (shape instanceof XSLFFreeformShape) {
				outShapes.add(new PPTShape((XSLFAutoShape) shape));
			}
		}
		return outShapes;
	}

	public List<PPTTextBox> getTextBox(int slideIndex) {
		List<PPTTextBox> outTextBox = new ArrayList<>();

		for (XSLFShape shape : this.pptFile.getSlides().get(slideIndex)) {

			// get table object
			if (shape instanceof XSLFTextBox) {
				outTextBox.add(new PPTTextBox((XSLFTextBox) shape));
			}
		}
		return outTextBox;
	}

	public List<PPTPicture> getPicture(int slideIndex) {
		List<PPTPicture> outPicture = new ArrayList<>();

		for (XSLFShape shape : this.pptFile.getSlides().get(slideIndex)) {

			// get table object
			if (shape instanceof XSLFPictureShape) {
				outPicture.add(new PPTPicture((XSLFPictureShape) shape));
			}
		}
		return outPicture;
	}

	public void createShape(Path2D path, int slideIndex) {

		// adjust path
		Path2D temptPath = GdalGlobal.pathFixPoint(path, 0, 0);

		// get fixed Bound
		Rectangle temptRec = temptPath.getBounds();
		double temptRecWidth = temptRec.getWidth();
		double temptRecHeight = temptRec.getHeight();

		double slideWidth = this.pptFile.getPageSize().getWidth();
		double slideHeight = this.pptFile.getPageSize().getHeight();

		double widthRatio = slideWidth / temptRecWidth;
		double heightRatio = slideHeight / temptRecHeight;

		// get ratio
		double ratioSize = 0;
		if (widthRatio > heightRatio) {
			ratioSize = heightRatio;
		} else {
			ratioSize = widthRatio;
		}

		temptPath = GdalGlobal.pathAdjustRatio(temptPath, 0, 0, ratioSize, ratioSize);
		temptPath = GdalGlobal.pathAdjustMirroXLine(temptPath, temptPath.getBounds().getCenterY());
		createShape_PrivateFunction(temptPath, slideIndex);
	}

	public void createShape(List<Path2D> pathList, int slideIndex) {
		double groupMinX = Double.POSITIVE_INFINITY;
		double groupMinY = Double.POSITIVE_INFINITY;
		double groupMaxY = Double.NEGATIVE_INFINITY;
		double groupMaxX = Double.NEGATIVE_INFINITY;

		for (Path2D temptPath : pathList) {
			Rectangle rec = temptPath.getBounds();
			if (rec.getMinX() < groupMinX) {
				groupMinX = rec.getMinX();
			}
			if (rec.getMaxX() > groupMaxX) {
				groupMaxX = rec.getMaxX();
			}
			if (rec.getMinY() < groupMinY) {
				groupMinY = rec.getMinY();
			}
			if (rec.getMaxY() > groupMaxY) {
				groupMaxY = rec.getMaxY();
			}
		}

		double pathWidth = groupMaxX - groupMinX;
		double pathHeight = groupMaxY - groupMinY;
		double pageWidth = this.pptFile.getPageSize().getWidth();
		double pageHeight = this.pptFile.getPageSize().getHeight();

		// get ratio
		double widthRatio = pageWidth / pathWidth;
		double heightRatio = pageHeight / pathHeight;

		double ratioSize = 0;
		if (widthRatio > heightRatio) {
			ratioSize = heightRatio;
		} else {
			ratioSize = widthRatio;
		}

		// flip y
		double flipY = pageHeight / 2;

		for (Path2D temptPath : pathList) {
			Rectangle temptRec = temptPath.getBounds();
			Path2D adjustedPath = GdalGlobal.pathFixPoint(temptPath, temptRec.getMinX() - groupMinX,
					temptRec.getMinY() - groupMinY);

			adjustedPath = GdalGlobal.pathAdjustRatio(adjustedPath, 0, 0, ratioSize, ratioSize);
			adjustedPath = GdalGlobal.pathAdjustMirroXLine(adjustedPath, flipY);

			createShape_PrivateFunction(adjustedPath, slideIndex);
		}
	}

	private void createShape_PrivateFunction(Path2D path, int slideIndex) {
		Shape shape = path.createTransformedShape(AffineTransform.getScaleInstance(1, 1));
		Path2D.Double pathDouble = new Path2D.Double(shape);
		XSLFFreeformShape freeForm = this.pptFile.getSlides().get(slideIndex).createFreeform();
		freeForm.setPath(pathDouble);
		freeForm.setFillColor(Color.white);
		freeForm.setLineColor(Color.black);
		freeForm.setLineWidth(1);
	}

	// <+++++++++++++++++++++++++++++>
	// <+++++++ public static function++++++++>
	// <+++++++++++++++++++++++++++++>
	public static void createNewPowerPoint(String filePath) throws IOException {
		XMLSlideShow ppt = new XMLSlideShow();
		File file = new File(filePath);
		FileOutputStream out = new FileOutputStream(file);

		// add a slide for initial
		ppt.createSlide();

		ppt.write(out);
		ppt.close();
	}
	// <=================================================>

	// <+++++++++++++++++++++++++++++>
	// <+++++++ public static class ++++++++++>
	// <+++++++++++++++++++++++++++++>
	public interface PPTObject {
		public String getName();

		public int getId();

		public String getXmlString();
	}

	public class PPTTable implements PPTObject {
		private XSLFTable table;

		public PPTTable(XSLFTable table) {
			this.table = table;
		}

		public String getValue(int row, int column) {
			return this.table.getCell(row, column).getText();
		}

		public void setValue(int row, int column, String value) {
			this.table.getCell(row, column).setText(value);
		}

		public void setColor(int row, int column, String color) {
			this.table.getCell(row, column).setFillColor(Color.decode(color));
		}

		public void setColor(int row, int column, Color color) {
			this.table.getCell(row, column).setFillColor(color);
		}

		public int getRowCount() {
			return this.table.getRows().size();
		}

		public int getColumnCount(int rowIndex) {
			return this.table.getRows().get(rowIndex).getCells().size();
		}

		public XSLFTable getTable() {
			return this.getTable();
		}

		public String getName() {
			return this.table.getShapeName();
		}

		public int getId() {
			return this.table.getShapeId();
		}

		public String getXmlString() {
			return this.table.getXmlObject().toString();
		}
	}

	public class PPTShape implements PPTObject {
		public XSLFSimpleShape shape;

		public PPTShape(XSLFSimpleShape shape) {
			this.shape = shape;
		}

		public void setFillColor(Color color) {
			this.shape.setFillColor(color);
		}

		public void setFillColor(String color) {
			this.setFillColor(Color.decode(color));
		}

		public void setLineColor(Color color) {
			this.shape.setLineColor(color);
		}

		public void setLineColor(String color) {
			this.setLineColor(Color.decode(color));
		}

		public void reShape(Graphics2D graphic) {
			this.shape.draw(graphic, null);
		}

		public XSLFSimpleShape getShape() {
			return this.shape;
		}

		public int getID() {
			return this.shape.getShapeId();
		}

		public String getName() {
			return this.shape.getShapeName();
		}

		public String getXmlString() {
			return this.shape.getXmlObject().toString();
		}

		public int getId() {
			return this.shape.getShapeId();
		}
	}

	public class PPTTextBox implements PPTObject {
		private XSLFTextBox textBox;

		public PPTTextBox(XSLFTextBox textBox) {
			this.textBox = textBox;
		}

		public String getText() {
			return this.textBox.getText();
		}

		public void setText(String text) {
			this.textBox.setText(text);
		}

		public void setFillColor(Color color) {
			this.textBox.setFillColor(color);
		}

		public void setFillColor(String color) {
			this.setFillColor(Color.decode(color));
		}

		public void setLineColor(Color color) {
			this.textBox.setLineColor(color);
		}

		public void setLineColor(String color) {
			this.textBox.setLineColor(Color.decode(color));
		}

		public String getName() {
			return this.textBox.getShapeName();
		}

		public int getID() {
			return this.textBox.getShapeId();
		}

		public XSLFTextBox getTextBox() {
			return this.textBox;
		}

		public String getXmlString() {
			return this.textBox.getXmlObject().toString();
		}

		public int getId() {
			return this.textBox.getShapeId();
		}

	}

	public class PPTPicture implements PPTObject {
		private XSLFPictureShape picture;

		public PPTPicture(XSLFPictureShape picture) {
			this.picture = picture;
		}

		public String getName() {
			return this.picture.getShapeName();
		}

		public int getID() {
			return this.picture.getShapeId();
		}

		public XSLFPictureShape getPicture() {
			return this.picture;
		}

		public void replacePicture(String picturePath) throws FileNotFoundException, IOException {
			byte[] byteArray = IOUtils.toByteArray(new FileInputStream(picturePath));
			replacePicture(byteArray);
		}

		public void replacePicture(byte[] byteArray) throws IOException {
			ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
			BufferedImage bImage = ImageIO.read(bis);

			int sourceWidth = bImage.getWidth();
			int sourceHeight = bImage.getHeight();
			double sourceRatio = sourceHeight * 1.0 / sourceWidth;

			Rectangle targetRec = this.picture.getAnchor().getBounds();
			double targetWidth = targetRec.getWidth();
			double reTargetHeight = sourceRatio * targetWidth;

			double targetMinX = targetRec.getCenterX() - 0.5 * targetWidth;
			double targetMinY = targetRec.getCenterY() - 0.5 * reTargetHeight;

			Rectangle reTargetRec = new Rectangle((int) targetMinX, (int) targetMinY, (int) targetWidth,
					(int) reTargetHeight);

			this.picture.setAnchor(reTargetRec);
			this.picture.getPictureData().setData(byteArray);
		}

		public byte[] getPictrueByte() {
			return this.picture.getPictureData().getData();
		}

		public int getId() {
			return this.picture.getShapeId();
		}

		@Override
		public String getXmlString() {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
