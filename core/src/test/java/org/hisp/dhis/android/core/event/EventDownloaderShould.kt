package org.hisp.dhis.android.core.event

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.capture
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.event.internal.EventWithLimitCallFactory
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.MockitoAnnotations

class EventDownloaderShould {

    private val callFactory: EventWithLimitCallFactory = mock()

    @Captor
    private val paramsCapture: ArgumentCaptor<ProgramDataDownloadParams> = ArgumentCaptor.forClass(
        ProgramDataDownloadParams::class.java
    )

    private lateinit var downloader: EventDownloader

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        downloader = EventDownloader(RepositoryScope.empty(), callFactory)
    }

    @Test
    fun should_parse_uid_eq_params() {
        downloader.byUid().eq("uid").download()

        verify(callFactory).downloadSingleEvents(capture(paramsCapture))
        val params = paramsCapture.value

        assertThat(params.uids().size).isEqualTo(1)
        assertThat(params.uids()[0]).isEqualTo("uid")
    }

    @Test
    fun should_parse_uid_in_params() {
        downloader.byUid().`in`("uid0", "uid1", "uid2").download()

        verify(callFactory).downloadSingleEvents(capture(paramsCapture))
        val params = paramsCapture.value

        assertThat(params.uids().size).isEqualTo(3)
        assertThat(params.uids()[0]).isEqualTo("uid0")
        assertThat(params.uids()[1]).isEqualTo("uid1")
        assertThat(params.uids()[2]).isEqualTo("uid2")
    }
}
