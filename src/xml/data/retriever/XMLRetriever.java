package xml.data.retriever;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class XMLRetriever {

	public static void retrieveData(String xmlFilePath, String tagName, String attributeName, String attribute) {
		File xmlData = new File(xmlFilePath);
		if(!xmlData.exists()) {
			System.out.println("File not found!");
			return;
		}
		
		Boolean tagFound = Boolean.FALSE;
		StringBuilder result = new StringBuilder();
		XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
		XMLEventReader xmlEventReader;
		
		try {
			xmlEventReader = xmlInputFactory.createXMLEventReader(new FileReader(xmlData));
			while(xmlEventReader.hasNext()) {
				XMLEvent event = xmlEventReader.nextEvent();
				switch(event.getEventType()) {
				case XMLStreamConstants.START_ELEMENT :
					StartElement startElement = event.asStartElement();
					System.out.println(startElement.getName());
					if(startElement.getName().getLocalPart().equals(tagName)) {
						Attribute attributeByName = startElement.getAttributeByName(QName.valueOf(attributeName));
						while(xmlEventReader.hasNext() && !xmlEventReader.peek().isCharacters())
						if(attributeByName.getValue().equals(attribute)) {
							result.append(attributeByName.getValue());
							result.append(": ");
							if(xmlEventReader.peek().isCharacters()) {
								tagFound = Boolean.TRUE;
								System.out.println("Tag found");
							} else {
								result.append(System.lineSeparator());
							}
						}
					}
					break;
				case XMLStreamConstants.CHARACTERS:
					if(tagFound.equals(Boolean.TRUE)) {
						System.out.println("data found");
						Characters chars = event.asCharacters();
						result.append(chars.getData());
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					tagFound = Boolean.FALSE;
					result.append(System.lineSeparator());
				default:break;
				};
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		
		System.out.println(result);
	}

	public static void main(String[] args) {
		retrieveData(args[0], args[1], args[2], args[3]);
	}

}
