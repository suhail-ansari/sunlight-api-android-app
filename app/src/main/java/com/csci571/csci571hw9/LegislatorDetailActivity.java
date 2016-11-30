package com.csci571.csci571hw9;

import android.content.Intent;
import android.content.res.Resources;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.csci571.csci571hw9.LocalStorage.LocalStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LegislatorDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static JSONObject jsonObject;
    public static String itemType = "legislator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legislator_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Legislator Info");
        actionBar.setDisplayHomeAsUpEnabled(true);

        String jsonStr = getIntent().getStringExtra("item");
        System.out.println(jsonStr);

        try{
            jsonObject = new JSONObject(jsonStr);
            setUpTopButtons(jsonObject);
            setUpProfileImage(jsonObject);
            setUpInfoTable(jsonObject);

        } catch (JSONException error) {
            System.out.println("Error::");
        }

    }

    public void setUpTopButtons(final JSONObject jsonObject) {

        ImageButton favoriteButton = (ImageButton) findViewById(R.id.favorite_button);
        ImageButton fbButton = (ImageButton) findViewById(R.id.fb_button);
        ImageButton twitterButton = (ImageButton) findViewById(R.id.twitter_button);
        ImageButton websiteButton = (ImageButton) findViewById(R.id.website_button);

        LocalStorage localStorage = LocalStorage.getInstance(this);

        try {

            if (localStorage.hasItem(itemType, jsonObject.getString("bioguide_id"))) {
                favoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_yellow, getApplicationContext().getTheme()));
            }

            String faceBookId = jsonObject.getString("facebook_id");
            String twitterId = jsonObject.getString("twitter_id");
            String website = jsonObject.getString("website");

            /*
            if(faceBookId == "null")
                fbButton.setVisibility(View.GONE);

            if(twitterId == "null")
                twitterButton.setVisibility(View.GONE);

            if(website == "null")
                websiteButton.setVisibility(View.GONE);
            */

        } catch (JSONException error) {
            System.out.println("Error::");
        }

        favoriteButton.setOnClickListener(this);
        fbButton.setOnClickListener(this);
        twitterButton.setOnClickListener(this);
        websiteButton.setOnClickListener(this);
    }


    public void setUpProfileImage(final JSONObject jsonObject) {
        try {
            String bioguideId = jsonObject.getString("bioguide_id");
            ImageView profileImageView = (ImageView) findViewById(R.id.profile_image);
            ImageLoader imageLoader = VolleySignleton.getInstance(this).getImageLoader();
            String imageUrl = "https://theunitedstates.io/images/congress/original/" + bioguideId + ".jpg";
            imageLoader.get(imageUrl, ImageLoader.getImageListener(profileImageView, R.drawable.ic_blank, R.drawable.ic_blank));

            ImageView partyImageView = (ImageView) findViewById(R.id.party_info_logo);
            TextView partyNameTextView = (TextView) findViewById(R.id.party_info_name);

            switch (jsonObject.getString("party")) {
                case "R":
                    partyImageView.setImageDrawable(getResources().getDrawable(R.drawable.r, getApplicationContext().getTheme()));
                    partyNameTextView.setText("Republican");
                    break;
                case "D":
                    partyImageView.setImageDrawable(getResources().getDrawable(R.drawable.d, getApplicationContext().getTheme()));
                    partyNameTextView.setText("Democrat");
                    break;
                case "I":
                    partyImageView.setImageDrawable(getResources().getDrawable(R.drawable.i, getApplicationContext().getTheme()));
                    partyNameTextView.setText("Independent");
                    break;
            }
        }
        catch (JSONException error) {
            System.out.println("Error::");
        }
    }

    public void setUpInfoTable(final JSONObject jsonObject) {
        try {
            // name row
            TextView legislatorNameTextView = (TextView) findViewById(R.id.legislator_name_text);
            String title = (jsonObject.getString("title") == "null")?"":(jsonObject.getString("title") + ".");
            String firstName = (jsonObject.getString("first_name") == "null")?"":jsonObject.getString("first_name");
            String lastName = (jsonObject.getString("last_name") == "null")?"":(jsonObject.getString("last_name") + ",");
            legislatorNameTextView.setText(String.format("%s %s %s", title, lastName, firstName));

            // email row
            TextView legislatorEmailTextView = (TextView) findViewById(R.id.legislator_email_text);
            String email = (jsonObject.getString("oc_email") == "null")?"N.A":jsonObject.getString("oc_email");
            legislatorEmailTextView.setText(email);

            // chamber row
            TextView legislatorChamberTextView = (TextView) findViewById(R.id.legislator_chamber_text);
            String chamber = (jsonObject.getString("chamber") == "null")?"N.A":jsonObject.getString("chamber");
            legislatorChamberTextView.setText((chamber.substring(0, 1).toUpperCase() + chamber.substring(1)));

            // contact row
            TextView legislatorContactTextView = (TextView) findViewById(R.id.legislator_contact_text);
            String contact = (jsonObject.getString("phone") == "null")?"N.A":jsonObject.getString("phone");
            legislatorContactTextView.setText(contact);


            SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-mm-dd");
            SimpleDateFormat dateFormatter= new SimpleDateFormat("MMM DD, YYYY");

            // term start row
            TextView legislatorStartTermTextView = (TextView) findViewById(R.id.legislator_start_term_text);
            String startTermDateStr = jsonObject.getString("term_start");
            if(startTermDateStr == "null") {
                legislatorStartTermTextView.setText("N.A");
            } else {
                try {
                    legislatorStartTermTextView.setText(dateFormatter.format(dateParser.parse(startTermDateStr)));
                } catch (ParseException error) {
                    System.out.println("Error::");
                }
            }

            // term end row
            TextView legislatorEndTermTextView = (TextView) findViewById(R.id.legislator_end_term_text);
            String endTermDateStr = jsonObject.getString("term_end");
            if(endTermDateStr == "null") {
                legislatorEndTermTextView.setText("N.A");
            } else {
                try {
                    legislatorEndTermTextView.setText(dateFormatter.format(dateParser.parse(endTermDateStr)));
                } catch (ParseException error) {
                    System.out.println("Error::");
                }
            }

            //tern progress row
            try {
                float startTs = dateParser.parse(startTermDateStr).getTime();
                float endTs = dateParser.parse(endTermDateStr).getTime();
                float nowTs = new Date().getTime();
                System.out.println(String.format("%s, %s, %s", String.valueOf(startTs), String.valueOf(endTs), String.valueOf(nowTs)));
                float termPercentage = (nowTs - startTs)/(endTs - startTs);
                ProgressBar termProgressBar = (ProgressBar) findViewById(R.id.legislator_term_progress_bar);
                termProgressBar.setProgress(((int) (termPercentage * 100)));

                TextView termPercentageTextView = (TextView) findViewById(R.id.legislator_term_percentage_text);
                termPercentageTextView.setText(String.valueOf((int) (termPercentage * 100)) + "%");

                System.out.println(String.format("progress: %s", String.valueOf(termPercentage)));
            } catch (ParseException error) {
                System.out.println("Error::");
            }

            // office row
            TextView officeTextView = (TextView) findViewById(R.id.legislator_office_text);
            String office = (jsonObject.getString("office") == "null")?"N.A":jsonObject.getString("office");
            officeTextView.setText(office);

            // state row
            TextView stateTextView = (TextView) findViewById(R.id.legislator_state_text);
            String state = (jsonObject.getString("state") == "null")?"N.A":jsonObject.getString("state");
            stateTextView.setText(state);

            // fax row
            TextView faxTextView = (TextView) findViewById(R.id.legislator_fax_text);
            String fax = (jsonObject.getString("fax") == "null")?"N.A":jsonObject.getString("fax");
            faxTextView.setText(fax);

            // birthday row
            TextView birthdayTextView = (TextView) findViewById(R.id.legislator_birthday_text);
            String birthday = jsonObject.getString("birthday");
            if(birthday == "null") {
                birthdayTextView.setText("N.A");
            } else {
                try {
                    birthdayTextView.setText(dateFormatter.format(dateParser.parse(birthday)));
                } catch (ParseException error) {
                    System.out.println("Error::");
                }
            }

        } catch (JSONException error) {
            System.out.println("Error::");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view)
    {
        try {
            switch (view.getId()) {
                case R.id.favorite_button:
                    handleFavoriteButtonClick((ImageButton) view);
                    break;

                case R.id.fb_button:
                    if(jsonObject.has("facebook_id")){
                        String faceBookId = jsonObject.getString("facebook_id");
                        if(faceBookId != "null"){
                            String fbUrl = "https://www.facebook.com/" + faceBookId;
                            Intent fbIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl));
                            startActivity(fbIntent);
                        } else {
                            showToast("Facebook ID not available");
                        }
                    } else {
                        showToast("Facebook ID not available");
                    }

                    break;

                case R.id.twitter_button:
                    if(jsonObject.has("twitter_id")){
                        String twitterId = jsonObject.getString("twitter_id");
                        if(twitterId != "null") {
                            String twitterUrl = "https://www.twitter.com/" + twitterId;
                            Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl));
                            startActivity(twitterIntent);
                        } else {
                            showToast("Twitter ID not available");
                        }
                    } else {
                        showToast("Twitter ID not available");
                    }
                    break;

                case R.id.website_button:
                    if(jsonObject.has("website")){
                        String website = jsonObject.getString("website");
                        if(website != "null") {
                            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                            startActivity(websiteIntent);
                        } else {
                            showToast("Website not available");
                        }
                    } else {
                        showToast("Website not available");
                    }
                    break;
            }
        } catch (JSONException error) {
            System.out.println("Error::");
        }
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void handleFavoriteButtonClick(ImageButton favoriteButton) {
        LocalStorage localStorage = LocalStorage.getInstance(this);
        try{
            String toggleItemActionResult = localStorage.toggleItem(itemType, jsonObject.getString("bioguide_id"), jsonObject);
            if (toggleItemActionResult == "deleted") {
                favoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_star, getApplicationContext().getTheme()));
            } else if (toggleItemActionResult == "added"){
                favoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_yellow, getApplicationContext().getTheme()));
            }
        } catch (JSONException error) {
            System.out.println("Error::");
        }
    }
}
