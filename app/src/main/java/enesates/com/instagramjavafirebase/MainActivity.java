package enesates.com.instagramjavafirebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // Firebase için Toolsdan Firebase'in Asistant'ından bak.

    private FirebaseAuth mAuth;
    EditText emailText;
    EditText passwordText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);

    }

    public void signUp(View view) {

        mAuth.createUserWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Kullanıcı oluşturmak başarılı.
                            Toast.makeText(getApplicationContext(), "User Created", Toast.LENGTH_LONG).show();

                            //intent
                            Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                            startActivity(intent);
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Hata mesajı
                Toast.makeText(MainActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void signIn(View view) {

        mAuth.signInWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // Giriş başarılı ise.

                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                            startActivity(intent);

                            Toast.makeText(getApplicationContext(), "Welcome "+ emailText.getText().toString(), Toast.LENGTH_LONG).show();

                            //intent
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Hata mesajı
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


}
