package com.example.notepadlocker;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scottyab.aescrypt.AESCrypt;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.security.GeneralSecurityException;

import es.dmoral.toasty.Toasty;

import static com.example.notepadlocker.MainActivity.user_id;

public class LockDialog extends DialogFragment {

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custompopup, container, false);
        TextView text = view.findViewById(R.id.txtcondition);
        Bundle bundle = this.getArguments();
        String position = bundle.getString("position");
        EditText edtpass = view.findViewById(R.id.edtnotepassword);
        Button btnlock = view.findViewById(R.id.btnunlock);
        try {
            String status = NoteFragment.status.get(Integer.parseInt(position));
            status = StringUtils.capitalize(status);
            text.setText(status);
        } catch (Exception e){
            text.setText("Unlocked");
        }


        btnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lockpass = edtpass.getText().toString().trim();
                try {
                    lockpass = AESCrypt.encrypt(user_id,lockpass);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
                String nopass = "0";
                try {
                    nopass = AESCrypt.encrypt(user_id,nopass);
                } catch (GeneralSecurityException e){
                    e.printStackTrace();
                }
                DatabaseReference syncPassword = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
                if (NoteFragment.status.isEmpty()) {
                    for (int i = 0; i < NoteFragment.title.size(); i++) {
                        if (i == Integer.parseInt(position)) {
                            syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue(lockpass);
                            syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("locked");
                        } else {
                            syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue(nopass);
                            syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("unlocked");
                        }
                    }
                } else {
                    for (int i = 0; i < NoteFragment.title.size(); i++) {
                        try {
                            String condition = NoteFragment.status.get(i);
                            if (i == Integer.parseInt(position) && NoteFragment.status.get(i).equals("locked")) {
                                Toasty.warning(getActivity().getApplicationContext(), "This Note Already Locked", Toasty.LENGTH_SHORT).show();
                            } else if (i == Integer.parseInt(position) && NoteFragment.status.get(i).equals("unlocked")) {
                                syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue(lockpass);
                                syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("locked");
                            } else if (condition.equals("locked")) {
                                continue;
                            } else {
                                syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue(nopass);
                                syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("unlocked");
                            }
                        } catch (Exception e) {
                            if (i == Integer.parseInt(position)) {
                                syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue(lockpass);
                                syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("locked");
                            } else {
                                syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue(nopass);
                                syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("unlocked");
                            }
                        }
                    }
                }
                getDialog().dismiss();
            }
        });
        return view;
    }

    public void onResume() {
        getDialog().getWindow().setLayout(850, 850);
        super.onResume();
    }
}