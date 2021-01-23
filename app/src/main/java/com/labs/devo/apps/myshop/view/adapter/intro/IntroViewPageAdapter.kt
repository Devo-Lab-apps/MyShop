package com.labs.devo.apps.myshop.view.adapter.intro

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.view.activity.intro.IntroScreenItem

/**
 * Adapter for the intro activity.
 */
class IntroViewPageAdapter(var mContext: Context?, var mListScreen: List<IntroScreenItem>) :
    PagerAdapter() {


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutScreen: View = inflater.inflate(R.layout.intro_layout_screen, null)
        val imgSlide = layoutScreen.findViewById<ImageView>(R.id.intro_layout_img)
        val title = layoutScreen.findViewById<TextView>(R.id.intro_layout_title)
        val description = layoutScreen.findViewById<TextView>(R.id.intro_layout_description)
        val item = mListScreen[position]
        title.text = item.title
        description.text = item.description
        imgSlide.setImageResource(item.imgId)
        container.addView(layoutScreen)
        return layoutScreen
    }

    override fun getCount(): Int {
        return mListScreen.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}