/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.ui.dialogs;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.job.JobExecutor;
import org.hisp.dhis.android.sdk.job.NetworkJob;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.persistence.loaders.DbLoader;
import org.hisp.dhis.android.sdk.persistence.models.BaseSerializableModel;
import org.hisp.dhis.android.sdk.persistence.models.Conflict;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;
import org.hisp.dhis.android.sdk.synchronization.data.event.EventLocalDataSource;
import org.hisp.dhis.android.sdk.synchronization.data.event.EventRemoteDataSource;
import org.hisp.dhis.android.sdk.synchronization.data.event.EventRepository;
import org.hisp.dhis.android.sdk.synchronization.data.faileditem.FailedItemRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.event.SyncEventUseCase;
import org.hisp.dhis.android.sdk.ui.views.FontTextView;
import org.hisp.dhis.android.sdk.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemStatusDialogFragment extends DialogFragment
        implements View.OnClickListener, LoaderManager.LoaderCallbacks<ItemStatusDialogFragmentForm> {
    private static final String TAG = ItemStatusDialogFragment.class.getSimpleName();

    private static final int LOADER_ID = 9564013;

    private ItemStatusDialogFragmentForm mForm;
    private TextView mDialogLabel;
    private ImageView mItemStatusImage;
    private FontTextView mDetails;
    private FontTextView mStatus;
    private int mDialogId;

    public static final String EXTRA_ID = "extra:id";
    public static final String EXTRA_TYPE = "extra:type";
    public static final String EXTRA_ARGUMENTS = "extra:Arguments";
    public static final String EXTRA_SAVED_INSTANCE_STATE = "extra:savedInstanceState";

//    public abstract ItemStatusDialogFragment newInstance(BaseSerializableModel item);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,
                R.style.Theme_AppCompat_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return inflater.inflate(R.layout.dialog_fragment_trackedentityinstancestatus, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mItemStatusImage = (ImageView) view.findViewById(R.id.itemstatus);

        mDetails = (FontTextView) view.findViewById(R.id.item_detailed_info);

        mStatus = (FontTextView) view.findViewById(R.id.statusinfo);

        ImageView syncDialogButton = (ImageView) view
                .findViewById(R.id.sync_dialog_button);
        ImageView closeDialogButton = (ImageView) view
                .findViewById(R.id.close_dialog_button);
        mDialogLabel = (TextView) view
                .findViewById(R.id.dialog_label);

        closeDialogButton.setOnClickListener(this);
        syncDialogButton.setOnClickListener(this);
        mDetails.setOnClickListener(this);
        registerForContextMenu(mDetails);

        setDialogLabel(R.string.status);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Bundle argumentsBundle = new Bundle();
        argumentsBundle.putBundle(EXTRA_ARGUMENTS, getArguments());
        argumentsBundle.putBundle(EXTRA_SAVED_INSTANCE_STATE, savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, argumentsBundle, this);
    }

    @Override
    public Loader<ItemStatusDialogFragmentForm> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id && isAdded()) {
            // Adding Tables for tracking here is dangerous (since MetaData updates in background
            // can trigger reload of values from db which will reset all fields).
            // Hence, it would be more safe not to track any changes in any tables
            List<Class<? extends Model>> modelsToTrack = new ArrayList<>();
            modelsToTrack.add(TrackedEntityInstance.class);
            modelsToTrack.add(Enrollment.class);
            modelsToTrack.add(Event.class);
            modelsToTrack.add(FailedItem.class);
            Bundle fragmentArguments = args.getBundle(EXTRA_ARGUMENTS);
            long idd = fragmentArguments.getLong(EXTRA_ID);
            String type = fragmentArguments.getString(EXTRA_TYPE);

            return new DbLoader<>(
                    getActivity().getBaseContext(), modelsToTrack, new ItemStatusDialogFragmentQuery(
                    idd, type)
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<ItemStatusDialogFragmentForm> loader, ItemStatusDialogFragmentForm data) {

        Log.d(TAG, "load finished");
        if (loader.getId() == LOADER_ID && isAdded())
        {
            mForm = data;
            switch (mForm.getStatus()) {
                case SENT:
                    mItemStatusImage.setImageResource(R.drawable.ic_from_server);
                    mStatus.setText(getString(R.string.status_sent_description));
                    break;
                case ERROR: {
                    FailedItem failedItem = TrackerController.getFailedItem(data.getType(), data.getItem().getLocalId());
                    if(failedItem.getHttpStatusCode()==-1) {
                        mItemStatusImage.setImageResource(R.drawable.ic_offline);
                    } else {
                        mItemStatusImage.setImageResource(R.drawable.ic_event_error);
                    }
                    mStatus.setText(getString(R.string.status_error_description));
                    if(failedItem!= null) {
                        String details = "";
                        if( failedItem.getErrorMessage() != null) {
                            details += failedItem.getErrorMessage() + '\n';
                        }
                        if ( failedItem.getImportSummary() != null && failedItem.getImportSummary().getDescription() != null ) {
                            details += failedItem.getImportSummary().getDescription() + '\n';
                        }
                        if ( failedItem.getImportSummary() != null && failedItem.getImportSummary().getConflicts() != null ) {
                            for(Conflict conflict: failedItem.getImportSummary().getConflicts() ) {
                                if( conflict != null ) {
                                    details += conflict.getObject() + ": " + conflict.getValue() + "\n";
                                }
                            }
                        }
                        mDetails.setText(details);
                    }
                }
                    break;
                case OFFLINE:
                    mStatus.setText(getString(R.string.status_offline_description));
                    mItemStatusImage.setImageResource(R.drawable.ic_offline);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<ItemStatusDialogFragmentForm> loader) {

    }

    /* This method must be called only after onViewCreated() */
    public void setDialogLabel(int resourceId) {
        if (mDialogLabel != null) {
            mDialogLabel.setText(resourceId);
        }
    }

    /* This method must be called only after onViewCreated() */
    public void setDialogLabel(CharSequence sequence) {
        if (mDialogLabel != null) {
            mDialogLabel.setText(sequence);
        }
    }

    public void setDialogId(int dialogId) {
        mDialogId = dialogId;
    }

    public int getDialogId() {
        return mDialogId;
    }

    /* This method must be called only after onViewCreated() */
    public CharSequence getDialogLabel() {
        if (mDialogLabel != null) {
            return mDialogLabel.getText();
        } else {
            return null;
        }
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }

    public void copyToClipboard()
    {
        if(mDetails!=null && mDetails.getText().length()>0)
        {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", mDetails.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getActivity(), getString(R.string.copied_text), Toast.LENGTH_SHORT).show();
        }
    }

    public void writeErrorToSDCard()
    {
        if(mDetails!=null && mDetails.getText().length()>0)
        {

            StringBuilder filePath = new StringBuilder();

            String dir = getResources().getString(R.string.directory);
            String fileName = getResources().getString(R.string.error_log_file_name);

            filePath.append(Environment.getExternalStorageDirectory().getAbsolutePath());
            filePath.append(dir);
            filePath.append(fileName);

            boolean success = LogUtils.writeErrorLogToSDCard(filePath.toString(), mDetails.getText().toString());
            new MediaScanner(getActivity(), filePath.toString());

            if(success)
                Toast.makeText(getActivity(), getResources().getString(R.string.text_written_to_sd_card), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), getResources().getString(R.string.sd_card_error_message), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.sync_dialog_button) {
            Toast.makeText(getActivity(), getString(R.string.sending_data_server), Toast.LENGTH_LONG).show();
            sendToServer(mForm.getItem(), this);
            ItemStatusDialogFragment.this.dismiss();
        } else if(v.getId() == R.id.close_dialog_button) {
            dismiss();
        } else if(v.getId() == R.id.item_detailed_info) {
            if(mDetails!=null && mDetails.getText().length()>0) {




            }
        }
    }

    public abstract void sendToServer(final BaseSerializableModel item, ItemStatusDialogFragment fragment);


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.item_detailed_info);
        {
            menu.setHeaderTitle(R.string.error_description);
            String[] contextMenu = getResources().getStringArray(R.array.copy_store_error_message);

            for(int i=0;i<contextMenu.length;i++)
            {
                menu.add(Menu.NONE, i, i, contextMenu[i]);
            }

            OnMenuItemClickListener listener = new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    onContextItemSelected(item); // workaround for bug in Android that onContextItemSelected is never called automatically
                    return true;
                }
            };

            for (int i = 0, n = menu.size(); i < n; i++)
                menu.getItem(i).setOnMenuItemClickListener(listener);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        int menuItemIndex = item.getItemId();
        String[] contextMenu = getResources().getStringArray(R.array.copy_store_error_message);
        String menuItemName = contextMenu[menuItemIndex];


        if(item.getTitle().toString().equalsIgnoreCase(contextMenu[0]))
            copyToClipboard();
        else if(item.getTitle().toString().equalsIgnoreCase(contextMenu[1]))
            writeErrorToSDCard();



        return super.onContextItemSelected(item);
    }



        private class MediaScanner implements MediaScannerConnection.MediaScannerConnectionClient
        {

            private MediaScannerConnection mediaScannerConnection;
            private String pathToFile;


            public MediaScanner(Context context, String pathToFile)
            {
                this.pathToFile = pathToFile;
                mediaScannerConnection = new MediaScannerConnection(context, this);
                mediaScannerConnection.connect();
            }

            @Override
            public void onMediaScannerConnected()
            {
                mediaScannerConnection.scanFile(pathToFile, null); // when passing null reference, grabbing extension from file path (.txt for a text file)
            }

            @Override
            public void onScanCompleted(String path, Uri uri)
            {
                mediaScannerConnection.disconnect();
            }
        }


    public static void sendEvent(final Event event) {
        JobExecutor.enqueueJob(new NetworkJob<Object>(0,
                ResourceType.EVENT) {

            @Override
            public Object execute() throws APIException {
                EventLocalDataSource mLocalDataSource = new EventLocalDataSource();
                EventRemoteDataSource mRemoteDataSource = new EventRemoteDataSource(DhisController.getInstance().getDhisApi());
                EventRepository eventRepository = new EventRepository(mLocalDataSource, mRemoteDataSource);
                FailedItemRepository failedItemRepository = new FailedItemRepository();

                SyncEventUseCase syncEventUseCase = new SyncEventUseCase(eventRepository, failedItemRepository);
                syncEventUseCase.execute(event);
                return new Object();
            }
        });
    }
}
