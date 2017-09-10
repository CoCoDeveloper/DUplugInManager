package com.cocodev.TDUCManager.articles;

import android.content.Context;
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

import com.cocodev.TDUCManager.Article_details;
import com.cocodev.TDUCManager.Articles;
import com.cocodev.TDUCManager.Events;
import com.cocodev.TDUCManager.MainActivity;
import com.cocodev.TDUCManager.PostNotice;
import com.cocodev.TDUCManager.R;
import com.cocodev.TDUCManager.Utility.Article;
import com.cocodev.TDUCManager.adapter.CustomArticleHolderAdapter;
import com.cocodev.TDUCManager.pArticleDetails;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class PendingArticles extends Fragment implements AbsListView.OnScrollListener {


    private int preLast = 0;
    private final String LAST_SCROLL_STATE = "lastScrollState";

    private long itemCount = 0;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private CustomArticleHolderAdapter mAdapter;

    public PendingArticles() {
    }

    ListView mListView;
    View mFooterView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("PendingArticles").child(MainActivity.CollegeName).child("Pending");

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

        mAdapter = new CustomArticleHolderAdapter<Article>(
                getActivity(),
                Article.class,
                0,
                new DatabaseReference[]{databaseReference}
        ) {

            @Override
            protected void populateView(View v, Article model, int position) {
                final CustomArticleHolderAdapter.ViewHolder viewHolder = (ViewHolder) v.getTag();
                viewHolder.titleView.setText(model.getTitle());
                viewHolder.UID.setText(model.getUid());

            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public View newView(Context context, int position, ViewGroup parent) {
                int viewType = getItemViewType(position);
                int layoutID = -1;
                if(viewType==0){
                    layoutID = R.layout.article_list_view_second_pending;
                }else if(viewType==1){
                    layoutID = R.layout.article_list_view_second;
                }else{
                    layoutID = R.layout.article_list_view_second_rejected;
                }

                View view = LayoutInflater.from(context).inflate(layoutID,parent,false);
                ViewHolder viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
                return view;

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

            Intent intent = new Intent(getContext(),pArticleDetails.class);
            intent.putExtra(Article_details.key, UID);
            intent.putExtra(Article_details.key_status,0);

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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Pending Articles");
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
