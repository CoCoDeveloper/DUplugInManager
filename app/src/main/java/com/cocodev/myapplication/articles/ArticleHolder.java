package com.cocodev.myapplication.articles;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cocodev.myapplication.Article_details;
import com.cocodev.myapplication.MyApplication;
import com.cocodev.myapplication.R;
import com.cocodev.myapplication.Utility.Article;
import com.cocodev.myapplication.Utility.Notice;
import com.cocodev.myapplication.adapter.CustomArticleHolderAdapter;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.leakcanary.RefWatcher;


public class ArticleHolder extends Fragment  {

    public static String key = "type";
    private int type =-1;
    private final int TYPE_HOME = 0;
    private final int TYPE_COLLEGE = 1;
    private final int TYPE_SPORTS = 2;
    private final int TYPE_DANCE = 3;
    private final int TYPE_MUSIC = 4;
    private final int ARTICLE_LOADER = 1;
    private final String SPORTS = "Sports";
    private final String DANCE = "Dance";
    private final String MUSIC = "Music";
    private final String LAST_SCROLL_STATE = "lastScrollState";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private CustomArticleHolderAdapter mAdapter;

    public ArticleHolder(){}

    ListView mListView;


    public static ArticleHolder newInstance(int type){
        ArticleHolder a = new ArticleHolder();
        a.setType(type);
        return  a;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            type = savedInstanceState.getInt(key);
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        if(type!=TYPE_HOME) {
            databaseReference = firebaseDatabase.getReference()
                    .child("Categories").child("Articles").child(getTypeString());
        }else{
            databaseReference = firebaseDatabase.getReference()
                    .child("Articles");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_article_holder, container, false);

        if(type!=TYPE_HOME) {
            mAdapter = new CustomArticleHolderAdapter<String>(
                    getActivity(),
                    String.class,
                    R.layout.adapter_notice,
                    new DatabaseReference[]{databaseReference}
            ) {

                @Override
                protected void populateView(View v, String model, int position) {
                    final CustomArticleHolderAdapter.ViewHolder viewHolder = (ViewHolder) v.getTag();

                    DatabaseReference dbref = firebaseDatabase.getReference().child("Articles")
                            .child(model);
                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Article article = dataSnapshot.getValue(Article.class);
                            if (viewHolder.authorView != null) {
                                viewHolder.authorView.setText(article.getAuthor());
                            }
                            viewHolder.timeView.setText(article.getTime());
                            viewHolder.titleView.setText(article.getTitle());
                            viewHolder.UID.setText(article.getUID());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("TAG", "onCancelled --> addValueEventListener --> populateView" + databaseError.toString());
                        }
                    });
                }
            };
        }else{
            mAdapter = new CustomArticleHolderAdapter<Article>(
                    getActivity(),
                    Article.class,
                    R.layout.adapter_notice,
                    new DatabaseReference[] {databaseReference}
            ) {

                @Override
                protected void populateView(View v, Article model, int position) {
                    final CustomArticleHolderAdapter.ViewHolder viewHolder = (ViewHolder) v.getTag();
                    if(viewHolder.authorView!=null){
                        viewHolder.authorView.setText(model.getAuthor());
                    }
                    viewHolder.titleView.setText(model.getTitle());
                    viewHolder.timeView.setText(model.getTime());
                    viewHolder.UID.setText(model.getUID());
                }
            };
        }

         mListView = (ListView) view.findViewById(R.id.listView_articleHolder);

        TextView textView = (TextView) view.findViewById(R.id.articleHolder_emptyView);
        textView.setText("There are currently no articles under this Category.");
        mListView.setEmptyView(textView);
        mListView.setAdapter(mAdapter);
         mListView.setOnItemClickListener(onItemClickListener);


         return view;
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String UID = (String) ((TextView) view.findViewById(R.id.article_UID)).getText();
            Intent intent = new Intent(getContext(),Article_details.class);
            intent.putExtra(Article_details.key,UID);
            Pair<View,String> pair1 = Pair.create(view.findViewById(R.id.articleImage),getString(R.string.home_share_image));
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    pair1
            );

            startActivity(intent,optionsCompat.toBundle());

        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public String getTypeString(){
        switch(type){
            case TYPE_SPORTS:
                return "Sports";
            case TYPE_DANCE:
                return "Dance";
            case TYPE_MUSIC:
                return "Music";
            case TYPE_HOME:
                return "Home";
            case TYPE_COLLEGE:
                return "College";
            default:
                return "Unknown Type";
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(key,type);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListView = null;
        mAdapter.removeListener();
        databaseReference =null;
    }

}
