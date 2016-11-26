package com.csci571.csci571hw9;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.csci571.csci571hw9.LocalStorage.LocalStorage;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class CommitteeDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public JSONObject jsonObject;
    public String itemType = "committee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_committee_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Committee Info");
        actionBar.setDisplayHomeAsUpEnabled(true);

        try {
            String jsonString = getIntent().getStringExtra("item");
            jsonObject = new JSONObject(jsonString);
            displayInfo();

            Log.i("Committee Info", jsonString);
        } catch (JSONException error) {
            Log.e("Committee Info", "onCreate Json Parse");
        }

    }

    public void displayInfo() {
        try {

            LocalStorage localStorage = LocalStorage.getInstance(this);
            ImageButton favoriteButton = (ImageButton) findViewById(R.id.committee_favorite_button);
            if(localStorage.hasItem(itemType, jsonObject.getString("committee_id"))){

                favoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_yellow, getApplicationContext().getTheme()));
            }
            favoriteButton.setOnClickListener(this);

            // committee id row
            TextView committeeIdTextView = (TextView) findViewById(R.id.committee_id_text);
            committeeIdTextView.setText(
                    (jsonObject.getString("committee_id") == "null")?"N.A":jsonObject.getString("committee_id").toUpperCase()
            );

            // Committee Name row
            TextView committeeNameTextView = (TextView) findViewById(R.id.committee_name_text);
            committeeNameTextView.setText(
                    (jsonObject.getString("name") == "null")?"N.A":jsonObject.getString("name")
            );

            // Committee chamber row
            TextView committeeChamberTextView = (TextView) findViewById(R.id.committee_chamber_text);
            ImageView chamberImageView = (ImageView) findViewById(R.id.committee_chamber_image);

            String chamber = jsonObject.getString("chamber");
            if(chamber == "null") {
                committeeChamberTextView.setText("N.A");
                chamberImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_blank, getApplicationContext().getTheme()));
            } else {
                committeeChamberTextView.setText(chamber.substring(0, 1).toUpperCase() + chamber.substring(1));

                switch (chamber) {
                    case "house":
                        chamberImageView.setImageDrawable(getResources().getDrawable(R.drawable.h, getApplicationContext().getTheme()));
                        break;
                    default:
                        chamberImageView.setImageDrawable(getResources().getDrawable(R.drawable.s, getApplicationContext().getTheme()));
                }
            }

            // committee parent row
            TextView committeeParentTextView = (TextView) findViewById(R.id.committee_parent_text);
            if(jsonObject.has("parent_committee_id")) {
                String committeeParent = jsonObject.getString("parent_committee_id");
                if(committeeParent != "null") {
                    committeeParentTextView.setText(committeeParent.toUpperCase());
                } else {
                    committeeParentTextView.setText("N.A");
                }
            } else {
                committeeParentTextView.setText("N.A");
            }

            // committee office row
            TextView committeeOfficeTextView = (TextView) findViewById(R.id.committee_office_text);
            if(jsonObject.has("office")) {
                String committeeOffice= jsonObject.getString("office");
                if(committeeOffice != "null") {
                    committeeOfficeTextView.setText(committeeOffice);
                } else {
                    committeeOfficeTextView.setText("N.A");
                }
            } else {
                committeeOfficeTextView.setText("N.A");
            }

            // committee parent row
            TextView committeeContactTextView = (TextView) findViewById(R.id.committee_contact_text);
            if(jsonObject.has("phone")) {
                String committeePhone = jsonObject.getString("phone");
                if(committeePhone != "null") {
                    committeeContactTextView.setText(committeePhone);
                } else {
                    committeeContactTextView.setText("N.A");
                }
            } else {
                committeeContactTextView.setText("N.A");
            }


        } catch (JSONException error) {
            error.printStackTrace();
            Log.e("Committee Info", "display Info");
        }

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.committee_favorite_button) {
            ImageButton favoriteButton = (ImageButton) view;
            LocalStorage localStorage = LocalStorage.getInstance(this);
            try{
                String toggleItemActionResult = localStorage.toggleItem(itemType, jsonObject.getString("committee_id"), jsonObject);
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

}
