package com.example.simblocker

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.TelecomManager

class MyCallScreeningService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        if (callDetails.callDirection != Call.Details.DIRECTION_INCOMING) {
            respondToCall(callDetails, CallResponse.Builder().build())
            return
        }

        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val blockingActive = prefs.getBoolean("blocking_active", true)
        if (!blockingActive) {
            respondToCall(callDetails, CallResponse.Builder().build())
            return
        }

        val accountHandle = callDetails.accountHandle
        val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        val accounts = try {
            telecomManager.callCapablePhoneAccounts
        } catch (e: SecurityException) {
            emptyList()
        }

        var isSim1 = true
        if (accounts.size >= 2 && accountHandle != null) {
            if (accountHandle == accounts[1]) {
                isSim1 = false
            }
        }

        val blockSim1 = prefs.getBoolean("block_sim_1", true)
        val blockSim2 = prefs.getBoolean("block_sim_2", true)
        val shouldCheckSim = (isSim1 && blockSim1) || (!isSim1 && blockSim2)

        val phoneNumber = callDetails.handle?.schemeSpecificPart
        val isContact = phoneNumber?.let { isNumberInContacts(it) } ?: false

        val response = CallResponse.Builder()
        if (shouldCheckSim && !isContact) {
            // Se agregó setDisallowCall(true) para que Android acepte el bloqueo sin errores
            response.setDisallowCall(true)
                .setRejectCall(true)
                .setSkipNotification(true)
        } else {
            response.setDisallowCall(false)
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
