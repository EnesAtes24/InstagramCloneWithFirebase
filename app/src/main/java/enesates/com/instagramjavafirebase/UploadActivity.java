package enesates.com.instagramjavafirebase;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {
    // Bu classda resim seçicez altına bir post commenti yazıcaz instagramda yapıldığı gibi.
    // Firebase için Toolsdan Firebase'in Asistant'ından bak.

    EditText commentText;
    ImageView imageView;
    private StorageReference mStorageRef; // Bu Toolsdaki Firebase'in Asistant'ından alındı.
    Uri selected;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        mAuth = FirebaseAuth.getInstance();

        commentText = findViewById(R.id.postCommentText);
        imageView = findViewById(R.id.postImageView);
        mStorageRef = FirebaseStorage.getInstance().getReference(); // Bu Toolsdaki Firebase'in Asistant'ından alındı.
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(); // myRef databasein, ağacın kendisi anlamına geliyor.


    }

    public void chooseImage(View view) {

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // if yapısının içinde diyor ki izin verilmediye o zaman izin isteyeceğiz.

            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            // Else ise izin verldiyse demek, o zaman izin verildiyse olacakları yazıcaz.
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,2); // Bunun anlamı bu activitynin sonunda bir sonuç elde edicem demek o da zaten seçeceğimiz fotoğraf.
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Burda chooseImage metodundaki if ile izin istendiğinde izin verildiyse ne yapılacağını yazıcaz.
        // grantResults alınan sonuç oluyor

        if(requestCode == 1) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // grantResults[0] == PackageManager.PERMISSION_GRANTED izin verildiyse demek.

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,2); // Bunun anlamı bu activitynin sonunda bir sonuç elde edicem demek o da zaten seçeceğimiz fotoğraf.
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Burada da image seçildikten sonra ne olacağını yazıcaz.

        if(requestCode == 2 && resultCode == RESULT_OK && data != null) {
            selected = data.getData(); // Buradaki selected Uri' seçilen fotoğrafın galerideki url'i olarak düşünebilirsin.
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selected);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void upload(View view) {
        // Birşeyi upload edebilmek için StorageReference2ı kullanıyoruz.

        UUID uuidImage = UUID.randomUUID(); // UUID demek bize uniq olarak uydurma bir id verir.
        String imageName = "images/"+uuidImage+".jpg";

        // child demek alt klasör, dosya gibi düşün mesela burda image klasörünün altında image.jpg resmini koyduk.
        StorageReference storageReference = mStorageRef.child(imageName);
        storageReference.putFile(selected).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            // Eğer resim yüklerken bir problemle karşılaştıysa resim yüklenmiyorsa comment koymanında bir matığı yok o yüzden addOnSuccessListener diyoruz yani başarılı olduysa.
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            // Başarılı olursa taskSnapshot diye bir şey veriyor.

                //getDownloadUrl() ile kaydettiğim resimlerin indirilip url'ini bulabiliyorum. Ondan sonrada url'leri kullanarak resim gösterme işlemleri yapıcaz.
                String downloadURL = taskSnapshot.getDownloadUrl().toString(); // Image alındı.

                FirebaseUser user = mAuth.getCurrentUser(); // Bu bize güncel olarak login yapmış kullanıcının bilgilerini verebilir.
                String userEmail = user.getEmail().toString(); // Kullanıcı alındı.

                // Şimdi yukarıda yapılan postun url'si aldık böylece elimizde image var hangi kullanıcının onu post ettiğini aldık çünkü güncel kullanıcı bunu yapacak.

                String userComment = commentText.getText().toString(); // Comment alındı.

                //Birde son olarak uniq id tanımlayalım postları uniq id ile çok rahat erişilebilir.
                UUID uuid = UUID.randomUUID();
                String uuidString = uuid.toString();


                // Aşağıda myRef ağacında Posts adında büyük bir dal açıp onunda altında bir dal açıp onu uniq id(uuidStirng) yapıyruz sonra o id değerinin altında bir dal açıp(yaprak olabilir) useremail, comment, downloadurl değerleri veriyoruz.
                myRef.child("Posts").child(uuidString).child("useremail").setValue(userEmail);
                myRef.child("Posts").child(uuidString).child("comment").setValue(userComment);
                myRef.child("Posts").child(uuidString).child("downloadurl").setValue(downloadURL);

                Toast.makeText(getApplicationContext(), "Post Shared", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                startActivity(intent); // Kaydettikten sonra Feed'e göndererek kaydettiği şeyleri gösteriyoruz.

            }
        }).addOnFailureListener(new OnFailureListener() { // Eğer bir hata olursa hatayı yazdık burda da.
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
