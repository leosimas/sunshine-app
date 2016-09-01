package br.com.android.estudos.sunshineapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by sosucesso on 8/28/16.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE = 1;
    private boolean mUseTodayLayout;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 && mUseTodayLayout ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final int viewType = this.getItemViewType( cursor.getPosition() );

        final int resLayout;
        if ( viewType == VIEW_TYPE_TODAY ) {
            resLayout = R.layout.list_item_forecast_today;
        } else {
            resLayout = R.layout.list_item_forecast;
        }

        View view = LayoutInflater.from(context).inflate( resLayout, parent, false );
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        final int iconRes;
        if ( getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY ) {
            iconRes = Utility.getArtResourceForWeatherCondition(weatherId);
        } else {
            iconRes = Utility.getIconResourceForWeatherCondition(weatherId);
        }

        // Use placeholder image for now
        viewHolder.iconView.setImageResource(iconRes);

        // Read date from cursor
        long dateInMillis = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it

        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateInMillis));

        // Read weather forecast from cursor
        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        // Find TextView and set weather forecast on it
        viewHolder.descriptionView.setText(description);
        viewHolder.iconView.setContentDescription(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.highView.setText(Utility.formatTemperature(context, high, isMetric));

        // Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.lowView.setText(Utility.formatTemperature(context, low, isMetric));
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        this.mUseTodayLayout = useTodayLayout;
    }

    private class ViewHolder {

        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highView;
        public final TextView lowView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }

    }
}
