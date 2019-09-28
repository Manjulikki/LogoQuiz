package com.manjunath.logoquiz.presenters;

import android.content.Context;
import android.graphics.Bitmap;
import com.manjunath.logoquiz.contracts.PresenterContract;
import com.manjunath.logoquiz.utils.Utils;
import com.squareup.picasso.Picasso;
import java.io.IOException;



public class Presenter implements PresenterContract.presenter {

    private Context context;
    private PresenterContract.view view;

    public Presenter(Context context) {
        this.context = context;
        view = (PresenterContract.view) context;
    }

    @Override
    public void loadJSONValue() {
        String input = Utils.loadJSONFromAsset(context);
        view.onInputJSONReceived(input);
    }

    @Override
    public void fetchImageForUrl(String url) {
        try {
            Bitmap image = Picasso.with(context).load(url).get();
            view.onImageFetched(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
