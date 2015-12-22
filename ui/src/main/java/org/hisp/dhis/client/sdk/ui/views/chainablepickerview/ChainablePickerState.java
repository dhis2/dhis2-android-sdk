package org.hisp.dhis.client.sdk.ui.views.chainablepickerview;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Parcelable implementation that can be used to store the state of {@link Picker} objects in a
 * {@link android.support.v7.widget.RecyclerView}
 */
public class ChainablePickerState implements Parcelable {

    List<List<Picker>> pickerTree;

    /**
     * Creates a ChainablePickerState by converting a List of linked pickers into a list of lists
     * @param rootNodes
     */
    public ChainablePickerState(List<Picker> rootNodes) {
        List<List<Picker>> rootNodesLists = new ArrayList<>();
        for(Picker rootNode : rootNodes) {
            List<Picker> rootNodeList = new ArrayList<>();
            Picker current = rootNode;
            rootNodeList.add(current);
            while(current.getNextLinkedSibling() != null) {
                current = current.getNextLinkedSibling();
                rootNodeList.add(current);
            }
            rootNodesLists.add(rootNodeList);
        }
        pickerTree = rootNodesLists;
    }

    protected ChainablePickerState(Parcel in) {
        in.readList(pickerTree, Picker.class.getClassLoader());
    }

    /**
     * Converts a List of List of pickers into a List of chained pickers
     * @return
     */
    public List<Picker> getRootNodes() {
        List<Picker> rootNodes = new ArrayList<>();
        for(List<Picker> rootNodeList : pickerTree) {
            if(rootNodeList.isEmpty()) {
                continue;
            }
            Picker rootNode = rootNodeList.get(0);
            Picker currentNode = rootNode;
            for(int i = 0; i<rootNodeList.size()-1; i++) {
                currentNode.setNextLinkedSibling(rootNodeList.get(i+1));
                currentNode = rootNodeList.get(i+1);
            }
            rootNodes.add(rootNode);
        }
        return rootNodes;
    }

    public static final Creator<ChainablePickerState> CREATOR = new Creator<ChainablePickerState>() {
        @Override
        public ChainablePickerState createFromParcel(Parcel in) {
            return new ChainablePickerState(in);
        }

        @Override
        public ChainablePickerState[] newArray(int size) {
            return new ChainablePickerState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(pickerTree);
    }
}
