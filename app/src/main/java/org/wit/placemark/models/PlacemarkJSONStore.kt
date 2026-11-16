package org.wit.placemark.models

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import org.wit.placemark.helpers.exists
import org.wit.placemark.helpers.read
import org.wit.placemark.helpers.write
import timber.log.Timber
import java.lang.reflect.Type
import kotlin.random.Random

const val JSON_FILE = "placemarks.json"

val gsonBuilder: Gson = GsonBuilder()
    .setPrettyPrinting()
    .create()

val listType: Type =
    object : TypeToken<ArrayList<PlacedMark>>() {}.type

class PlacemarkJSONStore(
    private val context: Context
) : PlacemarkStore {

    private var placemarks = mutableListOf<PlacedMark>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): List<PlacedMark> {
        logAll()
        return placemarks.toList()
    }

    override fun findOne(id: Long): PlacedMark? {
        return placemarks.find { it.id == id }
    }

    override fun create(placemark: PlacedMark) {
        placemark.validate()

        var newId: Long
        do {
            newId = Random.nextLong(1, Long.MAX_VALUE)
        } while (findOne(newId) != null)

        placemark.id = newId
        placemarks.add(placemark)

        serialize()
    }

    override fun update(placemark: PlacedMark): Boolean {
        placemark.validate()

        val foundPlacemark = findOne(placemark.id)
            ?: return false

        foundPlacemark.title = placemark.title
        foundPlacemark.desc = placemark.desc
        foundPlacemark.x = placemark.x
        foundPlacemark.y = placemark.y
        foundPlacemark.imageUri = placemark.imageUri

        serialize()
        return true
    }

    override fun delete(id: Long): Boolean {
        val foundPlacemark = findOne(id)
            ?: return false

        placemarks.remove(foundPlacemark)
        serialize()

        return true
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(
            placemarks,
            listType
        )

        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)

        if (jsonString.isBlank()) return

        try {
            val restored =
                gsonBuilder.fromJson<ArrayList<PlacedMark>>(
                    jsonString,
                    listType
                )

            placemarks = restored?.toMutableList()
                ?: mutableListOf()

        } catch (e: JsonParseException) {
            Timber.e(e, "Cannot parse placemarks JSON")
        }
    }

    private fun logAll() {
        placemarks.forEach {
            Timber.i("$it")
        }
    }
}
