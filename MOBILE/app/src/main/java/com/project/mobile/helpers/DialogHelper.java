package com.project.mobile.helpers;

import android.app.AlertDialog;
import android.content.Context;

public class DialogHelper {

    /**
     * Shows a confirmation dialog with Yes/No buttons
     * @param context The context
     * @param title Dialog title
     * @param message Dialog message
     * @param onConfirm Callback to run when user clicks Yes
     */
    public static void showConfirmDialog(Context context, String title, String message,
                                         Runnable onConfirm) {
        new AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                if (onConfirm != null) {
                    onConfirm.run();
                }
            })
            .setNegativeButton(android.R.string.no, null)
            .show();
    }

    /**
     * Shows a confirmation dialog with custom button text
     * @param context The context
     * @param title Dialog title
     * @param message Dialog message
     * @param positiveText Text for positive button
     * @param negativeText Text for negative button
     * @param onConfirm Callback to run when user clicks positive button
     */
    public static void showConfirmDialog(Context context, String title, String message,
                                         String positiveText, String negativeText,
                                         Runnable onConfirm) {
        new AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText, (dialog, which) -> {
                if (onConfirm != null) {
                    onConfirm.run();
                }
            })
            .setNegativeButton(negativeText, null)
            .show();
    }

    /**
     * Shows an information dialog with only OK button
     * @param context The context
     * @param title Dialog title
     * @param message Dialog message
     */
    public static void showInfoDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show();
    }
}
