package com.example.notepadlocker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

public class UnlockDialog extends DialogFragment {

    public interface unlocking{
        void unlockinginput(String input);
    }
    public unlocking inputUnlocking;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.custompopuptrue,container,false);
        EditText edtunlocking = view.findViewById(R.id.edtunlocking);
        Button btnunlocking = view.findViewById(R.id.btnunlocking);

        btnunlocking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = edtunlocking.getText().toString().trim();
                inputUnlocking.unlockinginput(input);
                getDialog().dismiss();
            }
        });
        return view;
    }

    public void onResume() {
        getDialog().getWindow().setLayout(1000, 1000);
        super.onResume();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try{
            inputUnlocking = (unlocking) getTargetFragment();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
