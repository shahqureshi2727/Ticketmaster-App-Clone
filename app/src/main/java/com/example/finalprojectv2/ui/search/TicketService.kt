package com.example.finalprojectv2.ui.search

//import android.telecom.Call
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TicketService {
//https://app.ticketmaster.com/discovery/v2/events.json?apikey=BTr7b0KDLrA2NX8vOclz55eCxmQqClKt

    @GET("events.json?")
    fun getTickets(@Query("apikey") apiKey : String,
                   @Query("city") city : String,
                   @Query("keyword") keyword : String,
                   @Query("sort") sort : String
    ) : Call<TicketData>
}