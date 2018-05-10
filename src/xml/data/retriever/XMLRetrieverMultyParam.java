package xml.data.retriever;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

	public static String retrieveData(String xmlFilePath, String tagName,
			String[] attributeNames, String[] attributes) {
		File xmlData = new File(xmlFilePath);
		if (!xmlData.exists()) {
			System.out.println("File not found : " + xmlData.toString());
			return null;
		}

		Map<String, Set<String>> map = toMap(attributeNames, attributes);

		Boolean tagFound = Boolean.FALSE;
		StringBuilder result = new StringBuilder();
		XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
		XMLEventReader xmlEventReader = null;

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
							Set<String> values = map.get(attributeName);
							if (values != null) {
								String nextValue = next.getValue();
								if (values.contains(nextValue)) {
									result.append(nextValue);
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
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} finally {
			if (xmlEventReader != null) {
				try {
					xmlEventReader.close();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				}
			}
		}

		return result.toString();
	}

	private static Map<String, Set<String>> toMap(String[] attributeNames,
			String[] attributes) {
		Map<String, Set<String>> map = new HashMap<>();
		for (int i = 0; i < attributeNames.length; i++) {
			String attributeName = attributeNames[i];
			Set<String> values = map.get(attributeName);
			if (values == null) {
				values = new HashSet<>();
				values.add(attributes[i]);
				map.put(attributeName, values);
			} else {
				values.add(attributes[i]);
			}
		}
		return map;
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
		if (namesOfAttributes.length != valuesOfAttributes.length) {
			System.out
					.println("Values corresponding to the attribute names are not of equal number");
			return;
		}
		String result = retrieveData(filePath, tagName, namesOfAttributes,
				valuesOfAttributes);
		System.out.println(result);
	}
}
