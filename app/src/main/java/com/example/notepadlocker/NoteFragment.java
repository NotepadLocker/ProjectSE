package com.example.notepadlocker;

import android.content.Intent;
import android.media.SyncParams;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import static android.content.Context.POWER_SERVICE;
import static com.example.notepadlocker.MainActivity.user_id;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoteFragment newInstance(String param1, String param2) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    static ArrayList < String > title = new ArrayList < > ();
    static ArrayList < String > note = new ArrayList < > ();
    static ArrayList < String > status = new ArrayList < > ();
    static ArrayList < String > lock = new ArrayList < > ();
    static ArrayAdapter arrayAdapter;
    int notesToLock;
    int notesToDelete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_expandable_list_item_1, title);
        ListView listView = view.findViewById(R.id.lstview);
        listView.setAdapter(arrayAdapter);

        ProgressBar pbId = view.findViewById(R.id.pbID);

        FloatingActionButton fab = view.findViewById(R.id.fabadd);
        fab.setVisibility(View.GONE);

        registerForContextMenu(listView);

        SearchView searchView = view.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                NoteFragment.this.arrayAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                NoteFragment.this.arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

        DatabaseReference userDataTitle = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
        userDataTitle.child("title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                title.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String value = ds.getValue(String.class);
                    String decode = null;
                    try {
                        decode = AESCrypt.decrypt(user_id, value);
                        title.add(decode);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }
                pbId.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference userDataNote = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
        userDataNote.child("notes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                note.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String value = ds.getValue(String.class);
                    String decode = null;
                    try {
                        decode = AESCrypt.decrypt(user_id, value);
                        note.add(decode);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference userDataLock = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note").child("lock");
        userDataLock.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                status.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    status.add(ds.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference userDataPassword = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note").child("lock");
        userDataPassword.child("password").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                lock.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String value = ds.getValue(String.class);
                    String decode = null;
                    try {
                        decode = AESCrypt.decrypt(user_id, value);
                        lock.add(decode);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), NoteEditor.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView < ? > parent, View view, int position, long id) {
                try {
                    String condition = status.get(position);
                    if (condition.equals("unlocked")) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), NoteEditor.class);
                        intent.putExtra("notedId", position);
                        startActivity(intent);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("position", String.valueOf(position));
                        UnlockDialog unlockDialog = new UnlockDialog();
                        unlockDialog.setArguments(bundle);
                        unlockDialog.show(getActivity().getSupportFragmentManager(), "UnlockDialog");
                    }
                } catch (Exception e) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), NoteEditor.class);
                    intent.putExtra("notedId", position);
                    startActivity(intent);
                }
            }
        });
        return view;
    }

    @Override
    public void onCreateContextMenu(@NonNull @NotNull ContextMenu menu, @NonNull @NotNull View v, @Nullable @org.jetbrains.annotations.Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.contextmenu, menu);
        menu.setHeaderTitle("Select Action");
    }

    @Override
    public boolean onContextItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.Lock) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            notesToLock = info.position;
            Bundle bundle = new Bundle();
            bundle.putString("position", String.valueOf(notesToLock));
            LockDialog lockDialog = new LockDialog();
            lockDialog.setArguments(bundle);
            lockDialog.show(getActivity().getSupportFragmentManager(), "LockDialog");
        } else if (item.getItemId() == R.id.delete) {
            sync();
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            notesToDelete = info.position;

            //            Title Remove
            title.remove(notesToDelete);
            String size = Integer.toString(title.size());
            for (int i = 0; i < title.size(); i++) {
                DatabaseReference syncTitle = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note").child("title");
                String user_title = title.get(i);
                try {
                    String encrypted = AESCrypt.encrypt(user_id, user_title);
                    syncTitle = syncTitle.child(String.valueOf(i));
                    syncTitle.setValue(encrypted);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
            DatabaseReference syncDeleteTittle = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
            syncDeleteTittle.child("title").child(size).removeValue();

            //            Note Remove
            note.remove(notesToDelete);
            size = Integer.toString(note.size());
            for (int i = 0; i < note.size(); i++) {
                DatabaseReference syncNote = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note").child("notes");
                String user_input = note.get(i);
                try {
                    String encrypted = AESCrypt.encrypt(user_id, user_input);
                    syncNote = syncNote.child(String.valueOf(i));
                    syncNote.setValue(encrypted);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
            DatabaseReference syncDeleteNote = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
            syncDeleteNote.child("notes").child(size).removeValue();
            try {
                //            Lock Remove
                lock.remove(notesToDelete);
                size = Integer.toString(lock.size());
                for (int i = 0; i < lock.size(); i++) {
                    DatabaseReference syncPassword = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note").child("lock").child("password");
                    String user_input = lock.get(i);
                    try {
                        String encrypted = AESCrypt.encrypt(user_id, user_input);
                        syncPassword = syncPassword.child(String.valueOf(i));
                        syncPassword.setValue(encrypted);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }
                DatabaseReference syncDeletePassword = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note").child("lock");
                syncDeletePassword.child("password").child(size).removeValue();

                //            Status Remove
                status.remove(notesToDelete);
                size = Integer.toString(status.size());
                for (int i = 0; i < status.size(); i++) {
                    DatabaseReference syncPassword = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note").child("lock").child("status");
                    String user_input = status.get(i);
                    syncPassword = syncPassword.child(String.valueOf(i));
                    syncPassword.setValue(user_input);
                }
                DatabaseReference syncPassword = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note").child("lock");
                syncPassword.child("status").child(size).removeValue();
                Toasty.success(getActivity().getApplicationContext(), "Success Delete Notes", Toasty.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toasty.success(getActivity().getApplicationContext(), "Success Delete Notes", Toasty.LENGTH_SHORT).show();
            }
        }
        return super.onContextItemSelected(item);
    }

    public void sync() {
        DatabaseReference userDataLock = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note").child("lock");
        userDataLock.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                status.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    status.add(ds.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference userDataPassword = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
        userDataPassword.child("lock").child("password").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                lock.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String value = ds.getValue(String.class);
                    String decode = null;
                    try {
                        decode = AESCrypt.decrypt(user_id, value);
                        lock.add(decode);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}