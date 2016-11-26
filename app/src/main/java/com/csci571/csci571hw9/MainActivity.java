package com.csci571.csci571hw9;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.csci571.csci571hw9.BillLists.BillListFragment;
import com.csci571.csci571hw9.CommitteesLists.CommitteesListFragment;
import com.csci571.csci571hw9.LegislatorLists.LegislatorListFragment;
import com.csci571.csci571hw9.LocalStorage.LocalStorage;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,

        LegislatorFragment.OnFragmentInteractionListener,
        BillFragment.OnFragmentInteractionListener,
        CommitteeFragment.OnFragmentInteractionListener,
        FavoriteFragment.OnFragmentInteractionListener,

        LegislatorListFragment.OnFragmentInteractionListener,

        BillListFragment.OnFragmentInteractionListener,

        CommitteesListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // initialize the fragment
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                LegislatorFragment.newInstance()).commit();

        navigationView.setCheckedItem(R.id.nav_legislators);
        navigationView.setNavigationItemSelectedListener(this);


        // local storage initialize local storage
        LocalStorage.getInstance(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        System.out.println(id);
        if (id == R.id.nav_legislators) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, LegislatorFragment.newInstance()).commit();
        } else if (id == R.id.nav_bills) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, BillFragment.newInstance()).commit();
        } else if (id == R.id.nav_committees) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, CommitteeFragment.newInstance()).commit();
        } else if (id == R.id.nav_favorites) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, FavoriteFragment.newInstance()).commit();
        } else if (id == R.id.nav_about_me) {
            Intent intent = new Intent(this, AboutMeActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        System.out.println(uri.toString());
    }
}
