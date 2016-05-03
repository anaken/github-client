package biz.mesto.anaken.githubclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class UserHelper {

    public static void setAvatarToView(Context context, User user, ImageView v) {
        if (user.login.contains("h")) {
            Bitmap bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.rgb(194, 167, 124));
            Canvas canvas = new Canvas(bitmap);
            Paint p = new Paint();
            p.setTextAlign(Paint.Align.CENTER);
            p.setColor(Color.BLACK);
            p.setTextSize(40);
            canvas.drawText(user.login.substring(0, 2), 25, 37, p);
            canvas.setBitmap(bitmap);
            v.setImageBitmap(getCroppedBitmap(bitmap));
        }
        else {
            Picasso.with(context).load(user.avatar_url)
                    .fit()
                    .transform(new CircleTransform())
                    .into(v);
        }
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
