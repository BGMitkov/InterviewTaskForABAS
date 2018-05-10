package xml.data.retriever;

public class XMLRetriverMultyParam {

	public static void main(String[] args) {
		if(args.length != 4) {
			System.out.println("There are too much or too few arguments");
			return;
		}
		String filePath = args[0];
		String tagName = args[1];
		String[] namesOfAttributes = args[2].split(",");
		String[] valuesOfAttributes = args[3].split(",");
	}

}
