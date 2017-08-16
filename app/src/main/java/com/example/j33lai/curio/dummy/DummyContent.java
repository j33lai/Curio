package com.example.j33lai.curio.dummy;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    public static JSONObject jsonobj = new JSONObject();
    /**
     * An array of sample (dummy) items.
     */
    public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static int COUNT = 0;
/*
    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }
*/
    public static void initItem(JSONObject obj) {
        ITEMS.clear();
        ITEM_MAP.clear();
        jsonobj = obj;
        try {
            JSONArray joa = jsonobj.getJSONArray("data");
            COUNT = joa.length();
            for (int i = 0; i < COUNT; i++) {
                JSONObject tmp_joa = joa.getJSONObject(i);
                JSONObject tmp_jo = tmp_joa.getJSONObject("attributes");

                addItem(createDummyItem(i+1, tmp_jo.getString("name"), "test"));
            }

        } catch (JSONException e) {
            Log.d("Dummy", "Error");
            COUNT = 25;
            for (int i = 1; i <= COUNT; i++) {
                addItem(createDummyItem(i));
            }
        }

    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), "Project " + position, makeDetails(position));
    }

    private static DummyItem createDummyItem(int position, String conts, String details) {
        return new DummyItem(String.valueOf(position), conts, details);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
