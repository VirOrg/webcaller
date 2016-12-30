package com.virorg.okhttplibtesting;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vivekjha on 23/09/15.
 */
public class FileUtils {


    private static final String TAG = FileUtils.class.getName();


    /**
     * Check the external storage status
     *
     * @return
     */
    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Copy a file from one destination to another
     *
     * @param src source destination
     * @param dst final destination
     * @throws IOException throws
     */
    public static boolean copy(File src, File dst) {
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }



     /* ----------  get real  Path  ------------*/

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    /**
     * Create a file Uri for saving an image or video
     */
    public static Uri getOutputMediaFileUri(String folderPath) {
        return Uri.fromFile(getOutputMediaFile(folderPath));
    }

    public static String getInternalDirectory(Context context) {
        File internalAppDirectory = context.getDir(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        Log.i(context.getClass().getName(), "Internal directory path ------->" + internalAppDirectory.getAbsolutePath());
        return internalAppDirectory.getAbsolutePath();
    }

    public static String createFolderInSDCard() throws Exception {
        File file = new File(Environment.getExternalStorageDirectory() + "testOK");
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    private static boolean isAppFolderExist() {
        File file = new File(Environment.getExternalStorageDirectory() + "testOK");
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }


    public static String createDirectory(String dir) throws Exception {
        File file = null;
        if (isAppFolderExist()) {
            file = new File(Environment.getExternalStorageDirectory() + "testOK" + dir);
            if (!file.exists()) {
                file.mkdir();
            }
        } else {
            createFolderInSDCard();
            file = new File(Environment.getExternalStorageDirectory() + "testOK" + dir);
            if (!file.exists()) {
                file.mkdir();
            }
        }

        return file.getAbsolutePath();
    }


    /**
     * Create file name for saving media
     *
     * @param folderPath name of sub folder to save data in
     * @return returns the file object where the media will be stored
     */
    public static File getOutputMediaFile(String folderPath) {

        File storageDir = new File(folderPath);

        // Create the storage directory if it does not exist
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.d("TAG", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String path = storageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg";
        return new File(path);
    }

    // save your video to SD card
    public static void saveVideo(final Uri uriVideo, String folderPath) {

        File vidDir = new File(folderPath);
        String videoName = "VID_" + System.currentTimeMillis() + ".mp4";
        File fileVideo = new File(vidDir.getAbsolutePath(), videoName);
        boolean success = false;
        try {
            fileVideo.createNewFile();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method to store a compressed file on a  particular location in memory
     *
     * @param bitmap     takes bitmap to convert ans store
     * @param imageName  name of the image
     * @param folderPath path of folder where data needs to be saved
     * @return returns the absolute path of the file
     */
    public static String storeFile(Bitmap bitmap, String imageName, String folderPath) {


        File mSubFolder = new File(folderPath);

        if (!mSubFolder.exists()) {
            mSubFolder.mkdirs();
        }

        File f = new File(mSubFolder.getAbsolutePath(), imageName);
        String strMyImagePath = f.getAbsolutePath();
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();

            Log.i(TAG, "File size : " + f.length() + "bytes");
        } catch (Exception e) {

            e.printStackTrace();
        }
        Log.i(TAG, "Path : " + strMyImagePath);

        return strMyImagePath;
    }


    /**
     * Method to store a compressed file on a  particular location in memory
     *
     * @param bitmap     takes bitmap to convert ans store
     * @param imageName  name of the image
     * @param folderPath path of folder where data needs to be saved
     * @return returns the absolute path of the file
     */
    public static String storeFile(Bitmap bitmap, String imageName, String folderPath, Bitmap.CompressFormat compressFormat) {


        File mSubFolder = new File(folderPath);

        if (!mSubFolder.exists()) {
            mSubFolder.mkdirs();
        }

        File f = new File(mSubFolder.getAbsolutePath(), imageName);
        String strMyImagePath = f.getAbsolutePath();
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(f);
            bitmap.compress(compressFormat, 0, fos);

            fos.flush();
            fos.close();

            Log.i(TAG, "File size : " + f.length() + "bytes");
        } catch (Exception e) {

            e.printStackTrace();
        }
        Log.i(TAG, "Path : " + strMyImagePath);

        return strMyImagePath;
    }


    public static void deleteFile(String fileName) {
        File file = new File(fileName);

        if (file.exists()) {
            file.delete();
        }
    }

    public static String compressFile(Context context, String sourceFile, String imageName, String folderPath, int factor) throws FileNotFoundException {

        Uri imageUri = Uri.fromFile(new File(sourceFile));
        Bitmap imageBitmap = decodeUri(context, imageUri);

        File mSubFolder = new File(folderPath);

        if (!mSubFolder.exists()) {
            mSubFolder.mkdirs();
        }

        File f = new File(mSubFolder.getAbsolutePath(), imageName);
        String strMyImagePath = f.getAbsolutePath();
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(f);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, factor, fos);

            fos.flush();
            fos.close();

            Log.i(TAG, "File size : " + f.length() + "bytes");
        } catch (Exception e) {

            e.printStackTrace();
        }
        Log.i(TAG, "Path : " + strMyImagePath);

        return strMyImagePath;

    }

    /**
     * Delete a file without throwing any exception
     *
     * @param path path of file to delete
     * @return status of deletion
     */
    public static boolean deleteFileNoThrow(String path) {
        File file;
        try {
            file = new File(path);
        } catch (NullPointerException e) {
            return false;
        }

        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * Method to Decode URI we get from gallery/Camera intents
     *
     * @param context       context required for content resolver
     * @param selectedImage uri of selected image to decode data
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap decodeUri(Context context, Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o);

// The new size we want to scale to
// final int REQUIRED_SIZE = (int) (((ImageView)clickedImageView).getHeight()) ;
        final int REQUIRED_HEIGHT = 512;
        final int REQUIRED_WIDTH = 512;

        // Find the correct scale value. It should be the power of 2.
        // int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        // takes less time than loop
        if (o.outHeight > REQUIRED_HEIGHT || o.outWidth > REQUIRED_WIDTH) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(REQUIRED_WIDTH /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }


        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o2);

    }

//    public static String getInternalStoragePath() {
//        //File internalAppDirectory = this.getDir(GugulyApplication.getInstance().getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
//        //Log.i(.getClass().getName(), "Internal directory path ------->" + internalAppDirectory.getAbsolutePath());
//       // return internalAppDirectory.getAbsolutePath();
//    }

    public static String downloadImage(String fileUrl, String path, String name) {

        String filepath = null;
        try {
            //String temp = URLEncoder.encode(path, "UTF-8");
            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.connect();

            File desFile = new File(path);

            if (!desFile.exists()) {
                desFile.mkdir();
            }

            File file = new File(desFile, name);
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
            }
            fileOutput.close();
            if (downloadedSize == totalSize) filepath = file.getPath();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            filepath = null;
            e.printStackTrace();
        }
        return filepath;
    }

    /**
     * method to get bitmap from URL
     */
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    /**
     * Sends a broadcast to have the media scanner scan a file
     *
     * @param path the file to scan
     */
    public static void scanMedia(Context mContext, String path) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        mContext.sendBroadcast(scanFileIntent);
    }

    /**
     * Sends a broadcast to have the media scanner scan a file
     *
     * @param uri uri of file to scan
     */
    public static void scanMedia(Context mContext, Uri uri) {
        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        mContext.sendBroadcast(scanFileIntent);
    }


    public static Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > 1024 || rotatedHeight > 1024) {
            float widthRatio = ((float) rotatedWidth) / ((float) 1024);
            float heightRatio = ((float) rotatedHeight) / ((float) 1024);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

    /*
     * if the orientation is not 0 (or -1, which means we don't know), we
     * have to do a rotation.
     */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }

    public static int getOrientation(Context context, Uri photoUri) {
    /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor == null) {

            return 0;
        } else {
            if (cursor.getCount() != 1) {
                return -1;
            }

            cursor.moveToFirst();
            return cursor.getInt(0);


        }


    }


    /**
     * This method is responsible for solving the rotation issue if exist. Also scale the images to
     * 1024x1024 resolution
     *
     * @param context       The current context
     * @param selectedImage The Image URI
     * @return Bitmap image results
     * @throws IOException
     */
    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

// First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize se
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(img, selectedImage);
        return img;
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
// Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

// Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

// This offers some additional logic in case the image has a strange
// aspect ratio. For example, a panorama may have a much larger
// width than height. In these cases the total pixels might still
// end up being too large to fit comfortably in memory, so we should
// be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

// Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * Rotate an image if required.
     *
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }


    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

}
