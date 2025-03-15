package com.weatherapp.fb

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.weatherapp.model.City
import com.weatherapp.model.User

class FBDatabase {
    interface Listener {
        fun onUserLoaded(user: User)
        fun onCityAdded(city: City)
        fun onCityUpdated(city: City)
        fun onCityRemoved(city: City)
        fun onUserSigOut(city: City)
    }

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var citiesListReg: ListenerRegistration? = null
    private var listener: Listener? = null

    init {
        auth.addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                citiesListReg?.remove()
                return@addAuthStateListener
            }

            val refCurrUser = db.collection("users").document(auth.currentUser!!.uid)
            refCurrUser.get().addOnSuccessListener {
                it.toObject(FBUser::class.java)?.let { user ->
                    listener?.onUserLoaded(user.toUser())
                }
            }

            citiesListReg = refCurrUser.collection("cities").addSnapshotListener { snapshots, ex ->
                if (ex != null) return@addSnapshotListener
                snapshots?.documentChanges?.forEach { change ->
                    val fbCity = change.document.toObject(FBCity::class.java)
                    when (change.type) {
                        DocumentChange.Type.ADDED ->
                            listener?.onCityAdded(fbCity.toCity())
                        DocumentChange.Type.MODIFIED ->
                            listener?.onCityUpdated(fbCity.toCity())
                        DocumentChange.Type.REMOVED ->
                            listener?.onCityRemoved(fbCity.toCity())
                    }
                }
            }
        }
    }

    fun setListener(listener: Listener? = null) {
        this.listener = listener
    }

    fun register(user: User) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).set(user.toFBUser())
    }

    fun add(city: City) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("cities")
            .document(city.name).set(city.toFBCity())
    }

    fun update(city: City) {
        if (auth.currentUser == null) throw RuntimeException("Not logged in!")
        val uid = auth.currentUser!!.uid
        val fbCity = city.toFBCity()
        val changes = mapOf(
            "monitored" to fbCity.monitored // Atualiza apenas o campo "monitored"
        )
        db.collection("users").document(uid)
            .collection("cities").document(fbCity.name!!).update(changes)
            .addOnSuccessListener {
                // Notifica o listener que a cidade foi atualizada
                listener?.onCityUpdated(city)
            }
            .addOnFailureListener { e ->
                // Trate o erro conforme necessário
                throw RuntimeException("Failed to update city: ${e.message}")
            }
    }

    fun remove(city: City) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("cities")
            .document(city.name).delete()
    }
}
