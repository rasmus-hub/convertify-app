package com.instamedia.convertify.models

import java.io.File

data class VideoFile(
    val file: File,
    val title: String,
    val author: String,
    val platform: Platform,
    val downloadDate: String,
    val format: String
) {
    enum class Platform(val displayName: String, val iconRes: String) {
        TIKTOK("TikTok", "tiktok_icon"),
        YOUTUBE("YouTube", "youtube_icon"),
        INSTAGRAM("Instagram", "instagram_icon"),
        FACEBOOK("Facebook", "facebook_icon"),
        UNKNOWN("Unknown", "video_icon")
    }

    companion object {
        fun fromFileName(file: File): VideoFile? {
            val fileName = getFileNameWithoutExtension(file)

            // Formato esperado: video_convertify_tiktok_${author}_${timestamp}
            if (fileName.indexOf("convertify") == -1) {
                return null
            }

            val parts = fileName.split("_".toRegex())
            if (parts.size < 4) {
                return null
            }

            return try {
                val platformName = parts[2].toLowerCase(java.util.Locale.getDefault())
                val platform = when (platformName) {
                    "tiktok" -> Platform.TIKTOK
                    "youtube" -> Platform.YOUTUBE
                    "instagram" -> Platform.INSTAGRAM
                    "facebook" -> Platform.FACEBOOK
                    else -> Platform.UNKNOWN
                }

                val author = parts[3]
                val timestamp = if (parts.size > 4) {
                    try {
                        java.lang.Long.parseLong(parts[4])
                    } catch (e: NumberFormatException) {
                        file.lastModified()
                    }
                } else {
                    file.lastModified()
                }
                val downloadDate = formatDate(timestamp)
                val format = getFileExtension(file).toUpperCase(java.util.Locale.getDefault())

                // Generar título basado en el nombre del archivo o usar un título por defecto
                val title = generateTitle(fileName, platform, author)

                VideoFile(
                    file = file,
                    title = title,
                    author = author,
                    platform = platform,
                    downloadDate = downloadDate,
                    format = format
                )
            } catch (e: Exception) {
                null
            }
        }

        private fun getFileNameWithoutExtension(file: File): String {
            val fileName = file.name
            val dotIndex = fileName.lastIndexOf('.')
            return if (dotIndex > 0) {
                fileName.substring(0, dotIndex)
            } else {
                fileName
            }
        }

        private fun getFileExtension(file: File): String {
            val fileName = file.name
            val dotIndex = fileName.lastIndexOf('.')
            return if (dotIndex > 0 && dotIndex < fileName.length - 1) {
                fileName.substring(dotIndex + 1)
            } else {
                ""
            }
        }

        private fun formatDate(timestamp: Long): String {
            val dateFormat = java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.getDefault())
            return "Downloaded " + dateFormat.format(java.util.Date(timestamp))
        }

        private fun generateTitle(fileName: String, platform: Platform, author: String): String {
            // Si el nombre del archivo tiene más información, úsala para el título
            // De lo contrario, genera un título genérico
            return platform.displayName + " video by " + author
        }
    }
}