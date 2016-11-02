package org.hisp.dhis.client.sdk.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.hisp.dhis.client.sdk.ui.R;

/**
 * Created by thomaslindsjorn on 11/10/16.
 */

public class EventListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_recycler_view, container, false);

        Bundle args = getArguments();

        drawEvent(inflater, rootView, args.getString("FILTER") + " Event " + 1, false);
        drawEvent(inflater, rootView, args.getString("FILTER") + " Event " + 2, true);
        drawEvent(inflater, rootView, args.getString("FILTER") + " Event " + 3, false);

        return rootView;
    }

    private void drawEvent(LayoutInflater inflater, ViewGroup eventContainer, final String eventName, boolean drawRefreshButton) {
        View event = inflater.inflate(R.layout.dashboard_event, eventContainer, false);
        ((TextView) event.findViewById(R.id.event_name)).setText(eventName);
        if (drawRefreshButton) {
            event.findViewById(R.id.refresh_button).setVisibility(View.VISIBLE);
        }
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), eventName, Toast.LENGTH_SHORT).show();
            }
        });

        eventContainer.addView(event);
    }

}
