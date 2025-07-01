package com.example.finalprojectv2.ui.search

// Copied over from original project
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectv2.R
import com.example.finalprojectv2.ui.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.fragment.app.activityViewModels

private val BASE_URL = "https://app.ticketmaster.com/discovery/v2/"
private const val TAG = "MainActivity"
var keyword = ""

class SearchTicketFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_search_ticket, container, false)

        //The following code (Lines: 38-57) is for setting up the Spinner
        // Create a list of some strings that will be shown in the spinner
        val spinnerList = listOf(
            "Choose an event type",
            "Music",
            "Sports",
            "Theater",
            "Family",
            "Arts and Theater",
            "Concerts",
            "Comedy",
            "Dance"
        )
        // Create an adapter with 3 parameters: activity (this), layout (using a pre-built layout), list to show
        val spinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinnerList)
        // Store the the listView widget in a variable
        val spinner = view.findViewById<Spinner>(R.id.spinner)
        // set the adapter to the spinner
        spinner.adapter = spinnerAdapter
        // set the onItemSelectedListener as (this).  (this) refers to this activity that implements OnItemSelectedListener interface
        spinner.onItemSelectedListener = this

        // Once the search button is clicked, ensure the parameters are selected,
        // if so, call the helper function to make the API call
        view.findViewById<Button>(R.id.button_search).setOnClickListener() {
            val city = view.findViewById<EditText>(R.id.edit_text).text.toString()
            if (city.isEmpty()) {
                showDialog("Missing Location", "You did not specify a location. Please enter a valid location.")
            } else if (keyword == "Choose an event type") {
                showDialog("Missing Event Category", "You did not specify an event category. Please select one")
            } else {
                searchButtonClick(city)
            }
        }

        return view
    }

    // The following two methods are callback methods of OnItemSelectedListener interface
    override fun onNothingSelected(parent: AdapterView<*>?) {
        //Callback method to be invoked when the selection disappears from this view. For example,
        // if the list becomes empty and the ArrayAdapter is notified, this callback will be invoked
        Toast.makeText(requireContext(), "List is empty", Toast.LENGTH_SHORT).show()
    }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // you can determine which item in the dropdown list is selected
        val selectedItem = parent?.getItemAtPosition(position).toString()

        // Based on the index of position selected, set the keyword variable
        // For instance, if position is equal to 0, assign the value of...
        keyword = when(position){
            0 -> "Choose an event type"
            1 -> "Music"
            2 -> "Sports"
            3 -> "Family"
            4 -> "Family"
            5 -> "Arts & Theater"
            6 -> "Concerts"
            7 -> "Comedy"
            else -> "Dance"
        }
    }

    // Helper function to make the API Call
    fun searchButtonClick(city : String) {
        // Define an array to store a list of events -- this will be the list storing information
        // coming from the API
        val eventList = ArrayList<Event>()
        // specify a viewAdapter for the dataset
        val adapter = MyRecyclerAdaptor(eventList)
        // Store the the recyclerView widget in a variable
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = adapter
        // use a linear layout manager for the recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Creating a Retrofit instance with specified base URL and Gson converter factory
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL) // Set the base URL for the REST API
            .addConverterFactory(GsonConverterFactory.create()) // Add Gson converter factory for JSON serialization/deserialization
            .build() // Build the Retrofit instance

        // Creating an instance of the TicketService interface using Retrofit
        val ticketAPI = retrofit.create(TicketService::class.java)

        // Using enqueue method allows to make asynchronous call without blocking/freezing main thread
        // ticketAPI.getTickets() end point gets multiple ticket info with "apikey", "city", "keyword" and "sort" as parameters
        // Shortcut to write the object portion quickly: CTRL + Shift + Space
        ticketAPI.getTickets("BTr7b0KDLrA2NX8vOclz55eCxmQqClKt", city, keyword, "date,asc").enqueue(object : Callback<TicketData?> {
            override fun onResponse(p0: Call<TicketData?>, result: Response<TicketData?>) {
                Log.d(TAG, "onResponse: ${result}")

                // Get access to the body with response.body().
                val body = result.body()
                if (body == null) {
                    Log.w(TAG, "Valid response was not received")
                    return
                }

                // If the correct result (".embedded") is not returned,
                // then alert the user.
                var nullText = view!!.findViewById<TextView>(R.id.null_text_view)
                if (body._embedded != null) {
                    nullText.text = ""
                    // Add all items from the API response (parsed using JSON) to the user list
                    eventList.addAll(body._embedded.events)
                    // Add events to view model for Maps fragment
                    viewModel.postEvents(body._embedded.events)
                    // Update the adapter with the new data
                    adapter.notifyDataSetChanged()
                } else {
                    nullText.text = "No results found" // Alert the user
                }
            }

            // Function for failed calls
            override fun onFailure(p0: Call<TicketData?>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t}")
            }
        })
    }

    /* Creates an alertdialog
    *  with a message, title, icon and actions buttons
    */
    fun showDialog(title : String, message : String) {
        // Create an alertdialog builder object,
        // then set attributes that you want the dialog to have
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)
        // Set an icon, optional
        builder.setIcon(android.R.drawable.ic_delete)
        builder.setNeutralButton("Ok"){ dialog, which ->
            // code to run when Cancel is pressed
        }
        // create the dialog and show it
        val dialog = builder.create()
        dialog.show()
    }

}