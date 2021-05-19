package com.example.notepadlocker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scottyab.aescrypt.AESCrypt;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.DataTruncation;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.example.notepadlocker.MainActivity.user_id;

public class NotepadActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static ArrayList<String> notes = new ArrayList<>();
    static ArrayList<String> tittle = new ArrayList<>();
    static ArrayAdapter arrayAdapter;
    ListView lstview;
    boolean isLongClick;
    String size = null;
    ProgressBar progressBar;
    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView naview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);
        progressBar = findViewById(R.id.pbID);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,tittle);

        getSupportActionBar().hide();

        lstview = findViewById(R.id.lstview);
        lstview.setAdapter(arrayAdapter);

        fab = findViewById(R.id.fabadd);
        fab.setVisibility(View.GONE);
        registerForContextMenu(lstview);

        drawerLayout = findViewById(R.id.drawerID);
        toolbar = findViewById(R.id.toolbar5);
        naview = findViewById(R.id.nav_view);

        naview.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(NotepadActivity.this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        naview.setNavigationItemSelectedListener(NotepadActivity.this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotepadActivity.this,NoteEditor.class);
                startActivity(intent);
            }
        });

        toolbar.setVisibility(View.GONE);

        /**
         * Fetch Data
         */
        DatabaseReference userDataTittle = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("tittle");
        userDataTittle.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tittle.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    String value = ds.getValue(String.class);
                    String decode = null;
                    try {
                        decode = AESCrypt.decrypt(user_id,value);
                        tittle.add(decode);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }
                progressBar.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.VISIBLE);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference userDataNote = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
        userDataNote.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notes.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    String value = ds.getValue(String.class);
                    String decode = null;
                    try {
                        decode = AESCrypt.decrypt(user_id,value);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    notes.add(decode);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        lstview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isLongClick) return;
                Intent intent = new Intent(getApplicationContext(),NoteEditor.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("noteId", i);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextmenu,menu);
        menu.setHeaderTitle("Select Action");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.Lock)
        {
            Toast.makeText(this, "Try Try", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(item.getItemId() == R.id.delete)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int notesToDelete = info.position;
            tittle.remove(notesToDelete);
            size = Integer.toString(tittle.size());
            for (int i=0; i < tittle.size(); i++)
            {
                DatabaseReference syncTittle = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("tittle");
                String user_input = tittle.get(i);
                try {
                    String encrypted = AESCrypt.encrypt(user_id,user_input);
                    syncTittle = syncTittle.child(String.valueOf(i));
                    syncTittle.setValue(encrypted);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
            DatabaseReference syncDeleteTittle = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("tittle").child(size);
            syncDeleteTittle.removeValue();
            notes.remove(notesToDelete);
            size = Integer.toString(notes.size());
            for (int i=0; i< notes.size();i++)
            {
                DatabaseReference syncNote = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
                String user_input = notes.get(i);
                try {
                    String encrypted = AESCrypt.encrypt(user_id,user_input);
                    syncNote = syncNote.child(String.valueOf(i));
                    syncNote.setValue(encrypted);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
            DatabaseReference syncDeleteNote = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note").child(size);
            syncDeleteNote.removeValue();
            Toast.makeText(this, "Delete Done", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.singout){
            SharedPreferences sharedPreferences = getSharedPreferences("Session",MODE_PRIVATE);
            sharedPreferences.edit().clear().commit();
            Intent intent = new Intent(NotepadActivity.this,MainActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * Sync Data with Firebase
     */
//    @Override
//    protected void onResume() {
//        super.onResume();
//        for (int i=0; i<notes.size(); i++)
//        {
//            DatabaseReference userdelete = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
//            String user_input = notes.get(i);
//            String encrypted = null;
//            try {
//                encrypted = AESCrypt.encrypt(user_id,user_input);
//            } catch (GeneralSecurityException e) {
//                e.printStackTrace();
//            }
//            userdelete = userdelete.child(String.valueOf(i));
//            userdelete.setValue(encrypted);
//            String x = String.valueOf(i) + "notes.txt";
//            try {
//                FileOutputStream fos = openFileOutput(x,MODE_PRIVATE);
//                fos.write(encrypted.getBytes());
//                fos.close();
//                System.out.println(getFilesDir());
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        for (int i=0; i<tittle.size(); i++)
//        {
//            DatabaseReference userdelete = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("tittle");
//            String user_input = tittle.get(i);
//            String b64 = Base64.getEncoder().encodeToString(user_input.getBytes());
//            userdelete = userdelete.child(String.valueOf(i));
//            userdelete.setValue(b64);
//            String x = String.valueOf(i) + "tittle.txt";
//            try {
//                FileOutputStream fos = openFileOutput(x,MODE_PRIVATE);
//                fos.write(b64.getBytes());
//                fos.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}