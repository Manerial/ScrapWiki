package scrapWiki;

import java.io.IOException;
import java.util.regex.Pattern;

public final class Launcher {
	public static Scrapper scrapper = new Scrapper();

	public static void main(String[] args) throws IOException {
		String message = parseArgs(args);
		if (message != null) {
			System.out.println(message);
			return;
		} else {
			scrapper.scrapToFile();
		}
	}

	private static String parseArgs(String[] args) {
		String message = null;
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-nc":
				scrapper.setCategory("Noms_communs");
				break;
			case "-v":
				scrapper.setCategory("Verbes");
				break;
			case "-a":
				scrapper.setCategory("Adjectifs");
				break;
			case "-l":
				Pattern pattern = Pattern.compile("^(500|[0-4]?[0-9]{0,2})$");
				if (pattern.matcher(args[i + 1]).matches()) {
					scrapper.setLimit(args[i + 1]);
				} else {
					message = "argument -l must be followed by a positive integer (max 500)";
				}
				break;
			case "-h":
				// @formatter:off
				message = String.join("\r\n", "", "Available arguments: ",
						"",
						"-nc \t: Use common_names type",
						"-v \t: Use verbs type",
						"-a \t:  Use adjectives type",
						"-s \t: Size of list to get",
						"-h \t: Help");
				// @formatter:on
				break;
			}
		}

		return message;
	}
}