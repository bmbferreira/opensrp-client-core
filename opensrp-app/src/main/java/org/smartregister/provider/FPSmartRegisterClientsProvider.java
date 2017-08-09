package org.smartregister.provider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import org.smartregister.R;
import org.smartregister.view.activity.SecuredActivity;
import org.smartregister.view.contract.FPSmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.controller.FPSmartRegisterController;
import org.smartregister.view.dialog.FPAllMethodsServiceMode;
import org.smartregister.view.dialog.FPMethodFilter;
import org.smartregister.view.dialog.FPPrioritizationMethodFilter;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.NameSort;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.ECProfilePhotoLoader;
import org.smartregister.view.viewholder.NativeFPSmartRegisterViewHolder;
import org.smartregister.view.viewholder.OnClickFormLauncher;
import org.smartregister.view.viewholder.ProfilePhotoLoader;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class FPSmartRegisterClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final SecuredActivity activity;
    private final View.OnClickListener onClickListener;
    private final ProfilePhotoLoader photoLoader;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    protected FPSmartRegisterController controller;
    private ServiceModeOption currentServiceModeOption;

    public FPSmartRegisterClientsProvider(SecuredActivity activity, View.OnClickListener
            onClickListener, FPSmartRegisterController controller) {
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.activity = activity;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        photoLoader = new ECProfilePhotoLoader(activity.getResources(),
                activity.getResources().getDrawable(R.drawable.woman_placeholder));

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) activity.getResources().getDimension(R.dimen.list_item_height));
    }

    @Override
    public View getView(SmartRegisterClient smartRegisterClient, View convertView, ViewGroup
            viewGroup) {
        ViewGroup itemView;
        NativeFPSmartRegisterViewHolder viewHolder;
        if (convertView == null) {
            itemView = (ViewGroup) inflater().inflate(R.layout.smart_register_fp_client, null);
            viewHolder = new NativeFPSmartRegisterViewHolder(itemView);
            itemView.setTag(viewHolder);
        } else {
            itemView = (ViewGroup) convertView;
            viewHolder = (NativeFPSmartRegisterViewHolder) itemView.getTag();
        }

        FPSmartRegisterClient client = (FPSmartRegisterClient) smartRegisterClient;

        setupClientProfileView(client, viewHolder);
        setupEcNumberView(client, viewHolder);
        setupGPLSAView(client, viewHolder);

        viewHolder.hideAllServiceModeOptions();
        currentServiceModeOption.setupListView(client, viewHolder, onClickListener);

        itemView.setLayoutParams(clientViewLayoutParams);
        return itemView;
    }

    private void setupClientProfileView(FPSmartRegisterClient client,
                                        NativeFPSmartRegisterViewHolder viewHolder) {
        viewHolder.profileInfoLayout().bindData(client, photoLoader);
        viewHolder.profileInfoLayout().setOnClickListener(onClickListener);
        viewHolder.profileInfoLayout().setTag(client);
    }

    private void setupEcNumberView(FPSmartRegisterClient client, NativeFPSmartRegisterViewHolder
            viewHolder) {
        viewHolder.txtECNumberView().setText(String.valueOf(client.ecNumber()));
    }

    private void setupGPLSAView(FPSmartRegisterClient client, NativeFPSmartRegisterViewHolder
            viewHolder) {
        viewHolder.gplsaAndChildLayout().bindData(client);
    }

    @Override
    public SmartRegisterClients getClients() {
        NameSort nameSortOption = new NameSort();
        FilterOption filterOption = getFPFilterOptionBasedOnDialogTab(currentServiceModeOption);
        return controller.getClients()
                .applyFilterWithFP(currentServiceModeOption, nameSortOption, filterOption);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption
            serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return controller.getClients()
                .applyFilterWithFP(serviceModeOption, sortOption, villageFilter, searchFilter,
                        getFPFilterOptionBasedOnDialogTab(serviceModeOption));
    }

    private FilterOption getFPFilterOptionBasedOnDialogTab(ServiceModeOption serviceModeOption) {
        if (serviceModeOption instanceof FPAllMethodsServiceMode) {
            return new FPMethodFilter(serviceModeOption.name());
        }
        return new FPPrioritizationMethodFilter(serviceModeOption.name());
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {
        currentServiceModeOption = serviceModeOption;
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return new OnClickFormLauncher(activity, formName, entityId, metaData);
    }

    public LayoutInflater inflater() {
        return inflater;
    }
}
