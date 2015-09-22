package net.parablack.clocktest.json;

import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Simon on 16.09.2015.
 */
class JSONReaderUtil {

    protected static void initByArray(AssetManager as, String fileName, String checksum, String arrayName, ScheduleInitCallback callback) throws InvalidDataException {
        try {
            // -------------- LOAD METAS
        JSONObject rootMetaJson = byAsset(as, fileName);
        if (!verifyMeta(rootMetaJson, checksum))
            throw new InvalidDataException("Error loading "+fileName + "! - Invalid Checksum (" + checksum + ")" );

        JSONArray innerArray = rootMetaJson.getJSONArray(arrayName);
        for (int i = 0; i < innerArray.length(); i++){
            JSONObject extractedObject = innerArray.getJSONObject(i);
            if(extractedObject != null){
                callback.callback(extractedObject);
            }

        }
            System.out.println("Loading of " + fileName + " completed, " + innerArray.length() + " entrys loaded!");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new InvalidDataException("JSON");
        }


    }

    protected static void fetchArray(JSONArray array, ScheduleInitCallback cb) throws JSONException {
      //  System.out.println("Array _ " + array + ", len " + array.length());
        for (int i = 0; i < array.length(); i++){
            JSONObject extractedObject = array.getJSONObject(i);
            if(extractedObject != null){
                cb.callback(extractedObject);
            }

        }
    }

    protected static JSONObject byAsset(AssetManager as, String fileName){
        try {
            String metaString = JSONReaderUtil.readFile(as.open(fileName));
            return new JSONObject(metaString);



        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    protected static String readFile(InputStream file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        return stringBuilder.toString();
    }

    protected static boolean verifyMeta(JSONObject obj, String verify){
        try {
            if(obj.getString("checksum").equals(verify)){
                System.out.println("Meta-Checksum verified " + obj.getString("checksum"));
                return true;
            } else{
                System.out.println("Meta-Checksum failed!!! " + obj.getString("checksum"));
                return false;
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }


    interface ScheduleInitCallback {
        void callback(JSONObject object) throws JSONException;
    }

}
