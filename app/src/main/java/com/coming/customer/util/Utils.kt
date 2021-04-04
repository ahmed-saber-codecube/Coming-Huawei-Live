package com.evente.customer.utils

import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import com.coming.customer.core.AppCommon
import com.coming.customer.di.App
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


object Utils {

    var thumbColumns = arrayOf(MediaStore.Video.Thumbnails.DATA)
    var mediaColumns = arrayOf(MediaStore.Video.Media._ID)

    private var screenWidth = 0
    private var screenHeight = 0

    val isAndroid5: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP


    val filename: String
        get() {
            val file = File(App.appContext!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path, "bidToStay/Images")
            if (!file.exists()) {
                file.mkdirs()
            }
            return file.absolutePath + "/" + System.currentTimeMillis() + ".jpg"

        }

    fun printHashKey(pContext: Context) {
        try {
            val info: PackageInfo = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.e("----> ", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("----> ", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("----> ", "printHashKey()", e)
        }
    }

    fun captializeAllFirstLetter(name: String): String? {
        val array = name.toCharArray()
        array[0] = Character.toUpperCase(array[0])
        for (i in 1 until array.size) {
            if (Character.isWhitespace(array[i - 1])) {
                array[i] = Character.toUpperCase(array[i])
            }
        }
        return String(array)
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun getScreenHeight(c: Context): Int {
        if (screenHeight == 0) {
            val wm = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            screenHeight = size.y
        }

        return screenHeight
    }

    fun getScreenWidth(c: Context): Int {
        if (screenWidth == 0) {
            val wm = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            screenWidth = size.x
        }

        return screenWidth
    }


    fun getPathFromUri(uri: Uri, context: Context): String? {


        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.MIME_TYPE)

        val cursor = context.contentResolver.query(uri, columns, null, null, null)

        if (cursor != null) {
            cursor.moveToFirst()
            val pathColumnIndex = cursor.getColumnIndex(columns[0])
            val contentPath = cursor.getString(pathColumnIndex)
            cursor.close()
            return contentPath
        }

        return null
    }


    fun saveFile(data: ByteArray): File? {
        var file: File? = null
        try {


            file = File(
                App.appContext!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/" + AppCommon.DIRECTORY,
                "bidToStay_" + System.currentTimeMillis() + "_.jpeg"
            )
            file.parentFile.mkdirs()
            val imageFileOS = FileOutputStream(file)
            imageFileOS.write(data)
            imageFileOS.flush()
            imageFileOS.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
    }

    fun createNewVideoFile(): String {
        val file = File(
            App.appContext!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + File.separator + "bidToStay",
            "snap_" + System.currentTimeMillis() + ".mp4"
        )
        file.parentFile.mkdirs()
        return file.path
    }

    fun saveImage(bitmap: Bitmap): File? {
        try {

            val file = File(
                App.appContext!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/" + AppCommon.DIRECTORY,
                "thumb_" + System.currentTimeMillis() + "_.jpeg"
            )
            file.parentFile.mkdirs()
            val outputStream = BufferedOutputStream(FileOutputStream(file))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            return file

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return null
    }

    fun resizeBitMapImage(filePath: String, targetWidth: Int, targetHeight: Int): Bitmap? {
        var bitMapImage: Bitmap? = null
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)
            var sampleSize = 0.0
            val scaleByHeight =
                Math.abs(options.outHeight - targetHeight) >= Math.abs(options.outWidth - targetWidth)
            if (options.outHeight * options.outWidth * 2 >= 1638) {
                sampleSize =
                    (if (scaleByHeight) options.outHeight / targetHeight else options.outWidth / targetWidth).toDouble()
                sampleSize = Math.pow(2.0, Math.floor(Math.log(sampleSize) / Math.log(2.0))).toInt()
                    .toDouble()
            }
            options.inJustDecodeBounds = false
            options.inTempStorage = ByteArray(128)
            while (true) {
                try {
                    options.inSampleSize = sampleSize.toInt()
                    bitMapImage = BitmapFactory.decodeFile(filePath, options)
                    break
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    try {
                        sampleSize = sampleSize * 2
                    } catch (ex1: Exception) {
                        ex1.printStackTrace()
                    }

                }

            }
        } catch (ex: Exception) {

            ex.printStackTrace()
        }

        return bitMapImage
    }


    /*public static File compressImage(String filePath) {


        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        try {

            File file = new File(Environment.getExternalStorageDirectory() + "/"+Commons.DIRECTORY, "image_" + System.currentTimeMillis() + "_.jpeg");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            boolean isCompress = bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
            if (isCompress)
                bitmap.recycle();
            return file;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }*/


    fun compressImage(filePath: String): String {

        var scaledBitmap: Bitmap? = null

        val options = BitmapFactory.Options()

        //      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)

        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

        //      max Height and width values of the compressed image is taken as 816x612

        val maxHeight = 1920.0f//1280.0f;//816.0f;
        val maxWidth = 1080.0f//852.0f;//612.0f;

        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight

        //      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()

            }
        }

        //      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

        //      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

        //      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)

        try {
            //          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f

        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )

        //      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath)

            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            Log.e("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
                Log.e("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180f)
                Log.e("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270f)
                Log.e("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var out: FileOutputStream? = null
        val filename = filename
        try {
            out = FileOutputStream(filename)

            //          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return filename
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }

        return inSampleSize
    }


    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    fun getPath(context: Context, uri: Uri): String? {

        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return App.appContext!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {

                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)

        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null)
                cursor.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }


    fun getFileFromStorage(uri: Uri, context: Context): String? {

        var isImageFromGoogleDrive = false
        var imgPath: String? = null
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.externalstorage.documents" == uri.authority) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    imgPath = App.appContext!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/" + split[1]
                } else {
                    val DIR_SEPORATOR = Pattern.compile("/")
                    val rv = HashSet<String>()
                    val rawExternalStorage = System.getenv("EXTERNAL_STORAGE")
                    val rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE")
                    val rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET")
                    if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
                        if (TextUtils.isEmpty(rawExternalStorage)) {
                            rv.add("/storage/sdcard0")
                        } else {
                            if (rawExternalStorage != null) {
                                rv.add(rawExternalStorage)
                            }
                        }
                    } else {
                        val rawUserId: String
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            rawUserId = ""
                        } else {
                            val path = App.appContext!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
                            val folders = DIR_SEPORATOR.split(path)
                            val lastFolder = folders[folders.size - 1]
                            var isDigit = false
                            try {
                                Integer.valueOf(lastFolder)
                                isDigit = true
                            } catch (ignored: NumberFormatException) {
                            }

                            rawUserId = if (isDigit) lastFolder else ""
                        }
                        if (TextUtils.isEmpty(rawUserId)) {
                            if (rawEmulatedStorageTarget != null) {
                                rv.add(rawEmulatedStorageTarget)
                            }
                        } else {
                            rv.add(rawEmulatedStorageTarget + File.separator + rawUserId)
                        }
                    }
                    if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                        val rawSecondaryStorages =
                            rawSecondaryStoragesStr?.split(File.pathSeparator.toRegex())
                                ?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                        if (rawSecondaryStorages != null)
                            Collections.addAll(rv, *rawSecondaryStorages)
                    }
                    val temp = rv.toTypedArray()
                    for (i in temp.indices) {
                        val tempf = File(temp[i] + "/" + split[1])
                        if (tempf.exists()) {
                            imgPath = temp[i] + "/" + split[1]
                        }
                    }
                }
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )

                var cursor: Cursor? = null
                val column = "_data"
                val projection = arrayOf(column)
                try {
                    cursor = context.contentResolver.query(contentUri, projection, null, null, null)
                    if (cursor != null && cursor.moveToFirst()) {
                        val column_index = cursor.getColumnIndexOrThrow(column)
                        imgPath = cursor.getString(column_index)
                    }
                } finally {
                    if (cursor != null)
                        cursor.close()
                }
            } else if ("com.android.providers.media.documents" == uri.authority) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                var cursor: Cursor? = null
                val column = "_data"
                val projection = arrayOf(column)

                try {
                    cursor = context.contentResolver.query(
                        contentUri!!,
                        projection,
                        selection,
                        selectionArgs,
                        null
                    )
                    if (cursor != null && cursor.moveToFirst()) {
                        val column_index = cursor.getColumnIndexOrThrow(column)
                        imgPath = cursor.getString(column_index)
                    }
                } finally {
                    if (cursor != null)
                        cursor.close()
                }
            } else if ("com.google.android.apps.docs.storage" == uri.authority) {
                isImageFromGoogleDrive = true
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)

            try {
                cursor = context.contentResolver.query(uri, projection, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val column_index = cursor.getColumnIndexOrThrow(column)
                    imgPath = cursor.getString(column_index)
                }
            } finally {
                if (cursor != null)
                    cursor.close()
            }

            if ("com.google.android.apps.docs.storage" == uri.authority) {
                isImageFromGoogleDrive = true
            }

        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            imgPath = uri.path
        }

        if (isImageFromGoogleDrive) {
            try {

                val bitmap =
                    BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                return saveImage(bitmap)!!.absolutePath

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        return imgPath
    }

    fun isVideoFile(url: String?): Boolean {

        if (url == null) return false

        var extention = ""
        val i = url.lastIndexOf('.')

        if (i > 1) {
            extention = url.substring(i)
            if (extention.length > 5)
                extention = extention.substring(0, 5)
        }

        return extention.toLowerCase().contains("mp4")
    }

    fun isImageFile(url: String?): Boolean {

        if (url == null) return false

        var extention = ""
        val i = url.lastIndexOf('.')

        if (i > 1) {
            extention = url.substring(i)
            if (extention.length > 5)
                extention = extention.substring(0, 5)
        }

        return extention.toLowerCase().contains("jpg") ||
                extention.toLowerCase().contains("jpeg") || extention.toLowerCase().contains("png")
    }

    fun convertTOLocal(utcDate: String): String {
        try {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            df.timeZone = TimeZone.getTimeZone("UTC")
            val date = df.parse(utcDate)
            df.timeZone = TimeZone.getDefault()
            df.applyPattern("dd MMM yy, h:mm a")
            return df.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }

    }

    fun convertTOLocal(utcDate: String, fromPattern: String, toPattern: String): String {
        try {
            val df = SimpleDateFormat(fromPattern)
            df.timeZone = TimeZone.getTimeZone("UTC")
            val date = df.parse(utcDate)
            df.timeZone = TimeZone.getDefault()
            df.applyPattern(toPattern)
            return df.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }

    }


    fun saveImage(bitmap: Bitmap, orientation: Int): File? {
        var bitmap = bitmap
        try {

            val file = File(
                App.appContext!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/" + AppCommon.DIRECTORY,
                System.currentTimeMillis().toString() + ".jpeg"
            )
            file.parentFile.mkdirs()
            val outputStream = BufferedOutputStream(FileOutputStream(file))


            if (orientation != -1 && orientation != 0) {

                val matrix = Matrix()
                matrix.postRotate(orientation.toFloat())
                bitmap = Bitmap.createBitmap(
                    bitmap, 0, 0,
                    bitmap.width, bitmap.height, matrix,
                    true
                )
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            return file

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return null
    }


    fun blurBitmapWithRenderscript(rs: RenderScript, bitmap2: Bitmap) {
        // this will blur the bitmapOriginal with a radius of 25
        // and save it in bitmapOriginal
        // use this constructor for best performance, because it uses
        // USAGE_SHARED mode which reuses memory
        val input = Allocation.createFromBitmap(rs, bitmap2)
        val output = Allocation.createTyped(
            rs,
            input.type
        )
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        // must be >0 and <= 25
        script.setRadius(25f)
        script.setInput(input)
        script.forEach(output)
        output.copyTo(bitmap2)
    }


}