package com.yl.yuanlu.pocketresume.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by LUYUAN on 5/29/2017.
 */

public class ModelUtils {

    //private static Gson gson = new Gson();
    private static SharedPreferences sp;

    private static Gson gsonForSerialization = new GsonBuilder()
            .registerTypeAdapter(Uri.class, new UriSerializer())
            .create();

    private static Gson gsonForDeserialization = new GsonBuilder()
            .registerTypeAdapter(Uri.class, new UriDeserializer())
            .create();

    public static void save_to_sp(String pref_name, Context context, String jsonString, String key) {
        sp = context.getSharedPreferences(pref_name, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, jsonString);
        editor.apply();
    }

    public static String load_from_sp(String pref_name, Context context, String key) {
        sp = context.getSharedPreferences(pref_name, context.MODE_PRIVATE);
        String jsonString = sp.getString(key, null);  //null is default value, will return null if no key present
        return jsonString;
    }

    public static void delete_from_sp(String pref_name, Context context, String key) {
        sp = context.getSharedPreferences(pref_name, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    //**static function**, so function can be called without need to creating an ModelUtils object
    public static <T> T toObject(String jsonString, TypeToken<T> typeToken) {
        return gsonForDeserialization.fromJson(jsonString, typeToken.getType());
    }

    public static <T> String toString(T object, TypeToken<T> typeToken) {
        return gsonForSerialization.toJson(object, typeToken.getType());
    }

    private static class UriSerializer implements JsonSerializer<Uri> {
        @Override
        public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    private static class UriDeserializer implements JsonDeserializer<Uri> {
        @Override
        public Uri deserialize(JsonElement src, Type srcType, JsonDeserializationContext context) throws JsonParseException {
            return Uri.parse(src.getAsString());
        }
    }

}
