package com.example.meplayermusic.ui.musiclist.viewpager

import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    val fragmentList: MutableList<Fragment> = mutableListOf()
    val titleList: MutableList<String> = mutableListOf()
    val iconList: MutableList<Drawable> = mutableListOf()

    fun getTitle(position: Int) = titleList[position]

    fun getIcon(position: Int) = iconList[position]

    fun addFragment(fragment: Fragment, title: String, icon: Drawable) {
        fragmentList.add(fragment)
        titleList.add(title)
        iconList.add(icon)
    }

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]

}