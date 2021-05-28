package com.example.notepadlocker;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import static com.example.notepadlocker.MainActivity.user_id;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends Fragment implements LockDialog.OnInputSelected,UnlockDialog.unlocking {

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

    static ArrayList<String> title = new ArrayList<>();
    static ArrayList<String> note = new ArrayList<>();
    static ArrayList<String> status = new ArrayList<>();
    static ArrayList<String> lock = new ArrayList<>();
    static ArrayAdapter arrayAdapter;
    int notesToLock,positionx;
    String lockpass,unlockPass;

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

        DatabaseReference userDataTitle = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
        userDataTitle.child("title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                title.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
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
                for (DataSnapshot ds : snapshot.getChildren()) {
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

        DatabaseReference userDataLock = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
        userDataLock.child("lock").child("status").addValueEventListener(new ValueEventListener() {
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

        DatabaseReference userDataPassword = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
        userDataPassword.child("lock").child("password").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                lock.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    lock.add(ds.getValue(String.class));
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String condition = status.get(position);
                if (condition.equals("unlocked")) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), NoteEditor.class);
                    intent.putExtra("notedId", position);
                    startActivity(intent);
                }else {
                    positionx = position;
                    UnlockDialog unlockDialog = new UnlockDialog();
                    unlockDialog.setTargetFragment(NoteFragment.this,1);
                    unlockDialog.show(getActivity().getSupportFragmentManager(),"UnlockDialog");
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
            LockDialog lockDialog = new LockDialog();
            lockDialog.setTargetFragment(NoteFragment.this,1);
            lockDialog.show(getActivity().getSupportFragmentManager(),"LockDialog");
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void sendInput(String input) {
        lockpass = input;
        System.out.println(lockpass);
        DatabaseReference syncPassword = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("note");
        if (status.isEmpty()) {
            for (int i = 0; i < title.size(); i++) {
                if (i == notesToLock) {
                    syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue(lockpass);
                    syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("locked");
                } else {
                    syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue("0");
                    syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("unlocked");
                }
            }
        } else {
            for (int i = 0; i < title.size(); i++) {
                try {
                    String condition = status.get(i);
                    if (i == notesToLock && status.get(i).equals("locked")) {
                        Toasty.warning(getActivity().getApplicationContext(), "This Note Already Locked", Toasty.LENGTH_SHORT).show();
                    } else if (i == notesToLock && status.get(i).equals("unlocked")) {
                        syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue(lockpass);
                        syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("locked");
                    } else if (condition.equals("locked")) {
                        continue;
                    } else {
                        syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue("0");
                        syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("unlocked");
                    }
                } catch (Exception e){
                    if (i == notesToLock){
                        syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue(lockpass);
                        syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("locked");
                    }else{
                        syncPassword.child("lock").child("password").child(String.valueOf(i)).setValue("0");
                        syncPassword.child("lock").child("status").child(String.valueOf(i)).setValue("unlocked");
                    }
                }
            }
        }
    }

    @Override
    public void unlockinginput(String input) {
        unlockPass = input;
        System.out.println(positionx);
        if(unlockPass.equals(lock.get(positionx))){
            Intent intent = new Intent(getActivity().getApplicationContext(), NoteEditor.class);
            intent.putExtra("notedId", positionx);
            startActivity(intent);
        }else{
            Toasty.warning(getActivity().getApplicationContext(),"Wrong Password",Toasty.LENGTH_SHORT).show();
        }
    }

}
