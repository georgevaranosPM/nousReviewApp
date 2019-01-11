package com.nousanimation.nousreview;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

//JSON Parser gia tin anagnosi tou JSON arxeiou apo to myJson API pou periexei tous periorismous gia ta 3D modela
public class JSONParser {

    private static final String TAG = "JSONParser";

    //String keys gia tin anagnosi twn 3exwristwn pediwn
    public static final String FILE_TYPE_KEY = "format";
    public static final String MAX_FACE_KEY = "maxFaceCount";
    public static final String MIN_FACE_KEY = "minFaceCount";
    public static final String MAX_VERT_KEY = "maxVertCount";
    public static final String MIN_VERT_KEY = "minVertCount";

    private Limit limits = new Limit("", 0,0,0,0);

    public JSONParser(Limit limits) {
    }

    public Limit getLimits() {
        return limits;
    }

    //Gemisma twn pediwn tis arraylist limits
    public Limit parseJson(String jsonText){
        try{
            JSONObject jObject = new JSONObject(jsonText);

            String filetype_value = jObject.getString(FILE_TYPE_KEY);
            int max_face_value = jObject.getInt(MAX_FACE_KEY);
            int min_face_value = jObject.getInt(MIN_FACE_KEY);
            int max_vert_value = jObject.getInt(MAX_VERT_KEY);
            int min_vert_value = jObject.getInt(MIN_VERT_KEY);

            limits.setFileType(filetype_value);
            limits.setMaxFaceCount(max_face_value);
            limits.setMinFaceCount(min_face_value);
            limits.setMaxVertCount(max_vert_value);
            limits.setMinVertCount(min_vert_value);


        } catch (JSONException e) {
            Log.e(TAG, "PARSING ERROR " , e );
        }

        return limits;
    }

}

