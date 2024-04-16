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

    private final int total_duration = 5000;
    private int fade_time;
    private int fade_delay;

    private WrappedViewModel wrappedVM;

    public SummaryFragment() {
        // Required empty public constructor
    }

    public static SummaryFragment newInstance() {
        SummaryFragment fragment = new SummaryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private View createFragmentView() {
        // Inflate the fragment's layout programmatically
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        return inflater.inflate(R.layout.fragment_summary, null, false);
    }

    public void exportImage(View view) {
        FrameLayout summaryBackground = view.findViewById(R.id.summaryBackground);
        summaryBackground.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = ImageExporter.captureLayoutAsBitmap(summaryBackground);
                wrappedVM.addImage(bitmap);
            }
        });
    }
}