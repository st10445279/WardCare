package com.wardcare.app.security

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordHasher {
    private const val COST = 10

    /**
     * Hashes a plaintext password using BCrypt with a cost factor of 10.
     */
    fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(COST, password.toCharArray())
    }

    /**
     * Verifies a plaintext password against a stored BCrypt hash.
     */
    fun verifyPassword(password: String, hash: String): Boolean {
        val result = BCrypt.verifyer().verify(password.toCharArray(), hash.toCharArray())
        return result.verified
    }
}
