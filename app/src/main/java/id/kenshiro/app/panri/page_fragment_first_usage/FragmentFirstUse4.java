package id.kenshiro.app.panri.page_fragment_first_usage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import id.kenshiro.app.panri.R;

public class FragmentFirstUse4 extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentFirstUse4() {
        // Required empty public constructor
    }

    public static FragmentFirstUse4 newInstance(String param1, String param2) {
        FragmentFirstUse4 fragment = new FragmentFirstUse4();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.acttutor_main_fragment_firstuse_4, container, false);

        TextView txt = layout.findViewById(R.id.frag4_id_txtatas);
        txt.setTypeface(Typeface.createFromAsset(this.getContext().getAssets(), "Comic_Sans_MS3.ttf"), Typeface.BOLD);
        txt.setTextColor(Color.WHITE);
        txt.setGravity(Gravity.CENTER);
        txt.setTextSize(15f);

        Button btn = layout.findViewById(R.id.frag4_id_img);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(v);
            }
        });
        return layout;
    }

    public void onButtonPressed(View btn) {
        if (mListener != null) {
            mListener.onFinalRequests(this, btn);
        }
    }

    public void setmListener(OnFragmentInteractionListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFinalRequests(Fragment results, View btn);
    }
}
