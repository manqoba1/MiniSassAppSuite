package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ImagesDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Chris on 2015-02-19.
 */
public class PhotoCacheUtil {

    static ResponseDTO response = new ResponseDTO();
    static ImagesDTO images;
    static Context ctx;
    static final String JSON_PHOTO = "photos.json";
    static final Gson gson = new Gson();
    static final String LOG = PhotoUploadService.class.getSimpleName();

    public interface PhotoCacheListener {
        public void onFileDataDeserialized(ResponseDTO response);
        public void onDataCached();
        public void onError();

    }
    public interface PhotoCacheRetrieveListener {
        public void onFileDataDeserialized(ResponseDTO response);
        public void onDataCached();
        public void onError();
    }

    static PhotoCacheListener photoCacheListener;
    static PhotoCacheRetrieveListener photoCacheRetrieveListener =
            new PhotoCacheRetrieveListener() {
                @Override
                public void onFileDataDeserialized(ResponseDTO response) {

                }

                @Override
                public void onDataCached() {

                }

                @Override
                public void onError() {

                }
            };

    private static String getStringFromInputStream(InputStream is) throws IOException {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }finally {
            if (br != null) {
                br.close();
            }
        }
        String json = sb.toString();
        return json;
    }

    public static void cachePhoto(Context context, final ImagesDTO image, PhotoCacheListener listener) {

        images = image;
        response.setLastCacheDate(new Date());
        photoCacheListener = listener;
        ctx = context;
        new CacheRetrieveForUpdateTask().execute();
    }

    public static void getCachedPhotos(Context context, PhotoCacheListener listener) {
        photoCacheListener = listener;
        ctx = context;
        new CacheRetrieveTask().execute();
    }

    static class CacheRetrieveTask extends AsyncTask<Void, Void, ResponseDTO> {

        private ResponseDTO getData(FileInputStream stream) throws IOException {
            String json = getStringFromInputStream(stream);
            ResponseDTO response = gson.fromJson(json, ResponseDTO.class);
            return response;
        }

        @Override
        protected ResponseDTO doInBackground(Void... voids) {
            ResponseDTO response = new ResponseDTO();
            response.setImagesList(new ArrayList<ImagesDTO>());
            FileInputStream stream;
            try {
                stream = ctx.openFileInput(JSON_PHOTO);
                response = getData(stream);
                Log.i(LOG, "photo cache retrieved photos: " + response.getImagesList().size());

            } catch (FileNotFoundException e){
                Log.w(LOG, "cache file not found, not initialised yet");
                return response;
            } catch (IOException e) {
                Log.e(LOG, "doInBackground - returning new response object");

            }
            return response;
        }
    }

    static class CacheRetrieveForUpdateTask extends AsyncTask<Void, Void, ResponseDTO> {

        private ResponseDTO getData(FileInputStream stream) throws IOException {
            String json = getStringFromInputStream(stream);
            ResponseDTO response = gson.fromJson(json, ResponseDTO.class);
            return response;
        }

        @Override
        protected ResponseDTO doInBackground(Void... voids) {
            ResponseDTO response = new ResponseDTO();
            response.setImagesList(new ArrayList<ImagesDTO>());
            FileInputStream stream;
            try {
                stream = ctx.openFileInput(JSON_PHOTO);
                response = getData(stream);
            }catch (FileNotFoundException e) {
                Log.w(LOG, "cache file not found, not initialised yet");
                return response;
            }catch (IOException e){
                Log.d(LOG, "doInBackground - returning a new response");
            }
            return response;
        }


    }

}
