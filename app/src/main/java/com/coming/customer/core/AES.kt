package com.coming.customer.core

import android.util.Base64
import okio.ByteString
import java.io.UnsupportedEncodingException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by hlink Team
 */
@Singleton
class AES @Inject
constructor(@Named("aes-key") private val encKey: String) {


    private val keyHash: ByteArray
    private val key: ByteArray = ByteArray(32)
    private val iv: ByteArray = ByteArray(16)

    init {

        // Generate Key Hash using generateSHA256 Algorithm
        keyHash = generateSHA256(encKey, 32)
        // copy first 32 bits of hash key to key
        System.arraycopy(keyHash, 0, key, 0, 32)
        // copy first 16 bits of plain key to iv
        System.arraycopy(encKey.toByteArray(charset("UTF-8")), 0, iv, 0, 16)


    }

    fun encrypt(plainText: String?): String? {
        try {
            if (plainText != null) {
                val plainTextBytes = plainText.toByteArray(charset("UTF-8"))
                return String(encrypt(plainTextBytes)!!)
            }

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return null
    }


    fun encrypt(plainTextBytes: ByteArray): ByteArray? {

        try {

            val keySpec = SecretKeySpec(key, "AES")
            val ivSpec = IvParameterSpec(iv)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            val enc = cipher!!.doFinal(plainTextBytes)
            return ByteString.of(*enc).base64().toByteArray()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        }

        return null
    }


    fun decrypt(cypherText: String): String? {

        try {
            return String(decrypt(cypherText.toByteArray(charset("UTF-8")))!!)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return null
    }

    fun decrypt(cypherText: ByteArray?): ByteArray? {

        try {
            if (cypherText != null) {

                val keySpec = SecretKeySpec(key, "AES")
                val ivSpec = IvParameterSpec(iv)
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
                val bytes = Base64.decode(cypherText, Base64.DEFAULT)
                if (bytes != null) {
                    return cipher!!.doFinal(bytes)
                }
            }

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        }
        return null
    }

    /***
     * This function computes the SHA256 hash of input string
     * @param text input text whose generateSHA256 hash has to be computed
     * @param length length of the text to be returned
     * @return returns generateSHA256 hash of input text
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
    private fun generateSHA256(text: String, length: Int): ByteArray {

        val resultStr: String
        val md = MessageDigest.getInstance("SHA-256")

        md.update(text.toByteArray(charset("UTF-8")))
        val digest = md.digest()

        val result = StringBuilder()
        for (b in digest) {
            result.append(String.format("%02x", b)) //convert to hex
        }
        //return result.toString();

        if (length > result.toString().length) {
            resultStr = result.toString()
        } else {
            resultStr = result.toString().substring(0, length)
        }

        return resultStr.toByteArray(charset("UTF-8"))
    }

}