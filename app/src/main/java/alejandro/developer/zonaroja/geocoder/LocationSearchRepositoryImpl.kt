package alejandro.developer.zonaroja.geocoder

import alejandro.developer.domain.main.LocationSearchRepository
import android.content.Context
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationSearchRepositoryImpl(
    private val context: Context
) : LocationSearchRepository {

    override suspend fun searchCity(query: String): Pair<Double, Double>? {

        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context)
                val results = geocoder.getFromLocationName(query, 1)

                val location = results?.firstOrNull()

                location?.let {
                    Pair(it.latitude, it.longitude)
                }

            } catch (e: Exception) {
                null
            }
        }
    }
}