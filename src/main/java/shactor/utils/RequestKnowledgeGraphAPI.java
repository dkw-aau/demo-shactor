package shactor.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

public class RequestKnowledgeGraphAPI {

    // Replace YOUR_API_KEY with your actual API key
    private static final String API_KEY = "AIzaSyApJxl1p-B9rZWoIKMZwf3bf2fKwnP2WoM";

    private static void lookupIRI(String queryIRI) {
        String entityName = queryIRI.substring(queryIRI.lastIndexOf("/") + 1);
        getEntityInfo(entityName.replace("_", "%20"));
    }

    public static String getEntityInfo(String query) {
        try {
            query = query.substring(query.lastIndexOf("/") + 1).replace("_", "%20");
            // Build the API query URL
            String url = String.format("https://kgsearch.googleapis.com/v1/entities:search?query=%s&key=%s&limit=%d",
                    query, API_KEY, 1);
            // Send a HTTP GET request to the API endpoint
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            // Read the API response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();
            String response = responseBuilder.toString();
            // Extract the entity information from the API response
            JSONObject json = new JSONObject(response);
            //System.out.println(json);
            //JSONObject entity = json.getJSONArray("itemListElement").getJSONObject(0).getJSONObject("result");
            //String entityType = entity.getString("description");
            //String entityIRI = entity.getString("@type");

            JSONObject jsonObj = new JSONObject(response);
            JSONArray itemList = jsonObj.getJSONArray("itemListElement");

            // Check if 'articleBody' exists and store it as a string
            String articleBody = null;
            if (itemList.getJSONObject(0).getJSONObject("result").has("detailedDescription")) {
                JSONObject detailedDesc = itemList.getJSONObject(0).getJSONObject("result").getJSONObject("detailedDescription");
                if (detailedDesc.has("articleBody")) {
                    articleBody = detailedDesc.getString("articleBody");
                }
            }

            // Get the '@type' values as a comma separated string
            JSONArray types = itemList.getJSONObject(0).getJSONObject("result").optJSONArray("@type");
            StringBuilder typeStr = new StringBuilder();
            if (types != null) {
                for (int i = 0; i < Objects.requireNonNull(types).length(); i++) {
                    typeStr.append(types.get(i).toString());
                    if (i < types.length() - 1) {
                        typeStr.append(",");
                    }
                }
            }

            // Get the 'name' and store as string
            //String name = itemList.getJSONObject(0).getJSONObject("result").getString("name");

            // Get the 'description' and store as string
            String description = itemList.getJSONObject(0).getJSONObject("result").getString("description");

            return description + "|" + articleBody + "|" + typeStr;

        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return "";
    }

    public static void main(String[] args) {
        // Specify the IRI you want to look up
        String queryIRI = "http://dbpedia.org/resource/Central_European_Time";
        lookupIRI(queryIRI);
        lookupIRI("");
    }
}