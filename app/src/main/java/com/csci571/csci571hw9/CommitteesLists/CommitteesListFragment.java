package com.csci571.csci571hw9.CommitteesLists;

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
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.csci571.csci571hw9.CommitteeDetailActivity;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CommitteesListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CommitteesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommitteesListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String COMMITTEE_TYPE = "param1";

    private String committeeType;
    private View inflatedView;
    private Context context;

    private OnFragmentInteractionListener mListener;

    public CommitteesListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommitteesListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommitteesListFragment newInstance(String _committeesType) {
        CommitteesListFragment fragment = new CommitteesListFragment();
        Bundle args = new Bundle();
        args.putString(COMMITTEE_TYPE, _committeesType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            committeeType = getArguments().getString(COMMITTEE_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.fragment_committees_list, container, false);

        TextView loadingTextView = (TextView) inflatedView.findViewById(R.id.committee_loading_text);
        loadingTextView.setVisibility(View.VISIBLE);

        String url;
        switch (getArguments().getString(COMMITTEE_TYPE)) {
            case "house":
                url = "http://csci571hw8.us-west-1.elasticbeanstalk.com/?req=committee-house";
                break;
            case "senate":
                url = "http://csci571hw8.us-west-1.elasticbeanstalk.com/?req=committee-senate";
                break;
            case "joint":
                url = "http://csci571hw8.us-west-1.elasticbeanstalk.com/?req=committee-joint";
                break;

            case "favorite":
                LocalStorage localStorage = LocalStorage.getInstance(getActivity());
                JSONArray jsonArray = localStorage.getItems("committee");
                displayList(CustomUtils.getInstance().sortJsonArray(jsonArray, "name"));
                return inflatedView;

            default:
                url = "http://104.198.0.197:8080/committees?per_page=all&chamber=house&order=name__asc";
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
            ListView listView = (ListView) inflatedView.findViewById(R.id.committee_list_view);
            CommitteeArrayAdapter committeeArrayAdapter= new CommitteeArrayAdapter(context, list);
            listView.setAdapter(committeeArrayAdapter);

            if(jsonArray.length() > 0) {
                TextView loadingTextView = (TextView) inflatedView.findViewById(R.id.committee_loading_text);
                loadingTextView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity().getApplication(), CommitteeDetailActivity.class);
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
        super.onAttach(context);
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

    public class CommitteeArrayAdapter extends ArrayAdapter<JSONObject> {

        private final Context context;
        private final ArrayList<JSONObject> items;

        public CommitteeArrayAdapter(Context context, ArrayList<JSONObject> items) {
            super(context, R.layout.committee_list_item, items);
            this.context = context;
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            JSONObject jsonObject = items.get(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.committee_list_item, parent, false);

            TextView committeeIdView = (TextView) rowView.findViewById(R.id.committee_id_text);
            TextView committeeNameView = (TextView) rowView.findViewById(R.id.committee_name_text);
            TextView committeeChamberView = (TextView) rowView.findViewById(R.id.committee_chamber_text);

            try {
                committeeIdView.setText(jsonObject.getString("committee_id").toUpperCase());
                committeeNameView.setText((jsonObject.getString("name") == "null")?"N.A":jsonObject.getString("name"));

                String chamber = jsonObject.getString("chamber");
                chamber = chamber.substring(0, 1).toUpperCase() + chamber.substring(1);
                committeeChamberView.setText(chamber);

            } catch (JSONException error) {
                System.out.println("Error::");
            }

            return rowView;
        }
    }
}
