package com.voiceit.voiceit2sdk;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

import com.voiceit.voiceit2.VoiceItAPI2;

public class MainActivity extends AppCompatActivity {

    private VoiceItAPI2 myVoiceIt;
    private String [] userId = {"usr_e14324bf532547cba99d735aca29d394", "usr_e14324bf532547cba99d735aca29d394"};
    private int userIdIndex = 0;
    private String groupId = "GROUP_ID";
    private String phrase = "Never forget tomorrow is a new day";
    private String contentLanguage = "en-US";
    private boolean doLivenessCheck = false; // Liveness detection is not used for enrollment views
    private boolean doLivenessAudioCheck = false;

    private Switch userIdSwitch;
    private Switch livenessSwitch;
    private Switch livenessAudioSwitch;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If using user tokens, replace API_KEY below with the user token,
        // and leave the second argument as an empty string
        myVoiceIt = new VoiceItAPI2("key_a682a40728464bd6bfb563b7237cd74d","tok_497be67e778241d18de68eb7fe576d24");

        userIdSwitch = findViewById(R.id.switch_user);
        livenessSwitch = findViewById(R.id.switch_liveness);
        livenessAudioSwitch = findViewById(R.id.switch_liveness_audio);
        userIdSwitch.setText("User 1");
    }

    public void toggleLiveness(View view) {
        doLivenessCheck = livenessSwitch.isChecked();
        if(doLivenessCheck){
            livenessAudioSwitch.setVisibility(View.VISIBLE);
    }
        else {
            livenessAudioSwitch.setVisibility(View.GONE);
        }
    }

    public void toggleLivenessAudio(View view) {
        doLivenessAudioCheck = livenessAudioSwitch.isChecked();
    }

    public void toggleUser(View view) {
        if(userIdIndex == 0) {
            userIdIndex = 1;
            userIdSwitch.setText("User 2 ");
        } else {
            userIdIndex = 0;
            userIdSwitch.setText("User 1 ");
        }
    }

    public void displayIdentifiedUser(JSONObject response) {
        try {
            String id = response.getString("userId");
            if(userId[0].equals(id)) {
                Toast.makeText(mContext, "User 1 Identified", Toast.LENGTH_LONG).show();
            } else if (userId[1].equals(id)) {
                Toast.makeText(mContext, "User 2 Identified", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            System.out.println("JSONException: " + e.getMessage());
        }
    }

    public void encapsulatedVoiceEnrollment(View view) {
        myVoiceIt.encapsulatedVoiceEnrollment(this, userId[userIdIndex], contentLanguage, phrase, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("encapsulatedVoiceEnrollment onSuccess Result : " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                checkResponse(errorResponse);
                if (errorResponse != null) {
                    System.out.println("encapsulatedVoiceEnrollment onFailure Result : " + errorResponse.toString());
                }
            }
        });
    }

    public void encapsulatedVoiceVerification(View view) {
        myVoiceIt.encapsulatedVoiceVerification(this, userId[userIdIndex], contentLanguage, phrase, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("encapsulatedVoiceVerification onSuccess Result : " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                checkResponse(errorResponse);
                if (errorResponse != null) {
                    System.out.println("encapsulatedVoiceVerification onFailure Result : " + errorResponse.toString());
                }
            }
        });
    }

    public void checkResponse(JSONObject response) {
        try {
            if (response.getString("responseCode").equals("IFVD")
                    || response.getString("responseCode").equals("ACLR")
                    || response.getString("responseCode").equals("IFAD")
                    || response.getString("responseCode").equals("SRNR")
                    || response.getString("responseCode").equals("UNFD")
                    || response.getString("responseCode").equals("MISP")
                    || response.getString("responseCode").equals("DAID")
                    || response.getString("responseCode").equals("UNAC")
                    || response.getString("responseCode").equals("CLNE")
                    || response.getString("responseCode").equals("INCP")
                    || response.getString("responseCode").equals("NPFC")) {
                Toast.makeText(this, "responseCode: " + response.getString("responseCode")
                         + ", " + getString(com.voiceit.voiceit2.R.string.CHECK_CODE), Toast.LENGTH_LONG).show();
                Log.e("MainActivity","responseCode: " + response.getString("responseCode")
                        + ", " + getString(com.voiceit.voiceit2.R.string.CHECK_CODE));
            }
        } catch (JSONException e) {
            Log.d("MainActivity","JSON exception : " + e.toString());
        }
    }

}
