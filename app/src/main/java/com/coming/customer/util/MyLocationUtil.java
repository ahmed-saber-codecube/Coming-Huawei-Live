package com.coming.customer.util;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.NonNull;

import com.huawei.hms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class MyLocationUtil {

    static String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    static String TYPE_AUTOCOMPLETE = "/autocomplete";
    static String OUT_JSON = "/json";


    static String LOG_TAG = "GooglePlacesAutoc";

    public static void getCurrantLocation(Context context, final LatLng latLng, SingleObserver<Address> singleObserver) {

        Geocoder geocoder;
        geocoder = new Geocoder(context);

        final Geocoder finalGeocoder = geocoder;
        Single.create(new SingleOnSubscribe<Address>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Address> e) throws Exception {

                List<Address> location = null;
                try {
                    location = finalGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch (IOException o) {
                    o.printStackTrace();
                }

                if (location != null) {
                    e.onSuccess(location.get(0));
                }
            }

        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(singleObserver);

    }

    /*public static void getCurrantLocation(final LatLng latLng, SingleObserver<Address> singleObserver) {

        Single.create(new SingleOnSubscribe<Address>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Address> e) throws Exception {
                OkHttpClient client = new OkHttpClient.Builder()
                        .writeTimeout(3, TimeUnit.MINUTES)
                        .readTimeout(3, TimeUnit.MINUTES)
                        .connectTimeout(3, TimeUnit.MINUTES)
                        .build();
                Request request = new Request.Builder()
                        .url("https://maps.googleapis.com/maps/api/geocode/json?latlng="
                                + latLng.latitude + "," + latLng.longitude
//                                + "&sensor=true"
                                + "&key=" + "AIzaSyCAyB5rbcxAq7KLR8FfavsY-UFZQT2bwwU"*//*LOCATION_SEARCH_KEY*//*)
                        .build();

                okhttp3.Response response = client.newCall(request).execute();

                if (response.code() == 200) {
                    String responce = response.body().string();
                    Address mObject = new Gson().fromJson(responce, Address.class);
                    e.onSuccess(mObject);
                }
            }

        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(singleObserver);

    }*/


    /*public static ArrayList<SearchLocationData> autocomplete(String input) {

        ArrayList<String> mainTextList;
        ArrayList<String> secondoryTextList;
        ArrayList<String> placeIDs;

        ArrayList<SearchLocationData> searchLocationDataList = new ArrayList();

        ArrayList<String> resultList1 = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + LOCATION_SEARCH_KEY);
//            sb.append("&components=country:IQ|country:US");
//            sb.append("&location=" + String.valueOf(LocationService.dblLatitude) + "," + String.valueOf(LocationService.dblLongitude));
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

//            sb.append("&radius=" + String.valueOf(50));

            URL url = new URL(sb.toString());

            System.out.println("URL: " + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing", e);
            return searchLocationDataList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return searchLocationDataList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList1 = new ArrayList<String>(predsJsonArray.length());
            mainTextList = new ArrayList<String>(predsJsonArray.length());
            secondoryTextList = new ArrayList<String>(predsJsonArray.length());
            placeIDs = new ArrayList<>(predsJsonArray.length());

            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");

                resultList1.add(predsJsonArray.getJSONObject(i).getString("description"));
                mainTextList.add(predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text"));
                String placeId = predsJsonArray.getJSONObject(i).getString("place_id");
                placeIDs.add(placeId);
                String sText;
                if (predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").has("secondary_text")) {
                    sText = predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("secondary_text");
                    secondoryTextList.add(predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("secondary_text"));
                } else {
                    sText = "";
                    secondoryTextList.add("");
                }


                SearchLocationData searchLocationData = new SearchLocationData(
                        predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text"),
                        sText,
                        placeId,
                        predsJsonArray.getJSONObject(i).getString("description")
                );

                searchLocationDataList.add(searchLocationData);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);

        }

        return searchLocationDataList;


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                return searchLocationDataList;
//            }
//        }, 2000);


    }


    public static class AutocompleteApi extends AsyncTask<Void, Void, ArrayList<SearchLocationData>> {

        private final String s;

        ArrayList<String> mainTextList;
        ArrayList<String> secondoryTextList;
        ArrayList<String> placeIDs;

        ArrayList<SearchLocationData> searchLocationDataList = new ArrayList();

        PlaceSearchListener placeSearchListener;

        public AutocompleteApi(String s, PlaceSearchListener placeSearchListener) {
            this.s = s;
            this.placeSearchListener = placeSearchListener;
        }

        @Override
        protected ArrayList<SearchLocationData> doInBackground(Void... voids) {
            return autocomplete(s);
        }

        @Override
        protected void onPostExecute(ArrayList<SearchLocationData> strings) {
            placeSearchListener.onPlaceFounds(strings);
        }

        public ArrayList<SearchLocationData> autocomplete(String input) {

            ArrayList<String> resultList = null;
            HttpURLConnection conn = null;
            StringBuilder jsonResults = new StringBuilder();
            try {
               *//* StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
                sb.append("?key=" + LOCATION_SEARCH_KEY);
                //sb.append("&components=country:au");
//                sb.append("&location=" + String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude));
                sb.append("&input=" + URLEncoder.encode(input, "utf8"));
//                sb.append("&radius=" + String.valueOf(50));*//*

                StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
                sb.append("?key=" + LOCATION_SEARCH_KEY);
//            sb.append("&components=country:IQ|country:US");
//            sb.append("&location=" + String.valueOf(LocationService.dblLatitude) + "," + String.valueOf(LocationService.dblLongitude));
                sb.append("&input=" + URLEncoder.encode(input, "utf8"));

                URL url = new URL(sb.toString());
                System.out.println("URL: " + url);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (MalformedURLException e) {
                return searchLocationDataList;
            } catch (IOException e) {
                return searchLocationDataList;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            try {
                // Create a JSON object hierarchy from the results
                JSONObject jsonObj = new JSONObject(jsonResults.toString());
                JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

                // Extract the Place descriptions from the results
                resultList = new ArrayList<String>(predsJsonArray.length());
                mainTextList = new ArrayList<String>(predsJsonArray.length());
                secondoryTextList = new ArrayList<String>(predsJsonArray.length());
                placeIDs = new ArrayList<>(predsJsonArray.length());

                for (int i = 0; i < predsJsonArray.length(); i++) {
                    System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                    System.out.println("============================================================");

                    resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                    mainTextList.add(predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text"));
                    String placeId = predsJsonArray.getJSONObject(i).getString("place_id");
                    placeIDs.add(placeId);
                    String sText;
                    if (predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").has("secondary_text")) {
                        sText = predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("secondary_text");
                        secondoryTextList.add(predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("secondary_text"));
                    } else {
                        sText = "";
                        secondoryTextList.add("");
                    }


                    SearchLocationData searchLocationData = new SearchLocationData(
                            predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text"),
                            sText,
                            placeId,
                            predsJsonArray.getJSONObject(i).getString("description")
                    );

                    searchLocationDataList.add(searchLocationData);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Cannot process JSON results", e);

            }

            return searchLocationDataList;
        }

    }


    public interface PlaceSearchListener {
        void onPlaceFounds(ArrayList<SearchLocationData> searchLocationList);
    }*/
}
