package net.ngorham.todolist;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
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

public class MainActivity extends Activity  implements TopFragment.TopListener {
    //Private variables
    private String[] drawerTitles;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private int drawerPos = 0;
    private int listId = 0;
    private int curMenu = 0;
    private String actionBarTitle;
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
        drawerTitles = getResources().getStringArray(R.array.drawer_titles); //replace with db query
        //Get drawer list view
        drawerList = (ListView) findViewById(R.id.drawer);
        //Get drawer layout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Display correct fragment on config changed
        if (savedInstanceState != null) {
            drawerPos = savedInstanceState.getInt("drawerPosition");
            actionBarTitle = savedInstanceState.getString("actionBarTitle");
            curMenu = savedInstanceState.getInt("curMenu");
            setActionBarTitle(actionBarTitle);
        } else {
            //Set Action bar title
            actionBarTitle = getResources().getString(R.string.app_name);
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
                //invalidateOptionsMenu();
            }
            //Call when drawer in open state
            public void onDrawerOpened(View view){
                super.onDrawerOpened(view);
                //invalidateOptionsMenu();
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
                            curMenu = 0;
                            actionBarTitle = getResources().getString(R.string.app_name);
                        }
                        if(frag instanceof  ListDetailFragment){
                            curMenu = 1;
                        }
                        invalidateOptionsMenu();
                        setActionBarTitle(actionBarTitle);
                        drawerList.setItemChecked(drawerPos, true);
                    }
                }
        );
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
        outState.putString("actionBarTitle", actionBarTitle);
        outState.putInt("curMenu", curMenu);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(curMenu == 0){ //TopFragment
            menu.findItem(R.id.edit_list).setVisible(false);
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
        if(curMenu == 0){
            menu.findItem(R.id.add_list).setVisible(true);
            menu.findItem(R.id.edit_list).setVisible(false);
        } else if(curMenu == 1){
            menu.findItem(R.id.add_list).setVisible(false);
            menu.findItem(R.id.edit_list).setVisible(true);
        }
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
        setActionBarTitle(actionBarTitle);
        //close drawer
        drawerLayout.closeDrawer(drawerList);
    }

    //Set title in action bar
    private void setActionBarTitle(String title){
        getActionBar().setTitle(title);
    }

    //Called when an item in TopFragment is clicked
    @Override
    public void itemClicked(long id){
        listId = (int)id;
        //Replace current fragment with ListDetailFragment
        ListDetailFragment frag = new ListDetailFragment();
        frag.setListId(id);
        //Update FrameLayout
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, frag, "visible_frag");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        //Update title in action bar
        String[] lists = getResources().getStringArray(R.array.top_frag); //replace with db query
        actionBarTitle = lists[listId];
        setActionBarTitle(actionBarTitle);
        //Update action bar items
        curMenu = 1;
        invalidateOptionsMenu();
        /*
        Intent intent = new Intent(this, ListDetailActivity.class);
        intent.putExtra(ListDetailActivity.EXTRA_LIST_ID, (int)id);
        startActivity(intent);
        */
    }
}
