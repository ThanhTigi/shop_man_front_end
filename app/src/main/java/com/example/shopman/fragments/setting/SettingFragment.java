package com.example.shopman.fragments.setting;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.shopman.R;
import com.example.shopman.auth.LoginActivity;
import com.example.shopman.utilitis.MyPreferences;

public class SettingFragment extends Fragment {
    public SettingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        LinearLayout accountButton = view.findViewById(R.id.accountButton);
        LinearLayout addressButton = view.findViewById(R.id.addressButton);
        LinearLayout logOutButton = view.findViewById(R.id.logOutButton);

        accountButton.setOnClickListener(v ->
        {
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            startActivity(intent);
        });

        addressButton.setOnClickListener(v ->
        {
            showEditAddressDialog();
        });


        logOutButton.setOnClickListener(v -> {
            MyPreferences.setString(getActivity(), "current_user_meta_data", "");

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }


    private void showEditAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_address, null);
        EditText etCountry = dialogView.findViewById(R.id.etCountry);
        EditText etCity = dialogView.findViewById(R.id.etCity);
        EditText etDetailedAddress = dialogView.findViewById(R.id.etDetailedAddress);
        EditText etContact = dialogView.findViewById(R.id.etContact);
        Button btnUpdateAddress = dialogView.findViewById(R.id.btnUpdateAddress);

        String address = MyPreferences.getString(getContext(),"user_address","216 St Paul’s Rd, London N1 2LL, UK");
        String phoneNumber = MyPreferences.getString(getContext(),"user_phone_number","0326739576");
        String[] addressParts = address.split(", ");
        if (addressParts.length >= 3) {
            etCountry.setText(addressParts[0]);
            etCity.setText(addressParts[1]);
            etDetailedAddress.setText(addressParts[2]);
        }
        etContact.setText(phoneNumber);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnUpdateAddress.setOnClickListener(v -> {
            String country = etCountry.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String detailedAddress = etDetailedAddress.getText().toString().trim();
            String contact = etContact.getText().toString().trim();

            if (country.isEmpty() || city.isEmpty() || detailedAddress.isEmpty() || contact.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!contact.matches("\\d{10}")) {
                Toast.makeText(getActivity(), "Contact must be a 10-digit phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullAddress = country + ", " + city + ", " + detailedAddress;
            String fullContact = contact;

            MyPreferences.setString(getContext(),"user_address",fullAddress);
            MyPreferences.setString(getContext(),"user_phone_number",fullContact);

            dialog.dismiss();
        });

        dialog.show();
    }
}
