package com.example.notificationlistener

import android.app.Notification
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class NotificationListenerService : NotificationListenerService() {
    
    companion object {
        private const val TAG = "NotificationListener"
        var instance: NotificationListenerService? = null
    }
    
    var notificationCount = 0
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d(TAG, "NotificationListenerService created")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        instance = null
        Log.d(TAG, "NotificationListenerService destroyed")
    }
    
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        notificationCount++
        
        try {
            // 通知データの抽出
            val notificationData = extractNotificationData(sbn)
            
            // ログ出力
            logNotificationData(notificationData)
            
            // JSON形式で詳細ログ
            val jsonData = createNotificationJson(notificationData)
            Log.d(TAG, "Notification JSON: $jsonData")
            
            // 特定アプリの通知を処理
            processTargetNotifications(notificationData)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error processing notification", e)
        }
    }
    
    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val appName = getAppName(packageName)
        Log.d(TAG, "Notification removed: $appName ($packageName)")
    }
    
    private fun extractNotificationData(sbn: StatusBarNotification): NotificationData {
        val packageName = sbn.packageName
        val appName = getAppName(packageName)
        val notification = sbn.notification
        val extras = notification.extras
        
        // 基本情報
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString() ?: ""
        
        // 内容の決定（bigTextがあればそれを優先）
        val content = when {
            bigText.isNotEmpty() -> bigText
            text.isNotEmpty() -> text
            else -> ""
        }
        
        // 重要度とカテゴリ
        val priority = notification.priority
        val category = notification.category ?: "unknown"
        
        return NotificationData(
            id = sbn.id.toString(),
            packageName = packageName,
            appName = appName,
            title = title,
            content = content,
            subText = subText,
            timestamp = sbn.postTime,
            priority = priority,
            category = category
        )
    }
    
    private fun getAppName(packageName: String): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }
    
    private fun logNotificationData(data: NotificationData) {
        val timestamp = dateFormat.format(Date(data.timestamp))
        
        Log.i(TAG, "=== NEW NOTIFICATION ===")
        Log.i(TAG, "App: ${data.appName} (${data.packageName})")
        Log.i(TAG, "Title: ${data.title}")
        Log.i(TAG, "Content: ${data.content}")
        if (data.subText.isNotEmpty()) {
            Log.i(TAG, "SubText: ${data.subText}")
        }
        Log.i(TAG, "Priority: ${getPriorityString(data.priority)}")
        Log.i(TAG, "Category: ${data.category}")
        Log.i(TAG, "Time: $timestamp")
        Log.i(TAG, "========================")
    }
    
    private fun createNotificationJson(data: NotificationData): String {
        val json = JSONObject()
        json.put("id", data.id)
        json.put("packageName", data.packageName)
        json.put("appName", data.appName)
        json.put("title", data.title)
        json.put("content", data.content)
        json.put("subText", data.subText)
        json.put("timestamp", data.timestamp)
        json.put("priority", data.priority)
        json.put("category", data.category)
        json.put("ledColor", getLEDColor(data.packageName))
        json.put("ledPattern", getLEDPattern(data.priority))
        
        return json.toString()
    }
    
    private fun processTargetNotifications(data: NotificationData) {
        // 対象アプリの通知のみ処理
        val targetApps = setOf(
            "com.whatsapp",
            "com.google.android.gm",
            "com.android.dialer",
            "com.spotify.music",
            "com.discord",
            "com.twitter.android",
            "com.facebook.orca" // Messenger
        )
        
        if (targetApps.contains(data.packageName)) {
            Log.w(TAG, "🎯 TARGET NOTIFICATION: ${data.appName} - ${data.title}")
            
            // ここで LED制御システムに送信
            sendToLEDController(data)
        }
    }
    
    private fun sendToLEDController(data: NotificationData) {
        // LED制御システムへの送信をシミュレート
        val ledColor = getLEDColor(data.packageName)
        val ledPattern = getLEDPattern(data.priority)
        
        Log.w(TAG, "💡 LED Control: Color=$ledColor, Pattern=$ledPattern")
    }
    
    private fun getLEDColor(packageName: String): String {
        return when (packageName) {
            "com.whatsapp" -> "#25D366"         // WhatsApp緑
            "com.google.android.gm" -> "#EA4335" // Gmail赤
            "com.android.dialer" -> "#FF0000"   // 電話赤
            "com.spotify.music" -> "#1DB954"    // Spotify緑
            "com.discord" -> "#5865F2"          // Discord紫
            "com.twitter.android" -> "#1DA1F2"  // Twitter青
            "com.facebook.orca" -> "#0084FF"    // Messenger青
            else -> "#FFFFFF"                   // デフォルト白
        }
    }
    
    private fun getLEDPattern(priority: Int): String {
        return when (priority) {
            Notification.PRIORITY_HIGH -> "fast_blink"
            Notification.PRIORITY_DEFAULT -> "slow_blink"
            Notification.PRIORITY_LOW -> "solid"
            else -> "fade"
        }
    }
    
    private fun getPriorityString(priority: Int): String {
        return when (priority) {
            Notification.PRIORITY_MIN -> "MIN"
            Notification.PRIORITY_LOW -> "LOW"
            Notification.PRIORITY_DEFAULT -> "DEFAULT"
            Notification.PRIORITY_HIGH -> "HIGH"
            Notification.PRIORITY_MAX -> "MAX"
            else -> "UNKNOWN($priority)"
        }
    }
}

// 通知データクラス
data class NotificationData(
    val id: String,
    val packageName: String,
    val appName: String,
    val title: String,
    val content: String,
    val subText: String,
    val timestamp: Long,
    val priority: Int,
    val category: String
)