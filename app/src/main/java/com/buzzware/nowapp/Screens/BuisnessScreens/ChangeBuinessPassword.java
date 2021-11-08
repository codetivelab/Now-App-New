package com.buzzware.nowapp.Screens.BuisnessScreens;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivityChangeBuinessPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ChangeBuinessPassword extends AppCompatActivity {

    ActivityChangeBuinessPasswordBinding binding;

    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChangeBuinessPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListener();
    }

    private void init() {

        mAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void setListener() {

        binding.btnConfirm.setOnClickListener(v -> {
            if (!binding.emailET.getText().toString().isEmpty()) {
                sendEmail();
            }
        });

    }

    private void sendEmail() {
        FirebaseAuth.getInstance().sendPasswordResetEmail(binding.emailET.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            finish();
                            Toast.makeText(ChangeBuinessPassword.this, "Email has been sent on your Email!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIUpdate.destroy();
    }


}