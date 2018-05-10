package xml.data.retriever;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class XMLRetrieverMultyParam {

	public static void retrieveData(String xmlFilePath, String tagName,
			String[] attributeNames, String[] attributes) {
		File xmlData = new File(xmlFilePath);
		if (!xmlData.exists()) {
			System.out.println("File not found : " + xmlData.toString());
			return;
		}

		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < attributeNames.length; i++) {
			map.put(attributeNames[i], attributes[i]);
		}

		Boolean tagFound = Boolean.FALSE;
		StringBuilder result = new StringBuilder();
		XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
		XMLEventReader xmlEventReader;

		try {
			xmlEventReader = xmlInputFactory
					.createXMLEventReader(new FileReader(xmlData));
			while (xmlEventReader.hasNext()) {
				XMLEvent event = xmlEventReader.nextEvent();
				switch (event.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					StartElement startElement = event.asStartElement();
					if (startElement.getName().getLocalPart().equals(tagName)) {
						@SuppressWarnings("unchecked")
						Iterator<Attribute> attributeIterator = startElement
								.getAttributes();
						while (attributeIterator.hasNext()) {
							Attribute next = attributeIterator.next();
							String attributeName = next.getName()
									.getLocalPart();
							String attributeValue = map.get(attributeName);
							if (attributeValue != null) {
								if (next.getValue().equals(attributeValue)) {
									result.append(attributeValue);
									result.append(": ");
									tagFound = Boolean.TRUE;
									break;
								}
							}
						}
					}
					break;
				case XMLStreamConstants.CHARACTERS:
					if (tagFound.equals(Boolean.TRUE)) {
						Characters chars = event.asCharacters();
						result.append(chars.getData());
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					EndElement endElement = event.asEndElement();
					if (endElement.getName().getLocalPart().equals(tagName)
							&& tagFound.equals(Boolean.TRUE)) {
						tagFound = Boolean.FALSE;
						result.append(System.lineSeparator());
					}
					break;
				default:
					break;
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(result);
	}

	public static void main(String[] args) {
		if (args.length != 4) {
			System.out.println("There are too much or too few arguments");
			return;
		}
		String filePath = args[0];
		String tagName = args[1];
		String[] namesOfAttributes = args[2].split(",");
		String[] valuesOfAttributes = args[3].split(",");
		
		retrieveData(filePath, tagName, namesOfAttributes, valuesOfAttributes);
	}

}
