package com.example.attendingfix

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import java.util.function.BiFunction
import kotlin.collections.ArrayList


class StatsSelectionHelper<T : IRecyclerViewItem<Map<String, String>>> : ISelectionHelper<T>() {
    //Мапа со всеми выбранными элементами
    private val selectedItems = mutableMapOf<Int, T>()

    //Обработка итема, если он уже выбран - убираем его, иначе - наоборот
    override fun handleItem(item: T) {
        if (selectedItems[item.id.toInt()] == null) {
            selectedItems[item.id.toInt()] = item
        } else {
            selectedItems.remove(item.id.toInt())
        }
        notifyChange()
    }

    override fun isSelected(id: Int): Boolean = selectedItems.containsKey(id)
    override fun getSelectedItems(): List<T> = selectedItems.values.toList()
    override fun getSelectedItemsSize(): Int = selectedItems.size
}


@BindingAdapter("statistic_helper", "item_id", requireAll = true)
fun <T : IRecyclerViewItem<Map<String, String>>> handleStatisticSelection(
    view: View,
    selectionHelper: ISelectionHelper<T>,
    itemId: String
) {
    //Смотрим текущее состояние итема
    val isSelected = selectionHelper.isSelected(itemId.toInt())
    //Выбираем цвет в зависимости от состояния
    val color = if (isSelected) {
        R.color.checkCardColorPressed
    } else {
        R.color.checkCardColor
    }
    view.setBackgroundColor(ContextCompat.getColor(view.context, color))
}

class StatisticRecyclerAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val filterRule: BiFunction<IRecyclerViewItemMapHandler, String, Boolean>,
    private val onBindHandler: (ViewDataBinding, IRecyclerViewItemMapHandler, TextView, TextView, TextView) -> Unit,
    @LayoutRes private val layoutRes: Int
) : RecyclerView.Adapter<StatisticRecyclerAdapter.ViewHolder>(), Filterable{
    private var items = mutableListOf<IRecyclerViewItemMapHandler>()
    private var visibleItems = mutableListOf<IRecyclerViewItemMapHandler>()
    private val selectionHelper: StatsSelectionHelper<IRecyclerViewItemMapHandler> = StatsSelectionHelper()
    private val itemBindingId: String = "1"
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutRes, parent, false)
        return ViewHolder(binding, onBindHandler)
    }

    override fun getItemCount(): Int = visibleItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = visibleItems[position]
        holder.onBind(item)
    }

    fun setItems(context: Context, newItems: List<IRecyclerViewItemMapHandler>) {
        this.context = context
        if(this.items.isEmpty()){
            items.apply {
                addAll(newItems)
            }
            visibleItems.apply {
                addAll(newItems)
            }
            notifyItemRangeInserted(0, visibleItems.size);
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return this@StatisticRecyclerAdapter.items.size
                }

                override fun getNewListSize(): Int {
                    return newItems.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return this@StatisticRecyclerAdapter.items.get(oldItemPosition)
                        .id == newItems.get(newItemPosition).id
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return areItemsTheSame(oldItemPosition, newItemPosition)
                }
            })
            this.items.apply {
                addAll(newItems)
            }
            this.visibleItems.apply {
                addAll(newItems)
            }
            result.dispatchUpdatesTo(this)
        }
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults? {
                val filteredList: MutableList<IRecyclerViewItemMapHandler> = ArrayList()
                val charString = charSequence?.toString()
                if (charString == null){
                    filteredList.addAll(items)
                }else if (charString.isEmpty()) {
                    filteredList.addAll(items)
                } else {
                    for (item in items) {
                        if (filterRule.apply(item, charString)) {
                            filteredList.add(item)
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                filterResults.count = filteredList.size
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults
            ) {
                visibleItems.apply {
                    clear()
                    addAll(filterResults.values as ArrayList<IRecyclerViewItemMapHandler>)
                }
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(
        private val binding: ViewDataBinding,
        private val onBindHandler: (ViewDataBinding, IRecyclerViewItemMapHandler, TextView, TextView, TextView) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        val text1: TextView = binding.root.findViewById(R.id.statTextViewLesson)
        val text2: TextView = binding.root.findViewById(R.id.statTextViewDate)
        val text3: TextView = binding.root.findViewById(R.id.statTextViewAttendance)

        fun onBind(item: IRecyclerViewItemMapHandler) {
            onBindHandler(binding, item, text1, text2, text3)
            binding.apply {
                //Установка переменных
                setVariable(BR.item, item)
                setVariable(BR.selectionHelper, selectionHelper)

                root.setOnClickListener {
                    //Вызываем обработку элемента
                    selectionHelper.handleItem(item)
                }
                lifecycleOwner = this@StatisticRecyclerAdapter.lifecycleOwner
            }
        }
    }
}