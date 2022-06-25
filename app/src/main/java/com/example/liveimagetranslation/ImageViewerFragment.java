package com.example.liveimagetranslation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.liveimagetranslation.databinding.FragmentImageViewerBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.List;

public class ImageViewerFragment extends Fragment {
    public static final String TAG = "ImageViewerFragment";
    private final Uri imageUri;

    private FragmentImageViewerBinding binding;
    private Bitmap imageBitmap;
    private Bitmap displayedBitmap;

    private ObjectDetector objectDetector;

    public ImageViewerFragment(Uri imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Multiple object detection in static images
        ObjectDetectorOptions options =
                new ObjectDetectorOptions.Builder()
                        .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
                        .enableMultipleObjects()
                        .enableClassification()  // Optional
                        .build();
        this.objectDetector = ObjectDetection.getClient(options);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentImageViewerBinding.inflate(inflater, container, false);
        binding.buttonRecognizeText.setOnClickListener(v -> {
            recognizeText();
        });
        binding.buttonRecognizeObject.setOnClickListener(v -> {
            recognizeObject();
        });
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (imageBitmap != null) {
            displayedBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
            binding.imageView.setImageBitmap(displayedBitmap);
        } else {
            Toast.makeText(getActivity(), "Failed to show image, no bitmap.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void recognizeText() {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage inputImage = InputImage.fromBitmap(imageBitmap, 0);

        Task<Text> result =
                recognizer.process(inputImage)
                .addOnSuccessListener(text -> {
                    markTextOnBitmap(text);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Fail to recognize the text", Toast.LENGTH_SHORT)
                            .show();
                });
    }

    private void markTextOnBitmap(Text text) {
        displayedBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(displayedBitmap);

        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setAntiAlias(true);
        p.setFilterBitmap(true);
        p.setDither(true);
        p.setColor(Color.RED);
        p.setAlpha(120);

        for (Text.TextBlock block : text.getTextBlocks()) {
            String blockText = block.getText();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            canvas.drawRect(blockFrame, p);
        }

        binding.imageView.setImageBitmap(null);
        binding.imageView.setImageBitmap(displayedBitmap);
    }

    private void recognizeObject() {
        InputImage inputImage = InputImage.fromBitmap(imageBitmap, 0);
        objectDetector.process(inputImage).addOnSuccessListener(new OnSuccessListener<List<DetectedObject>>() {
            @Override
            public void onSuccess(List<DetectedObject> detectedObjects) {
                markObjects(detectedObjects);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Fail to recognize objects", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void markObjects(List<DetectedObject> detectedObjects) {
        displayedBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(displayedBitmap);

        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setAntiAlias(true);
        p.setFilterBitmap(true);
        p.setDither(true);
        p.setColor(Color.RED);
        p.setAlpha(120);

        for (DetectedObject detectedObject : detectedObjects) {
            Rect blockFrame = detectedObject.getBoundingBox();
            canvas.drawRect(blockFrame, p);
        }

        binding.imageView.setImageBitmap(null);
        binding.imageView.setImageBitmap(displayedBitmap);
    }

}
