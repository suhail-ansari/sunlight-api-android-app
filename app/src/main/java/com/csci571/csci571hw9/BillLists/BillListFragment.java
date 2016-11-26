package com.csci571.csci571hw9.BillLists;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.csci571.csci571hw9.BillDetailActivity;
import com.csci571.csci571hw9.CustomUtils;
import com.csci571.csci571hw9.LocalStorage.LocalStorage;
import com.csci571.csci571hw9.R;
import com.csci571.csci571hw9.VolleySignleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BillListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BillListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BillListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String BILL_TYPE = "param1";

    // TODO: Rename and change types of parameters
    private String billType;
    private View inflatedView;
    private Context context;

    private OnFragmentInteractionListener mListener;

    public BillListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param billType active/new
     * @return A new instance of fragment BillListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BillListFragment newInstance(String billType) {
        BillListFragment fragment = new BillListFragment();
        Bundle args = new Bundle();
        args.putString(BILL_TYPE, billType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            billType = getArguments().getString(BILL_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.fragment_bill_list, container, false);

        TextView loadingTextView = (TextView) inflatedView.findViewById(R.id.bill_loading_text);
        loadingTextView.setVisibility(View.VISIBLE);

        String url;
        switch (getArguments().getString(BILL_TYPE)) {
            case "active":
                url = "http://csci571hw8.us-west-1.elasticbeanstalk.com/?req=bill-active";
                break;
            case "new":
                url = "http://csci571hw8.us-west-1.elasticbeanstalk.com/?req=bill-new";
                break;

            case "favorite":
                LocalStorage localStorage = LocalStorage.getInstance(getActivity());
                JSONArray jsonArray = localStorage.getItems("bill");
                displayList(CustomUtils.getInstance().sortJsonArray(jsonArray, "introduced_on"));
                return inflatedView;

            default:
                url = "http://104.198.0.197:8080/bills?per_page=50&history.active=true&order=introduced_on__desc";
        }

        Cache.Entry entry = VolleySignleton.getInstance(context).getRequestQueue().getCache().get(url);

        if(entry != null){
            try {

                String jsonString = new String(entry.data);
                JSONObject response = new JSONObject(jsonString);
                JSONArray jsonArray = response.getJSONArray("results");
                displayList(jsonArray);

            } catch (JSONException error){
                System.out.println("Error::");
            }

        } else {
            getJson(url);
        }

        return inflatedView;
    }

    public void getJson(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //response = res;
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            displayList(jsonArray);

                        } catch (JSONException error) {
                            System.out.println("Error::");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error::");
            }
        });

        jsonObjectRequest.setShouldCache(true);

        VolleySignleton.getInstance(context).getRequestQueue().add(jsonObjectRequest);
    }

    public void displayList(JSONArray jsonArray) {
        try {
            final ArrayList<JSONObject> list = new ArrayList<JSONObject>();
            for (int i = 0; i < jsonArray.length(); list.add(jsonArray.getJSONObject(i++))) ;
            ListView listView = (ListView) inflatedView.findViewById(R.id.bill_list_view);
            BillArrayAdapter billArrayAdapter = new BillArrayAdapter(context, list);
            listView.setAdapter(billArrayAdapter);

            if(jsonArray.length() > 0) {
                TextView loadingTextView = (TextView) inflatedView.findViewById(R.id.bill_loading_text);
                loadingTextView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity().getApplication(), BillDetailActivity.class);
                    JSONObject jsonObject = list.get(position);
                    intent.putExtra("item", jsonObject.toString());
                    startActivity(intent);
                }
            });

        } catch (JSONException error) {
            System.out.println("Error::");
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context _context) {
        super.onAttach(_context);
        context = _context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class BillArrayAdapter extends ArrayAdapter<JSONObject> {

        private final Context context;
        private final ArrayList<JSONObject> items;

        public BillArrayAdapter(Context context, ArrayList<JSONObject> items) {
            super(context, R.layout.bill_list_item, items);
            this.context = context;
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            JSONObject jsonObject = items.get(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.bill_list_item, parent, false);

            TextView billIdView = (TextView) rowView.findViewById(R.id.bill_id_text);
            TextView billDescriptionView = (TextView) rowView.findViewById(R.id.bill_description_text);
            TextView billDateView = (TextView) rowView.findViewById(R.id.bill_date_text);

            SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-mm-dd");
            SimpleDateFormat dateFormatter= new SimpleDateFormat("MMM DD, YYYY");

            try {
                billIdView.setText(jsonObject.getString("bill_id").toUpperCase());

                if(!(jsonObject.getString("short_title") == "null")) {
                    billDescriptionView.setText(jsonObject.getString("short_title"));
                } else if(!(jsonObject.getString("official_title") == "null")) {
                    String officialTitle = jsonObject.getString("official_title");
                    if (officialTitle.length() > 95) {
                        billDescriptionView.setText(jsonObject.getString("official_title").substring(0, 95) + " ...");
                    } else {
                        billDescriptionView.setText(jsonObject.getString("official_title"));
                    }

                } else {
                    billDescriptionView.setText("N.A");
                }

                String dateStr = jsonObject.getString("introduced_on");

                billDateView.setText(dateFormatter.format(dateParser.parse(dateStr)));

            } catch (JSONException error) {
                error.printStackTrace();
                System.out.println("Error Json ::");
            } catch (ParseException error) {
                System.out.println("Error::");
            }

            return rowView;
        }
    }
}
