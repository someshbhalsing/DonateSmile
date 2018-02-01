package com.chromastone.donatesmile;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class ImageUploaderService extends IntentService {

    public static final String ACTION_FOO = "com.chromastone1.donatesmile.action.FOO";

    public static final String EXTRA_IMAGE_URI = "com.chromastone1.donatesmile.extra.PARAM1";
    public static final String EXTRA_PATH = "com.chromastone1.donatesmile.extra.PARAM2";

    public ImageUploaderService() {
        super("ImageUploaderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final Uri param1 = intent.getParcelableExtra(EXTRA_IMAGE_URI);
                final String param2 = intent.getParcelableExtra(EXTRA_PATH);
                handleActionFoo(param1,param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(Uri param1,String param2) {
        Log.d("Uri",param1.toString());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference(param2);
        reference.putFile(param1)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                        UploadNotification.URI = taskSnapshot.getDownloadUrl().toString();
                        Log.d("ABC",UploadNotification.URI);
                        builder.setPhotoUri(taskSnapshot.getDownloadUrl());
                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(builder.build());
                        UploadNotification.notify(ImageUploaderService.this,"Uploaded successfully!",101,0,false);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        UploadNotification.notify(ImageUploaderService.this,"Uploading",101,(int) progress,true);
                    }
                });
    }
}
