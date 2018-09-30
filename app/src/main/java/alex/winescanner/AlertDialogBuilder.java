package alex.winescanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AlertDialogBuilder {

    public void createNewAlertDialog(Context ctx, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);

        // Initialize a new foreground color span instance
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.WHITE);

        // Initialize a new spannable string builder instance
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(title);
        SpannableStringBuilder ssMessage = new SpannableStringBuilder(message);

        // Apply the text color span
        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                title.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        // Apply the text color span
        ssMessage.setSpan(
                foregroundColorSpan,
                0,
                message.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        alertDialogBuilder.setCancelable(true)
                .setTitle(ssBuilder)
                .setMessage(ssMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });


        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();


        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.rgb(50,50,50)));

    }

}
