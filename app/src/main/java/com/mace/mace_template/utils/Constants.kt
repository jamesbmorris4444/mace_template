package com.mace.mace_template.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import com.mace.mace_template.BottomNavItem
import com.mace.mace_template.repository.storage.Donor

object Constants {

    const val MOVIES_ARRAY_DATA_TAG = "results"
    val DONOR_LIST_CLASS_TYPE = (ArrayList<Donor>()).javaClass
    const val THEATRE_BLOOD_BASE_URL = "https://api.themoviedb.org/3/discover/"
    const val IMAGE_URL_PREFIX = "https://image.tmdb.org/t/p/"
    const val API_KEY_REQUEST_PARAM = "api_key"
    const val LANGUAGE_REQUEST_PARAM = "language"
    const val PAGE_REQUEST_PARAM = "page"
    const val API_KEY = "17c5889b399d3c051099e4098ad83493"
    const val LANGUAGE = "en"
    const val MAIN_DATABASE_NAME = "TheatreBlood.db"
    const val MODIFIED_DATABASE_NAME = "TheatreBloodModified.db"
    const val LOG_TAG = "LogUtils"

    // Bottom Navigation Bar Items

    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Home",
            icon = Icons.Filled.Home,
            route = "home"
        ),
        BottomNavItem(
            label = "Search",
            icon = Icons.Filled.Search,
            route = "search"
        ),
        BottomNavItem(
            label = "Profile",
            icon = Icons.Filled.Person,
            route = "profile"
        )
    )

}