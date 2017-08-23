package com.cocodev.TDUCManager.Utility;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Sudarshan on 20-06-2017.
 */

public abstract class  RefListAdapter<T> extends ArrayAdapter<T>{
    private DatabaseReference[] db;
    private Query[] queries;
    private ChildEventListener childEventListener;
    private Class<T> modelclass;
    private int limit=2;
    private String lastArticle="";
    private ListView mListView;
    private View mFooterView;
    public RefListAdapter(@NonNull Context context,Class<T> modelclass, @LayoutRes int resource,DatabaseReference[] db) {
        super(context, resource);
        this.db=db;
        this.modelclass=modelclass;
        instantiateList();
    }


    private void instantiateList() {
        setChildEventListener();

        for (DatabaseReference databaseReference:db
             ) {
            databaseReference.addChildEventListener(childEventListener);

        }

    }

    private void setChildEventListener() {
        childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(!lastArticle.equals(dataSnapshot.getKey())) {
                    lastArticle = dataSnapshot.getKey();
                    T t =dataSnapshot.getValue(modelclass);
                    add(t);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                T t = dataSnapshot.getValue(modelclass);
                int position = getPosition(t);
                remove(t);
                insert(t,position);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                T t = dataSnapshot.getValue(modelclass);
                remove(t);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    //do Nothing
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                    //error in connection
            }

        };
    }

    public void removeListener(){
        for (DatabaseReference databaseReference:db
             ) {
            databaseReference.removeEventListener(childEventListener);
        }
        childEventListener=null;
    }

    @NonNull
    @Override
    public  View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        int layoutID= getItemViewType(position);
        View view = LayoutInflater.from(getContext()).inflate(layoutID,parent,false);
        return view;
    }



    public void populateMoreList(final ListView listView,final View view){
        for (DatabaseReference databaseReference : db
                ) {
            databaseReference.startAt(null, lastArticle).limitToLast(limit).addChildEventListener(childEventListener);
        }
    }

    protected abstract void populateView(View v, T model, int position);


}