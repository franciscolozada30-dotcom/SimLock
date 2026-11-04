package com.example.simblocker

import android.net.Uri
import android.provider.ContactsContract
import android.telecom.Call
import android.telecom.CallScreeningService

class MyCallScreeningService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        if (callDetails.callDirection != Call.Details.DIRECTION_INCOMING) {
            respondToCall(callDetails, CallResponse.Builder().build())
            return
        }

        val phoneNumber = callDetails.handle?.schemeSpecificPart
        val isContact = phoneNumber?.let { isNumberInContacts(it) } ?: false

        val response = CallResponse.Builder()
        if (!isContact) {
            response.setRejectCall(true)
                .setSkipCallLog(false)
                .setSkipNotification(true)
        }

        respondToCall(callDetails, response.build())
    }

    private fun isNumberInContacts(phoneNumber: String): Boolean {
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            return cursor.moveToFirst()
        }
        return false
    }
}
