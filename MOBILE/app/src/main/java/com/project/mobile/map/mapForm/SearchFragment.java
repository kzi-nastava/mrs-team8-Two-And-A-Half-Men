package com.project.mobile.map.mapForm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.project.mobile.DTO.NominatimResult;
import com.project.mobile.R;
import com.project.mobile.map.ViewModel.SheredLocationViewModel;


public class SearchFragment extends Fragment {
    private EditText inputSearch;
    private RecyclerView rvSuggestions;
    private SugestionAdapter suggestionsAdapter;
    private SheredLocationViewModel sheredLocationViewModel;

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        sheredLocationViewModel =
                new ViewModelProvider(requireActivity()).get(SheredLocationViewModel.class);

        inputSearch = view.findViewById(R.id.input_search);
        rvSuggestions = view.findViewById(R.id.recycler_suggestions);
        setupSuggestionsRecyclerView();
        setupSearchInput();
        setupObservers();

        return view;
    }
    private void setupSuggestionsRecyclerView() {
        rvSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));

        suggestionsAdapter = new SugestionAdapter(result -> {
            sheredLocationViewModel.addLocation(result);

            inputSearch.setText("");
            inputSearch.clearFocus();
            rvSuggestions.setVisibility(View.GONE);

            hideKeyboard();
        });

        rvSuggestions.setAdapter(suggestionsAdapter);
    }
    private void setupSearchInput() {
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    if (query.length() >= 3) {
                        sheredLocationViewModel.searchLocations(query);
                    } else {
                        sheredLocationViewModel.clearSuggestions();
                        rvSuggestions.setVisibility(View.GONE);
                    }
                };

                searchHandler.postDelayed(searchRunnable, 2000);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        inputSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                v.postDelayed(() -> rvSuggestions.setVisibility(View.GONE), 200);
            }
        });
    }
    private void setupObservers() {
        // Observe suggestions
        sheredLocationViewModel.getSuggestions().observe(getViewLifecycleOwner(), suggestions -> {
            suggestionsAdapter.submitList(suggestions);

            if (suggestions != null && !suggestions.isEmpty()) {
                rvSuggestions.setVisibility(View.VISIBLE);
            } else {
                rvSuggestions.setVisibility(View.GONE);
            }
        });
    }

    private void hideKeyboard() {
        if (getActivity() != null && getView() != null) {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager)
                            getActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }
}