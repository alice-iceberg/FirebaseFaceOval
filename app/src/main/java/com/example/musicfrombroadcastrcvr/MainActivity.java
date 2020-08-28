package com.example.musicfrombroadcastrcvr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;

import static com.google.mlkit.vision.face.FaceDetection.getClient;

public class MainActivity extends AppCompatActivity {

    ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        photo = findViewById(R.id.photo);


        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                        .build();

        final Bitmap bitmap_photo = BitmapFactory.decodeResource(this.getResources(), R.drawable.woman);
        final InputImage image = InputImage.fromBitmap(bitmap_photo, 0); //check second parameter

        final FaceDetector detector = getClient(options);
        detector.process(image).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onSuccess(List<Face> faces) {
                Log.e("TAG", "onSuccess: Face detected");
                FaceContour contour = faces.get(0).getContour(FaceContour.FACE);

                Bitmap mutableBitmap = image.getBitmapInternal().copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(mutableBitmap);
                Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                myPaint.setColor(Color.parseColor("#99ff0000"));
                Path path = new Path();
                assert contour != null;
                path.moveTo(contour.getPoints().get(0).x, contour.getPoints().get(0).y);
                for (PointF item : contour.getPoints()) {
                    path.lineTo(item.x, item.y);
                }
                path.close();
                detector.close();

                //canvas.drawPath(path, myPaint);
                //photo.setImageBitmap(mutableBitmap);


                //my trial
                Bitmap output_my = Bitmap.createBitmap(mutableBitmap.getWidth(), mutableBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas_mine = new Canvas(output_my);
                Paint paint_mine = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint_mine.setColor(Color.BLUE);
                canvas_mine.drawPath(path, paint_mine);
                paint_mine.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas_mine.drawBitmap(bitmap_photo, 0, 0, paint_mine);
                photo.setImageBitmap(output_my);



            }

        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "onFailure: Failed to detect face");
                        photo.setImageBitmap(bitmap_photo);
                    }
                });


    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
