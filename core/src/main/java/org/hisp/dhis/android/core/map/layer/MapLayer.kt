/*
 *  Copyright (c) 2004-2023, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.map.layer

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.ObjectWithUidInterfaceKt

@ModelBuilder
@Suppress("TooManyFunctions")
data class MapLayer(
    override val uid: String,
    val name: String,
    val displayName: String,
    val external: Boolean,
    val mapLayerPosition: MapLayerPosition,
    val style: String?,
    val imageUrl: String,
    val subdomains: List<String>?,
    val subdomainPlaceholder: String?,
    val imageryProviders: List<MapLayerImageryProvider>?,
    val code: String?,
    val mapService: MapService?,
    val imageFormat: ImageFormat?,
    val layers: String?,
    val linkedLayerUid: String?,
) : CoreObject, ObjectWithUidInterfaceKt {

    fun name(): String = name
    fun displayName(): String = displayName
    fun external(): Boolean = external
    fun mapLayerPosition(): MapLayerPosition = mapLayerPosition
    fun style(): String? = style
    fun imageUrl(): String = imageUrl
    fun subdomains(): List<String>? = subdomains
    fun subdomainPlaceholder(): String? = subdomainPlaceholder
    fun imageryProviders(): List<MapLayerImageryProvider>? = imageryProviders
    fun code(): String? = code
    fun mapService(): MapService? = mapService
    fun imageFormat(): ImageFormat? = imageFormat
    fun layers(): String? = layers
    fun linkedLayerUid(): String? = linkedLayerUid

    fun toBuilder(): Builder = MapLayerBuilder.from(this)

    class Builder : MapLayerBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
