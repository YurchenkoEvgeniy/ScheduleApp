package com.sfedu.scheduleapp.ui
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.sfedu.scheduleapp.R
import com.sfedu.scheduleapp.mvp.DBHelper


abstract class DayFragment : Fragment() {
    protected lateinit var lessonViews: Array<TextView>
    protected lateinit var lessonViewsExtra: Array<TextView>
    protected lateinit var LinearLayouts: Array<LinearLayout>
    protected lateinit var tabLayout: TabLayout
    protected lateinit var spin: Spinner
    protected lateinit var dbHelper: DBHelper

    abstract fun getLayoutResId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(getLayoutResId(), container, false)
        initViews(view)
        return view

    }
    private fun initViews(view: View) {
        lessonViews = arrayOf(
            view.findViewById(R.id.textViewInfo1),
            view.findViewById(R.id.textViewInfo2),
            view.findViewById(R.id.textViewInfo3),
            view.findViewById(R.id.textViewInfo4),
            view.findViewById(R.id.textViewInfo5)
        )

        lessonViewsExtra = arrayOf(
            view.findViewById(R.id.textViewInfo1_1),
            view.findViewById(R.id.textViewInfo2_1),
            view.findViewById(R.id.textViewInfo3_1),
            view.findViewById(R.id.textViewInfo4_1),
            view.findViewById(R.id.textViewInfo5_1)
        )

        LinearLayouts = arrayOf(
            view.findViewById(R.id.LinearLayout1),
            view.findViewById(R.id.LinearLayout2),
            view.findViewById(R.id.LinearLayout3),
            view.findViewById(R.id.LinearLayout4),
            view.findViewById(R.id.LinearLayout5)
        )

        tabLayout = requireActivity().findViewById(R.id.tab_layout)
        spin = requireActivity().findViewById(R.id.spinner)
    }

    private var isFragmentActive = true

    override fun onDetach() {
        super.onDetach()
        isFragmentActive = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DBHelper(requireContext())
        setupTabListener()
        setupSpinnerListener()
        updateData()
    }

    private fun setupTabListener() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                updateData()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupSpinnerListener() {
        spin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateData()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    protected fun updateData() {
        val day = when (tabLayout.selectedTabPosition) {
            0 -> "MONDAY"
            1 -> "TUESDAY"
            2 -> "WEDNESDAY"
            3 -> "THURSDAY"
            4 -> "FRIDAY"
            5 -> "SATURDAY"
            else -> "MONDAY"
        }
        loadLessons(day)
    }

    private fun loadLessons(day: String) {
        if (!isFragmentActive || !isAdded) return
        val spin_item = spin.selectedItem.toString()
        val cursor = dbHelper.getLessons("'$spin_item'", day)

        cursor?.use {
            if (it.moveToFirst()) {
                val column = it.getColumnIndex("group_1")

                for (i in lessonViews.indices) {
                    try {
                        val lessonText = it.getString(column)
                        // Задаю вид для для разных недель/подгрупп
                        if ("all_ud" in lessonText) {
                            LinearLayouts[i].background = null
                            lessonViews[i].gravity = Gravity.CENTER
                            lessonViewsExtra[i].visibility = View.GONE
                            //LinearLayouts[i].orientation = LinearLayout.HORIZONTAL



                            lessonViews[i].text = lessonText.substring(7)
                        }
                        else if ("all_up" in lessonText) {
                            LinearLayouts[i].background = ContextCompat.getDrawable(requireContext(), R.drawable.diagonal_line)
                            lessonViews[i].gravity = Gravity.START or Gravity.TOP
                            if ("all_down" in lessonText) {
                                //LinearLayouts[i].orientation = LinearLayout.VERTICAL
                                lessonViewsExtra[i].visibility = View.VISIBLE
                                lessonViews[i].text = lessonText.substring(7, lessonText.indexOf('&') - 1)
                                lessonViewsExtra[i].text = lessonText.substring(lessonText.indexOf("all_down") + 9)
                                lessonViewsExtra[i].gravity = Gravity.END or Gravity.BOTTOM
                            }
                            else {
                                lessonViews[i].text = lessonText.substring(7)
                                lessonViewsExtra[i].text = ""
                            }
                        }
                        else if("all_down" in lessonText) {
                            LinearLayouts[i].background = ContextCompat.getDrawable(requireContext(), R.drawable.diagonal_line)
                            //lessonViews[i].gravity = Gravity.START
                            lessonViews[i].text = ""
                            lessonViewsExtra[i].visibility = View.VISIBLE
                            lessonViewsExtra[i].text = lessonText.substring(9)
                            lessonViewsExtra[i].gravity = Gravity.END or Gravity.BOTTOM
                        }
                        else if ("pt1" in lessonText) {
                            lessonViewsExtra[i].visibility = View.VISIBLE
                            lessonViewsExtra[i].text = "---"
                            LinearLayouts[i].background = null
                            if ("pt2" in lessonText) {
                                if ("pt1_ud" in lessonText) {
                                    lessonViews[i].gravity = Gravity.CENTER
                                    lessonViews[i].text = lessonText.substring(6, lessonText.indexOf("&") - 1)
                                } else if ("pt1_up" in lessonText) {
                                    lessonViews[i].gravity = Gravity.START or Gravity.TOP
                                    lessonViews[i].text = lessonText.substring(6, lessonText.indexOf("&") - 1)
                                } else if ("pt1_down" in lessonText) {
                                    lessonViews[i].gravity = Gravity.END or Gravity.BOTTOM
                                    lessonViews[i].text = lessonText.substring(8, lessonText.indexOf("&") - 1)
                                }

                                if ("pt2_ud" in lessonText) {
                                    lessonViewsExtra[i].gravity = Gravity.CENTER
                                    lessonViewsExtra[i].text = lessonText.substring(lessonText.indexOf("pt2_ud") + 7)
                                } else if ("pt2_up" in lessonText) {
                                    lessonViewsExtra[i].gravity = Gravity.START or Gravity.TOP
                                    lessonViewsExtra[i].text = lessonText.substring(lessonText.indexOf("pt2_up") + 7)
                                } else if ("pt2_down" in lessonText) {
                                    lessonViewsExtra[i].gravity = Gravity.END or Gravity.BOTTOM
                                    lessonViewsExtra[i].text = lessonText.substring(lessonText.indexOf("pt2_down") +9)
                                }
                            }
                            else {
                                if ("pt1_ud" in lessonText) {
                                    lessonViews[i].gravity = Gravity.CENTER
                                    lessonViews[i].text = lessonText.substring(6)
                                } else if ("pt1_up" in lessonText) {
                                    lessonViews[i].gravity = Gravity.START or Gravity.TOP
                                    lessonViews[i].text = lessonText.substring(6)
                                } else if ("pt1_down" in lessonText) {
                                    lessonViews[i].gravity = Gravity.END or Gravity.BOTTOM
                                    lessonViews[i].text = lessonText.substring(6)
                                }
                            }
                        }
                        else if ("pt2" in lessonText) {
                            lessonViewsExtra[i].visibility = View.VISIBLE
                            lessonViews[i].text = "---"
                            LinearLayouts[i].background = null
                            if ("pt2_ud" in lessonText) {
                                lessonViewsExtra[i].gravity = Gravity.CENTER
                                lessonViewsExtra[i].text = lessonText.substring(lessonText.indexOf("pt2_ud") + 7)
                            } else if ("pt2_up" in lessonText) {
                                lessonViewsExtra[i].gravity = Gravity.START or Gravity.TOP
                                lessonViewsExtra[i].text = lessonText.substring(lessonText.indexOf("pt2_up") + 7)
                            } else if ("pt2_down" in lessonText) {
                                lessonViewsExtra[i].gravity = Gravity.END or Gravity.BOTTOM
                                lessonViewsExtra[i].text = lessonText.substring(lessonText.indexOf("pt2_down") +9)
                            }
                        }
                        else {
                            LinearLayouts[i].background = null
                            lessonViews[i].gravity = Gravity.CENTER
                            lessonViewsExtra[i].visibility = View.GONE
                            lessonViews[i].text = lessonText + "---"
                        }

                        if (i < lessonViews.size - 1 && !it.isLast) {
                            it.moveToNext()
                        }
                    } catch (e: Exception) {
                        lessonViews[i].text = "Ошибка"
                        Log.e("Monday", "Error loading lesson $i", e)
                    }
                }
            }
        }
    }
}