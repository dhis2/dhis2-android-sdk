package org.hisp.dhis.android.core.fileresource.internal

import android.util.Log
import org.hisp.dhis.android.core.arch.helpers.FileResizerHelper

fun getDimension (dimension: FileResizerHelper.Dimension): String? {
    return if (dimension == FileResizerHelper.Dimension.ORIGINAL) {
        Log.d("GetDimensionShould", "Dimension is original, return null")
        null
    } else{
        Log.d("GetDimensionShould", "Dimension is not original, return dimension name")
        dimension.name
    }
}