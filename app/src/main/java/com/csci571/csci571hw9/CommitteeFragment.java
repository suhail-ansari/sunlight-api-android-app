package com.csci571.csci571hw9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csci571.csci571hw9.CommitteesLists.CommitteesListFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CommitteeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CommitteeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommitteeFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private View inflatedView;

    public CommitteeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommitteeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommitteeFragment newInstance() {
        CommitteeFragment fragment = new CommitteeFragment();
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
        inflatedView = inflater.inflate(R.layout.fragment_committee, container, false);

        getChildFragmentManager().beginTransaction().replace(R.id.committee_list_container, CommitteesListFragment.newInstance("house")).commit();

        TabLayout tabLayout = (TabLayout) inflatedView.findViewById(R.id.committee_tabs);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        System.out.println("tab: 0");
                        getChildFragmentManager().beginTransaction().replace(R.id.committee_list_container, CommitteesListFragment.newInstance("house")).commit();
                        break;
                    case 1:
                        getChildFragmentManager().beginTransaction().replace(R.id.committee_list_container, CommitteesListFragment.newInstance("senate")).commit();
                        System.out.println("tab: 1");
                        break;
                    case 2:
                        getChildFragmentManager().beginTransaction().replace(R.id.committee_list_container, CommitteesListFragment.newInstance("joint")).commit();
                        System.out.println("tab: 1");
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setTitle("Committees");
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
