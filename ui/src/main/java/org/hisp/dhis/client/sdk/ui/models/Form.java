package org.hisp.dhis.client.sdk.ui.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public final class Form implements Parcelable {

    // This Uid corresponds to either an Event or Enrollment
    private final String dataModelUid;

    private final String title;
    private final List<FormSection> formSections;
    private final String programUid;
    private final String programStageUid;
    private final String orgUnit;

    private Form(Builder builder) {
        title = builder.getTitle();
        dataModelUid = builder.getDataModelUid();
        formSections = builder.getFormSections();
        programUid = builder.getProgramUid();
        programStageUid = builder.getProgramStageUid();
        orgUnit = builder.getOrgUnit();
    }

    public List<FormSection> getFormSections() {
        return formSections;
    }

    public String getProgramUid() {
        return programUid;
    }

    public String getProgramStageUid() {
        return programStageUid;
    }

    public String getTitle() {
        return title;
    }

    public String getDataModelUid() {
        return dataModelUid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dataModelUid);
        dest.writeString(title);
        dest.writeList(formSections);
        dest.writeString(programUid);
        dest.writeString(programStageUid);
    }

    public static final Parcelable.Creator<Form> CREATOR
            = new Parcelable.Creator<Form>() {

        public Form createFromParcel(Parcel in) {
            Builder builder = new Builder();
            builder.setDataModelUid(in.readString()).
                    setTitle(in.readString());

            ArrayList<FormSection> formSections = new ArrayList<>();
            in.readList(formSections, null);

            builder.setFormSections(formSections).
                    setProgramUid(in.readString()).
                    setProgramStageUid(in.readString());

            return builder.build();
        }

        public Form[] newArray(int size) {
            return new Form[size];
        }
    };

    public static final class Builder {
        private String dataModelUid;
        private String title;
        private List<FormSection> formSections;
        private String programUid;
        private String programStageUid;
        private String orgUnit;

        public Builder() {
        }

        public static Builder fromForm(Form form) {
            return new Builder().
                    setDataModelUid(form.getDataModelUid()).
                    setTitle(form.getTitle()).
                    setFormSections(form.getFormSections()).
                    setProgramUid(form.getProgramUid()).
                    setProgramStageUid(form.getProgramStageUid());
        }

        public Form build() {
            return new Form(this);
        }

        /**
         * This Uid corresponds to either an Event or Enrollment
         */
        public String getDataModelUid() {
            return dataModelUid;
        }

        /**
         * This Uid corresponds to either an Event or Enrollment
         */
        public Builder setDataModelUid(String dataModelUid) {
            this.dataModelUid = dataModelUid;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public List<FormSection> getFormSections() {
            return formSections;
        }

        public Builder setFormSections(List<FormSection> formSections) {
            this.formSections = formSections;
            return this;
        }

        public String getProgramUid() {
            return programUid;
        }

        public Builder setProgramUid(String programUid) {
            this.programUid = programUid;
            return this;
        }

        public String getProgramStageUid() {
            return programStageUid;
        }

        public Builder setProgramStageUid(String programStageUid) {
            this.programStageUid = programStageUid;
            return this;
        }

        public String getOrgUnit() {
            return orgUnit;
        }

        public void setOrgUnit(String orgUnit) {
            this.orgUnit = orgUnit;
        }
    }
}
