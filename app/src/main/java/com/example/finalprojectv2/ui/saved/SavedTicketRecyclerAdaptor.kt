package com.example.finalprojectv2.ui.saved

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalprojectv2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class SavedTicketRecyclerAdaptor(private val eventList: ArrayList<Event>) : RecyclerView.Adapter<SavedTicketRecyclerAdaptor.MyViewHolder>()  {

    private val TAG = "SavedTicketRecyclerAdaptor"

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    inner class MyViewHolder (itemView: View): RecyclerView.ViewHolder (itemView) {
        // This class will represent a single row in the recyclerView list
        // This class also allows caching views and reuse them
        // Each MyViewHolder object keeps a reference to 5 view items in the card_row.xml file
        val eventImage = itemView.findViewById<ImageView>(R.id.event_image)
        val eventName = itemView.findViewById<TextView>(R.id.event_name)
        val venueName = itemView.findViewById<TextView>(R.id.venue_name)
        val venueAddress = itemView.findViewById<TextView>(R.id.venue_address)
        val eventDate = itemView.findViewById<TextView>(R.id.event_date)
        val button = itemView.findViewById<Button>(R.id.button)
        val price = itemView.findViewById<TextView>(R.id.price_range)
        val unsaveButton = itemView.findViewById<Button>(R.id.unsave_button)

        init{
            // Attach a click listener to the entire card view
            button.setOnClickListener {
                // Obtain the position of the row view
                val selectedItem = adapterPosition
                val currentEvent = eventList[selectedItem]

                // Create an implicit intent to launch second activity
                // Load a web URL
                val browserIntent = Intent(Intent.ACTION_VIEW)
                browserIntent.data = Uri.parse(currentEvent.url)
                itemView.context.startActivity(browserIntent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate a layout from our XML (row_item.XML) and return the holder
        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.savedcard_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val currentItem = eventList[position]
        holder.eventName.text = "${currentItem.name}"
        holder.venueName.text = "${currentItem._embedded.venues.get(0).name}, ${currentItem._embedded.venues.get(0).city.name}"
        holder.venueAddress.text = "Address: ${currentItem._embedded.venues.get(0).address.line1}, " + "${currentItem._embedded.venues.get(0).city.name}, " + "${currentItem._embedded.venues.get(0).state.name}"
        holder.eventDate.text = dateFormatter(currentItem.dates.start.localDate, currentItem.dates.start.localTime)

        // Lines 76-87 determine the highest quality image and place it into the imageView (using Glide)
        val highestQualityImage = currentItem.images.maxByOrNull {
            it.width * it.height
        }
        // Get the context for glide
        val context = holder.itemView.context
        // Load the image from the url using Glide library
        Glide.with(context)
            .load(highestQualityImage?.url)
            .placeholder(R.drawable.ic_launcher_background) // In case the image is not loaded show this placeholder image
            //.circleCrop() // optional - Circle image with rounded corners
            .into(holder.eventImage)

        // If the correct result (".embedded") is not returned,
        // then alert the user.
        if (currentItem.priceRanges != null) {
            val minPrice = currentItem.priceRanges.get(0).min.toString()
            val maxPrice = currentItem.priceRanges.get(0).max.toString()
            holder.price.text = "$${minPrice} - ${maxPrice}"
        }

        // onClick listener for unsave button
        holder.unsaveButton.setOnClickListener {
            // Obtain the current User UID and Database
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val db = FirebaseFirestore.getInstance()
            val userDoc = db.collection("savedTickets").document(uid.toString())

            // Remove the event from the User's Saved Tickets and alert them
            userDoc
                .update("saved", FieldValue.arrayRemove(currentItem))
                .addOnSuccessListener {
                    eventList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, eventList.size)
                    Toast.makeText(holder.itemView.context, "Removed from saved", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error unsaving event", e)
                    Toast.makeText(holder.itemView.context, "Couldn’t remove event", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun getItemCount(): Int {
        // Return the size of your dataset (invoked by the layout manager)
        return eventList.size
    }

    // Helper function to format the localDate and localTime to the correct format
    fun dateFormatter(date : String, time : String): String {
        val finalString = try {
            // Obtain to format of the passed Strings
            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val timeFormat = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            // Specifiy the new format and parse the Strings
            val outputDateFormat = java.text.SimpleDateFormat("M/d/yyyy", java.util.Locale.getDefault())
            val outputTimeFormat = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
            val parsedDate = dateFormat.parse(date)
            val parsedTime = if (time != null) timeFormat.parse(time) else null

            val newDate = parsedDate?.let { outputDateFormat.format(it) } ?: date
            val newTime = parsedTime?.let { outputTimeFormat.format(it) } ?: "TBD"
            "Date: $newDate @ $newTime"
        } catch (e: Exception) {
            "Date: $date @ ${time ?: "TBD"}"
        }
        return finalString
    }
}