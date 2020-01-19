package tech.eatnow.ui.provider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import tech.eatnow.MainActivity;
import tech.eatnow.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.DocumentKey;
import com.google.firebase.firestore.model.ResourcePath;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class AddFoodFragment extends Fragment {

    private final int CHOOSE_PHOTO_REQUEST_CODE = 0;

    private EditText nameField, descriptionField, providerField;
    private Uri photoUri;

    private ImageView photo;
    private Button submitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_food, container, false);

        photo = view.findViewById(R.id.add_food_photo);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Intent.createChooser(
                        new Intent(Intent.ACTION_GET_CONTENT).setType("image/jpeg"),
                        "Choose image");
                startActivityForResult(intent, CHOOSE_PHOTO_REQUEST_CODE);
            }
        });

        view.findViewById(R.id.add_food_photo_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo.setImageURI(null);
                photoUri = null;
            }
        });

        submitButton = view.findViewById(R.id.add_food_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButton.setEnabled(false);
                FirebaseStorage storage = ((MainActivity) getActivity()).storage;
                final StorageReference photoReference = storage.getReference("photos/" +
                        photoUri.getLastPathSegment());
                Task<Uri> task = photoReference.putFile(photoUri)
                        .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return photoReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            photoUri = task.getResult();
                            postFood();
                        } else {
                            // Handle failures
                            // ...
                            submitButton.setEnabled(true);
                        }
                    }
                });



            }
        });

        nameField = view.findViewById(R.id.add_food_name);
        descriptionField = view.findViewById(R.id.add_food_description);
        providerField = view.findViewById(R.id.add_food_provider);

        return view;
    }

    private void postFood() {
        FirebaseFirestore firestore = ((MainActivity) getActivity()).firestore;
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("name", nameField.getText().toString());
        fields.put("description", descriptionField.getText().toString());
        fields.put("providers", "providers/" + providerField.getText());
        fields.put("photo", photoUri.toString());
        firestore.collection("foods").document().update(fields).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        submitButton.setEnabled(true);
                        if (task.isSuccessful()) {
                            nameField.setText("");
                            descriptionField.setText("");
                            providerField.setText("");
                            photoUri = null;
                            photo.setImageURI(null);
                        }
                    }
                }
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                photoUri = data.getData();
                photo.setImageURI(photoUri);
            }
        }
    }
}
