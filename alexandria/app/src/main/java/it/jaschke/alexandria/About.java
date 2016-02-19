package it.jaschke.alexandria;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class About extends Fragment
{

    public About(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ((MainActivity)getActivity()).hideKeyboard();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity)
        {
            super.onAttach(activity);
            getActivity().setTitle(R.string.about);
    }
    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        getActivity().setTitle(R.string.about);
    }
}
