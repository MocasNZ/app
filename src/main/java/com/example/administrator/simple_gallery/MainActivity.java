package com.example.administrator.simple_gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final static int PERMISSION_RESULT = 0;
    GridView gv;
    GridAdapter mGridAdapter;

    ArrayList<String> image_Path_List = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gv =(GridView) findViewById(R.id.gridView);
        mGridAdapter = new GridAdapter();
       // gv.setAdapter(mGridAdapter);
        checkReadExternalStoragePermission();

        gv.setAdapter(mGridAdapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Intent callImage = new Intent(getApplicationContext(), Single_Image.class);
                callImage.putExtra("img",image_Path_List.get(i));
                startActivity(callImage);
            }
        });

    }


    protected void onResume() {
        super.onResume();
        mGridAdapter.notifyDataSetInvalidated();
       getContentResolver().notifyChange(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null);
    }

    public void onPause() {
        super.onPause();
        getSupportLoaderManager().destroyLoader(0);
        getContentResolver().notifyChange(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_RESULT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                  //  init();
                    getSupportLoaderManager().initLoader(0,null,this);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void checkReadExternalStoragePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED){
                // Start cursor loader
                //init();
                getSupportLoaderManager().initLoader(0,null,this);

            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_RESULT);
            }

        } else {
             // Start cursor loader
            //init();
            getSupportLoaderManager().initLoader(0,null,this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Media.ORIENTATION},
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED+ " DESC");

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.setNotificationUri(getContentResolver(),MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(data.moveToFirst()){
            do{
                String data_Path = data.getString(data.getColumnIndex("_data"));
                //  Date varaible = (Date) cur.getString(cur.getColumnIndex("date_added"));
                String varaible3 = data.getString(data.getColumnIndex("orientation"));
                image_Path_List.add(data_Path);

            }while (data.moveToNext());
        }
        mGridAdapter.notifyDataSetInvalidated();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mGridAdapter.notifyDataSetInvalidated();
    }



        public class GridAdapter extends BaseAdapter {
        // how many tiles
        @Override
        public int getCount() {
            return image_Path_List.size();
        }
        // not used
        @Override
        public Object getItem(int i) {
            return null;
        }
        // not used
        @Override
        public long getItemId(int i) {
            return i;
        }

        // populate a view
        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {

            ImageView image;
            if (convertView == null) {
                // if it's not recycled, inflate it from xml
                convertView = getLayoutInflater().inflate(R.layout.image_detail, null);
                // convertview will be a LinearLayout
            }
            // set size to be square
            convertView.setMinimumHeight(gv.getWidth() /  gv.getNumColumns());
            // get the imageview in this view
            image = (ImageView) convertView.findViewById(R.id.imageView);
            // make sure it isn't rotated
           // image.setRotationY(0);
            // if it's turned over, show it's icon
            if (image_Path_List.get(i)!=null)
               // image.setImageBitmap(BitmapFactory.decodeFile(image_Path_List.get(i)));
                image.setImageBitmap(getBitmapFromMediaStore(i));
            else
                image.setImageDrawable(null);
            image.setTag(i);
            return convertView;
        }
        private Bitmap getBitmapFromMediaStore(int position){
            BitmapFactory.Options sampleSize = new BitmapFactory.Options();
            sampleSize.inSampleSize = 2;
            return BitmapFactory.decodeFile(image_Path_List.get(position),sampleSize);
        }
    }
}
