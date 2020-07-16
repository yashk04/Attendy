// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.firebase.samples.apps.mlkit.facedetection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.samples.apps.mlkit.AttendanceActivity;
import com.google.firebase.samples.apps.mlkit.TeacherAttendanceActivity;
import com.google.firebase.samples.apps.mlkit.others.FrameMetadata;
import com.google.firebase.samples.apps.mlkit.others.GraphicOverlay;
import com.google.firebase.samples.apps.mlkit.LivePreviewActivity;
import com.google.firebase.samples.apps.mlkit.others.SharedPref;
import com.google.firebase.samples.apps.mlkit.others.VisionProcessorBase;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

/** Face Detector Demo. */
public class FaceDetectionProcessor extends VisionProcessorBase<List<FirebaseVisionFace>> {

    private static final String TAG = "FaceDetectionProcessor";
    private Bitmap bitmap;
    private final FirebaseVisionFaceDetector detector;
    private LinearLayout layout;
    private SharedPref sharedPref;
    View v;
    Context c;
    Resources res;
    int total;
    public Activity activity;
    String packageName;
    HashMap<Integer, ImageView> integerImageViewHashMap;
    HashMap<Integer, Integer> singleFaceDetectCount;
    public static int threadCount;

    public FaceDetectionProcessor(Activity _activity) {
        activity=_activity;
        //res=context.getResources();
        //c=context;
        //packageName=context.getPackageName();
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .enableTracking()
                        .build();
        detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
    }

    public FaceDetectionProcessor(SharedPref sharedPref,LinearLayout layout, LivePreviewActivity livePreviewActivity) {
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .enableTracking()
                        .build();
        this.layout = layout;
        c=livePreviewActivity;
        this.sharedPref = sharedPref;
        detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
//        idlist=new ArrayList<>();
//        imageViewArr = new ArrayList<>();
        integerImageViewHashMap = new HashMap<>();
        singleFaceDetectCount = new HashMap<>();
        threadCount =0;
    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: " + e);
        }
    }

    @Override
    protected Task<List<FirebaseVisionFace>> detectInImage(FirebaseVisionImage image) {
        this.bitmap =  image.getBitmap();
        return detector.detectInImage(image);
    }

    @Override
    protected void onSuccess(
            @NonNull List<FirebaseVisionFace> faces,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        total++;
            Log.d(TAG, "onSuccess: Total: " + total);
            for (int i = 0; i < faces.size(); ++i) {
                FirebaseVisionFace face = faces.get(i);
                FaceGraphic faceGraphic = new FaceGraphic(graphicOverlay);
                graphicOverlay.add(faceGraphic);
                faceGraphic.updateFace(face, frameMetadata.getCameraFacing());
                if( total % 2 == 0 && TeacherAttendanceActivity.startRecog)
                {
                    int id = face.getTrackingId();
                    int x = face.getBoundingBox().left;
                    int y = face.getBoundingBox().top;
                    int w = face.getBoundingBox().width();
                    int h = face.getBoundingBox().height();
                    if (x < 0) x = 0;
                    if (y < 0) y = 0;
                    if (x + w >= bitmap.getWidth()) w = (bitmap.getWidth() - x);
                    if (y + h >= bitmap.getHeight()) h = (bitmap.getHeight() - y);

                    if (!integerImageViewHashMap.containsKey(id)) {
                        ImageView imageView = new ImageView(c);
                        imageView.setImageBitmap(Bitmap.createBitmap(bitmap, x, y, w, h));
                        // layout.addView(imageView);
                        integerImageViewHashMap.put(id, imageView);
                        singleFaceDetectCount.put(id,0);
                    } else {
                        int prev_count = singleFaceDetectCount.get(id);
                        prev_count++;
                        singleFaceDetectCount.put(id,prev_count);
                        ImageView img = integerImageViewHashMap.get(id);
                        if( prev_count <= 1 )
                        {
                            img.setImageBitmap(Bitmap.createBitmap(bitmap, x, y, w, h));
                        }
                        else if( prev_count ==2 )
                        {
                            layout.removeView(integerImageViewHashMap.get(id));
                            layout.addView(integerImageViewHashMap.get(id));
//                            Bitmap bmp=Bitmap.createBitmap(bitmap,x,y,w,h);
                            Bitmap bmp = ((BitmapDrawable)img.getDrawable()).getBitmap();
                            Log.d(TAG, "onSuccess: ");
                            createThread(id,bmp);
                        }
                    }
                }
            }
    }

    private void createThread(Integer id,Bitmap face) {
        threadCount++;
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        face.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte [] bytes=stream.toByteArray();

        UploadImage uploadImage=new UploadImage();
        Pair<Integer,byte[]> map = new Pair<>(id,bytes);
        uploadImage.execute(map);
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Face detection failed " + e);
    }

    private class UploadImage extends AsyncTask<Pair<Integer,byte[]>,Void,Pair<Integer,String>> {
        boolean isError = false;
        @Override
        protected Pair<Integer,String> doInBackground(Pair<Integer,byte[]> ... test ) {
            String resp = null;
            Pair<Integer,String> p = null;
            p = new Pair<>(test[0].first,null);
            try {
                Socket s ;
                String ip = sharedPref.getIP();
                if(ip.equals(""))
                    ip = "127.0.0.1";
                String port = sharedPref.getPort();
                if(port.equals(""))
                    port = "7800";
                int id = test[0].first;
                byte[] bytes = test[0].second;
                s = new Socket();
                s.connect(new InetSocketAddress(ip, Integer.valueOf(port)),3000);
                ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
                Log.d(TAG, "doInBackground: sent here");
                oos.writeObject(bytes);
                InputStreamReader isr=new InputStreamReader(s.getInputStream());
                BufferedReader br=new BufferedReader(isr);
                resp= br.readLine();
                p = new Pair<>(id,resp);
            }catch (Exception e){
                //if fail go to checkbox activity
                e.printStackTrace();
                isError = true;
            }
            return p;
        }

        @Override
        protected void onPostExecute(Pair<Integer,String> p) {
            super.onPostExecute(p);
            if(isError) {
                Toast.makeText(c, "Server Unreachable", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(c, TeacherAttendanceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Activity activity = (Activity) c;
                activity.finish();
                TeacherAttendanceActivity.startRecog = true;
                c.startActivity(intent);

            }
            else {
                threadCount--;
                if(p.second!=null)
                {
                    int imageId = p.first;
                    String id = p.second;
                    Log.d("Mytag","Thread count "+threadCount);
                    Log.d("Mytag","ID "+id);
                    if( id != null && !id.equals("unknown")) {
                        LivePreviewActivity.recognizedIds.add(id);
                    }else{
                        //what if unknown
                        TeacherAttendanceActivity.imageViews.add(integerImageViewHashMap.get(imageId));
                        Log.d(TAG, "onPostExecute: " + TeacherAttendanceActivity.imageViews.size());
                    }
                }
                else
                {
                    TeacherAttendanceActivity.imageViews.add(integerImageViewHashMap.get(p.first));
                }
            }

        }
    }
}
