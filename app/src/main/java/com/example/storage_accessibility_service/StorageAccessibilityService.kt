package com.example.storage_accessibility_service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo





class StorageAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> runSaf(event)
        }
    }

    override fun onInterrupt() {

    }

    private fun runSaf(event: AccessibilityEvent) {
        // Checks if the window open is SAF
        if (event.packageName == "com.google.android.documentsui") {
            // Gets the page XML
            val currentNode = event.source

            val checkedNode = currentNode.getChild(0).getChild(0).getChild(0).getChild(6).getChild(1)
            if (checkedNode.className == "android.widget.Button" && checkedNode.text.toString() == "SAVE") {
                checkedNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

//            saveButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            dfs(currentNode, "")
//            Log.d("BeatriceLog", "currentNode: $currentNode")
        }
    }

    fun dfs(info: AccessibilityNodeInfo?, level: String) {
        if (info == null) return
//        if (info.text != null && info.text.length > 0) Log.d("BeatriceLog", info.text.toString() + " class: " + info.className + " level: " + level )
        if (info.text != null) Log.d("BeatriceLog", info.text.toString() + " class: " + info.className + " level: " + level )
        for (i in 0 until info.childCount) {
            val child = info.getChild(i)
            dfs(child, "$level -> $i")
            child?.recycle()
        }
    }
}