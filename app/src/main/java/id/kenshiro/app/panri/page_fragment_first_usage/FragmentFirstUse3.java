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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentFirstUse3.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentFirstUse3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFirstUse3 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final float txt_besar = 20f;
    private final float txt_kecil = 12f;
    private OnFragmentInteractionListener mListener;

    public FragmentFirstUse3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentFirstUse3.
     */
    // TODO: Rename and change types and number of parameters
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
        setTextAppearanceLayout(layout, R.id.frag3_id_txtatas, txt_besar, Typeface.BOLD, Gravity.CENTER);
        // section txt 1 section 1
        setTextAppearanceLayout(layout, R.id.frag3_id_txtim1, txt_besar, Typeface.BOLD, Gravity.LEFT);
        // section txt 2 section 1
        setTextAppearanceLayout(layout, R.id.frag3_id_txtim2, txt_kecil, Typeface.NORMAL, Gravity.LEFT);
        // section txt 1 section 2
        setTextAppearanceLayout(layout, R.id.frag3_id_txtim21, txt_besar, Typeface.BOLD, Gravity.LEFT);
        // section txt 2 section 2
        setTextAppearanceLayout(layout, R.id.frag3_id_txtim22, txt_kecil, Typeface.NORMAL, Gravity.LEFT);
        return layout;
    }

    private void setTextAppearanceLayout(@NonNull View layout, @IdRes int resId, @Size float size, int typeface_type, int gravity) {
        TextView txt = layout.findViewById(resId);
        txt.setTypeface(Typeface.createFromAsset(this.getContext().getAssets(), "Comic_Sans_MS3.ttf"), typeface_type);
        txt.setTextColor(Color.WHITE);
        txt.setGravity(gravity);
        txt.setTextSize(size);
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
