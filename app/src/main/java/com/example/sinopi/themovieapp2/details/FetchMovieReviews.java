package com.example.sinopi.themovieapp2.details;

import android.os.AsyncTask;
import android.util.Log;
import com.example.sinopi.themovieapp2.Config;
import com.example.sinopi.themovieapp2.network.MovieDatabaseService;
import com.example.sinopi.themovieapp2.network.Reviews;
import com.example.sinopi.themovieapp2.network.Review;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Encapsulates fetching the movie's reviews from the movie db api.
 */
public class FetchMovieReviews extends AsyncTask<Long, Void, List<Review>> {

    @SuppressWarnings("unused")
    public static String LOG_TAG = FetchMovieReviews.class.getSimpleName();
    private final Listener mListener;

    /**
     * Interface definition for a callback to be invoked when reviews are loaded.
     */
    interface Listener {
        void onReviewsFetchFinished(List<Review> review);
    }

    public FetchMovieReviews(Listener listener) {
        mListener = listener;
    }

    @Override
    protected List<Review> doInBackground(Long... params) {
        // If there's no movie id, there's nothing to look up.
        if (params.length == 0) {
            return null;
        }
        long movieId = params[0];

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDatabaseService service = retrofit.create(MovieDatabaseService.class);
        Call<Reviews> call = service.findReviewsById(movieId,
                Config.THE_MOVIE_DATABASE_API_KEY);
        try {
            Response<Reviews> response = call.execute();
            Reviews reviews = response.body();
            return reviews.getReviews();
        } catch (IOException e) {
            Log.e(LOG_TAG, "A problem occurred talking to the movie db ", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Review> reviews) {
        if (reviews != null) {
            mListener.onReviewsFetchFinished(reviews);
        } else {
            mListener.onReviewsFetchFinished(new ArrayList<Review>());
        }
    }
}
