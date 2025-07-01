package com.example.finalprojectv2.ui.search

import com.example.finalprojectv2.ui.maps.AddressLine

data class TicketData(
    val _embedded : Events
)

data class Events(
    val events : List<Event>
)

data class Event(
    val name : String,
    val dates : Date,
    val _embedded : AdditionalData,
    val images : List<Image>,
    val url : String,
    val priceRanges : List<Range>
)

data class Range (
    val min : Double,
    val max : Double
)

data class Image(
    val url : String,
    val width : Int,
    val height : Int,
)

data class Date(
    val start : dateAndTime
)

data class dateAndTime(
    val localDate : String,
    val localTime: String
)

// Used to get information about the Venue (Look at code block below)
data class AdditionalData(
    val venues : List<Venue>
)

// Following data classes are for the venue
data class Venue(
    val name : String,
    val city : CityName,
    val state : StateName,
    val address : AddressLine,
    val location : Cordinate
)
data class Cordinate(
    val longitude : String,
    val latitude : String
)
data class CityName(
    val name : String
)
data class StateName(
    val name : String
)
data class AddressLine(
    val line1 : String,
    val line2 : String
)