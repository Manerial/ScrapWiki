package scrapWiki;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Scrapper {
	private static final String BASE_URL = "https://fr.wiktionary.org/w/api.php?action=query";
	private static final String P_LIST = "list=categorymembers";
	private static final String P_FORMAT = "format=json";
	private static final String P_TITLE = "cmtitle=Category:";
	private static final String P_LIMIT = "cmlimit=";
	private static final String P_CONTINUE = "cmcontinue=";

	private String category = "Verbes";
	private String langue = "_en_fran%C3%A7ais";
	private String limit = "500";
	private String nextPage = "";

	public void scrapToFile() {
		try {
			List<String> wordList = parseWiki();
			Files.write(Paths.get(System.getProperty("user.dir") + "/" + category + ".txt"),
					String.join("\r\n", wordList).getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	private List<String> parseWiki() {
		List<String> wordList = new ArrayList<>();
		do {
			JSONObject json = scrapLink();
			wordList.addAll(getWordsFromJson(json));
			nextPage = json.has("continue") ? json.getJSONObject("continue").get("cmcontinue").toString() : null;
		} while (nextPage != null);
		return wordList;
	}

	private List<String> getWordsFromJson(JSONObject json) {
		List<String> wordList = new ArrayList<>();
		JSONArray wordArray = json.getJSONObject("query").getJSONArray("categorymembers");
		wordArray.forEach(object -> {
			wordList.add(((JSONObject) object).getString("title"));
		});
		return wordList;
	}

	private JSONObject scrapLink() {
		try {
			String scrapLink = getScrapLink();

			HttpURLConnection connection = (HttpURLConnection) new URL(scrapLink).openConnection();
			connection.setRequestProperty("accept", "application/json");

			InputStreamReader responseStream = new InputStreamReader(connection.getInputStream());
			BufferedReader reader = new BufferedReader(responseStream);

			StringBuilder results = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				results.append(line);
			}

			connection.disconnect();

			return new JSONObject(results.toString());
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return null;
	}

	private String getScrapLink() {
		String scrapLink = String.join("&", BASE_URL, P_LIST, P_TITLE + category + langue, P_FORMAT, P_LIMIT + limit);
		if (!nextPage.equals("")) {
			scrapLink = String.join("&", scrapLink, P_CONTINUE + nextPage);
		}
		return scrapLink;
	}
}