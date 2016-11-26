package com.csci571.csci571hw9;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.csci571.csci571hw9.LocalStorage.LocalStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class BillDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static JSONObject jsonObject;
    public static String itemType = "bill";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Bill Info");
        actionBar.setDisplayHomeAsUpEnabled(true);

        ImageButton favoriteButton = (ImageButton) findViewById(R.id.bill_favorite_button);
        favoriteButton.setOnClickListener(this);

        String jsonStr = getIntent().getStringExtra("item");
        try {
            jsonObject = new JSONObject(jsonStr);
            displayInfo();

            LocalStorage localStorage = LocalStorage.getInstance(this);
            if(localStorage.hasItem(itemType, jsonObject.getString("bill_id"))) {
                favoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_yellow, getApplicationContext().getTheme()));
            }

        } catch (JSONException error) {
            Log.e("Bill Info", "Parse Json form intent Error.");
        }

    }

    public void displayInfo() {
        try{

            // Bill id row
            TextView billIdTextView = (TextView) findViewById(R.id.bill_id_text);
            billIdTextView.setText((jsonObject.getString("bill_id") == "null")?"N.A":jsonObject.getString("bill_id").toUpperCase());

            // Bill title row
            TextView billTitleTextView = (TextView) findViewById(R.id.bill_title_text);
            billTitleTextView.setText((jsonObject.getString("official_title") == "null")?"N.A":jsonObject.getString("official_title"));

            // Bill title row
            TextView billTypeTextView = (TextView) findViewById(R.id.bill_type_text);
            billTypeTextView.setText((jsonObject.getString("bill_type") == "null")?"N.A":jsonObject.getString("bill_type").toUpperCase());

            // Bill sponsor row
            TextView billSponsorTextView = (TextView) findViewById(R.id.bill_sponsor_text);
            JSONObject sponsorJsonObject = jsonObject.getJSONObject("sponsor");
            String sponsorName = "";
            sponsorName += (sponsorJsonObject.getString("title") == "null")?"":(sponsorJsonObject.getString("title") + ". ");
            sponsorName += (sponsorJsonObject.getString("last_name") == "null")?"":(sponsorJsonObject.getString("last_name") + ", ");
            sponsorName += (sponsorJsonObject.getString("first_name") == "null")?"":(sponsorJsonObject.getString("first_name"));
            if (sponsorName == "") {
                billSponsorTextView.setText("N.A");
            } else {
                billSponsorTextView.setText(sponsorName);
            }

            // Bill chamber row
            TextView billChamberTextView = (TextView) findViewById(R.id.bill_chamber_text);
            String chamber = jsonObject.getString("chamber");
            billChamberTextView.setText(
                    (chamber == "null")?"N.A":(chamber.substring(0, 1).toUpperCase() + chamber.substring(1))
            );

            // Bill status row
            TextView billStatusTextView = (TextView) findViewById(R.id.bill_status_text);
            JSONObject statusJsonObject = jsonObject.getJSONObject("history");
            if (statusJsonObject == null) {
                billStatusTextView.setText("N.A");
            } else {
                boolean status = statusJsonObject.getBoolean("active");
                billStatusTextView.setText(
                        (status)?"Active":"New"
                );
            }

            SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-mm-dd");
            SimpleDateFormat dateFormatter= new SimpleDateFormat("MMM DD, YYYY");

            // Bill status row
            TextView billIntroducedOnTextView = (TextView) findViewById(R.id.bill_introduced_text);
            String introducedOnDateStr = jsonObject.getString("introduced_on");
            if(introducedOnDateStr == "null") {
                billIntroducedOnTextView.setText("N.A");
            } else {
                try {
                    billIntroducedOnTextView.setText(dateFormatter.format(dateParser.parse(introducedOnDateStr)));
                } catch (ParseException error) {
                    Log.e("Bill Info", "Date Parsing Error");
                }
            }

            // Bill congress URL
            TextView billCongressUrlTextView = (TextView) findViewById(R.id.bill_congress_url_text);
            JSONObject urlsJsonObject = jsonObject.getJSONObject("urls");
            if (urlsJsonObject.has("congress")) {
                billCongressUrlTextView.setText(
                        (urlsJsonObject.getString("congress") == "null")?"N.A":urlsJsonObject.getString("congress")
                );
            } else {
                billCongressUrlTextView.setText("N.A");
            }

            // Bill version row
            TextView billVersionTextView = (TextView) findViewById(R.id.bill_version_text);
            TextView billUrlView = (TextView) findViewById(R.id.bill_url_text);
            if (jsonObject.has("last_version")) {

                JSONObject versionJsonObject = jsonObject.getJSONObject("last_version");

                if (versionJsonObject.has("version_name")) {
                    billVersionTextView.setText(
                            (versionJsonObject.getString("version_name") == "null")?"N.A":versionJsonObject.getString("version_name")
                    );
                } else {
                    billVersionTextView.setText("N.A");
                }

                if (versionJsonObject.has("urls")) {
                    JSONObject versionUrlJsonObject = versionJsonObject.getJSONObject("urls");
                    if (versionUrlJsonObject.has("pdf")) {
                        billUrlView.setText(
                                (versionUrlJsonObject.getString("pdf") == "null")?"N.A":versionUrlJsonObject.getString("pdf")
                        );
                    } else {
                        billUrlView.setText("N.A");
                    }
                } else {
                    billUrlView.setText("N.A");
                }

            } else {

                billVersionTextView.setText("N.A");
                billUrlView.setText("N.A");
            }




        } catch (JSONException error) {
            Log.e("Bill Info, displayInfo", "Json Error");
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
    public void onClick(View view) {
        if(view.getId() == R.id.bill_favorite_button) {
            ImageButton favoriteButton = (ImageButton) view;
            LocalStorage localStorage = LocalStorage.getInstance(this);
            try{
                String toggleItemActionResult = localStorage.toggleItem(itemType, jsonObject.getString("bill_id"), jsonObject);
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

}

