package com.cocodev.TDUCManager.articles;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cocodev.TDUCManager.Article_details;
import com.cocodev.TDUCManager.Articles;
import com.cocodev.TDUCManager.Events;
import com.cocodev.TDUCManager.MainActivity;
import com.cocodev.TDUCManager.PostNotice;
import com.cocodev.TDUCManager.R;
import com.cocodev.TDUCManager.Utility.Article;
import com.cocodev.TDUCManager.Utility.EmployeeContentArticle;
import com.cocodev.TDUCManager.adapter.CustomArticleHolderAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SubmittedArticles extends Fragment implements AbsListView.OnScrollListener {


    private int preLast = 0;
    private final String LAST_SCROLL_STATE = "lastScrollState";

    private long itemCount = 0;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private CustomArticleHolderAdapter mAdapter;

    public SubmittedArticles() {
    }

    ListView mListView;
    View mFooterView;

    public static SubmittedArticles newInstance(String type) {
        SubmittedArticles a = new SubmittedArticles();
        return a;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("EmployeeContent").child(MainActivity.currentUser.getUid())
                .child("Articles");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_submitted_articles, container, false);
        mListView = (ListView) view.findViewById(R.id.listView_articleHolder);
        com.github.clans.fab.FloatingActionMenu submit = (com.github.clans.fab.FloatingActionMenu) view.findViewById(R.id.post);
        FloatingActionButton submitArticle = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_item1);
        FloatingActionButton submitEvent = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_item2);
        FloatingActionButton submitNotice = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_item3);

        submitArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), Articles.class);
                startActivity(i);
            }
        });
        submitEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), Events.class);
                startActivity(i);
            }
        });
        submitNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), PostNotice.class);
                startActivity(i);
            }
        });


        //view to be added while loading more data;
        mFooterView = LayoutInflater.from(getContext()).inflate(R.layout.footer_progress_bar, null);

        mListView.addFooterView(mFooterView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mListView == null)
                    return;
                mListView.removeFooterView(mFooterView);
                mAdapter.notifyDataSetChanged();
            }
        }, 5000);

        //view when list is empty
        TextView textView = (TextView) view.findViewById(R.id.articleHolder_emptyView);
        textView.setText("There are currently no articles under this Category.");
        mListView.setEmptyView(textView);

        //if this is the home page else ...

        mAdapter = new CustomArticleHolderAdapter<EmployeeContentArticle>(
                getActivity(),
                EmployeeContentArticle.class,
                0,
                new DatabaseReference[]{databaseReference}
        ) {

            @Override
            protected void populateView(View v, EmployeeContentArticle model, int position) {
                final CustomArticleHolderAdapter.ViewHolder viewHolder = (ViewHolder) v.getTag();


                DatabaseReference dbref = firebaseDatabase.getReference().child("PendingArticles")
                        .child(MainActivity.CollegeName);

                dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Article article = dataSnapshot.getValue(Article.class);
                        if (article == null)
                            return;
                        if (viewHolder.authorView != null) {
                            viewHolder.authorView.setText(article.getAuthor());
                        }
                       // viewHolder.timeView.setText(article.getTime());
                        viewHolder.titleView.setText(article.getTitle());
                        viewHolder.UID.setText(article.getUID());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(), "There has been some problem establishing connection with the server.", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        };

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(onItemClickListener);
        mListView.setOnScrollListener(this);

        return view;
    }


    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String UID = (String) ((TextView) view.findViewById(R.id.article_UID)).getText();
            Intent intent = new Intent(getContext(), Article_details.class);
            intent.putExtra(Article_details.key, UID);
            Pair<View, String> pair1 = Pair.create(view.findViewById(R.id.articleImage), getString(R.string.home_share_image));
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    pair1
            );

            startActivity(intent, optionsCompat.toBundle());

        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Submitted Articles");
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListView = null;
        mAdapter.removeListener();
        databaseReference = null;

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //do nothing
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // Make your calculation stuff here. You have all your
        // needed info from the parameters of this function.

        // Sample calculation to determine if the last
        // item is fully visible.
        final int lastItem = firstVisibleItem + visibleItemCount - mListView.getFooterViewsCount();

        if (lastItem == totalItemCount) {
            if (preLast != lastItem) {
                Log.e("his", Integer.toString(preLast) + " " + Integer.toString(lastItem));
                if (mListView.getFooterViewsCount() == 0) {
                    mListView.addFooterView(mFooterView);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mListView == null)
                                return;
                            mListView.removeFooterView(mFooterView);
                            mAdapter.notifyDataSetChanged();
                        }
                    }, 5000);
                }
                preLast = lastItem;
                //to avoid multiple calls for last item
                mAdapter.populateMoreList(mListView, mFooterView);
            }
        }

    }
}
