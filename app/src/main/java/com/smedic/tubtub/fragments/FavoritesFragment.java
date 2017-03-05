/*
 * Copyright (C) 2016 SMedic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smedic.tubtub.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smedic.tubtub.MainActivity;
import com.smedic.tubtub.R;
import com.smedic.tubtub.adapters.VideosAdapter;
import com.smedic.tubtub.database.YouTubeSqlDb;
import com.smedic.tubtub.interfaces.OnItemSelected;
import com.smedic.tubtub.model.YouTubeVideo;
import com.smedic.tubtub.utils.NetworkConf;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stevan Medic on 21.3.16..
 */
public class FavoritesFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "SMEDIC Favorites";
    private List<YouTubeVideo> favoriteVideos;

    private ListView favoritesListView;
    private VideosAdapter videoListAdapter;
    private NetworkConf conf;
    private Context context;
    private OnItemSelected itemSelected;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        favoriteVideos = new ArrayList<>();
        conf = new NetworkConf(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        favoritesListView = (ListView) v.findViewById(R.id.fragment_list_items);
        favoritesListView.setOnItemClickListener(this);
        videoListAdapter = new VideosAdapter(getActivity(), favoriteVideos, false);
        videoListAdapter.setOnItemEventsListener(this);
        favoritesListView.setAdapter(videoListAdapter);

        //disable swipe to refresh for this tab
        v.findViewById(R.id.swipe_to_refresh).setEnabled(false);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        favoriteVideos.clear();
        favoriteVideos.addAll(YouTubeSqlDb.getInstance().videos(YouTubeSqlDb.VIDEOS_TYPE.FAVORITE).readAll());
        videoListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);

        if (visible && isResumed()) {
            //Log.d(TAG, "RecentlyWatchedFragment visible and resumed");
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume
            onResume();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.context = context;
            itemSelected = (MainActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    /**
     * Clears recently played list items
     */
    public void clearFavoritesList() {
        favoriteVideos.clear();
        videoListAdapter.notifyDataSetChanged();
    }

    /**
     * Adds listener for list item choosing
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        YouTubeSqlDb.getInstance().videos(YouTubeSqlDb.VIDEOS_TYPE.RECENTLY_WATCHED)
                .create(favoriteVideos.get(pos));
        itemSelected.onPlaylistSelected(favoriteVideos, pos);
    }
}
