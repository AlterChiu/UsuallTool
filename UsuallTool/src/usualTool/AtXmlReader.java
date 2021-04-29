package usualTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AtXmlReader {
//	private String nameSpace = "//";
	private Document document;

	// <+++++++++++++++++++++++++++++++++++++++>
	// <+++++++++++++ Constructor +++++++++++++++++>
	// <+++++++++++++++++++++++++++++++++++++++>
	public AtXmlReader(String xmlFileAdd, String encode) throws IOException {
		this.document = Jsoup.parse(new File(xmlFileAdd), encode);
	}

	public AtXmlReader(Document doc) {
		this.document = doc;
	}

	public static AtXmlReader xmlParser(String xmlContent) {
		return new AtXmlReader(Jsoup.parse(xmlContent));
	}

	public List<Element> getNodeByTag(String tag) {
		List<Element> outList = new ArrayList<>();
		this.document.getElementsByTag(tag).forEach(e -> outList.add(e));
		return outList;
	}

	public List<Element> getNodeByName(String name) {
		List<Element> outList = new ArrayList<>();
		this.document.getElementsByAttributeValue("name", name).forEach(e -> outList.add(e));
		return outList;
	}

}
