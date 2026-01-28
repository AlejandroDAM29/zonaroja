package alejandro.developer.data.repositories

import alejandro.developer.data.remote.datasources.CiudadesRemoteDataSource
import alejandro.developer.domain.main.CiudadesRepository
import javax.inject.Inject

class CiudadesRepositoryImpl @Inject constructor(
    private val remoteDataSource: CiudadesRemoteDataSource
) : CiudadesRepository {

    override suspend fun getCiudades(): List<String> =
        remoteDataSource.getCiudades()
}