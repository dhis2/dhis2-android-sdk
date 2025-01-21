package org.hisp.dhis.android.core.fileresource.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.FileResizerHelper
import org.junit.Test

class GetDimensionShould {
    @Test
    fun null_if_dimension_is_original() {
        val dimension = getDimension(FileResizerHelper.Dimension.ORIGINAL)

        assertThat(dimension).isNull()
    }

    @Test
    fun dimension_name_if_dimension_is_not_original() {
        val dimensionLarge = getDimension(FileResizerHelper.Dimension.LARGE)
        val dimensionMedium = getDimension(FileResizerHelper.Dimension.MEDIUM)
        val dimensionSmall = getDimension(FileResizerHelper.Dimension.SMALL)

        assertThat(dimensionLarge).isEqualTo("LARGE")
        assertThat(dimensionMedium).isEqualTo("MEDIUM")
        assertThat(dimensionSmall).isEqualTo("SMALL")
    }
}