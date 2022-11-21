package com.example.storage_accessibility_service

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class StorageAccessibilityService : AccessibilityService() {
    val mBroadcastReadAction = "com.accessibilityservice.broadcast.read"
    var action:String? = null
    var filename:String? = null
    var lastEventSource: AccessibilityNodeInfo? = null

    override fun onServiceConnected() {
        val mIntentFilter = IntentFilter()
        mIntentFilter.addAction(mBroadcastReadAction)

        registerReceiver(mReceiver, mIntentFilter)
        super.onServiceConnected()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d("BeatriceLog", "Event type ${event.eventType} and package name ${event.packageName} and source ${event.source}")
//        if (event.source != null) {
//            lastEventSource = event.source
//        }
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> runSaf(event)
        }
    }

    override fun onInterrupt() {

    }

    private fun runSaf(event: AccessibilityEvent) {
        // TODO figure out why currentNode is null
        // Checks if the window open is SAF
        if (event.packageName == "com.google.android.documentsui") {
            // Gets the page XML
            var currentNode = rootInActiveWindow

            Log.d("BeatriceLog", "filename $filename and current node $currentNode")

            if (currentNode == null) {
                Log.d("BeatriceLog", "Used last node")
                return
            }
//            val checkedNode = currentNode.getChild(0).getChild(0).getChild(0).getChild(6).getChild(1)
//            if (checkedNode.className == "android.widget.Button" && checkedNode.text.toString() == "SAVE") {
//                checkedNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            }

//            saveButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            var nodeChilds = currentNode.getChild(0)
            if (nodeChilds.className == "android.view.ViewGroup") {
                nodeChilds = nodeChilds.getChild(0)
                if (nodeChilds.className == "androidx.drawerlayout.widget.DrawerLayout") {
                    nodeChilds = nodeChilds.getChild(0)
                    if (nodeChilds.className == "android.view.ViewGroup") {
                        nodeChilds = nodeChilds.getChild(5)
                        if (nodeChilds.className == " androidx.recyclerview.widget.RecyclerView") {
                            var tempNodeChild = nodeChilds
                            for (i in 1..nodeChilds.childCount) {
                                tempNodeChild = nodeChilds.getChild(i - 1)
                                if (tempNodeChild.className == "androidx.cardview.widget.CardView") {
                                    tempNodeChild = tempNodeChild.getChild(1)
                                    if (tempNodeChild.className == "android.widget.TextView" && tempNodeChild.text == filename) {
                                        Log.d("BeatriceLog", "Reach inside")
                                        tempNodeChild.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                    }
                                }
                            }
                        }
                    }
                }
            }
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

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.d("BeatriceLog", "On receive called")
            if (intent.action == mBroadcastReadAction) {
                if (intent.hasExtra("action")) {
                    val b: Bundle? = intent.extras
                    action = b!!.getString("action")
                }
                if (intent.hasExtra("filename")) {
                    val b: Bundle? = intent.extras
                    filename = b!!.getString("filename")
                }
            }
//            val stopIntent = Intent(
//                this@MainActivity,
//                BroadcastService::class.java
//            )
//            stopService(stopIntent)
        }
    }
}