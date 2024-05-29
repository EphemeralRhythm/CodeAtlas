package com.example.codeatlas;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CodeforcesApiHelper {

    private static final String API_URL = "https://codeforces.com/api/contest.list?gym=false";

    public interface ApiCallback {
        void onSuccess(ArrayList<Contest> contests);
        void onFailure(Exception e);
    }

    public static void fetchUpcomingContests(ApiCallback callback) {
        new FetchContestsTask(callback).execute();
    }

    private static class FetchContestsTask extends AsyncTask<Void, Void, ArrayList<Contest>> {
        private ApiCallback callback;

        FetchContestsTask(ApiCallback callback) {
            this.callback = callback;
        }

        @Override
        protected ArrayList<Contest> doInBackground(Void... voids) {
            ArrayList<Contest> contests = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();

            try {
                Request request = new Request.Builder().url(API_URL).build();
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();

                JSONObject jsonObject = new JSONObject(responseData);
                String status = jsonObject.getString("status");

                if ("OK".equals(status)) {
                    JSONArray result = jsonObject.getJSONArray("result");
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject contestJson = result.getJSONObject(i);
                        int id = contestJson.getInt("id");
                        String name = contestJson.getString("name");
                        long startTimeSeconds = contestJson.getLong("startTimeSeconds");
                        long durationSeconds = contestJson.getLong("durationSeconds");

                        // Filter out past contests
                        long currentTimeSeconds = System.currentTimeMillis() / 1000;
                        if (startTimeSeconds > currentTimeSeconds) {
                            Contest contest = new Contest(id, name, startTimeSeconds, durationSeconds);
                            contests.add(contest);
                        }
                    }
                }

            } catch (Exception e) {
                Log.e("FetchContestsTask", "Error fetching contests", e);
                callback.onFailure(e);
            }

            return contests;
        }

        @Override
        protected void onPostExecute(ArrayList<Contest> contests) {
            if (contests != null && !contests.isEmpty()) {
                callback.onSuccess(contests);
            } else {
                callback.onFailure(new Exception("No upcoming contests found"));
            }
        }
    }
}
