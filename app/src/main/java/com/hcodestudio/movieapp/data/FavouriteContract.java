package com.hcodestudio.movieapp.data;

import android.provider.BaseColumns;

/**
 * Created by hassan on 11/25/2017.
 */

public class FavouriteContract {
    public static final class FavouriteEntry implements BaseColumns{
        public static final String TABLE_NAME = "favourite";
        public static final String COLUMN_MOVIEID = "movieid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_USERRATING = "userrating";
        public static final String COLUMN_POSTERPATH = "posterpath";
        public static final String COLUMN_PLOTSYNOPSIS = "plotsynopsis";

    }
}
