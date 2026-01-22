package alejandro.developer.domain.repositories

interface TextRepository {
   suspend fun getTexts(): List<String>
}