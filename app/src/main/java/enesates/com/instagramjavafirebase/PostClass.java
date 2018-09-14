package enesates.com.instagramjavafirebase;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostClass extends ArrayAdapter<String> {

    private final ArrayList<String> useremail;
    private final ArrayList<String> userImage;
    private final ArrayList<String> userComment;
    private final Activity context;



    public PostClass(ArrayList<String> useremail, ArrayList<String> userImage, ArrayList<String> userComment, Activity context) {
        super(context, R.layout.custom_view, useremail);
        this.useremail = useremail;
        this.userImage = userImage;
        this.userComment = userComment;
        this.context = context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Bunu biz üstteki Code'a ordan da Override metod'a tıklayıp ordan manuel olarak seçtik.
        // Burada custom_view'ın içine koyduğumuz objeleri tanımlayıp değer atıycaz.

        LayoutInflater layoutInflater = context.getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.custom_view, null, true);
        // Bundan sonra artık tek tek custom_view içindeki herşeye ulaşabilirim.


        TextView useremailText = customView.findViewById(R.id.postUsernameText); // Dikkat edersen direk findViewById demedik customView.findViewById dedik
        TextView commentText = customView.findViewById(R.id.postCommentText);
        ImageView imageView = customView.findViewById(R.id.postImageView);

        // position ListView içindeki sırayla giden değerler oluyor.(0,1,2 gibi)
        useremailText.setText(useremail.get(position));
        commentText.setText(userComment.get(position));

        //Image için Picasso Android Java diye bir eklenti var github'da onu kullanacağız. Download'a Gradle'a ekleyerek yapıcaz. siteye gidince görürsün.
        Picasso.get().load(userImage.get(position)).into(imageView); // Önce hangi path'te kullanıcağımızı sonrada hangi imageView içinde kullanacağımızı yazıcaz.

        // Burada yazanı silip customView'ı döndüreceğimizi yazdık.
        return customView;
    }
}
