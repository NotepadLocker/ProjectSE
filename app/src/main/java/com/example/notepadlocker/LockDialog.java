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

import org.jetbrains.annotations.NotNull;

public class LockDialog extends DialogFragment {

    public interface OnInputSelected{
        void sendInput(String input);
    }
    public OnInputSelected m0nInputSelected;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.custompopup,container,false);
        TextView text = view.findViewById(R.id.txtcondition);
        text.setText("Uncloked");
        EditText edtpass = view.findViewById(R.id.edtnotepassword);
        Button btnlock = view.findViewById(R.id.btnunlock);
        btnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = edtpass.getText().toString().trim();
                m0nInputSelected.sendInput(input);
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
            m0nInputSelected = (OnInputSelected) getTargetFragment();
        } catch (ClassCastException e){
            e.printStackTrace();
        }
    }
}
