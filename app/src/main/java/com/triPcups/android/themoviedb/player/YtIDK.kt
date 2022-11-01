package com.triPcups.android.themoviedb.player

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.lang.ref.WeakReference

//interface YouTubeLifecycleEventObserver : LifecycleEventObserver {
//
//    val youTubePlayerViewRef: WeakReference<YouTubePlayerView?>?
//
//    override fun onStateChanged(lifecycleOwner: LifecycleOwner, event: Lifecycle.Event) {
//        Log.d("wow", "onStateChanged: $event")
//        when(event) {
//            Lifecycle.Event.ON_CREATE -> {
//
//            }
//            Lifecycle.Event.ON_START -> {
//                youTubePlayerViewRef?.get()?.let {
//                    lifecycleOwner.lifecycle.addObserver(it)
//                }
//            }
//            Lifecycle.Event.ON_RESUME -> {
//
//            }
//            Lifecycle.Event.ON_PAUSE -> {
////                youTubePlayerView?.get()?.play
//            }
//            Lifecycle.Event.ON_STOP -> {
//
//            }
//            Lifecycle.Event.ON_DESTROY -> {
//                youTubePlayerViewRef?.get()?.let {
//                    lifecycleOwner.lifecycle.removeObserver(it)
//                }
//            }
//            Lifecycle.Event.ON_ANY -> {
//
//            }
//        }
//    }
//
//}