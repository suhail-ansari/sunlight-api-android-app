package com.csci571.csci571hw9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csci571.csci571hw9.LegislatorLists.LegislatorListFragment;

import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LegislatorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LegislatorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LegislatorFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private View inflatedView;
    private JSONObject response;

    public LegislatorFragment() {
        // Required empty public constructor
    }

    public static LegislatorFragment newInstance() {
        LegislatorFragment fragment = new LegislatorFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView =  inflater.inflate(R.layout.fragment_legislator, container, false);

        getChildFragmentManager().beginTransaction().replace(R.id.legislator_list_container, LegislatorListFragment.newInstance("state")).commit();

        TabLayout tabLayout = (TabLayout) inflatedView.findViewById(R.id.legislator_tabs);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        System.out.println("tab: 0");
                        getChildFragmentManager().beginTransaction().replace(R.id.legislator_list_container, LegislatorListFragment.newInstance("state")).commit();
                        break;
                    case 1:
                        getChildFragmentManager().beginTransaction().replace(R.id.legislator_list_container, LegislatorListFragment.newInstance("house")).commit();
                        System.out.println("tab: 1");
                        break;
                    case 2:
                        System.out.println("tab: 2");
                        getChildFragmentManager().beginTransaction().replace(R.id.legislator_list_container, LegislatorListFragment.newInstance("senate")).commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return inflatedView;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        getActivity().setTitle("Legislators");
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
}

