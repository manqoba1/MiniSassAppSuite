package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;
import android.util.Log;

import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ImagesDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 2015-02-26.
 */
public class PictureUtil {

    public static void uploadImage (ImagesDTO dto , boolean isFullPicture,
                                      Context ctx, final ImagesDTO.PhotoUploadedListener listener) {

        if (dto.getDateUploaded() != null) return;
        if (dto.getThumbFilePath() == null) return;
        File imageFile = new File(dto.getThumbFilePath());
        if (isFullPicture) {
            imageFile = new File(dto.getImageFilePath());
        }
        Log.w(LOG, "FILE about to upload - length: " + imageFile.length() + " - " + imageFile.getAbsolutePath());
        List<File> files = new ArrayList<File>();
        if (imageFile.exists()) {
            files.add(imageFile);
            //setting up image uploading process
            ImageUpload.upload(dto, files, ctx,
                    new ImageUpload.ImageUploadListener(){
                        @Override
                    public void onUploadError() {
                            listener.onPhotoUploadFailed();
                            Log.e(LOG, "issue uploading - onUploadError");
                        }
                        @Override
                    public void onImageUploaded(ResponseDTO response) {
                            if (response.getStatusCode() == 0) {
                                listener.onPhotoUploaded();
                            } else {
                                Log.e(LOG, "Uploading error - onImageUploaded: " + response.getMessage()
                                );
                            }
                        }
                    });
        }


    }

    private static final String LOG = "pictureUtil";
}
