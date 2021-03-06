package com.android254.droidconke19.repository

import com.android254.droidconke19.models.ReserveSeatModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import com.android254.droidconke19.datastates.FirebaseResult
import com.android254.droidconke19.datastates.runCatching

interface ReserveSeatRepo {
    suspend fun reserveSeat(reserveSeatModel: ReserveSeatModel): FirebaseResult<String>

    suspend fun unReserveSeat(reserveSeatModel: ReserveSeatModel): FirebaseResult<String>
}

class ReserveSeatRepoImpl(val firestore: FirebaseFirestore) : ReserveSeatRepo {


    override suspend fun reserveSeat(reserveSeatModel: ReserveSeatModel): FirebaseResult<String> {
        return if (!isSeatReserved(reserveSeatModel)) {
            runCatching {
                firestore.collection("reserved_seats").add(reserveSeatModel).await()
                "Seat successfully reserved"
            }
        } else {
            return FirebaseResult.Error("Seat already reserved")
        }
    }

    private suspend fun isSeatReserved(reserveSeatModel: ReserveSeatModel): Boolean {
        return try {
            val snapshot = firestore.collection("reserved_seats")
                    .whereEqualTo("day_number", reserveSeatModel.day_number)
                    .whereEqualTo("session_id", reserveSeatModel.session_id)
                    .whereEqualTo("user_id", reserveSeatModel.user_id)
                    .get()
                    .await()
            !snapshot.isEmpty
        } catch (e: FirebaseFirestoreException) {
            false
        }
    }


    override suspend fun unReserveSeat(reserveSeatModel: ReserveSeatModel): FirebaseResult<String> {
        return runCatching {
            val snapshot = firestore.collection("reserved_seats")
                    .whereEqualTo("day_number", reserveSeatModel.day_number)
                    .whereEqualTo("session_id", reserveSeatModel.session_id)
                    .whereEqualTo("user_id", reserveSeatModel.user_id)
                    .get()
                    .await()
            if (!snapshot.isEmpty) {
                snapshot.documents.first().reference.delete().await()
            }
            "Seat un-reserved"
        }
    }
}