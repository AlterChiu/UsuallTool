package Microsoft.Office.PowerPoint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.sl.usermodel.FreeformShape;
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

public class PPTBasicControl {
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

	public void createShape(Path2D path, int slideIndex) {
		createShape(path, slideIndex, 1);
	}

	public void createShape(Path2D path, int slideIndex, int ratio) {
		Shape shape = path.createTransformedShape(AffineTransform.getScaleInstance(ratio, ratio));
		Path2D.Double pathDouble = new Path2D.Double(shape);
		FreeformShape freeForm = this.pptFile.getSlides().get(slideIndex).createFreeform();
		freeForm.setPath(pathDouble);
		freeForm.setFillColor(Color.black);
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
	public class PPTTable {
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

	public class PPTShape {
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
	}


	public class PPTTextBox {
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

	}


	public class PPTPicture {
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
			this.picture.getPictureData().setData(IOUtils.toByteArray(new FileInputStream(picturePath)));
		}

		public byte[] getPictrueByte() {
			return this.picture.getPictureData().getData();
		}
	}

}
