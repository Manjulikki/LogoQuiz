package com.manjunath.logoquiz.contracts;

import android.graphics.Bitmap;

public interface PresenterContract {

     interface presenter{
          void loadJSONValue();
          void fetchImageForUrl(String url);
     }

     interface view {
          void onInputJSONReceived(String input);
          void onImageFetched(Bitmap imageBitmap);
     }
}
