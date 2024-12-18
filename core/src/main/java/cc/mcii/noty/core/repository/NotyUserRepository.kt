package cc.mcii.noty.core.repository

import cc.mcii.noty.core.model.AuthCredential
import javax.inject.Singleton

/**
 * Network Repository for user authorization of noty.
 */
@Singleton
interface NotyUserRepository {

    /**
     * Register/Create a new user using [username] and [password]
     */
    suspend fun addUser(username: String, password: String): Either<AuthCredential>

    /**
     * Sign ins a user using [username] and [password] which is already exists.
     */
    suspend fun getUserByUsernameAndPassword(
        username: String,
        password: String
    ): Either<AuthCredential>
}
