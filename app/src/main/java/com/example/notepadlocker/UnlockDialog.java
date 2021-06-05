package com.example.notepadlocker;

import android.content.Context;
import android.content.Intent;
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

import es.dmoral.toasty.Toasty;

public class UnlockDialog extends DialogFragment {

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.custompopuptrue,container,false);
        EditText edtunlocking = view.findViewById(R.id.edtunlocking);
        Button btnunlocking = view.findViewById(R.id.btnunlocking);
        Bundle bundle = this.getArguments();
        String position = bundle.getString("position");

        btnunlocking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = edtunlocking.getText().toString().trim();
                if(password.equals(NoteFragment.lock.get(Integer.parseInt(position)))){
                    System.out.println(position);
                    Intent intent = new Intent(getActivity().getApplicationContext(), NoteEditor.class);
                    intent.putExtra("notedId", Integer.parseInt(position));
                    getDialog().dismiss();
                    startActivity(intent);
                }else{
                    Toasty.warning(getActivity().getApplicationContext(),"Wrong Password",Toasty.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    public void onResume() {
        getDialog().getWindow().setLayout(750, 750);
        super.onResume();
    }
}
