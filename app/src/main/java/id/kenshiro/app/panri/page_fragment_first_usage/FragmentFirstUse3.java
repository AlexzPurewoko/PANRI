package id.kenshiro.app.panri.page_fragment_first_usage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import id.kenshiro.app.panri.R;


public class FragmentFirstUse3 extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private final float txt_besar = 18f;
    private final float txt_kecil = 11f;
    private OnFragmentInteractionListener mListener;

    public FragmentFirstUse3() {
        // Required empty public constructor
    }

    public static FragmentFirstUse3 newInstance(String param1, String param2) {
        FragmentFirstUse3 fragment = new FragmentFirstUse3();
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
        View layout = inflater.inflate(R.layout.acttutor_main_fragment_firstuse_3, container, false);
        // section upper
        setTextAppearanceLayout(layout, R.id.frag3_id_txtatas, Typeface.BOLD, Gravity.CENTER);
        // section txt 1 section 1
        setTextAppearanceLayout(layout, R.id.frag3_id_txtim1, Typeface.BOLD, Gravity.LEFT);
        // section txt 2 section 1
        setTextAppearanceLayout(layout, R.id.frag3_id_txtim2, Typeface.NORMAL, Gravity.LEFT);
        // section txt 1 section 2
        setTextAppearanceLayout(layout, R.id.frag3_id_txtim21, Typeface.BOLD, Gravity.LEFT);
        // section txt 2 section 2
        setTextAppearanceLayout(layout, R.id.frag3_id_txtim22, Typeface.NORMAL, Gravity.LEFT);
        return layout;
    }

    private void setTextAppearanceLayout(@NonNull View layout, @IdRes int resId, int typeface_type, int gravity) {
        TextView txt = layout.findViewById(resId);
        txt.setTypeface(Typeface.createFromAsset(this.getContext().getAssets(), "Comic_Sans_MS3.ttf"), typeface_type);
        txt.setTextColor(Color.WHITE);
        txt.setGravity(gravity);
        //txt.setTextSize(size);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        void onFragmentInteraction(Uri uri);
    }
}
