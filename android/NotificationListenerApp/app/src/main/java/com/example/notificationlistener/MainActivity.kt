package com.example.notificationlistener

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    
    private lateinit var statusTextView: TextView
    private lateinit var permissionButton: Button
    private lateinit var notificationCountText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // ビューの初期化
        statusTextView = findViewById(R.id.statusTextView)
        permissionButton = findViewById(R.id.permissionButton)
        notificationCountText = findViewById(R.id.notificationCountText)
        
        // ボタンクリックリスナー
        permissionButton.setOnClickListener {
            openNotificationSettings()
        }
        
        // 他の権限チェック
        checkOtherPermissions()
        
        // 初期状態更新
        updatePermissionStatus()
    }
    
    override fun onResume() {
        super.onResume()
        updatePermissionStatus()
        updateNotificationCount()
    }
    
    private fun isNotificationServiceEnabled(): Boolean {
        val packageName = packageName
        val flat = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        )
        
        if (!flat.isNullOrEmpty()) {
            val names = flat.split(":").toTypedArray()
            for (name in names) {
                val componentName = ComponentName.unflattenFromString(name)
                if (componentName != null) {
                    if (packageName == componentName.packageName) {
                        return true
                    }
                }
            }
        }
        return false
    }
    
    private fun updatePermissionStatus() {
        if (isNotificationServiceEnabled()) {
            statusTextView.text = "✅ 通知アクセス許可: 有効"
            statusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            permissionButton.text = "設定を開く"
        } else {
            statusTextView.text = "❌ 通知アクセス許可: 無効"
            statusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            permissionButton.text = "許可を有効にする"
        }
    }
    
    private fun updateNotificationCount() {
        // 一時的にコメントアウト
        // val service = NotificationListenerService.instance
        // val count = service?.notificationCount ?: 0
        notificationCountText.text = "検出した通知数: 準備中"
    }
    
    private fun openNotificationSettings() {
        val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        startActivity(intent)
    }
    
    private fun checkOtherPermissions() {
        // 必要に応じて他の権限もチェック
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                arrayOf(Manifest.permission.WAKE_LOCK), 1)
        }
    }
}