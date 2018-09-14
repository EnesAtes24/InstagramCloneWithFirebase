package enesates.com.instagramjavafirebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FeedActivity extends AppCompatActivity {
    // Bu classda download işlemi yapılacak.
    // Firebase için Toolsdan Firebase'in Asistant'ından bak.

    ArrayList<String> userEmailsFromFB;
    ArrayList<String> userCommentFromFB;
    ArrayList<String> userImageFromFB;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    ListView listView;
    PostClass adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_post, menu);




        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Menü seçilince ne olacağıyla ilgili.

        if(item.getItemId() == R.id.add_post) {
            Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        userEmailsFromFB = new ArrayList<String>();
        userCommentFromFB = new ArrayList<String>();
        userImageFromFB = new ArrayList<String>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();

        listView = findViewById(R.id.listView);

        adapter = new PostClass(userEmailsFromFB, userImageFromFB, userCommentFromFB, this); // Adapter ile bağlama yaptık.
        listView.setAdapter(adapter);

        getDataFromFirebase();
    }

    protected void  getDataFromFirebase() {

        DatabaseReference newReference = firebaseDatabase.getReference("Posts"); // Download edeceğimiz şeyler Posts'ta olduğu için bu  şekilde belirttik.
        newReference.addValueEventListener(new ValueEventListener() {
        // addValueEventListener bir event olay olacağını demek oluyor
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Database'de bir değişiklik olduğunda ne yapacağını soruyor.

                // System.out.println("children : " + dataSnapshot.getChildren());
                // System.out.println("key : " + dataSnapshot.getKey());
                // System.out.println("value : " + dataSnapshot.getValue());

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                    userEmailsFromFB.add(hashMap.get("useremail"));
                    userImageFromFB.add(hashMap.get("downloadurl"));
                    userCommentFromFB.add(hashMap.get("comment"));


                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
