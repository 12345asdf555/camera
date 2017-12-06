package com.test.camera;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter{
	
	private LayoutInflater inflater;
	private ArrayList<Image> arraylist;

	public ImageAdapter(LayoutInflater inflater,ArrayList<Image> arraylist){
		
        this.inflater=inflater;
        this.arraylist=arraylist;
        
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arraylist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_listimage, null);
		Image image = arraylist.get(position);
		ImageView imageview=(ImageView) view.findViewById(R.id.imageView1);
		imageview.setImageBitmap(image.getImage());
		return view;
	}
}
