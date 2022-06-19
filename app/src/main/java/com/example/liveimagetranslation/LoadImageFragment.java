package com.example.liveimagetranslation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.liveimagetranslation.databinding.FragmentLoadImageBinding;


public class LoadImageFragment extends Fragment {
    public static final String TAG = "LoadImageFragment";

    private FragmentLoadImageBinding binding;
    private ActivityResultLauncher<String> getImageIntent;

    public LoadImageFragment(ActivityResultLauncher<String> getImageIntent) {
        this.getImageIntent = getImageIntent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentLoadImageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonLoadImage.setOnClickListener(view1 -> {
            getImageIntent.launch("image/*");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
