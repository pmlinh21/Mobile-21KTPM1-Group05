package com.example.applepie

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.applepie.database.FirebaseManager

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Report.newInstance] factory method to
 * create an instance of this fragment.
 */
class Report : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_report, container, false)

        reportTitle = rootView.findViewById(R.id.report_title)

        val typeList = listOf("Daily Report", "Weekly Report", "Monthly Report")
        val adapter = ArrayAdapter(requireContext(), R.layout.report_selected_item, typeList)
        adapter.setDropDownViewResource(R.layout.report_dropdown_item)
        reportTitle.adapter = adapter
        reportTitle.prompt = getString(R.string.select_report)

        reportTitle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = typeList[position]
                if (selectedItem == "Daily Report") {
                    replaceFragment(DailyReport.newInstance("", ""))
                }
                else if (selectedItem == "Weekly Report") {
                    replaceFragment(WeeklyReport.newInstance("", ""))
                }
                else if (selectedItem == "Monthly Report") {
                    if (FirebaseManager.getUserInfo().isPremium) {
                        replaceFragment(MonthlyReport.newInstance("", ""))
                    }
                    else {
                        // open subscribe activity

                        val subscribeActivity = Intent(requireContext(), SubscribeActivity::class.java)
                        startActivity(subscribeActivity)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        return rootView
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.container_report, fragment)
            .commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Report.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Report().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private lateinit var reportTitle: Spinner
}