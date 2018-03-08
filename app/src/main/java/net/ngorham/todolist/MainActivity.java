package net.ngorham.todolist;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView;

public class MainActivity extends Activity {
    //Private variables
    private String[] drawerTitles;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private int drawerPos = 0;
    private ActionBarDrawerToggle drawerToggle;

    //Private classes
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id){
            selectItem(pos);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get drawer titles
        drawerTitles = getResources().getStringArray(R.array.drawer_titles);
        //Get drawer list view
        drawerList = (ListView) findViewById(R.id.drawer);
        //Get drawer layout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Display correct fragment on config changed
        if (savedInstanceState != null) {
            drawerPos = savedInstanceState.getInt("drawerPosition");
            setActionBarTitle(drawerPos);
        } else {
            selectItem(0);
        }
        //Populate ListView of DrawerLayout
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                drawerTitles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        //Create ActionBarDrawerToggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.open_drawer, R.string.close_drawer){
            //Call when drawer in closed state
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
            //Call when drawer in open state
            public void onDrawerOpened(View view){
                super.onDrawerOpened(view);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        FragmentManager fragMan = getFragmentManager();
                        Fragment frag = fragMan.findFragmentByTag("visible_frag");
                        if(frag instanceof TopFragment){
                            drawerPos = 0;
                        }
                        setActionBarTitle(drawerPos);
                        drawerList.setItemChecked(drawerPos, true);
                    }
                }
        );
        //Update FrameLayout
        /*TopFragment topFrag = new TopFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, topFrag);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();*/
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("drawerPosition", drawerPos);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    //Displays Action Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //Inflate menu, add items to action bar
        if(drawerPos == 0){
            getMenuInflater().inflate(R.menu.menu_main, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_list, menu);
        }
        //getMenuInflater().inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Call when user clicks an item in action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        //Handle action items
        switch(item.getItemId()){
            case R.id.add_list:
                //Add list action
                return true;
            case R.id.app_settings:
                //Settings action
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Called when invalidateOptionsMenu() is called
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.add_list).setVisible(!drawerOpen);
        //menu.findItem(R.id.app_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    //Navigation drawer item selected
    private void selectItem(int pos){
        drawerPos = pos;
        Fragment frag;
        switch(pos){
            default:
                frag = new TopFragment();
        }
        //Update FrameLayout
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, frag, "visible_frag");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        //Show title in action bar
        setActionBarTitle(pos);
        //close drawer
        drawerLayout.closeDrawer(drawerList);
    }
    //Set title in action bar
    private void setActionBarTitle(int pos){
        String title;
        if(pos == 0){
            title = getResources().getString(R.string.app_name);
        } else {
            title = drawerTitles[pos];
        }
        getActionBar().setTitle(title);
    }
}
