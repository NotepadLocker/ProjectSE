package com.example.notepadlocker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

import static com.example.notepadlocker.MainActivity.user_id;

public class NoteEditor extends AppCompatActivity {

    EditText edtnote;
    EditText edttitle;
    String titlex = null;
    String notex = null;
    int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        getSupportActionBar().hide();

        edtnote = findViewById(R.id.edtnoteeditor);
        edttitle = findViewById(R.id.edttitle);

        Intent intent = getIntent();
        noteId = intent.getIntExtra("notedId",-1);

        if(noteId != -1){
            edttitle.setText(NoteFragment.title.get(noteId));
            edtnote.setText(NoteFragment.note.get(noteId));
        }else{
            NoteFragment.title.add("");
            NoteFragment.note.add("");
            noteId = NoteFragment.title.size() - 1;
            NoteFragment.arrayAdapter.notifyDataSetChanged();
        }

        edttitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                titlex = String.valueOf(s);
                try{
                    String encrypted = AESCrypt.encrypt(user_id,titlex);
                    DatabaseReference userData = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
                    userData = userData.child("title").child(String.valueOf(noteId));
                    userData.setValue(encrypted);
                    NoteFragment.title.set(noteId,String.valueOf(s));
                    NoteFragment.arrayAdapter.notifyDataSetChanged();
                } catch (GeneralSecurityException e){
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
                notex = String.valueOf(s);
                try {
                    String encrypted = AESCrypt.encrypt(user_id,notex);
                    DatabaseReference userData = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
                    userData = userData.child("notes").child(String.valueOf(noteId));
                    userData.setValue(encrypted);
                    NoteFragment.note.set(noteId,String.valueOf(s));
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}