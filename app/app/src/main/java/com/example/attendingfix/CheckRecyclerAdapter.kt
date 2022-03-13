package com.example.attendingfix

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.function.Function

interface IRecyclerViewItem<T> {
    val id: Int
    val data: T
}

class IRecyclerViewItemMapHandler(override val id: Int = 0, override val data: Map<String, String>): IRecyclerViewItem<Map<String, String>>

@BindingAdapter("selection_helper", "item_id", requireAll = true)
fun <T : IRecyclerViewItem<Map<String, String>>> handleSelection(
    view: View,
    selectionHelper: ISelectionHelper<T>,
    itemId: Int
) {
    //Смотрим текущее состояние итема
    val isSelected = selectionHelper.isSelected(itemId)
    //Выбираем цвет в зависимости от состояния
    val color = if (isSelected) {
        R.color.checkCardColorPressed
    } else {
        R.color.checkCardColor
    }
    view.setBackgroundColor(ContextCompat.getColor(view.context, color))
}

class SelectionHelper<T : IRecyclerViewItem<Map<String, String>>>(private val onClick: (() -> Unit)?) : ISelectionHelper<T>() {
    //Мапа со всеми выбранными элементами
    private val selectedItems = mutableMapOf<Int, T>()
    private var clickState = false

    //Обработка итема, если он уже выбран - убираем его, иначе - наоборот
    override fun handleItem(item: T) {
        if (selectedItems[item.id] == null) {
            selectedItems.clear()
            selectedItems[item.id] = item
            if(!clickState){
                onClick?.invoke()
                clickState = !clickState
            }
        } else {
            selectedItems.remove(item.id)
            if(clickState){
                onClick?.invoke()
                clickState = !clickState
            }
        }
        notifyChange()
    }

    override fun isSelected(id: Int): Boolean = selectedItems.containsKey(id)
    override fun getSelectedItems(): List<T> = selectedItems.values.toList()
    override fun getSelectedItemsSize(): Int = selectedItems.size
}

// Наследуем класс от BaseObservable, для того, что бы dataBinding мог следить за
// изменением сотояния хелпера
abstract class ISelectionHelper<T : IRecyclerViewItem<Map<String, String>>> : BaseObservable() {
    abstract fun handleItem(item: T)
    abstract fun isSelected(id: Int): Boolean
    abstract fun getSelectedItems(): List<T>
    abstract fun getSelectedItemsSize(): Int
}

class CheckRecyclerAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val onClick: (() -> Unit)? = null
) : RecyclerView.Adapter<CheckRecyclerAdapter.ViewHolder>() {
    private val items = mutableListOf<IRecyclerViewItemMapHandler>()
    private val selectionHelper: SelectionHelper<IRecyclerViewItemMapHandler> = SelectionHelper(onClick)
    private val itemBindingId: Int? = 1
    @LayoutRes private val layoutRes: Int = R.layout.fragment_check_view_item

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutRes, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.onBind(item)
    }

    fun setItems(newItems: List<IRecyclerViewItemMapHandler>) {
        val diffUtilCallback = DiffUtilCallback(newItems)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        items.apply {
            clear()
            addAll(newItems)
        }
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(
        private val binding: ViewDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: IRecyclerViewItemMapHandler) {
            binding.apply {
                val lesson: TextView = binding.root.findViewById(R.id.checkTextViewLesson)
                val time: TextView = binding.root.findViewById(R.id.checkTextViewTime)
                val date: TextView = binding.root.findViewById(R.id.checkTextViewDate)
                lesson.text = item.data["lesson"]
                time.text = item.data["time"]
                date.text = item.data["date"]
                //Установка переменных
                setVariable(itemBindingId ?: BR.item, item)
                setVariable(BR.selectionHelper, selectionHelper)

                root.setOnClickListener {
                    //Вызываем обработку элемента
                    selectionHelper.handleItem(item)
                }
                lifecycleOwner = this@CheckRecyclerAdapter.lifecycleOwner
            }
        }
    }

    private inner class DiffUtilCallback(private val newItems: List<IRecyclerViewItemMapHandler>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = itemCount
        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return newItems[newItemPosition].id == items[oldItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return newItems[newItemPosition] == items[oldItemPosition]
        }
    }
}