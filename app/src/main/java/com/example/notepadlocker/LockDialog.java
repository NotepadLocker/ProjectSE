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
import android.widget.Toast;

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
        TextView x = view.findViewById(R.id.editVerifyPassword);
        String status = null;
        try {
            status = NoteFragment.status.get(Integer.parseInt(position));
            status = StringUtils.capitalize(status);
            text.setText(status);
            if (status.equals("Unlocked")) {
                getDialog().getWindow().setLayout(750, 750);
                x.setVisibility(View.GONE);
                btnlock.setText("Lock A Notes");
            }
        } catch (Exception e) {
            status = "Unlocked";
            text.setText("Unlocked");
            x.setVisibility(View.GONE);
            btnlock.setText("Lock A Notes");
        }
        String finalStatus = status;
        btnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalStatus.equals("Unlocked")) {
                    if (edtpass.getText().toString().trim().isEmpty()) {
                        x.setText("");
                        Toasty.warning(getActivity().getApplicationContext(), "Password Empty", Toasty.LENGTH_SHORT).show();
                    } else {
                        String lockpass = edtpass.getText().toString().trim();
                        try {
                            lockpass = AESCrypt.encrypt(user_id, lockpass);
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                        String nopass = "0";
                        try {
                            nopass = AESCrypt.encrypt(user_id, nopass);
                        } catch (GeneralSecurityException e) {
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
                            Toasty.success(getActivity().getApplicationContext(), "Note Locked", Toasty.LENGTH_SHORT).show();
                            getDialog().dismiss();
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
                            Toasty.success(getActivity().getApplicationContext(), "Note Locked", Toasty.LENGTH_SHORT).show();
                            getDialog().dismiss();
                        }
                    }
                } else {
                    String password = x.getText().toString().trim();
                    String password1 = edtpass.getText().toString().trim();
                    if (edtpass.getText().toString().trim().isEmpty() || x.getText().toString().trim().isEmpty()) {
                        x.setText("");
                        edtpass.setText("");
                        Toasty.warning(getActivity().getApplicationContext(), "Password Empty", Toasty.LENGTH_SHORT).show();
                    } else if (!password1.equals(NoteFragment.lock.get(Integer.parseInt(position)))) {
                        x.setText("");
                        edtpass.setText("");
                        Toasty.warning(getActivity().getApplicationContext(), "Wrong Old Password", Toasty.LENGTH_SHORT).show();
                    } else{
                        try {
                            password = AESCrypt.encrypt(user_id, password);
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                        String nopass = "0";
                        try {
                            nopass = AESCrypt.encrypt(user_id, nopass);
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                        DatabaseReference syncPassword = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
                        for (int i = 0; i < NoteFragment.title.size(); i++) {
                            if (i == Integer.parseInt(position)) {
                                syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue(password);
                                syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("locked");
                            } else if (NoteFragment.status.get(i).equals("locked")) {
                                continue;
                            } else {
                                syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue(nopass);
                                syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("unlocked");
                            }
                        }
                        Toasty.success(getActivity().getApplicationContext(), "Password Changed", Toasty.LENGTH_SHORT).show();
                        getDialog().dismiss();
                    }
                }
            }
        });
        return view;
    }

    public void onResume() {
        getDialog().getWindow().setLayout(1000, 750);
        super.onResume();
    }
}