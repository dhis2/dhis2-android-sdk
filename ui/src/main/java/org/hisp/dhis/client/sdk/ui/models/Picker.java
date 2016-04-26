/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.ui.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class Picker implements Serializable {
    // implements Parcelable {
    // hint which describes the content of picker
    private final String hint;

    // if picker represents item
    private final String id;
    private final String name;

    // parent node
    private final Picker parent;

    // available options (child nodes in tree)
    private final List<Picker> children;

    // selected item (represents path to selected node)
    private Picker selectedChild;

    private Picker(String id, String name, String hint, Picker parent) {
        this.id = id;
        this.name = name;
        this.hint = hint;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public static Picker create(String label) {
        return new Picker(null, null, label, null);
    }

    public static Picker create(String id, String name, Picker parent) {
        return new Picker(id, name, null, parent);
    }

    public static Picker create(String label, Picker parent) {
        return new Picker(null, null, label, parent);
    }

    public static Picker create(String id, String name, String label, Picker parent) {
        return new Picker(id, name, label, parent);
    }

    public String getHint() {
        return hint;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Picker getParent() {
        return parent;
    }

    public boolean addChild(Picker picker) {
        isNull(picker, "Picker must not be null");
        return children.add(picker);
    }

    public boolean addChildren(Collection<Picker> pickers) {
        isNull(pickers, "Collection of pickers must not be null");
        return children.addAll(pickers);
    }

    public List<Picker> getChildren() {
        return children;
    }

    public Picker getSelectedChild() {
        return selectedChild;
    }

    public void setSelectedChild(Picker selectedChild) {
        // if we set new selected child, we have to reset all descendants
        if (this.selectedChild != null) {
            this.selectedChild.setSelectedChild(null);
        }

        this.selectedChild = selectedChild;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Picker picker = (Picker) o;
        if (hint != null ? !hint.equals(picker.hint) : picker.hint != null) {
            return false;
        }
        if (id != null ? !id.equals(picker.id) : picker.id != null) {
            return false;
        }

        return name != null ? name.equals(picker.name) : picker.name == null;

    }

    @Override
    public int hashCode() {
        int result = hint != null ? hint.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
