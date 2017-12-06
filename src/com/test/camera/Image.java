package com.test.camera;

import android.graphics.Bitmap;

public class Image {
	
	private Bitmap image;

	


	public Image(Bitmap bitmap) {
		// TODO Auto-generated constructor stub
		 this.image=bitmap;
	}
	public Bitmap getImage() {
		
        return image;
        
    }
    public void setImage(Bitmap image) {
    	
        this.image = image;
        
    }
}
