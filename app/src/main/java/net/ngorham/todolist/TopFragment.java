package net.ngorham.todolist;

import android.app.Activity;
import android.app.ListFragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * A simple {@link ListFragment} subclass.
 */
public class TopFragment extends ListFragment {

    interface TopListener {
        void itemClicked(ListView listView, int position, long id);
    }

    //Private Variables
    private TopListener listener;
    //private SQLiteDatabase db;
    //private Cursor cursor;

    //Private inner classes
    private class DisplayListsTask extends AsyncTask<Integer, Void, Boolean>{
        protected void onPreExecute(){}
        protected Boolean doInBackground(Integer... integers){
            try {
                SQLiteOpenHelper toDoListDatabaseHelper = new ToDoListDatabaseHelper(getActivity());
                SQLiteDatabase db = toDoListDatabaseHelper.getReadableDatabase();
                Cursor cursor = db.query("LIST",
                        new String[]{"_id", "NAME"},
                        null, null, null, null, null);
                CursorAdapter listAdapter = new SimpleCursorAdapter(getActivity(),
                        android.R.layout.simple_list_item_1,
                        cursor,
                        new String[]{"NAME"},
                        new int[]{android.R.id.text1},
                        0);
                setListAdapter(listAdapter);
                //db.close();
                return true;
            } catch(SQLiteException e){
                return false;
            }
        }
        protected void onPostExecute(Boolean success){
            if(!success){
                Toast toast = Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    //Default constructor
    public TopFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Create cursor
        new DisplayListsTask().execute(0);
        /*try {
            SQLiteOpenHelper toDoListDatabaseHelper = new ToDoListDatabaseHelper(getActivity());
            db = toDoListDatabaseHelper.getReadableDatabase();
            cursor = db.query("LIST",
                    new String[]{"_id", "NAME"},
                    null, null, null, null, null);
            CursorAdapter listAdapter = new SimpleCursorAdapter(getActivity(),
                    android.R.layout.simple_list_item_1,
                    cursor,
                    new String[]{"NAME"},
                    new int[]{android.R.id.text1},
                    0);
            setListAdapter(listAdapter);
            //cursor.close();
            //db.close();
        } catch(SQLiteException e){
            Toast toast = Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }*/
        //Dummy list for testing
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(),
        //       android.R.layout.simple_list_item_1,
        //        getResources().getStringArray(R.array.top_frag));
        //setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    //Attach listFragment listener to current activity
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.listener = (TopListener)activity;
    }

    @Override
    public void onStart(){
        super.onStart();
        new DisplayListsTask().execute(0);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        //cursor.close();
        //db.close();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int pos, long id){
        if(listener != null){
            listener.itemClicked(listView, pos, id);
        }
    }

}
