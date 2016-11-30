package com.csci571.csci571hw9.LegislatorLists;

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
import com.android.volley.toolbox.StringRequest;
import com.csci571.csci571hw9.CustomUtils;
import com.csci571.csci571hw9.LegislatorDetailActivity;
import com.csci571.csci571hw9.LocalStorage.LocalStorage;
import com.csci571.csci571hw9.R;
import com.csci571.csci571hw9.VolleySignleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LegislatorListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LegislatorListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LegislatorListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LEGISLATOR_LIST_TYPE = "param1";

    // TODO: Rename and change types of parameters
    private String legislatorListType;

    private OnFragmentInteractionListener mListener;

    private View inflatedView;
    private Context context;
    Map<String, Integer> mapIndex;

    public LegislatorListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param legislatorListType Legislator List Type state/senate/house
     * @return A new instance of fragment LegislatorListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LegislatorListFragment newInstance(String legislatorListType) {
        LegislatorListFragment fragment = new LegislatorListFragment();
        Bundle args = new Bundle();
        args.putString(LEGISLATOR_LIST_TYPE, legislatorListType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            legislatorListType = getArguments().getString(LEGISLATOR_LIST_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        inflatedView = inflater.inflate(R.layout.fragment_legislator_list, container, false);

        TextView loadingTextView = (TextView) inflatedView.findViewById(R.id.legislator_loading_text);
        loadingTextView.setVisibility(View.VISIBLE);

        LinearLayout indexLayout = (LinearLayout) inflatedView.findViewById(R.id.legislator_list_index);
        indexLayout.setVisibility(View.GONE);

        String url;
        switch (getArguments().getString(LEGISLATOR_LIST_TYPE)) {
            case "state":
                url ="http://csci571hw8.us-west-1.elasticbeanstalk.com/?req=legislator-state";
                break;
            case "senate":
                url = "http://csci571hw8.us-west-1.elasticbeanstalk.com/?req=legislator-senate";
                break;
            case "house":
                url = "http://csci571hw8.us-west-1.elasticbeanstalk.com/?req=legislator-house";
                break;

            case "favorite":
                LocalStorage localStorage = LocalStorage.getInstance(getActivity());
                JSONArray jsonArray = localStorage.getItems("legislator");
                displayList(CustomUtils.getInstance().sortJsonArray(jsonArray, "last_name"));
                return inflatedView;

            default:
                url ="http://104.198.0.197:8080/legislators?per_page=all&order=last_name__asc";
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
        //return inflater.inflate(R.layout.fragment_legislator_list, container, false);
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
            //ListView listView = (ListView) inflatedView.findViewById(R.id.legislator_list_view);
            ListView listView = (ListView) inflatedView.findViewById(R.id.legislator_list_view);
            LegislatorArrayAdapter legislatorArrayAdapter = new LegislatorArrayAdapter(context, list);
            listView.setAdapter(legislatorArrayAdapter);

            if (jsonArray.length() > 0){
                TextView loadingTextView = (TextView) inflatedView.findViewById(R.id.legislator_loading_text);
                loadingTextView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }

            getIndexList(jsonArray);
            displayIndex();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity().getApplication(), LegislatorDetailActivity.class);
                    JSONObject jsonObject = list.get(position);
                    intent.putExtra("item", jsonObject.toString());
                    startActivity(intent);
                }
            });

        } catch (JSONException error) {
            System.out.println("Error::");
        }
    }

    private void getIndexList(JSONArray jsonArray) {
        mapIndex = new LinkedHashMap<String, Integer>();

        String indexType = getArguments().getString(LEGISLATOR_LIST_TYPE);

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String lastName;
                if(indexType == "state") {
                    lastName = jsonArray.getJSONObject(i).getString("state_name");
                } else {
                    lastName = jsonArray.getJSONObject(i).getString("last_name");
                }
                String index = lastName.substring(0, 1);

                if (mapIndex.get(index) == null)
                    mapIndex.put(index, i);
            } catch (JSONException error) {
                System.out.println("Error::");
            }
        }
    }

    private void displayIndex() {

        LinearLayout indexLayout = (LinearLayout) inflatedView.findViewById(R.id.legislator_list_index);
        indexLayout.removeAllViews();
        indexLayout.setVisibility(View.VISIBLE);
        TextView textView;
        List<String> indexList = new ArrayList<String>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getActivity().getLayoutInflater().inflate(
                    R.layout.list_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView selectedIndex = (TextView) v;
                    ListView listView = (ListView) inflatedView.findViewById(R.id.legislator_list_view);
                    listView.setSelection(mapIndex.get(selectedIndex.getText()));
                }
            });
            indexLayout.addView(textView);
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

    @Override
    public void onResume() {
        super.onResume();
        String listType = getArguments().getString(LEGISLATOR_LIST_TYPE);
        if (listType == "favorite") {

            TextView loadingTextView = (TextView) inflatedView.findViewById(R.id.legislator_loading_text);
            loadingTextView.setVisibility(View.VISIBLE);

            LinearLayout indexLayout = (LinearLayout) inflatedView.findViewById(R.id.legislator_list_index);
            indexLayout.setVisibility(View.GONE);

            LocalStorage localStorage = LocalStorage.getInstance(getActivity());
            JSONArray jsonArray = localStorage.getItems("legislator");
            displayList(CustomUtils.getInstance().sortJsonArray(jsonArray, "last_name"));
        }
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

    public class LegislatorArrayAdapter extends ArrayAdapter<JSONObject> {

        private final Context context;
        private final ArrayList<JSONObject> items;

        public LegislatorArrayAdapter(Context context, ArrayList<JSONObject> items) {
            super(context, R.layout.legislator_list_item, items);
            this.context = context;
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            JSONObject jsonObject = items.get(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.legislator_list_item, parent, false);

            TextView rowNameView = (TextView) rowView.findViewById(R.id.legislator_list_item_name);
            TextView rowPartyView = (TextView) rowView.findViewById(R.id.legislator_list_item_subtext);

            try {
                rowNameView.setText(String.format("%s, %s", jsonObject.getString("last_name"), jsonObject.getString("first_name")));

                String subText = String.format("(%s)%s - District %s", jsonObject.getString("party"), jsonObject.getString("state_name"),
                        (jsonObject.getString("district") == "null")?"0":jsonObject.getString("district"));
                rowPartyView.setText(subText);

                ImageView imageView = (ImageView) rowView.findViewById(R.id.legislator_list_item_image);

                // Get the ImageLoader through your singleton class.
                ImageLoader imageLoader = VolleySignleton.getInstance(this.context).getImageLoader();
                String imageUrl = "https://theunitedstates.io/images/congress/225x275/" + jsonObject.getString("bioguide_id") + ".jpg";
                imageLoader.get(imageUrl, ImageLoader.getImageListener(imageView, R.drawable.ic_blank, R.drawable.ic_blank));

            } catch (JSONException error) {
                System.out.println("Error::");
            }

            return rowView;
        }
    }
}
