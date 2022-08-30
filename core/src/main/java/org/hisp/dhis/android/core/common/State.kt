/*
 *  Copyright (c) 2004-2022, University of Oslo
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
package org.hisp.dhis.android.core.common

enum class State {
    /** Data created locally that does not exist in the server yet.  */
    TO_POST,

    /** Data modified locally that exists in the server.  */
    TO_UPDATE,

    /** Data that received an error from the server after the last upload.  */
    ERROR,

    /** The element is synced with the server. There are no local changes for this value.  */
    SYNCED,

    /** Data that received a warning from the server after the last upload.  */
    WARNING,

    /** Data is being uploaded. If it is modified before receiving any server response, its state is back to
     * **TO_UPDATE**. When the server response arrives, its state does not change to **SYNCED**,
     * but it remains in **TO_UPDATE** to indicate that there are local changes.
     */
    UPLOADING,

    /** This TrackedEntityInstance has been downloaded with the sole purpose of fulfilling a relationship to another
     * TEI. This **RELATIONSHIP** TEI only has basic information (uid, type, etc) and the list of
     * TrackedEntityAttributes to be able to print meaningful information about the relationship. Other data such
     * enrollments, events or relationships is not downloaded for this TEI.
     * Also, this TEI cannot be modified or uploaded to the server.
     */
    RELATIONSHIP,

    /** Data is sent by sms and there is no server response yet. Some servers does not have the capability to send a
     * response, so this state means that data has been sent, but we do not know if it has been correctly
     * imported in the server or not.
     */
    SENT_VIA_SMS,

    /** Data is sent by sms and there is a successful response from the server.  */
    SYNCED_VIA_SMS;

    companion object {

        @JvmStatic
        fun uploadableStates(): Array<State> {
            return arrayOf(TO_POST, TO_UPDATE, SENT_VIA_SMS, SYNCED_VIA_SMS, UPLOADING)
        }

        @JvmStatic
        fun uploadableStatesIncludingError(): Array<State> {
            return arrayOf(TO_POST, TO_UPDATE, SENT_VIA_SMS, SYNCED_VIA_SMS, UPLOADING, ERROR, WARNING)
        }
    }
}
