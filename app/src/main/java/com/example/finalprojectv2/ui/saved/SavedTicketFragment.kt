package com.example.finalprojectv2.ui.saved

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectv2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class savedTicketFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Obtain the Firestore reference, the User UID and the User's Document
        db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val userDoc = db.collection("savedTickets").document(uid)

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_saved_ticket, container, false)

        // Get a realtime update of the database
        userDoc.get()
            .addOnSuccessListener { snap ->
                // Obtain a raw list of maps
                val raw = snap.get("saved") as? List<Map<String,Any>> ?: emptyList()

                // Use Gson to turn each map to JSON then to Event
                val gson = Gson()
                val eventList = raw.mapNotNull { map ->
                    // Serialize back to JSON
                    val json = gson.toJson(map)
                    // Deserialize into Event class
                    gson.fromJson(json, Event::class.java)
                }.let { ArrayList(it) }

                // Store the the recyclerView widget in a variable
                val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
                recyclerView.adapter = SavedTicketRecyclerAdaptor(eventList)
                // Use a linear layout manager for the recyclerView
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                // Update the adapter with the new data
                recyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { e -> // Alert the user of an unsuccessful fetch
                Log.e("Firestore", "fetch failed", e)
            }

        view.findViewById<Button>(R.id.button2).setOnClickListener {
            userDoc
                .update("saved", emptyList<Event>())
                .addOnSuccessListener {
                    val emptyList = ArrayList<Event>()
                    val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
                    recyclerView.adapter = SavedTicketRecyclerAdaptor(emptyList)
                    Toast.makeText(requireContext(), "All saved events removed", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Couldn’t remove all saved events", Toast.LENGTH_SHORT).show()
                }
        }
        return view
    }

}