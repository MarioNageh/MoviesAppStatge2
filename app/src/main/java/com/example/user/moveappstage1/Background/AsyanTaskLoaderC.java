package com.example.user.moveappstage1.Background;

import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.user.moveappstage1.JsonParse.JsonAnalysis;
import com.example.user.moveappstage1.Movies;
import com.example.user.moveappstage1.Networking.Network;
import com.example.user.moveappstage1.PagesState;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by user on 02/03/2018.
 */

public  class   AsyanTaskLoaderC implements LoaderManager.LoaderCallbacks<String> {

    Context context;
    AsyncTaskCompleteListener<ArrayList<Movies>, PagesState, Boolean> listener;
    ProgressBar bar;
    boolean getpages;
    String Query;
static     ProgressDialog progressDialogl;
    static   ArrayList<Movies> movies = new ArrayList<>();
    static    PagesState state = new PagesState();
   static   int lastcount = -1;

    public AsyanTaskLoaderC(Context context, String QueryInBunder, AsyncTaskCompleteListener<ArrayList<Movies>, PagesState, Boolean> listener, boolean getpages) {
        this.context = context;
        this.listener = listener;
        this.bar = bar;
        this.getpages = getpages;
        this.Query = QueryInBunder;


    }

    private static class Loaderrrr extends android.support.v4.content.AsyncTaskLoader<String> {
        Bundle args;
        String Query;

        Context context;

        public Loaderrrr(Context context, Bundle args, String query, ProgressDialog progressDialogl) {
            super(context);
            this.context=context;
            this.args = args;
            Query = query;
        }



        @Override
        public String loadInBackground() {
            String Url = args.getString(Query);
            if (Url == null || TextUtils.isEmpty(Url)) {
                return null;
            }
            String Data = "";
            try {
                URL urls = new URL(Url);
                Data = Network.GetDataFromHtml(urls);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return Data;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (args == null) {
                return;
            }
            if (loading()) {
                forceLoad();
                progressDialogl = new ProgressDialog(context);
                progressDialogl.setCancelable(false);
                progressDialogl.setTitle("Please Watting");
                progressDialogl.show();
            }
        }

    }


    @Override
    public  Loader<String> onCreateLoader(int id, final Bundle args) {
    return     new Loaderrrr(context,args,Query,progressDialogl);
    }


    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data != null && !data.equals("")) {
            lastcount=movies.size();
             movies= JsonAnalysis.parseMoviesJson(data);
             state=JsonAnalysis.MakePageState(data);

            if(getpages) {
                listener.onTaskComplete(movies, state, true);
            }
            else{
                listener.onTaskComplete(movies,state,false);
            }
            }

        progressDialogl.dismiss();

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }


public static boolean loading(){
        Log.i("Looogerloer",lastcount+"  "+movies.size());




        if(lastcount<=movies.size()){
            return true;
        }else {
            return false;
        }
 }

}
