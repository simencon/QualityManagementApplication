package com.simenko.qmapp.ui.neworder

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.FragmentNewOrderBinding
import com.simenko.qmapp.domain.DomainInputForOrder
import com.simenko.qmapp.domain.DomainReason
import com.simenko.qmapp.domain.DomainTeamMember
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.viewmodels.ViewModelProviderFactory
import javax.inject.Inject
import kotlin.collections.ArrayList

var listDomainInputForOrder = arrayListOf<DomainInputForOrder>()
var listDomainMeasurementReasons = arrayListOf<DomainReason>()
private const val TAG = "OrderFragment"

class PlaceOrderFragment : Fragment() {

    /**
     * Used lazy init due to the fact - is not possible to get the activity,
     * until the moment the view is created
     */
//    Custom way to obtain view model instance -----------------------------------
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    private val viewModel: QualityManagementViewModel by lazy {
        ViewModelProvider(
            this, providerFactory
        )[QualityManagementViewModel::class.java]
    }
//    Simple way to obtain view model instance-----------------------------------
//    private val viewModel: QualityManagementViewModel by viewModels()

    private lateinit var binding: FragmentNewOrderBinding
    private lateinit var dialog: Dialog

    override fun onCreateView(p0: LayoutInflater, p1: ViewGroup?, p2: Bundle?): View? {
        (context?.applicationContext as BaseApplication).appComponent.mainComponent().create().inject(this)

        binding = DataBindingUtil.inflate(p0, R.layout.fragment___new_order, p1, false)
                as FragmentNewOrderBinding

        binding.lifecycleOwner = viewLifecycleOwner

        binding.root.findViewById<TextView>(R.id.text_department_spinner)
            .setOnClickListener(createOnClickListener<DomainInputForOrder>(TargetSpinner.DEPARTMENTS))

        binding.root.findViewById<TextView>(R.id.text_sub_dep_spinner)
            .setOnClickListener(createOnClickListener<DomainInputForOrder>(TargetSpinner.SUB_DEPARTMENTS))

        binding.root.findViewById<TextView>(R.id.text_channel_spinner)
            .setOnClickListener(createOnClickListener<DomainInputForOrder>(TargetSpinner.CHANNELS))

        binding.root.findViewById<TextView>(R.id.text_item_type_spinner)
            .setOnClickListener(createOnClickListener<DomainInputForOrder>(TargetSpinner.PRODUCT_TYPES))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.inputForOrder.observe(
            viewLifecycleOwner,
            Observer<List<DomainInputForOrder>> { items ->
                items?.apply {
                    items.forEach {
                        listDomainInputForOrder.add(it)
                    }
                }
            })


        viewModel.investigationReasons.observe(
            viewLifecycleOwner,
            Observer<List<DomainReason>> { items ->
                items.apply {
                    items.forEach {
                        listDomainMeasurementReasons.add(it)
                    }
                }
            }
        )
    }

    private fun <T> createOnClickListener(targetSpinner: TargetSpinner): View.OnClickListener {
        val originalList = targetSpinner.originalList?.toList()
        lateinit var finalList: List<DomainInputForOrder>
        var parentId: Int = 0

        return View.OnClickListener {

//          First - get parent filter if exist
            if (targetSpinner.previousSpinnerId != 0) {
                parentId =
                    binding.root.findViewById<TextView>(targetSpinner.previousSpinnerId).run {
                        if (tag == null) 0 else tag as Int
                    }
            }
//            First - create final list of originals
            if (parentId != 0) {
                when (targetSpinner) {
                    TargetSpinner.INVESTIGATION_REASON -> {

                    }
                    TargetSpinner.DEPARTMENTS -> {}
                    TargetSpinner.SUB_DEPARTMENTS -> {
                        finalList =
                            ((originalList as List<DomainInputForOrder>).filter { it.id == parentId }).toList()
                                .sortedBy { it.subDepOrder }
                    }
                    TargetSpinner.CHANNELS -> {
                        finalList =
                            (listDomainInputForOrder.filter { it.subDepId == parentId }).toList()
                                .sortedBy { it.channelOrder }
                    }
                    else -> {}
                }
            } else {
                finalList = listDomainInputForOrder.toList().sortedBy { it.depOrder }
            }

            dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_searchable_spinner)
            dialog.show()

            dialog.findViewById<TextView>(R.id.dialog_title).text = targetSpinner.dialogTitle

            val editTextFilter = dialog.findViewById<EditText>(R.id.edit_text_dialog_filter)
            val itemsListView = dialog.findViewById<ListView>(R.id.list_view_dialog)

            val itemsList = arrayListOf<OrderDialogItem>()

            finalList.forEach() { mainIterator ->
//                save currently interested id for decision if it needs to be added
                val keyId = when (targetSpinner) {
                    TargetSpinner.DEPARTMENTS -> {
                        mainIterator.id
                    }
                    TargetSpinner.SUB_DEPARTMENTS -> {
                        mainIterator.subDepId
                    }
                    TargetSpinner.CHANNELS -> {
                        mainIterator.chId
                    }
                    TargetSpinner.PRODUCT_TYPES -> {
                        mainIterator.itemVersionId
                    }
                    else -> {
                        mainIterator.id
                    }
                }
//                if currently interested id not exists - add item to the list
                if ((itemsList.filter { it.getItemId() == keyId }).isEmpty())
                    itemsList.add(
                        when (targetSpinner) {
                            TargetSpinner.DEPARTMENTS -> {
                                OrderDialogItem(
                                    mainIterator.id,
                                    mainIterator.depOrder.toString(),
                                    mainIterator.depAbbr
                                )
                            }
                            TargetSpinner.SUB_DEPARTMENTS -> {
                                OrderDialogItem(
                                    mainIterator.subDepId,
                                    mainIterator.subDepOrder.toString(),
                                    mainIterator.subDepAbbr
                                )
                            }
                            TargetSpinner.CHANNELS -> {
                                OrderDialogItem(
                                    mainIterator.chId,
                                    mainIterator.channelOrder.toString(),
                                    mainIterator.channelAbbr
                                )
                            }
                            TargetSpinner.PRODUCT_TYPES -> {
                                OrderDialogItem(
                                    mainIterator.itemVersionId,
                                    mainIterator.itemKey,
                                    mainIterator.itemDesignation
                                )
                            }
                            else -> {
                                OrderDialogItem(
                                    mainIterator.id,
                                    mainIterator.depOrder.toString(),
                                    mainIterator.depAbbr
                                )
                            }
                        }
                    )
            }

            val arrayAdapter =
                CustomArrayAdapter(requireContext(), TargetSpinner.PRODUCT_TYPES, itemsList)
            itemsListView.adapter = arrayAdapter

            editTextFilter.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        arrayAdapter.filter.filter(p0)
                    }

                    override fun afterTextChanged(p0: Editable?) {
                    }
                }
            )

            itemsListView.onItemClickListener =
                OnItemClickListener { p0, p1, p2, p3 ->
                    val textView =
                        binding.root.findViewById<TextView>(targetSpinner.targetSpinnerId)

                    textView.text =
                        arrayAdapter.filter.convertResultToString(arrayAdapter.getItem(p2))

                    textView.tag = arrayAdapter.getItem(p2)!!.getItemId()

                    textView.setTypeface(textView.typeface, Typeface.BOLD)
                    dialog.dismiss()
                }
        }
    }

}

class OrderDialogItem(
    private var itemId: Int,
    private var itemShortName: String,
    private var itemLongName: String
) {
    fun getItemId() = itemId
    fun getItemShortName() = itemShortName
    fun getItemLongName() = itemLongName
}

class CustomArrayAdapter(
    context: Context,
    spinner: TargetSpinner,
    productsList: List<OrderDialogItem>
) :
    ArrayAdapter<OrderDialogItem>(context, 0, productsList) {

    private var productListFull: List<OrderDialogItem>

    init {
        productListFull = productsList.map { it }
    }

    override fun getFilter(): Filter {
        return productFilter
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val resultConvertView: View

        if (convertView == null) {
            resultConvertView = LayoutInflater.from(context).inflate(
                R.layout.item_order_dialog_item, parent, false
            )
        } else {
            resultConvertView = convertView
        }

        val textViewShortName = resultConvertView.findViewById<TextView>(R.id.text_item_short_name)
        val textViewLongName = resultConvertView.findViewById<TextView>(R.id.text_item_long_name)

        val productItem: OrderDialogItem? = getItem(position)

        if (productItem != null) {
            textViewShortName.text = productItem.getItemShortName()
            textViewLongName.text = productItem.getItemLongName()
        }

        return resultConvertView
    }

    private var productFilter: Filter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {

            val results: FilterResults = object : FilterResults() {}
            val suggestions: ArrayList<OrderDialogItem> = arrayListOf<OrderDialogItem>()

            if (constraint == null || constraint.isEmpty()) {
                suggestions.addAll(productListFull)
            } else {
                var filterPattern: String = constraint.toString().lowercase().trim()
                productListFull.forEach {
                    if (it.getItemLongName().lowercase().contains(filterPattern)) {
                        suggestions.add(it)
                    }
                }
            }
            results.values = suggestions
            results.count = suggestions.size

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            clear() //makes original passed to adapter list empty
            addAll(results!!.values as List<OrderDialogItem>)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            var resultString: String = (resultValue as OrderDialogItem).getItemShortName() +
                    "-" +
                    (resultValue as OrderDialogItem).getItemLongName()

            return resultString
        }

    }
}

enum class TargetSpinner(
    val originalList: List<Any>?,
    val spinnerOrder: Int,
    val dialogTitle: String,
    val targetSpinnerId: Int,
    val previousSpinnerId: Int,
    val nextSpinnerId: Int
) {
    INVESTIGATION_REASON(
        listDomainMeasurementReasons,
        1,
        "Причина дослідження",
        0,
        0,
        R.id.text_department_spinner
    ),
    DEPARTMENTS(
        listDomainInputForOrder,
        2,
        "Виробничий підрозділ",
        R.id.text_department_spinner,
        0,
        R.id.text_sub_dep_spinner
    ),
    SUB_DEPARTMENTS(
        listDomainInputForOrder,
        3,
        "Дільниця",
        R.id.text_sub_dep_spinner,
        R.id.text_department_spinner,
        0
    ),
    ORDER_PLACERS(null, 4, "Замовник", 0, 0, 0),
    CHANNELS(
        listDomainInputForOrder,
        5,
        "Виробничий канал",
        R.id.text_channel_spinner,
        R.id.text_sub_dep_spinner,
        0
    ),
    LINES(listDomainInputForOrder, 6, "Виробнича лінія", 0, 0, 0),
    PRODUCT_TYPES(
        listDomainInputForOrder,
        7,
        "Позначення деталі",
        R.id.text_item_type_spinner,
        0,
        0
    ),
    OPERATIONS(listDomainInputForOrder, 8, "Операція", 0, 0, 0),
    SAMPLES_QUANTITY(null, 9, "Кількість", 0, 0, 0),
    CHARACTERISTICS(listDomainInputForOrder, 10, "Параметри", 0, 0, 0);

    companion object {
        const val cKey: String = "TARGET_LIST"
    }
}

fun <T> generateOptionsList(
    spinner: TargetSpinner,
    binding: FragmentNewOrderBinding,
    originList1: List<DomainReason>,
    originList2: List<DomainInputForOrder>,
    originList3: List<DomainTeamMember>
): List<OrderDialogItem> {

    var parentId: Int = 0
    var secondParentId: Int = 0

    lateinit var finalList1: List<DomainReason>
    lateinit var finalList2: List<DomainInputForOrder>
    lateinit var finalList3: List<DomainTeamMember>
    lateinit var finalList4: List<Int>

//    First - get parent filter if exist
    if (spinner.previousSpinnerId != 0) {
        parentId =
            binding.root.findViewById<TextView>(spinner.previousSpinnerId).run {
                if (tag == null) 0 else tag as Int
            }
        secondParentId = 1
    }

    if (parentId != 0) {
        when (spinner) {
            TargetSpinner.INVESTIGATION_REASON -> {
                finalList1 = originList1.toList().sortedBy { it.reasonOrder }
            }
            TargetSpinner.DEPARTMENTS -> {
                finalList2 = originList2.toList().sortedBy { it.depOrder }
            }
            TargetSpinner.SUB_DEPARTMENTS -> {
                finalList2 =
                    (originList2.filter { it.id == parentId }).toList()
                        .sortedBy { it.subDepOrder }
            }
            TargetSpinner.ORDER_PLACERS -> {
                finalList3 =
                    (originList3.filter { it.departmentId == parentId }).toList()
                        .sortedBy { it.fullName }
            }
            TargetSpinner.CHANNELS -> {
                finalList2 =
                    (originList2.filter { it.subDepId == parentId }).toList()
                        .sortedBy { it.channelOrder }
            }
            TargetSpinner.LINES -> {
                finalList2 =
                    (originList2.filter { it.chId == parentId }).toList()
                        .sortedBy { it.channelOrder }
            }
            TargetSpinner.PRODUCT_TYPES -> {
                finalList2 =
                    (originList2.filter { it.lineId == parentId }).toList()
                        .sortedBy { it.channelOrder }
            }
            TargetSpinner.OPERATIONS -> {
                finalList2 =
                    (originList2.filter { it.itemVersionId == parentId && it.lineId == secondParentId }).toList()
                        .sortedBy { it.channelOrder }
            }
            TargetSpinner.SAMPLES_QUANTITY -> {
                finalList4 = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            }
            TargetSpinner.CHARACTERISTICS -> {
                finalList2 =
                    (originList2.filter { it.subDepId == parentId }).toList()
                        .sortedBy { it.channelOrder }
            }

        }
    } else {
        finalList1 = originList1.toList().sortedBy { it.reasonOrder }
        finalList2 = originList2.toList().sortedBy { it.depOrder }
        finalList3 = originList3.toList().sortedBy { it.fullName }
    }

    return listOf()
}