package com.example.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class PrecautionsFragment extends Fragment {

    private ListView listView;
    private PrecautionAdapter adapter;
    private ArrayList<Precaution> precautions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflates the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_precautions, container, false);

        // Initialize ListView and set adapter
        listView = view.findViewById(R.id.precautions_list_view);
        precautions = new ArrayList<>();
        precautions.add(new Precaution("Control Blood Pressure", "Regularly monitor and manage your blood pressure through medication, diet, and exercise to reduce the risk of stroke. High blood pressure is the leading cause of strokes, and maintaining it within a healthy range can significantly lower the risk."));
        precautions.add(new Precaution("Maintain a Healthy Diet", "Adopt a balanced diet rich in fruits, vegetables, whole grains, and lean proteins. Limit intake of salt, saturated fats, and cholesterol to help prevent stroke by maintaining a healthy weight and reducing blood pressure."));
        precautions.add(new Precaution("Exercise Regularly", "Engage in physical activities like walking, jogging, swimming, or cycling for at least 30 minutes a day, most days of the week. Regular exercise improves cardiovascular health and helps control weight, reducing stroke risk."));
        precautions.add(new Precaution("Quit Smoking", "Smoking cessation is crucial as it significantly decreases the risk of stroke. Smoking damages blood vessels, raises blood pressure, and accelerates the buildup of plaque in the arteries."));
        precautions.add(new Precaution("Manage Diabetes", "Keep blood sugar levels under control through medication, diet, and regular check-ups. High blood sugar can damage blood vessels and increase the likelihood of clots forming, leading to a stroke."));
        precautions.add(new Precaution("Limit Alcohol Consumption", "Drink alcohol in moderation, if at all. Excessive alcohol intake can increase blood pressure and contribute to various health problems that elevate stroke risk. Stick to recommended guidelines of up to one drink per day for women and two for men."));
        precautions.add(new Precaution("Manage Stress", "Practice stress-reducing techniques such as mindfulness, meditation, yoga, or deep breathing exercises. Chronic stress can contribute to high blood pressure and other stroke risk factors."));

        adapter = new PrecautionAdapter(getContext(), precautions);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Precaution selectedPrecaution = precautions.get(position);
                Intent intent = new Intent(getActivity(), PrecautionDetailActivity.class);
                intent.putExtra("title", selectedPrecaution.getTitle());
                intent.putExtra("detail", selectedPrecaution.getDetail());
                startActivity(intent);
            }
        });

        return view;
    }
}
