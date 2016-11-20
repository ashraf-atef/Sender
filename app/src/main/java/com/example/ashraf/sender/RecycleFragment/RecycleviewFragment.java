package com.example.ashraf.sender.RecycleFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ashraf.sender.R;
import com.example.ashraf.sender.RecycleFragment.RecycleAdaptor;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecycleviewFragment extends Fragment {


   public RecycleAdaptor recycleAdaptor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_recycleview, container, false);
        RecyclerView recyclerView = (RecyclerView) rootview.findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        recycleAdaptor = new RecycleAdaptor(getContext());
        recyclerView.setAdapter(recycleAdaptor);
        return rootview;
    }

}
