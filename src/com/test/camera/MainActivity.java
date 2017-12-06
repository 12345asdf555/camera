package com.test.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private Button takePicBtn = null;
    private Button takeVideoBtn = null;
    private ImageView imageView = null;
    private ListView listView = null;
    private ArrayList<Image> arraylist;
    private LayoutInflater inflater;
    private Uri fileUri;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		takePicBtn = (Button) findViewById(R.id.button1);
        takePicBtn.setOnClickListener(takePiClickListener);

        takeVideoBtn = (Button) findViewById(R.id.button2);
        takeVideoBtn.setOnClickListener(takeVideoClickListener);
        
        listView = (ListView) findViewById(R.id.listView1);
        inflater=getLayoutInflater();
        arraylist =new ArrayList<Image>();

        View view = getLayoutInflater().inflate(R.layout.activity_listimage, null);
        imageView=(ImageView) view.findViewById(R.id.imageView1);
		
	}

	 private final View.OnClickListener takePiClickListener = new View.OnClickListener()
	    {

	        @Override
	        public void onClick(View v)
	        {
	            // 利用系统自带的相机应用:拍照
	            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

	            // create a file to save the image
	            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

	            // 此处这句intent的值设置关系到后面的onActivityResult中会进入那个分支，即关系到data是否为null，如果此处指定，则后来的data为null
	            // set the image file name
	            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

	            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	        }

	    };

	    private final OnClickListener takeVideoClickListener = new View.OnClickListener()
	    {

	        @Override
	        public void onClick(View v)
	        {
	        	
	        	// create a file to save the image
	            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
	        	
	        	screenshot();
	        }
	    };

	    public static final int MEDIA_TYPE_IMAGE = 1;

	    /** Create a file Uri for saving an image or video */
	    private static Uri getOutputMediaFileUri(int type)
	    {
	        return Uri.fromFile(getOutputMediaFile(type));
	    }

	    /** Create a File for saving an image or video */
	    private static File getOutputMediaFile(int type)
	    {
	        // To be safe, you should check that the SDCard is mounted
	        // using Environment.getExternalStorageState() before doing this.

	        File mediaStorageDir = null;
	        try
	        {
	            // This location works best if you want the created images to be
	            // shared
	            // between applications and persist after your app has been
	            // uninstalled.
	            mediaStorageDir = new File(
	                    Environment
	                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
	                    "MyCameraApp");


	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();

	        }

	        // Create the storage directory if it does not exist
	        if (!mediaStorageDir.exists())
	        {
	            if (!mediaStorageDir.mkdirs())
	            {
	                // 在SD卡上创建文件夹需要权限：
	                // <uses-permission
	                // android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	      
	                return null;
	            }
	        }

	        // Create a media file name
	        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
	                .format(new Date());
	        File mediaFile;
	        if (type == MEDIA_TYPE_IMAGE)
	        {
	            mediaFile = new File(mediaStorageDir.getPath() + File.separator
	                    + "IMG_" + timeStamp + ".jpg");
	        }
	        else
	        {
	            return null;
	        }

	        return mediaFile;
	    }

	    @SuppressWarnings("deprecation")
		@Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data)
	    {
	        super.onActivityResult(requestCode, resultCode, data);

	        // 如果是拍照
	        if (CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE == requestCode)
	        {

	            if (RESULT_OK == resultCode)
	            {

	                // Check if the result includes a thumbnail Bitmap
	                if (data != null)
	                {
	                    // 没有指定特定存储路径的时候

	                    // 指定了存储路径的时候（intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);）
	                    // Image captured and saved to fileUri specified in the
	                    // Intent
	                    Toast.makeText(this, "Image saved to:\n" + data.getData(),
	                            Toast.LENGTH_LONG).show();

	                    if (data.hasExtra("data"))
	                    {
	                        Bitmap thumbnail = data.getParcelableExtra("data");
	                        imageView.setImageBitmap(thumbnail);
	                    }
	                }
	                else
	                {
	                    // If there is no thumbnail image data, the image
	                    // will have been stored in the target output URI.

	                    // Resize the full image to fit in out image view.
	                    int width = imageView.getWidth();
	                    int height = imageView.getHeight();

	                    BitmapFactory.Options factoryOptions = new BitmapFactory.Options();

	                    factoryOptions.inJustDecodeBounds = true;
	                    BitmapFactory.decodeFile(fileUri.getPath(), factoryOptions);

	                    int imageWidth = factoryOptions.outWidth;
	                    int imageHeight = factoryOptions.outHeight;

	                    // Determine how much to scale down the image
	                    int scaleFactor = Math.min(imageWidth / width, imageHeight
	                            / height);

	                    // Decode the image file into a Bitmap sized to fill the
	                    // View
	                    factoryOptions.inJustDecodeBounds = false;
	                    factoryOptions.inSampleSize = scaleFactor;
	                    factoryOptions.inPurgeable = true;

	                    Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
	                            factoryOptions);
	                    
	                    Image image = new Image(bitmap);
	                    arraylist.add(image);
	                    ImageAdapter imageadapter = new ImageAdapter(inflater, arraylist);
	                    listView.setAdapter(imageadapter);

	                    //删除图片
	                    String path = fileUri.getPath();
	                    String where = MediaStore.Images.Media.DATA + "='" + path + "'";
	                    File file = new File(path);
	                    file.delete();
	                    ContentResolver mContentResolver = MainActivity.this.getContentResolver();
	                    mContentResolver.delete(fileUri, where, null);

	                }
	            }
	            else if (resultCode == RESULT_CANCELED)
	            {
	                // User cancelled the image capture
	            }
	            else
	            {
	                // Image capture failed, advise user
	            }
	        }

	    }
	    
	    private void screenshot()
	    {
	        // 获取屏幕
	        View dView = getWindow().getDecorView();  
	        dView.setDrawingCacheEnabled(true);   
	        dView.buildDrawingCache();   
	        Bitmap bmp = dView.getDrawingCache();
	        
	        //保存截图
	        String path = fileUri.getPath();
	        File file = new File(path);
	        try {
				FileOutputStream out = new FileOutputStream(file);
				bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
		        try {
					out.flush();
			        out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        Image image = new Image(bmp);
            arraylist.add(image);
            ImageAdapter imageadapter = new ImageAdapter(inflater, arraylist);
            listView.setAdapter(imageadapter);
	        
            //imageView.setImageBitmap(bmp);
            
            //删除截图
            String where = MediaStore.Images.Media.DATA + "='" + path + "'";
            file.delete();
            ContentResolver mContentResolver = MainActivity.this.getContentResolver();
            mContentResolver.delete(fileUri, where, null);
	        
	    }
	    

	}
























/*package com.test.camera;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final String LOG_TAG = "HelloCamera";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

    private Button takePicBtn = null;
    private Button takeVideoBtn = null;

    private ImageView imageView = null;

    private Uri fileUri;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		takePicBtn = (Button) findViewById(R.id.button1);
        takePicBtn.setOnClickListener(takePiClickListener);

        takeVideoBtn = (Button) findViewById(R.id.button2);
        takeVideoBtn.setOnClickListener(takeVideoClickListener);

        imageView = (ImageView) findViewById(R.id.imageView1);
		
	}

	 private final View.OnClickListener takePiClickListener = new View.OnClickListener()
	    {

	        @Override
	        public void onClick(View v)
	        {
	            // 利用系统自带的相机应用:拍照
	            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

	            // create a file to save the image
	            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

	            // 此处这句intent的值设置关系到后面的onActivityResult中会进入那个分支，即关系到data是否为null，如果此处指定，则后来的data为null
	            // set the image file name
	            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

	            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	        }

	    };

	    private final OnClickListener takeVideoClickListener = new View.OnClickListener()
	    {

	        @Override
	        public void onClick(View v)
	        {
	            // 摄像
	            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

	            // create a file to save the video
	            fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
	            // set the image file name
	            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

	            // set the video image quality to high
	            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

	            startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
	        }
	    };

	    public static final int MEDIA_TYPE_IMAGE = 1;
	    public static final int MEDIA_TYPE_VIDEO = 2;

	    *//** Create a file Uri for saving an image or video *//*
	    private static Uri getOutputMediaFileUri(int type)
	    {
	        return Uri.fromFile(getOutputMediaFile(type));
	    }

	    *//** Create a File for saving an image or video *//*
	    private static File getOutputMediaFile(int type)
	    {
	        // To be safe, you should check that the SDCard is mounted
	        // using Environment.getExternalStorageState() before doing this.

	        File mediaStorageDir = null;
	        try
	        {
	            // This location works best if you want the created images to be
	            // shared
	            // between applications and persist after your app has been
	            // uninstalled.
	            mediaStorageDir = new File(
	                    Environment
	                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
	                    "MyCameraApp");


	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();

	        }

	        // Create the storage directory if it does not exist
	        if (!mediaStorageDir.exists())
	        {
	            if (!mediaStorageDir.mkdirs())
	            {
	                // 在SD卡上创建文件夹需要权限：
	                // <uses-permission
	                // android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	      
	                return null;
	            }
	        }

	        // Create a media file name
	        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
	                .format(new Date());
	        File mediaFile;
	        if (type == MEDIA_TYPE_IMAGE)
	        {
	            mediaFile = new File(mediaStorageDir.getPath() + File.separator
	                    + "IMG_" + timeStamp + ".jpg");
	        }
	        else if (type == MEDIA_TYPE_VIDEO)
	        {
	            mediaFile = new File(mediaStorageDir.getPath() + File.separator
	                    + "VID_" + timeStamp + ".mp4");
	        }
	        else
	        {
	            return null;
	        }

	        return mediaFile;
	    }

	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data)
	    {
	        super.onActivityResult(requestCode, resultCode, data);

	        // 如果是拍照
	        if (CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE == requestCode)
	        {

	            if (RESULT_OK == resultCode)
	            {

	                // Check if the result includes a thumbnail Bitmap
	                if (data != null)
	                {
	                    // 没有指定特定存储路径的时候

	                    // 指定了存储路径的时候（intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);）
	                    // Image captured and saved to fileUri specified in the
	                    // Intent
	                    Toast.makeText(this, "Image saved to:\n" + data.getData(),
	                            Toast.LENGTH_LONG).show();

	                    if (data.hasExtra("data"))
	                    {
	                        Bitmap thumbnail = data.getParcelableExtra("data");
	                        imageView.setImageBitmap(thumbnail);
	                    }
	                }
	                else
	                {
	                    // If there is no thumbnail image data, the image
	                    // will have been stored in the target output URI.

	                    // Resize the full image to fit in out image view.
	                    int width = imageView.getWidth();
	                    int height = imageView.getHeight();

	                    BitmapFactory.Options factoryOptions = new BitmapFactory.Options();

	                    factoryOptions.inJustDecodeBounds = true;
	                    BitmapFactory.decodeFile(fileUri.getPath(), factoryOptions);

	                    int imageWidth = factoryOptions.outWidth;
	                    int imageHeight = factoryOptions.outHeight;

	                    // Determine how much to scale down the image
	                    int scaleFactor = Math.min(imageWidth / width, imageHeight
	                            / height);

	                    // Decode the image file into a Bitmap sized to fill the
	                    // View
	                    factoryOptions.inJustDecodeBounds = false;
	                    factoryOptions.inSampleSize = scaleFactor;
	                    factoryOptions.inPurgeable = true;

	                    Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
	                            factoryOptions);

	                    imageView.setImageBitmap(bitmap);
	                }
	            }
	            else if (resultCode == RESULT_CANCELED)
	            {
	                // User cancelled the image capture
	            }
	            else
	            {
	                // Image capture failed, advise user
	            }
	        }

	        // 如果是录像
	        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE)
	        {

	            if (resultCode == RESULT_OK)
	            {
	            }
	            else if (resultCode == RESULT_CANCELED)
	            {
	                // User cancelled the video capture
	            }
	            else
	            {
	                // Video capture failed, advise user
	            }
	        }
	    }

	}*/