package com.example.notepadlocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scottyab.aescrypt.AESCrypt;

import org.jetbrains.annotations.NotNull;

import java.security.GeneralSecurityException;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static com.example.notepadlocker.MainActivity.user_id;

public class NotepadActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static ArrayList<String> notes = new ArrayList<>();
    static ArrayList<String> tittle = new ArrayList<>();
    static ArrayList<String> status = new ArrayList<>();
    static ArrayList<String> passwordnote = new ArrayList<>();
    static ArrayAdapter arrayAdapter;
    ListView lstview;
    boolean isLongClick;
    String size = null;
    ProgressBar progressBar;
    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView naview;
    TextView navName,navType;
    String usernamenav,usertype;
    Dialog myDialog,passwordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);
        progressBar = findViewById(R.id.pbID);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, tittle);

        getSupportActionBar().hide();

        myDialog = new Dialog(NotepadActivity.this);
        passwordDialog = new Dialog(NotepadActivity.this);

        lstview = findViewById(R.id.lstview);
        lstview.setAdapter(arrayAdapter);

        fab = findViewById(R.id.fabadd);
        fab.setVisibility(View.GONE);
        registerForContextMenu(lstview);

        drawerLayout = findViewById(R.id.drawerID);
        toolbar = findViewById(R.id.toolbar5);
        naview = findViewById(R.id.nav_view);

        naview.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(NotepadActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        naview.setNavigationItemSelectedListener(NotepadActivity.this);
        View headerView = naview.getHeaderView(0);
        navName = headerView.findViewById(R.id.txtviewnickname);
        navType = headerView.findViewById(R.id.txtviewstatus);
        DatabaseReference username = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        username.child("userdata").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                usernamenav = snapshot.child("username").getValue(String.class);
                usertype = snapshot.child("type").getValue(String.class);
                navName.setText(usernamenav);
                navType.setText(usertype);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotepadActivity.this, NoteEditor.class);
                startActivity(intent);
            }
        });

        toolbar.setVisibility(View.GONE);

        /**
         * Fetch Data
         */
        DatabaseReference userDataTittle = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
        userDataTittle.child("title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tittle.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String value = ds.getValue(String.class);
                    String decode = null;
                    try {
                        decode = AESCrypt.decrypt(user_id, value);
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
        userDataNote.child("notes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notes.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String value = ds.getValue(String.class);
                    String decode = null;
                    try {
                        decode = AESCrypt.decrypt(user_id, value);
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

        DatabaseReference userDatalock = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
        userDatalock.child("lock").child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                status.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    status.add(ds.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference userNotePass = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
        userNotePass.child("lock").child("password").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                passwordnote.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    passwordnote.add(ds.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        /**
         * End Fetch Data
         */

        /**
         *  Start
         *  List View to Note Editor
         */

        lstview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isLongClick) return;
                String codition = status.get(i);
                if (codition.equals("unlocked")){
                    Intent intent = new Intent(getApplicationContext(),NoteEditor.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("noteId", i);
                    startActivity(intent);
                }else{
                    ShowPopUpChecker(i);
                }
            }
        });

        /**
         *  End Code
         */
    }

    public void ShowPopUpChecker(int position){
        passwordDialog.setContentView(R.layout.custompopuptrue);
        EditText edtpassword = passwordDialog.findViewById(R.id.edtunlocking);
        Button btnunlock = passwordDialog.findViewById(R.id.btnunlocking);
        btnunlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = edtpassword.getText().toString().trim();
                if(password.equals(passwordnote.get(position))){
                    Intent intent = new Intent(getApplicationContext(),NoteEditor.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("noteId", position);
                    startActivity(intent);
                    passwordDialog.dismiss();
                }else{
                    Toasty.warning(NotepadActivity.this,"Wrong Password",Toasty.LENGTH_SHORT).show();
                }
            }
        });
        passwordDialog.show();
    }

    public void ShowPopup(int position){
        myDialog.setContentView(R.layout.custompopup);
        TextView txtstatus = myDialog.findViewById(R.id.txtcondition);
        txtstatus.setText("Unlocked");
        int jmlh = tittle.size();
        System.out.println(size);
        EditText edtnotepadlock = myDialog.findViewById(R.id.edtnotepassword);
        Button btnlock = myDialog.findViewById(R.id.btnunlock);
        btnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = edtnotepadlock.getText().toString().trim();
                DatabaseReference syncPassword = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
                if(status.isEmpty()){
                    for (int i=0;i<jmlh;i++){
                        if(i == position){
                            syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue(password);
                            syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("locked");
                        }else{
                            syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue("0");
                            syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("unlocked");
                        }
                    }
                }else{
                    for (int i=0;i<jmlh;i++){
                        try{
                            String condition = status.get(i);
                            if(i == position){
                                syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue(password);
                                syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("locked");
                            }else if(condition.equals("locked")){
                                continue;
                            }else{
                                syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue("0");
                                syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("unlocked");
                            }
                        }catch (Exception e){
                            syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue("0");
                            syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("unlocked");
                        }
                    }
                }
                myDialog.dismiss();
            }
        });
        myDialog.show();
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
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int notesToLock = info.position;
            ShowPopup(notesToLock);

            System.out.println(info.position);
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
                DatabaseReference syncTittle = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
                String user_input = tittle.get(i);
                try {
                    String encrypted = AESCrypt.encrypt(user_id,user_input);
                    syncTittle.child("title").child(String.valueOf(i)).setValue(encrypted);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
            DatabaseReference syncDeleteTittle = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
            syncDeleteTittle.child("title").child(size).removeValue();
            notes.remove(notesToDelete);
            size = Integer.toString(notes.size());
            for (int i=0; i< notes.size();i++)
            {
                DatabaseReference syncNote = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
                String user_input = notes.get(i);
                try {
                    String encrypted = AESCrypt.encrypt(user_id,user_input);
                    syncNote.child("notes").child(String.valueOf(i)).setValue(encrypted);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
            DatabaseReference syncDeleteNote = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
            syncDeleteNote.child("notes").child(size).removeValue();
            Toasty.success(this, "Delete Done", Toast.LENGTH_SHORT).show();
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
}