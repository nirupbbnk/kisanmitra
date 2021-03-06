package pathrer.com.kisanmitra;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class CropAddFragment extends Fragment {

    private ImageButton mselectimage;
    private DatabaseReference mDatabase,getmDatabase;
    private StorageReference mStorage;
    private EditText mtitle;
    private EditText mpost;
    private EditText mprice;
    private EditText mphmo;
    private Button msubitb;
    private Uri imageuri = null;
    private Query query;
    private static final int GALLERY_REQUEST = 1;
    public CropAddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crop_add, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStorage = FirebaseStorage.getInstance().getReference();
        // mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("CropsP");
        getmDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        query = getmDatabase.orderByChild("Fuid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mselectimage = (ImageButton) view.findViewById(R.id.imageSelect_c);
        mtitle = (EditText)view. findViewById(R.id.titleField_c);
        mpost = (EditText) view.findViewById(R.id.descField_c);
        mprice = (EditText)view. findViewById(R.id.price_c);
        mphmo = (EditText)view. findViewById(R.id.pro_c);
        msubitb =(Button) view.findViewById(R.id.submit_c);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, Object> newPost = (Map<String, Object>) dataSnapshot.getValue();
                mphmo.setText(newPost.get("phone").toString(), TextView.BufferType.EDITABLE);
                mpost.setText(newPost.get("dist").toString(), TextView.BufferType.EDITABLE);
                System.out.println("phone: " + newPost.get("phone"));
                System.out.println("place: " + newPost.get("dist"));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mselectimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallerintent = new Intent(Intent.ACTION_PICK);
                gallerintent.setType("image/*");
                startActivityForResult(gallerintent,GALLERY_REQUEST);
            }
        });
        msubitb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String title_val = mtitle.getText().toString();
                final String desc_val = mpost.getText().toString();
                final String price = mprice.getText().toString();
                final String ph  = mphmo.getText().toString();

                StorageReference filepath = mStorage.child("CropImages").child(imageuri.getLastPathSegment());
                filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloaduri = taskSnapshot.getDownloadUrl();
                        DatabaseReference newPost = mDatabase.push();

                        newPost.child("cropname").setValue(title_val);
                        newPost.child("place").setValue(desc_val);
                        newPost.child("price").setValue(price);
                        newPost.child("phno").setValue(ph);
                        newPost.child("cuid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                        newPost.child("imageurl").setValue(downloaduri.toString());


                        Toast.makeText(getActivity(),"Upload done",Toast.LENGTH_LONG).show();
                        CropFragment cp = new CropFragment();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame,cp,"fragment");
                        fragmentTransaction.commit();


                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == getActivity().RESULT_OK);
        imageuri = data.getData();
        mselectimage.setImageURI(imageuri);
    }
}
