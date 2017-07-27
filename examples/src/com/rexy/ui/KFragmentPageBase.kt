package com.rexy.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.ToggleButton
import com.rexy.common.KBaseFragment
import com.rexy.model.KTestPageTransformer
import com.rexy.widgetlayout.example.R
import com.rexy.widgets.group.PageScrollView

/**
 * TODO:功能说明

 * @author: rexy
 * *
 * @date: 2017-06-05 15:02
 */
open abstract class KFragmentPageBase : KBaseFragment(), CompoundButton.OnCheckedChangeListener {

    protected var mDensity: Float = 0.toFloat()
    protected var mContentVertical: Boolean = false
    protected var mPageTransformer = KTestPageTransformer()

    protected var mToggleAnim: ToggleButton?=null
    protected var mToggleCenter: ToggleButton?=null
    protected var mPageScrollView: PageScrollView? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let { mContentVertical = it.getBoolean(KEY_VERTICAL) }
        mContentVertical = !mContentVertical
        setContentOrientationInner(!mContentVertical, false)
    }


    private val parentContainerFragment: KFragmentPageScrollContainer?
        get() {
            if (parentFragment is KFragmentPageScrollContainer) {
                return parentFragment as KFragmentPageScrollContainer
            }
            if (targetFragment is KFragmentPageScrollContainer) {
                return targetFragment as KFragmentPageScrollContainer
            }
            return null
        }

    override fun onFragmentVisibleChanged(visible: Boolean, fromLifecycle: Boolean) {
        if (visible) {
            parentContainerFragment?.setViewOrientation(mContentVertical)
        }
    }

    fun setContentOrientation(vertical: Boolean) {
        setContentOrientationInner(vertical, false)
    }

    protected open fun initView(root: View) {
        mPageScrollView = root.findViewById(R.id.pageScrollView) as PageScrollView
        mToggleAnim = root.findViewById(R.id.toggleTransform) as ToggleButton
        mToggleCenter = root.findViewById(R.id.toggleChildCenter) as ToggleButton
        mToggleAnim!!.setOnCheckedChangeListener(this)
        mToggleCenter!!.setOnCheckedChangeListener(this)
    }

    protected open fun setContentOrientationInner(vertical: Boolean, init: Boolean): Boolean {
        if (mContentVertical != vertical) {
            mContentVertical = vertical
            mPageScrollView?.let {
                it.orientation = if (vertical) PageScrollView.VERTICAL else PageScrollView.HORIZONTAL
            }
            if (init) {
                adjustTransformAnimation(mToggleAnim!!.isChecked)
                adjustChildLayoutCenter(mToggleCenter!!.isChecked)
            }
            return true
        }
        return false
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mDensity = context!!.resources.displayMetrics.density
    }

    fun adjustTransformAnimation(haveAnim: Boolean) {
        mPageScrollView?.let { it.pageTransformer = if (haveAnim) mPageTransformer else null }
    }

    fun adjustChildLayoutCenter(layoutCenter: Boolean) {
        mPageScrollView?.let { it.isChildCenter = layoutCenter }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (mToggleAnim === buttonView) {
            adjustTransformAnimation(isChecked)
        }
        if (mToggleCenter === buttonView) {
            adjustChildLayoutCenter(isChecked)
        }
    }

    companion object {
        val KEY_VERTICAL = "KEY_VERTICAL"
    }
}