package com.example.notepadlocker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.example.notepadlocker.MainActivity.user_id;

public class NoteEditor extends AppCompatActivity {

    Button btnsave;
    EditText edtnote;
    EditText edttitle;
    String tittle = null;
    String user_input = null;
    String b64 = null;
    int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        getSupportActionBar().hide();

        edtnote = findViewById(R.id.edtnoteeditor);
        edttitle = findViewById(R.id.edttittle);

        Intent intent = getIntent();
        noteId = intent.getIntExtra("noteId",-1);
        
        if(noteId != -1){
            edtnote.setText(NotepadActivity.notes.get(noteId));
            edttitle.setText(NotepadActivity.tittle.get(noteId));
        }
        else{
            NotepadActivity.tittle.add("");
            NotepadActivity.notes.add("");
            noteId = NotepadActivity.notes.size() - 1;
            NotepadActivity.arrayAdapter.notifyDataSetChanged();
        }

        edttitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tittle = String.valueOf(s);
                try {
                    String encrypted = AESCrypt.encrypt(user_id,tittle);
                    DatabaseReference userData = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("tittle");
                    userData = userData.child(String.valueOf(noteId));
                    userData.setValue(encrypted);
                    NotepadActivity.tittle.set(noteId,String.valueOf(s));
                    NotepadActivity.arrayAdapter.notifyDataSetChanged();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtnote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user_input = String.valueOf(s);
                try {
                    String encrypted = AESCrypt.encrypt(user_id,user_input);
                    DatabaseReference userData = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
                    userData = userData.child(String.valueOf(noteId));
                    userData.setValue(encrypted);
                    NotepadActivity.notes.set(noteId,String.valueOf(s));
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnsave = findViewById(R.id.btnsave);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteEditor.this,NotepadActivity.class);
                startActivity(intent);
            }
        });
    }
}