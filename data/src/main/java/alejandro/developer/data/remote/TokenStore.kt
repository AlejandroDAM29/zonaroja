package alejandro.developer.data.remote

import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class TokenStore @Inject constructor() {
    var token: String? = null
}
