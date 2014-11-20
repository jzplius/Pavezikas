package lt.justplius.android.pavezikas.post;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.Time;

import java.text.ParseException;
import java.util.Calendar;

import lt.justplius.android.pavezikas.R;

/**
 * Created by JUSTPLIUS on 2014.11.13.
 */
public class PostUtils {
    public static String getFormattedDate(Context context, Long timeInMilliseconds) throws ParseException {
        Time time = new Time();
        time.set(timeInMilliseconds);
        StringBuilder date = new StringBuilder();
        Resources res = context.getResources();
        switch (time.month){
            case 0:
                date.append(res.getString(R.string.month_1_ltu));
                break;
            case 1:
                date.append(res.getString(R.string.month_2_ltu));
                break;
            case 2:
                date.append(res.getString(R.string.month_3_ltu));
                break;
            case 3:
                date.append(res.getString(R.string.month_4_ltu));
                break;
            case 4:
                date.append(res.getString(R.string.month_5_ltu));
                break;
            case 5:
                date.append(res.getString(R.string.month_6_ltu));
                break;
            case 6:
                date.append(res.getString(R.string.month_7_ltu));
                break;
            case 7:
                date.append(res.getString(R.string.month_8_ltu));
                break;
            case 8:
                date.append(res.getString(R.string.month_9_ltu));
                break;
            case 9:
                date.append(res.getString(R.string.month_10_ltu));
                break;
            case 10:
                date.append(res.getString(R.string.month_11_ltu));
                break;
            case 11:
                date.append(res.getString(R.string.month_12_ltu));
                break;
        }
        date.append(" ")
                .append(time.monthDay)
                .append(" d.");
        return date.toString();
    }

    public static String getFormattedSeats(Context context, String seatsString){
        String seats = context.getResources().getString(R.string.seats);
        return seats + seatsString;
    }

    public static String getFormattedPrice(Context context, String priceString){
        Resources res = context.getResources();
        String price = res.getString(R.string.price);
        String currency = res.getString(R.string.currency);
        return price + priceString + currency;
    }

    public static String getFormattedTime(Context context, boolean isChecked,
                                          long fromMilliseconds, long toMilliseconds){
        Resources res = context.getResources();
        String hoursString = res.getString(R.string.hours);
        Calendar from = Calendar.getInstance();
        from.setTimeInMillis(fromMilliseconds);

        // If time leaving is not flexible
        if (!isChecked) {
            return from.get(Calendar.HOUR_OF_DAY)
                    + ":"
                    + String.format("%02d", from.get(Calendar.MINUTE))
                    + " "
                    + hoursString;
        // If the leaving time is flexible
        } else{
            Calendar to = Calendar.getInstance();
            to.setTimeInMillis(toMilliseconds);

            return from.get(Calendar.HOUR_OF_DAY)
                    + " - "
                    + to.get(Calendar.HOUR_OF_DAY)
                    + " "
                    + hoursString;
        }



    }
}
