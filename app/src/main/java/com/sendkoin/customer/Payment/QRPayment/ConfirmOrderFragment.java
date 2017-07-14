package com.sendkoin.customer.Payment.QRPayment;

import android.app.Fragment;

/**
 * Created by warefhaque on 7/11/17.
 */

/**
 * Possible changes made could be using the same recycler view as the inventory layout
 * Reasons why that wasnt done:
 * 1) We can have a much larger number of customer views in this 'final' ordering list. this could include:
 * 'People who have ordered Butter Chicken have also ordered Paneer stuff'. Or recommendations right there
 * to get people to order.
 * 2) other promos or details.
 *
 * These would make it a little bit clunky if you had around 5 types of views in that adapter.
 */
public class ConfirmOrderFragment extends Fragment {
}
