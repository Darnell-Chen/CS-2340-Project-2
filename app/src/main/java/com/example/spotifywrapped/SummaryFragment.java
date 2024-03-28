package com.example.spotifywrapped;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private WrappedViewModel wrappedVM;

    public SummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SummaryFragment newInstance(String param1, String param2) {
        SummaryFragment fragment = new SummaryFragment();
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
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FrameLayout background = getView().findViewById(R.id.summaryBackground);

        AnimationDrawable animDrawable = (AnimationDrawable) background.getBackground();
        animDrawable.setEnterFadeDuration(2000);
        animDrawable.setExitFadeDuration(2500);
        animDrawable.start();

        wrappedVM = new ViewModelProvider(requireActivity()).get(WrappedViewModel.class);

        exportImage(view);
    }

    public void exportImage(View view) {
        FrameLayout summaryBackground = view.findViewById(R.id.summaryBackground);
        summaryBackground.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = ImageExporter.captureLayoutAsBitmap(summaryBackground);
                wrappedVM.addImage(bitmap);
                /*boolean exported = ImageExporter.saveBitmapToGallery(requireContext(), bitmap,
                        "summary_image", "Image exported from layout");
                if (exported) {
                    Toast.makeText(requireContext(), "Image exported successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to export image", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }

    private View createFragmentView() {
        // Inflate the fragment's layout programmatically
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        return inflater.inflate(R.layout.fragment_summary, null, false);
    }
}